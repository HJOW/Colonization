using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
    }
}
