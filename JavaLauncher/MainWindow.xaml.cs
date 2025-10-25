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

namespace WinLauncher
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
        private string versionString = "";
        private string javaPath = null;
        private string jarPath = null;
        private string pathInstalled = null;

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

            // 아키텍처 체크
            if (Environment.Is64BitOperatingSystem) arch = "win_x64";
            else arch = "win_x86";

            // 약관 동의 내용 불러오기
            taAgreements.Text = WinLauncher.Properties.Resources.agreement;

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

        private void BtnInst_Click(object sender, RoutedEventArgs e)
        {
            btnInst.IsEnabled = false;
            btnRun.IsEnabled = false;
            progMain.Value = 0;
            progMain.IsIndeterminate = true;
            progressPads = 0;

            Thread mainThread = new Thread(InstallWith);
            mainThread.Start();
        }

        private void BtnRun_Click(object sender, RoutedEventArgs e)
        {
            btnInst.IsEnabled = false;
            btnRun.IsEnabled = false;
            progMain.Value = 0;
            progMain.IsIndeterminate = true;
            progressPads = 0;

            Thread mainThread = new Thread(RunWith);
            mainThread.Start();
        }

        private void BtnExplore_Click(object sender, RoutedEventArgs e)
        {
            Util.OpenExplorer(pathInstalled);
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
            string statusMsg = "";
            bool err = false;
            bool installNeeded = false;

            try
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

                // Check Install needed
                SetStatusMessage("Java Runtime (JRE) 확인 중...");
                installNeeded = false;

                // javaPath = Environment.GetEnvironmentVariable("JAVA_HOME");
                javaPath = null;

                string javaInsPath = null;
                string javaBinPath = null;

                if (string.IsNullOrEmpty(javaPath))
                {
                    javaInsPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "jre";
                    if (!System.IO.Directory.Exists(javaInsPath))
                    {
                        System.IO.Directory.CreateDirectory(javaInsPath);
                    }

                    string[] lists = Directory.GetDirectories(javaInsPath);
                    bool jreExists = false;
                    foreach (string dirOne in lists) // May be JRE
                    {
                        if (!Directory.Exists(dirOne)) continue;
                        string mayBeJavaBinPath = Util.GetJavaBinPath(dirOne);
                        if (mayBeJavaBinPath != null)
                        {
                            jreExists = true;
                            javaPath = dirOne;
                            javaBinPath = mayBeJavaBinPath;
                            break;
                        }
                    }

                    // Check already exists on directory
                    if (!jreExists)
                    {
                        installNeeded = true;
                        statusMsg = "Java Runtime 다운로드가 필요합니다.";
                    }
                }
                else
                {
                    javaBinPath = Util.GetJavaBinPath(javaPath);
                    if (javaBinPath == null)
                    {
                        installNeeded = true;
                        statusMsg = "Java Runtime 다운로드가 필요합니다.";
                        Console.WriteLine(statusMsg);
                        Console.WriteLine(javaPath);
                        Console.WriteLine(javaBinPath);
                    }
                }

                // Check version
                versionString = swingBuild["version"].ToString();
                JObject versionInfo = (swingBuild["builds"] as JObject)[versionString] as JObject;
                string versionUrl = versionInfo["url"].ToString();
                if (!versionUrl.StartsWith("http")) versionUrl = ROOTURL + versionUrl;

                if (!installNeeded)
                {
                    // Check colonization is installed
                    SetStatusMessage("Colonization 설치 확인 중...");

                    // Prepare JAR (Libraries) Dir
                    string libDir = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "lib";
                    if (!System.IO.Directory.Exists(libDir))
                    {
                        installNeeded = true;
                        statusMsg = "Colonization JAR 다운로드가 필요합니다.";
                    }

                    jarPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "build";
                    if (!installNeeded)
                    {
                        if (!System.IO.Directory.Exists(jarPath))
                        {
                            installNeeded = true;
                            statusMsg = "Colonization JAR 다운로드가 필요합니다.";
                        }
                    }

                    if (!installNeeded)
                    {
                        if (!File.Exists(jarPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionString + ".jar"))
                        {
                            installNeeded = true;
                            statusMsg = "Colonization JAR 다운로드가 필요합니다.";
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                err = true;
                statusMsg = "오류 : " + ex.Message;
                Console.WriteLine(statusMsg);
                Console.WriteLine(ex.StackTrace);
            }

            Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
            {
                btnRun.IsEnabled = true;
                progMain.Value = 0;
                progMain.IsIndeterminate = false;

                if (err)
                {
                    btnInst.IsEnabled = false;
                    btnInst.Visibility = Visibility.Hidden;
                    btnRun.IsEnabled = false;
                }
                else if (installNeeded)
                {
                    btnInst.IsEnabled = true;
                    btnRun.IsEnabled = false;
                }
                else
                {
                    btnInst.IsEnabled = false;
                    btnInst.Visibility = Visibility.Hidden;
                    btnRun.IsEnabled = true;
                    statusMsg = "Colonization 실행 준비 완료";
                }

                if (!err)
                {
                    string noticeUrl = swingBuild["noticeKo"].ToString();
                    if (!noticeUrl.StartsWith("http")) noticeUrl = ROOTURL + noticeUrl;
                    webMain.Address = noticeUrl;
                }
                
                SetStatusMessage(statusMsg);
            }));
        }

        private void InstallWith()
        {
            try
            {
                Install();
                Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
                {
                    progMain.IsIndeterminate = false;
                    progMain.Value = 0;
                    btnInst.IsEnabled = false;
                    btnRun.IsEnabled = true;

                    if (!string.IsNullOrEmpty(pathInstalled))
                    {
                        btnExplore.Visibility = Visibility.Visible;
                    }
                    else
                    {
                        btnExplore.Visibility = Visibility.Hidden;
                    }
                }));
            }
            catch (Exception ex)
            {
                MessageBox.Show("오류 : " + ex.Message);
                Console.WriteLine("오류 : " + ex.Message);
                Console.WriteLine(ex.StackTrace);

                Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
                {
                    mainWindow.Close();
                    Application.Current.Shutdown();
                }));
            }
        }

        private void Install()
        {
            pathInstalled = "";
            
            // javaPath = Environment.GetEnvironmentVariable("JAVA_HOME");
            string javaInsPath = null;
            string javaBinPath = null;
            bool installNeeded = false;

            progressPads = 0;

            SetStatusMessage("Java Runtime (JRE) 확인 중...");

            if (string.IsNullOrEmpty(javaPath))
            {
                javaInsPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "jre";
                if (!System.IO.Directory.Exists(javaInsPath))
                {
                    System.IO.Directory.CreateDirectory(javaInsPath);
                }

                string[] lists = Directory.GetDirectories(javaInsPath);
                bool jreExists = false;
                foreach (string mayBeJrePath in lists)
                {
                    if (!Directory.Exists(mayBeJrePath)) continue;
                    string mayBeJavaBinPath = Util.GetJavaBinPath(mayBeJrePath);
                    if (mayBeJavaBinPath != null)
                    {
                        jreExists = true;
                        javaPath = mayBeJrePath;
                        javaBinPath = mayBeJavaBinPath;
                        break;
                    }
                }

                // Check already exists on directory
                if (!jreExists)
                {
                    installNeeded = true;
                }
            }
            else
            {
                javaBinPath = Util.GetJavaBinPath(javaPath);
                if (javaBinPath == null)
                {
                    installNeeded = true;
                }
            }
            
            // Download JRE if not exist
            if (installNeeded)
            {
                javaPath = null;
                javaBinPath = null;

                javaInsPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "jre";
                if (!System.IO.Directory.Exists(javaInsPath))
                {
                    System.IO.Directory.CreateDirectory(javaInsPath);
                }

                SetStatusMessage("Java Runtime (JRE) 다운로드 준비 중...");

                // Prepare to download
                string jreUrl = (json["jre"] as JObject)[arch].ToString();
                if (!jreUrl.StartsWith("http")) jreUrl = ROOTURL + jreUrl;

                Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
                {
                    progMain.IsIndeterminate = false;
                    progMain.Value = 10;
                }));

                SetStatusMessage("Java Runtime (JRE) 다운로드 중...");

                // Download JRE
                using (System.Net.WebClient client = new System.Net.WebClient())
                {
                    string downloadFile = javaInsPath + System.IO.Path.DirectorySeparatorChar + "jre.zip";

                    client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(DownloadProgressChanged);
                    client.DownloadFile(jreUrl, downloadFile); // Download

                    progressPads = 0;
                    SetProgrssValue(110);

                    // Extract ZIP
                    System.IO.Compression.ZipFile.ExtractToDirectory(downloadFile, javaInsPath);
                    File.Delete(downloadFile);

                    javaPath = null;
                    javaBinPath = null;

                    // Get Directories
                    string[] children = Directory.GetDirectories(javaInsPath);
                    foreach (string mayBeJrePath in children)
                    {
                        if (!Directory.Exists(mayBeJrePath)) continue;
                        string mayBeJavaBinPath = Util.GetJavaBinPath(mayBeJrePath);
                        if (mayBeJavaBinPath != null)
                        {
                            javaPath = mayBeJrePath;
                            javaBinPath = mayBeJavaBinPath;
                            break;
                        }
                    }

                    if (javaPath == null)
                    {
                        throw new Exception("Java Runtime (JRE) 검증에 실패하였습니다.");
                    }
                }

                SetStatusMessage("Java Runtime (JRE) 다운로드 완료");
                pathInstalled = javaPath;
            }

            SetStatusMessage("Colonization 버전 확인...");

            // Check version
            versionString = swingBuild["version"].ToString();
            JObject versionInfo = (swingBuild["builds"] as JObject)[versionString] as JObject;
            string versionUrl = versionInfo["url"].ToString();
            if (!versionUrl.StartsWith("http")) versionUrl = ROOTURL + versionUrl;

            jarPath = null;

            // Check local
            string localPath = Util.GetThisExePath();
            if (File.Exists(localPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionString + ".jar"))
            {
                jarPath = localPath;
            }

            // Download Game
            progressPads = 110;

            if (jarPath == null)
            {
                jarPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "build";
                if (!System.IO.Directory.Exists(jarPath))
                {
                    System.IO.Directory.CreateDirectory(jarPath);
                }

                if (!File.Exists(jarPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionString + ".jar"))
                {
                    SetStatusMessage("Colonization 버전 " + versionString + "다운로드 중...");
                    using (System.Net.WebClient client = new System.Net.WebClient())
                    {
                        client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(DownloadProgressChanged);
                        client.DownloadFile(versionUrl, jarPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionString + ".jar");
                    }
                    SetStatusMessage("Colonization 버전 " + versionString + " 다운로드 완료");
                    pathInstalled = jarPath;
                }
            }
            

            // Download Additional Libraries
            string libDir = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "lib";
            if (!System.IO.Directory.Exists(libDir))
            {
                System.IO.Directory.CreateDirectory(libDir);
            }

            SetStatusMessage("추가 라이브러리 다운로드 중...");
            SetProgrssValue(1);

            JArray libArr = (swingBuild["libs"] as JArray);
            int libCount = libArr.Count;
            int indexes = 0;
            foreach (JObject libOne in libArr)
            {
                string libUrl  = libOne["url"].ToString();
                string libName = libOne["name"].ToString();

                if (string.IsNullOrEmpty(libUrl) || string.IsNullOrEmpty(libName)) { indexes++; continue;  }
                SetStatusMessage("라이브러리 " + libName + " 다운로드 중...");

                using (System.Net.WebClient client = new System.Net.WebClient())
                {
                    client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(DownloadProgressChanged);
                    client.DownloadFile(libUrl, libDir + System.IO.Path.DirectorySeparatorChar + libName);
                }

                SetProgrssValue(1 + indexes);
                indexes++;
            }

            SetStatusMessage("Colonization 실행 준비 완료");
        }

        private void RunWith()
        {
            try
            {
                Run();
                Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
                {
                    SetStatusMessage("");
                    mainWindow.Close();
                }));
            }
            catch (Exception ex)
            {
                MessageBox.Show("오류 : " + ex.Message);
                Console.WriteLine("오류 : " + ex.Message);
                Console.WriteLine(ex.StackTrace);

                Dispatcher.Invoke(DispatcherPriority.Normal, new Action(delegate
                {
                    mainWindow.Close();
                    Application.Current.Shutdown();
                }));
            }
        }

        private void Run()
        {
            SetProgrssValue(0);
            SetProgressIndeterminate(true);

            string javaBinPath = Util.GetJavaBinPath(javaPath);
            string libDir = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "lib";
            jarPath = ROOTPATH + System.IO.Path.DirectorySeparatorChar + "build";
            progressPads = 0;

            // Re-Checking installation
            Install();
            
            // JAR 실행
            SetStatusMessage("Colonization 실행 중...");

            System.Diagnostics.ProcessStartInfo info = new System.Diagnostics.ProcessStartInfo();
            System.Diagnostics.Process process = new System.Diagnostics.Process();

            info.FileName = javaBinPath + System.IO.Path.DirectorySeparatorChar + "javaw.exe";
            info.CreateNoWindow  = true;
            info.UseShellExecute = false;
            info.WorkingDirectory = jarPath;
            info.Arguments = " -jar \"" + jarPath + System.IO.Path.DirectorySeparatorChar + "colonization_" + versionString + ".jar\" -cp \"" + libDir + System.IO.Path.DirectorySeparatorChar + "*" + "\"";

            info.RedirectStandardInput  = true;
            info.RedirectStandardOutput = true;
            info.RedirectStandardError  = true;

            process.StartInfo = info;
            process.Start();
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
