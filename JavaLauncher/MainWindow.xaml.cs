using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Threading;

namespace JavaLauncher
{
    /// <summary>
    /// MainWindow.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class MainWindow : Window
    {
        private static string ROOTPATH = Util.GetUserHomePath() + System.IO.Path.DirectorySeparatorChar + ".colonization";
        public MainWindow()
        {
            InitializeComponent();
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            Application.Current.Shutdown();
        }

        private void Window_Initialized(object sender, EventArgs e)
        {
            // 디렉토리 없으면 생성
            if (!System.IO.Directory.Exists(ROOTPATH))
            {
                System.IO.Directory.CreateDirectory(ROOTPATH);
            }

            progMain.IsIndeterminate = true;

            Thread mainThread = new Thread(Prepare);
            mainThread.Start();
        }

        private void BtnExit_Click(object sender, RoutedEventArgs e)
        {
            mainWindow.Close();
            Application.Current.Shutdown();
        }

        private void BtnRun_Click(object sender, RoutedEventArgs e)
        {
            // TODO
        }

        private async void Prepare()
        {
            string javaPath = Environment.GetEnvironmentVariable("JAVA_HOME");
            string javaBinPath = null;
            if (string.IsNullOrEmpty(javaPath))
            {
                Dictionary<string, object> dict = null;
                // Access Server
                using (System.Net.Http.HttpClient client = new System.Net.Http.HttpClient())
                {
                    System.Net.Http.HttpResponseMessage response = await client.GetAsync("http://hjow.duckdns.org/colonization/content.json");
                    response.EnsureSuccessStatusCode();

                    string body = await response.Content.ReadAsStringAsync();
                    dict = System.Text.Json.JsonSerializer.Deserialize<Dictionary<string, object>>(body);
                }
                

                // Parsing JSON - System.Text.Json;

            }
            else
            {
                javaBinPath = javaPath + System.IO.Path.DirectorySeparatorChar + "bin" + System.IO.Path.DirectorySeparatorChar + "javaw.exe";
            }



        }
    }
}
