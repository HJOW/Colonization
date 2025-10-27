import platform
from pathlib import Path
import os
import json
import requests

userhome = str(Path.home())
osname   = platform.system()
arch     = 'unknown';

if osname == 'Windows':
    if platform.machine() == 'AMD64' or 'x86_64':
        arch = 'win_x64'
    elif platform.machine() == 'i386':
        arch = 'win_x86'
    elif platform.machine() == 'aarch64':
        arch = 'win_arm64'
    else:
        arch = 'win_x86'
elif osname == 'Darwin': # TODO
    if platform.machine() == 'AMD64' or 'x86_64':
        arch = 'mac_x64'
    elif platform.machine() == 'aarch64' or platform.machine() == 'arm64':
        arch = 'mac_arm64'
    else:
        arch = 'mac_x86'
elif osname == 'Linux':
    if platform.machine() == 'AMD64' or 'x86_64':
        arch = 'linux_x64'
    elif platform.machine() == 'i386':
        arch = 'linux_x86'
    elif platform.machine() == 'aarch64' or platform.machine() == 'arm64':
        arch = 'linux_arm64'
    else:
        arch = 'linux_x86'

print(osname)
print(arch)
print(userhome)

colSaveHome = userhome + os.path.sep + '.colonization'
buildHome   = colSaveHome + os.path.sep + 'build'
jreListHome = colSaveHome + os.path.sep + 'jre'
libHome     = colSaveHome + os.path.sep + 'lib'

responses = requests.get('http://hjow.duckdns.org/colonization/content.json')
webConfigs = json.loads(responses.text)
swingInfo = webConfigs['swing']

print(webConfigs)
print(swingInfo)