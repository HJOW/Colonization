using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Windows;
using System.Diagnostics;
using System.Runtime.InteropServices;

namespace WinModern
{
    public class Util
    {
        /** null 이면 true 반환, 그외의 경우 문자열 형변환 후 TRIM, 빈 문자열인 경우 true 그외에는 false */
        public static bool IsEmpty(object obj)
        {
            if (obj == null) return true;
            string str = obj.ToString().Trim();
            if (str.Equals("")) return true;
            return false;
        }

        /** 어떤 객체를 bool 타입으로 변환, 변환 실패 시 예외 */
        public static bool ParseBool(object obj)
        {
            if (obj == null) return false;
            if (obj is bool) return (bool) obj;
            string str = obj.ToString().Trim().ToLower();
            if (str.Equals("y") || str.Equals("yes") || str.Equals("true") || str.Equals("t")) return true;
            if (str.Equals("n") || str.Equals("no") || str.Equals("false") || str.Equals("f")) return false;

            throw new Exception("Wrong boolean value " + obj);
        }

        /** 실행 파일의 위치 경로를 반환 (DLL 라이브러리로 빠지면 DLL 파일의 경로를 리턴할 수 있으니 주의 !) exe 파일 전체경로를 반환 */
        public static string GetThisExePath()
        {
            return System.Reflection.Assembly.GetExecutingAssembly().Location;
        }

        /** 실행 파일의 위치 경로를 반환 (DLL 라이브러리로 빠지면 DLL 파일의 경로를 리턴할 수 있으니 주의 !) 디렉토리를 반환 */
        public static string GetThisExeDir()
        {
            string exes = GetThisExePath();
            FileInfo info = new FileInfo(exes);
            return info.DirectoryName;
        }

        /** 사용자 홈 폴더 경로 반환 */
        public static string GetUserHomePath()
        {
            return Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
        }

        /** 운영체제가 Windows 인지 확인 */
        public static bool IsWindows()
        {
            // 운영체제가 Windows 인지 확인 (64비트/32비트 구분없이)
            if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows)) return true;
            return false;
        }

        /** JRE 혹은 JDK의 bin 디렉토리 (java.exe와 javaw.exe가 있는 디렉토리를 말함) 경로를 받아, 해당 Java 버전을 정수로 반환, 버전이 1.X 형태인 경우 X 값을 반환, java.exe 가 없으면 -1을 반환  */
        public static int GetJavaVersion(string javaBinPath)
        {
            if (!Directory.Exists(javaBinPath)) return -1;

            string javaExePath = javaBinPath + System.IO.Path.DirectorySeparatorChar + "java.exe";
            if (!File.Exists(javaExePath)) return -1;

            System.Diagnostics.ProcessStartInfo info = new System.Diagnostics.ProcessStartInfo();

            info.FileName = javaExePath;
            info.CreateNoWindow = true;
            info.UseShellExecute = false;
            info.WorkingDirectory = javaBinPath;
            info.Arguments = "-version";

            info.RedirectStandardInput = true;
            info.RedirectStandardOutput = true;
            info.RedirectStandardError = true;

            string outputs = "";
            using (System.Diagnostics.Process process = new System.Diagnostics.Process())
            {
                process.StartInfo = info;
                process.Start();
                
                outputs = process.StandardOutput.ReadToEnd() + process.StandardError.ReadToEnd();
                process.WaitForExit();

                outputs = outputs.Trim();

                int quoteIndex = outputs.IndexOf("\"");
                string versionPart = outputs.Substring(quoteIndex + 1);

                quoteIndex = versionPart.IndexOf("\"");
                versionPart = versionPart.Substring(0, quoteIndex);

                int underbarIndex = versionPart.IndexOf("_");
                if (underbarIndex >= 0)
                {
                    versionPart = versionPart.Substring(0, underbarIndex);
                }

                versionPart = versionPart.Trim();
                if (versionPart.StartsWith("1.")) versionPart = versionPart.Substring(2);

                double values = double.Parse(versionPart);
                return (int) values;
            }
        }

        /** 특정 폴더를 탐색기로 열기 (해당 폴더가 존재해야 동작함) */
        public static void OpenExplorer(string path)
        {
            if (Directory.Exists(path))
            {
                Process.Start("explorer.exe", path);
            }
        }

        /** JRE 설치 디렉토리 안에서 bin 디렉토리 찾아 반환. 존재하지 않으면 null 반환. */
        public static string GetJavaBinPath(string javaInstallPath)
        {
            if (!Directory.Exists(javaInstallPath)) return null;
            string[] children = Directory.GetDirectories(javaInstallPath);
            if (children == null) return null;

            foreach (string child in children)
            {
                if (!Directory.Exists(child)) continue;
                if (child.EndsWith(Path.DirectorySeparatorChar + "bin"))
                {
                    if (GetJavaVersion(child) >= 8) return child;
                }
                
                string[] grands = Directory.GetDirectories(child);
                bool binExists = false;
                foreach (string g in grands)
                {
                    if (g.EndsWith(Path.DirectorySeparatorChar + "bin")) { binExists = true; break; }
                }
                if (!binExists) continue;

                string binPath = child + Path.DirectorySeparatorChar + "bin";
                if (!Directory.Exists(binPath)) continue;

                if (GetJavaVersion(binPath) < 8) continue;
                return binPath;
            }
            return null;
        }
    }
}
