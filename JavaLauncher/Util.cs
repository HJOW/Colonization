using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Windows;

namespace JavaLauncher
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

        /** 실행 파일의 위치 경로를 반환 (DLL 라이브러리로 빠지면 DLL 파일의 경로를 리턴할 수 있으니 주의 !) */
        public static string GetThisExePath()
        {
            return System.Reflection.Assembly.GetExecutingAssembly().Location;
        }

        /** 사용자 홈 폴더 경로 반환 */
        public static string GetUserHomePath()
        {
            return Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
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
                Console.WriteLine(outputs);

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
    }
}
