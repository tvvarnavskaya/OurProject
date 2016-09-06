package com.tasl.other;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * run: com.tasl.other.SampleClient tatiana 121
 *
 * Created by tvarnavskaya on 26.08.2016.
 */
public class SampleClient extends Thread {
//    public static void main(String args[]) {
//        SampleClient sampleClient = new SampleClient();
//        sampleClient.listenSocket(args);
//    }

    private final String toServer;
    private final String sendingType;

    public SampleClient(String sendingType, String toServer) {
        this.toServer = toServer;
        this.sendingType = sendingType;
    }

    public boolean listenSocket() {
        boolean out = false;
        try {
            // открываем сокет и коннектимся к localhost:3128
            // получаем сокет сервера
            Socket s = new Socket("localhost", 3128);

            switch(sendingType) {
                case "LoginData":
                    System.out.println("in case");
                    LoginDataSenderToServer loginDataSenderToServer = new LoginDataSenderToServer(s, toServer);
                    out = loginDataSenderToServer.sendLoginData();
                    System.out.println("isValid="+out);
                    break;
                default:
                    System.out.println("no actions");
            }

//            ClientSender clientSender = new ClientSender(s, toServer);
//            Thread tSender = new Thread(clientSender);
//            tSender.start();
//
//            ClientReceiver clientReceiver = new ClientReceiver(s);
//            Thread tReceiver = new Thread(clientReceiver);
//            tReceiver.start();


        }
        catch(Exception e) {
            System.out.println("init error: "+e);
        } // вывод исключений
        return out;
    }

    class LoginDataSenderToServer {
        private final Socket server;
        private final String toServer;

        public LoginDataSenderToServer(Socket server, String toServer) {
            this.server = server;
            this.toServer = toServer;
        }
        public boolean sendLoginData() {
            boolean isValid = false;
            try {

                // Берем выходной поток сокета, теперь можем получать и отсылать данные клиентом.
                OutputStream sout = server.getOutputStream();
                InputStream sin = server.getInputStream();
                // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                DataOutputStream out = new DataOutputStream(sout);
                DataInputStream in = new DataInputStream(sin);
                while (true) {
                    out.writeUTF(toServer); // отсылаем введенную строку текста серверу.
                    out.flush();
                      String lineFromServer = null;
                    try {
                        lineFromServer = in.readUTF();
                        System.out.println("lineFromServer="+lineFromServer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isValid = Boolean.valueOf(lineFromServer);

                    if (lineFromServer != null && !lineFromServer.equals(""))
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return isValid;
        }

//        private void tryReConnect() {
//            try
//            {
//                server.close();
//                //empty my old lost connection and let it get by garbage col. immediately
//                clientSocket=null;
//                System.gc();
//                //Wait a new client Socket connection and address this to my local variable
//                clientSocket= ServerSocket.accept(); // Waiting for another Connection
//                System.out.println("Connection established...");
//            }catch (Exception e) {
//                String message="ReConnect not successful "+e.getMessage();
//            }
//        }
    }

    class ClientSender implements Runnable {

        private final Socket server;
        private final String toServer;

        public ClientSender(Socket server, String toServer) {
            this.server = server;
            this.toServer = toServer;
        }

        public void run() {
            try {

                // Берем выходной поток сокета, теперь можем получать и отсылать данные клиентом.
                OutputStream sout = server.getOutputStream();
                // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                DataOutputStream out = new DataOutputStream(sout);

                // Создаем поток для чтения с клавиатуры.
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                String line;
                System.out.println("Type msg:");
                System.out.println();

                while ((line = keyboard.readLine()) != null) {
//                line = keyboard.readLine(); // ждем пока пользователь введет что-то и нажмет кнопку Enter.
                    out.writeUTF(toServer); // отсылаем введенную строку текста серверу.
                    out.flush(); // заставляем поток закончить передачу данных.1
                    System.out.println("sended: " + line);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ClientReceiver implements Runnable {

        private final Socket server;

        public ClientReceiver(Socket server) {
            this.server = server;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                InputStream sin = server.getInputStream();
                DataInputStream in = new DataInputStream(sin);
                while (true) {
                    System.out.println("before asking server...");
                    String lineFromServer = in.readUTF(); // ждем пока сервер отошлет строку текста.
                    System.out.println("Got from server : " + lineFromServer);
                    System.out.println();
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - startTime > 10000) {
                        System.out.println("wait for msgs");
                        startTime = currentTime;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}