package sample;

import com.gtranslate.Audio;
import com.gtranslate.Language;
import com.sun.corba.se.impl.activation.ServerMain;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javazoom.jl.decoder.JavaLayerException;

import javax.naming.ldap.Control;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class    Controller {

    private Text codeTarget;

    public Controller(Text codeTarget) {
        this.codeTarget = codeTarget;
    }

    public void handleSubmitButtonAction() {

        try {
            new ServerThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Integer i = -1 * toInt(InetAddress.getLocalHost().getAddress(), 0);
            codeTarget.setText(i.toString());
        } catch (UnknownHostException e) {
            //
        }

    }

    public static int toInt(byte[] bytes, int offset) {
        int ret = 0;
        for (int i = 0; i < 4 && i + offset < bytes.length; i++) {
            ret <<= 8;
            ret |= (int) bytes[i] & 0xFF;
        }
        return ret;
    }
}
