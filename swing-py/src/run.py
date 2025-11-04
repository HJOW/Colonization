import platform
from pathlib import Path
import os
import json
import subprocess
import shutil
import requests

userhome = str(Path.home())
osname   = platform.system()
arch     = 'unknown';

# Detect system and architecture
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

colSaveHome = userhome + os.path.sep + '.colonization'
buildHome   = colSaveHome + os.path.sep + 'build'
jreListHome = colSaveHome + os.path.sep + 'jre'
libHome     = colSaveHome + os.path.sep + 'lib'

rootUrl = 'http://hjow.duckdns.org/colonization/'
responses = requests.get(rootUrl + 'content.json')
webConfigs = json.loads(responses.text)

# parse infos
swingInfo = webConfigs['swing']
swingVer = swingInfo['version']
swingBuilds = swingInfo['builds']
swingCurrBuild = swingBuilds[swingVer]
swingUrl = swingCurrBuild['url']

if not swingUrl.startswith('http'):
    swingUrl = rootUrl + swingUrl

# mkdirs
if not os.path.exists(colSaveHome):
    os.mkdir(colSaveHome)
if not os.path.exists(buildHome):
    os.mkdir(buildHome)
if not os.path.exists(jreListHome):
    os.mkdir(jreListHome)
if not os.path.exists(libHome):
    os.mkdir(libHome)

# Check game install needed, and install
swingFileName = 'colonization-swing-' + swingVer + '.jar'
swingFileFull = buildHome + os.path.sep + swingFileName
if not os.path.exists(swingFileFull): # Check main game jar already exists
    with open(swingFileFull, 'wb') as file: # Download main game jar
        responseFile = requests.get(swingUrl)
        file.write(responseFile.content)
    
# Check JRE install needed
jreBinPath = ''
if os.path.exists(jreListHome):
    lists = os.listdir(jreListHome)
    for dirOne in lists:
        currentJreBinPath = dirOne + os.path.sep + 'bin'
        if os.path.exists(currentJreBinPath + os.path.sep + 'java') or os.path.exists(currentJreBinPath + os.path.sep + 'java.exe'): # Check JRE already exists
            jreBinPath = currentJreBinPath;
            break

# Download libs
packClassList = []
libs = swingInfo['libs']
for libOne in libs:
    libUrl  = str(libOne['url'])
    libName = str(libOne['name'])
    if libUrl == None:
        continue
    if not libUrl.startswith('http'):
        libUrl = rootUrl + libUrl
    libFile = libHome + os.path.sep + libName
    with open(libFile, 'wb') as file:
        responseFile = requests.get(libUrl)
        file.write(responseFile.content)
    if libOne['pack'] != None:
        packClassOne = str(libOne['pack'])
        packClassList.append(packClassOne)

packClassFile = libHome + os.path.sep + 'packs.txt'
if not os.path.exists(packClassFile):
    with open(packClassFile, 'a', encoding='utf-8') as file:
        firstLine = True
        for packClassOne in packClassList:
            if firstLine:
                file.write('\n')
            file.write(packClassOne)
            firstLine = False

# Download JRE
if (not os.path.exists(jreBinPath)) or (jreBinPath == ''):
    jreInfo = webConfigs['jre']
    jreArchInfo = jreInfo[arch]
    
    if jreArchInfo is None:
        print('This system does not supported !')
        exit()
    
    zipUrl  = rootUrl + jreArchInfo
    zipFile = jreListHome + os.path.sep + jreArchInfo
    with open(zipFile, 'wb') as file: # Download main game jar
        responseFile = requests.get(zipUrl)
        file.write(responseFile.content)
    shutil.unpack_archive(zipFile, jreListHome, 'zip')
    os.remove(zipFile)
    
    # re-search jre path
    lists = os.listdir(jreListHome)
    for dirOne in lists:
        currentJreBinPath = jreListHome + os.path.sep + dirOne + os.path.sep + 'bin'
        if os.path.exists(currentJreBinPath + os.path.sep + 'java') or os.path.exists(currentJreBinPath + os.path.sep + 'java.exe'): # Check JRE already exists
            jreBinPath = currentJreBinPath;
            break

# Run Game
commands = ['java', '-cp', libHome + os.path.sep + '*:' + swingFileFull, 'org.duckdns.hjow.colonization.Colonization', '--updator', 'N']
subprocess.call(commands, cwd=jreBinPath)