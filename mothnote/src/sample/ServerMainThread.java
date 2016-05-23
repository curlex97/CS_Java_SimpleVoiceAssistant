package sample;

import javafx.scene.control.Alert;
import javafx.scene.text.Text;

import javax.net.ServerSocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 07.01.2016.
 */
class ServerThread implements Runnable {
    Thread t;
    int port = 29349;
    Socket clientSocket;
    public static ArrayList<Socket> Sockets = new ArrayList<Socket>();
    ServerSocket ss; // создаем сокет сервера и привязываем его к вышеуказанному порту

    ServerThread() throws IOException {
        //   this.list1 = list1;
        ss = new ServerSocket(port);
        t = new Thread(this);
        t.start(); // Start the thread
    }

    // This is the entry point for thread.
    public void run() {
        while (true) {

            // случайный порт (может быть любое число от 1025 до 65535)
            try {
                //  list1.setText("Ожидаем подключение клиента...");
                Socket cs = ss.accept();
                Sockets.add(cs); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером

                //   list1.setText("Соединение установлено");
                new AuthClientThread(Sockets.get(Sockets.size() - 1));
                //socket.shutdownOutput();
            } catch (Exception x) {
                JOptionPane.showMessageDialog(null, x);
            }
            try {
                //if(sockets.get(sockets.size() -1) != null){
                //   sockets.get(sockets.size() -1).shutdownInput();
                //   sockets.get(sockets.size() -1).shutdownOutput();
                //   sockets.get(sockets.size() -1).close();
                // }
            } catch (Exception x) {
                JOptionPane.showMessageDialog(null, "Клиент: " + x);
            }
        }
    }
}


class AuthClientThread implements Runnable {
    Thread t;
    Socket clientSocket;
    TcpAuthClient client;
    VoiceExecutor voiceExecutor;

    AuthClientThread(Socket socket) {

        clientSocket = socket;

        InputStream sin;
        try {
            sin = clientSocket.getInputStream();
        } catch (IOException ex) {
            return;
        }
        //  this.list1 = list1;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {

        client = new TcpAuthClient();
        // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
        InputStream sin;
        try {
            sin = clientSocket.getInputStream();
            String theString;
            //theString = IOUtils.toString(sin, "UTF-8");
        } catch (IOException ex) {
            //  list1.setText("1 " + ex);
            return;
        }
        OutputStream sout;
        try {
            sout = clientSocket.getOutputStream();
        } catch (IOException ex) {
            //  list1.setText("2 " + ex);
            return;
        }

        // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
        DataInputStream in = new DataInputStream(sin);
        DataOutputStream out = new DataOutputStream(sout);
        // list1.setText("Ожидаение инициализации...");
        String line = null;
        voiceExecutor = new VoiceExecutor();
        while (true) {
            try {
                byte[] bytes = new byte[1024];
                int u = in.read(bytes);
                byte[] retbytes = new byte[u];
                for (int i = 0; i < u; i++) retbytes[i] = bytes[i];
                line = new String(retbytes, Charset.forName("UTF-8"));

                voiceExecutor.Execute(line);
                //  JOptionPane.showMessageDialog(null, "Клиент: " + line);


            } catch (IOException ex) {
                //   list1.setText(client.Name + " - error: " + ex);
            }
        }
    }
}

