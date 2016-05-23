using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Phone.Speech.Synthesis;

namespace VitaVoice
{
    class PigaScriptExecutor
    {
        public void Execute(string script)
        {
            script = script.Replace("\n", String.Empty).Replace("\t", String.Empty).Replace("\r", String.Empty);

             SpeechSynthesizer speech = new SpeechSynthesizer();
             speech.SpeakTextAsync(script);
            
        }
    }

}
