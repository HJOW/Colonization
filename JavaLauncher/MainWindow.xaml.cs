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
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Net;
using System.Windows.Threading;

namespace JavaLauncher
{
    /// <summary>
    /// MainWindow.xaml에 대한 상호 작용 논리
    /// </summary>
    public partial class MainWindow : Window
    {
        private static string ROOTPATH = Util.GetUserHomePath() + System.IO.Path.DirectorySeparatorChar + ".colonization";
        private const string ROOTURL = "http://hjow.duckdns.org/colonization/";

        private int progressPads = 0;
        private string arch = "win_x64";
        private JObject json = null;
        private JObject swingBuild = null;

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

            // 약관 동의 내용 불러오기
            taAgreements.Text = JavaLauncher.Properties.Resources.agreement;

            // 사전 준비 작업 시작

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
            btnRun.IsEnabled = false;
            progMain.Value = 0;
            progMain.IsIndeterminate = true;
            progressPads = 0;

            Thread mainThread = new Thread(Run);
            mainThread.Start();
        }

        private void BtnAgreeOk_Click(object sender, RoutedEventArgs e)
        {
            tabItemMainAction.IsEnabled = true;
            tabMain.SelectedIndex = 1;
        }

        private void BtnAgreeCancel_Click(object sender, RoutedEventArgs e)
        {
            mainWindow.Close();
            Application.Current.Shutdown();
        }

        private async void Prepare()
        {
            // Access Server
            using (System.Net.Http.HttpClient client = new System.Net.Http.HttpClient())
            {
                System.Net.Http.HttpResponseMessage response = await client.GetAsync(ROOTURL + "content.json");
                response.EnsureSuccessStatusCode();

                string body = await response.Content.ReadAsStringAsync();
                json = JObject.Parse(body);
                swingBuild = json["swing"] as JObject;
            }

            Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
            {
                btnRun.IsEnabled = true;
                progMain.Value = 0;
                progMain.IsIndeterminate = false;
                webMain.Address = swingBuild["noticeKo"].ToString();
            }));
        }

        private void Run()
        {
            string javaPath = Environment.GetEnvironmentVariable("JAVA_HOME");
            string javaBinPath = null;
            javaPath = null;
            
            progressPads = 0;

            SetStatusMessage("Java Runtime (JRE) 확인 중...");

            // Download JRE if not exist
            if (string.IsNullOrEmpty(javaPath))
            {
                javaPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "jre";
                if (!System.IO.Directory.Exists(javaPath))
                {
                    System.IO.Directory.CreateDirectory(javaPath);
                }

                // Check already exists on directory
                if (!File.Exists(javaPath + System.IO.Path.DirectorySeparatorChar + "bin" + System.IO.Path.DirectorySeparatorChar + "javaw.exe"))
                {
                    SetStatusMessage("Java Runtime (JRE) 다운로드 준비 중...");

                    // Prepare to download
                    string jreUrl = (json["jre"] as JObject)[arch].ToString();
                    if (! jreUrl.StartsWith("http")) jreUrl = ROOTURL + jreUrl;

                    Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
                    {
                        progMain.IsIndeterminate = false;
                        progMain.Value = 10;
                    }));

                    SetStatusMessage("Java Runtime (JRE) 다운로드 중...");

                    // Download JRE
                    using (System.Net.WebClient client = new System.Net.WebClient())
                    {
                        client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(DownloadProgressChanged);
                        client.DownloadFile(jreUrl, javaPath + System.IO.Path.DirectorySeparatorChar + "jre.zip");

                        progressPads = 0;
                        SetProgrssValue(110);
                        System.IO.Compression.ZipFile.ExtractToDirectory(javaPath + System.IO.Path.DirectorySeparatorChar + "jre.zip", javaPath);
                        File.Delete(javaPath + System.IO.Path.DirectorySeparatorChar + "jre.zip");
                    }

                    SetStatusMessage("Java Runtime (JRE) 다운로드 완료");
                }
            }
            javaBinPath = javaPath + System.IO.Path.DirectorySeparatorChar + "bin" + System.IO.Path.DirectorySeparatorChar + "javaw.exe";

            SetStatusMessage("Colonization 버전 확인...");

            // Check version
            string versionNew = swingBuild["version"].ToString();
            JObject versionInfo = (swingBuild["builds"] as JObject)[versionNew] as JObject;
            string versionUrl = versionInfo["url"].ToString();
            if (! versionUrl.StartsWith("http")) versionUrl = ROOTURL + versionUrl;

            // Prepare JAR (Libraries) Dir
            string libDir = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "lib";
            if (!System.IO.Directory.Exists(libDir))
            {
                System.IO.Directory.CreateDirectory(libDir);
            }

            // Download Game
            progressPads = 110;

            string jarPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "build";
            if (!System.IO.Directory.Exists(jarPath))
            {
                System.IO.Directory.CreateDirectory(jarPath);
            }

            if (! File.Exists(jarPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionNew + ".jar"))
            {
                SetStatusMessage("Colonization 버전 " + versionNew + "다운로드 중...");
                using (System.Net.WebClient client = new System.Net.WebClient())
                {
                    client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(DownloadProgressChanged);
                    client.DownloadFile(versionUrl, jarPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionNew + ".jar");
                }
                SetStatusMessage("Colonization 버전 " + versionNew + "다운로드 완료");
            }

            progressPads = 0;
            SetProgrssValue(0);
            SetProgressIndeterminate(true);

            // JAR 실행

            SetStatusMessage("Colonization 실행 중...");

            System.Diagnostics.ProcessStartInfo info = new System.Diagnostics.ProcessStartInfo();
            System.Diagnostics.Process process = new System.Diagnostics.Process();

            info.FileName = javaBinPath;
            info.CreateNoWindow  = true;
            info.UseShellExecute = false;
            info.WorkingDirectory = jarPath;
            info.Arguments = " -jar \"" + jarPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionNew + ".jar\" -cp \"" + libDir + System.IO.Path.DirectorySeparatorChar + "*" + "\"";

            info.RedirectStandardInput  = true;
            info.RedirectStandardOutput = true;
            info.RedirectStandardError  = true;

            process.StartInfo = info;
            process.Start();

            Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
            {
                SetStatusMessage("");
                mainWindow.Close();
            }));
        }

        private void SetProgressIndeterminate(bool indeterminate)
        {
            Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
            {
                progMain.IsIndeterminate = indeterminate;
            }));
        }

        private void SetProgrssValue(int val)
        {
            Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
            {
                progMain.Value = val;
            }));
        }

        private void SetStatusMessage(string msg)
        {
            Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
            {
                lbStatus.Content = msg;
            }));
        }

        private void DownloadProgressChanged(object sender, DownloadProgressChangedEventArgs e)
        {
            SetProgrssValue(e.ProgressPercentage + progressPads + 10);
        }
    }
}
