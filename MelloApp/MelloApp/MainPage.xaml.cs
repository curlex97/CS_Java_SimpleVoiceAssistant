using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Media.Imaging;
using Windows.Networking;
using Windows.Networking.Sockets;
using Windows.Phone.Speech.Recognition;
using Windows.Phone.Speech.Synthesis;
using Windows.Storage.Streams;
using VitaVoice;

namespace MelloApp
{
    public partial class MainPage
    {

        SpeechRecognizerUI _recoWithUi;
        SpeechSynthesizer _synth;
        readonly Vita _vita = new Vita();
        private int _fl = 1;
        private string _press = "bp.png";
        private string _release = "bs.png";
        // Constructor
        public MainPage()
        {
            InitializeComponent();
            _synth = new SpeechSynthesizer();
            // Sample code to localize the ApplicationBar
            //BuildLocalizedApplicationBar();
        }

        public SpeechSynthesizer Synth
        {
            get { return _synth; }
            set { _synth = value; }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
           
        }

        private async void Sp(object sender, RoutedEventArgs e)
        {
            try
            {
                if (sender == null) { }
                if (e == null) { }

                _recoWithUi = new SpeechRecognizerUI();
                _recoWithUi.Settings.ReadoutEnabled = false;
                _recoWithUi.Settings.ShowConfirmation = false;
                _recoWithUi.Settings.ExampleText = "";
                string[] b = _vita.GetAllCommands();
                _recoWithUi.Recognizer.Grammars.AddGrammarFromList("frenchNumbers", b);

                IEnumerable<SpeechRecognizerInformation> frenchRecognizers = from recognizerInfo in InstalledSpeechRecognizers.All
                                                                             where recognizerInfo.Language == "ru-RU"
                                                                             select recognizerInfo;

                _recoWithUi.Recognizer.SetRecognizer(frenchRecognizers.ElementAt(0));
                SpeechRecognitionUIResult recoResult = await _recoWithUi.RecognizeWithUIAsync();
                //SpeechSynthesizer synth = new SpeechSynthesizer();
                //await synth.SpeakTextAsync(recoResult.RecognitionResult.Text);
                MoonPadTcpClient.Send(recoResult.RecognitionResult.Text);
                _fl = 1;
            }
            catch(Exception ex)
            {
                _fl = 1;
            }
                
        }

        private void button_Click_1(object sender, RoutedEventArgs e)
        {
           

        }

        private void image_MouseLeftButtonDown(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            image.Source = new BitmapImage(new Uri("/Resources/" + _press, UriKind.RelativeOrAbsolute));
        }

        private void image_MouseLeftButtonUp(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {

            if (_press != "sp.png")
            {
                _press = "sp.png";
                _release = "sr.png";
                image.Source = new BitmapImage(new Uri("/Resources/" + _release, UriKind.RelativeOrAbsolute));
                try
                {
                    byte[] bytes = BitConverter.GetBytes(-1 * Convert.ToInt32(textBox.Text));
                    MoonPadTcpClient.Hostname = bytes[3] + "." + bytes[2] + "." + bytes[1] + "." + bytes[0];
                    MoonPadTcpClient.Port = textBox1.Text;
                    MoonPadTcpClient.Connect();
                }
                catch
                {
                    // Ignored
                }
                return;
            }
           
            image.Source = new BitmapImage(new Uri("/Resources/" + _release, UriKind.RelativeOrAbsolute));

            if (_fl == -1)
            {
                MoonPadTcpClient.Send("phone" + DateTime.Now.Day + DateTime.Now.Hour + DateTime.Now.Minute +
                                  DateTime.Now.Second + DateTime.Now.Millisecond);
                _fl = 1;
            }
            else if (_fl == 1)
            {
                _fl = 0;

                Sp(sender, e);

            }
        }

        // Sample code for building a localized ApplicationBar
        //private void BuildLocalizedApplicationBar()
        //{
        //    // Set the page's ApplicationBar to a new instance of ApplicationBar.
        //    ApplicationBar = new ApplicationBar();

        //    // Create a new button and set the text value to the localized string from AppResources.
        //    ApplicationBarIconButton appBarButton = new ApplicationBarIconButton(new Uri("/Assets/AppBar/appbar.add.rest.png", UriKind.Relative));
        //    appBarButton.Text = AppResources.AppBarButtonText;
        //    ApplicationBar.Buttons.Add(appBarButton);

        //    // Create a new menu item with the localized string from AppResources.
        //    ApplicationBarMenuItem appBarMenuItem = new ApplicationBarMenuItem(AppResources.AppBarMenuItemText);
        //    ApplicationBar.MenuItems.Add(appBarMenuItem);
        //}
    }



    public static class MoonPadTcpClient
    {
        private static StreamSocket clientSocket = new StreamSocket();
        private static HostName serverHost;
        public static string Hostname;
        public static string Port;
        public static string recieveData = "";
        private static bool connected = false;
        private static bool closing = false;
        public static byte[] Buffer;

        public static string RecieveData
        {
            get
            {
                return recieveData;
            }
            set { recieveData = value; }
        }

        public static bool Connected
        {
            get { return connected; }
            set { connected = value; }
        }

        public async static void Connect()
        {
            if (connected)
            {
                //StatusText.Text = "Already connected";
                return;
            }

            try
            {
                //OutputView.Text = "";
                //StatusText.Text = "Trying to connect ...";

                serverHost = new HostName(Hostname);
                // Try to connect to the 
                await clientSocket.ConnectAsync(serverHost, Port);
                connected = true;
                //StatusText.Text = "Connection established" + Environment.NewLine;

            }
            catch (Exception exception)
            {
                // If this is an unknown status, 
                // it means that the error is fatal and retry will likely fail.
                //if (exception.HResult as SocketErrorStatus == SocketErrorStatus.Unknown)
                //{
                //    throw;
                //}

                // StatusText.Text = "Connect failed with error: " + exception.Message;
                // Could retry the connection, but for this simple example
                // just close the socket.

                closing = true;
                // the× Close method is mapped to the C# Dispose
                clientSocket.Dispose();
                clientSocket = null;

            }
        }

        public async static void Send(string msg)
        {
            if (!connected)
            {
                // StatusText.Text = "Must be connected to send!";
                return;
            }

            uint len = 0; // Gets the UTF-8 string length.

            try
            {
                //  OutputView.Text = "";
                // StatusText.Text = "Trying to send data ...";

                // add a newline to the text to send
                string sendData = msg;
                DataWriter writer = new DataWriter(clientSocket.OutputStream);
                //len = writer.MeasureString(sendData); // Gets the UTF-8 string length.
                //len =       
                   writer.WriteBytes(System.Text.Encoding.UTF8.GetBytes(sendData));
                // Call StoreAsync method to store the data to a backing stream
                await writer.StoreAsync();

                //StatusText.Text = "Data was sent" + Environment.NewLine;

                // detach the stream and close it
                writer.DetachStream();
                writer.Dispose();

            }
            catch (Exception exception)
            {
                // If this is an unknown status, 
                // it means that the error is fatal and retry will likely fail.
                //if (SocketError.GetStatus(exception.HResult) == SocketErrorStatus.Unknown)
                //{
                //    throw;
                //}

                //StatusText.Text = "Send data or receive failed with error: " + exception.Message;
                // Could retry the connection, but for this simple example
                // just close the socket.

                closing = true;
                clientSocket.Dispose();
                clientSocket = null;
                connected = false;

            }


        }

        public static async void Recieve()
        {

            string ret = "";
            if (!connected) return;

            try
            {
                //OutputView.Text = "";
                // StatusText.Text = "Trying to receive data ...";

                DataReader reader = new DataReader(clientSocket.InputStream);
                // Set inputstream options so that we don't have to know the data size
                reader.InputStreamOptions = InputStreamOptions.Partial;
                uint bytesAvailable = await reader.LoadAsync(4096);

                byte[] byArray = new byte[bytesAvailable];
                reader.ReadBytes(byArray);
                recieveData = Encoding.UTF8.GetString(byArray, 0,
                    Convert.ToInt32(bytesAvailable));
                reader.DetachStream();
                reader.Dispose();
            }
            catch (Exception exception)
            {


                //StatusText.Text = "Receive failed with error: " + exception.Message;
                // Could retry, but for this simple example
                // just close the socket.

                closing = true;
                clientSocket.Dispose();
                clientSocket = null;
                connected = false;


            }
        }

        public static byte[] ReadFully(Stream input)
        {
            byte[] buffer = new byte[16 * 1024];
            using (MemoryStream ms = new MemoryStream())
            {
                int read;
                while ((read = input.Read(buffer, 0, buffer.Length)) > 0)
                {
                    ms.Write(buffer, 0, read);
                }
                return ms.ToArray();
            }
        }

        

    }
}