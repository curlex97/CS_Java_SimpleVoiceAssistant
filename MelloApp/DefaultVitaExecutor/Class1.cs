using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Windows.Phone.Speech.Synthesis;

namespace DefaultVitaExecutor
{
    public class Executor
    {
        public void Execute(string script)
        {
            if (script.Contains("как"))
            {
                SpeechSynthesizer speech = new SpeechSynthesizer();
                speech.SpeakTextAsync("каком к верху");
            }
        }
    }
}
