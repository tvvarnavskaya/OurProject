package com.tasl;

import entity.LoginData;
import process.LoginDataProcessor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tvarnavskaya on 26.08.2016.
 */
public class SampleServer2 {

    private ServerSocket server;
    private CopyOnWriteArrayList<ChatObj> objs = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, Integer> rooms = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SampleServer2 s = new SampleServer2();
        s.listenSocket();
    }

    public void listenSocket(){
        try{
            server = new ServerSocket(3128);
        } catch (IOException e) {
            System.out.println("Could not listen on port 3128");
            System.exit(-1);
        }
        while(true){
            try{
                //server.accept returns a client connection
                Socket socket = server.accept();
                InputStream sin = socket.getInputStream();
                DataInputStream in = new DataInputStream(sin);
                String line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                String className = line.substring(0, line.indexOf("{"));
                String jsonData = line.substring(line.indexOf("{"));
                System.out.println("className="+className+"jsonData="+jsonData);

                //JSON from String to Object
                switch (className) {
                    case "LoginData" :
                        System.out.println("in case");
                        LoginDataProcessor loginDataProcessor = new LoginDataProcessor();
                        System.out.println("1");
                        LoginData loginData = loginDataProcessor.processData(jsonData);
                        System.out.println("loginData="+loginData);
                        boolean isValid = loginDataProcessor.isValidLoginData(loginData.getUserName(), loginData.getPassword());
                        System.out.println("isValid="+isValid);
                        LoginDataServerSender loginDataServerSender = new LoginDataServerSender(socket, isValid);
                        System.out.println("before send");
                        loginDataServerSender.send();
                        break;
                    default:
                        System.out.println("there is no any realization for class: " + className);
                }


//                ServerReceiver serverReceiver = new ServerReceiver(socket);
//                Thread tReceiver = new Thread(serverReceiver);
//                tReceiver.start();
//
//                ServerSender serverSender = new ServerSender("", "", socket);
//                Thread tSender = new Thread(serverSender);
//                tSender.start();


            } catch (IOException e) {
                System.out.println("Accept failed: 3128");
                System.exit(-1);
            }
        }
    }



    class LoginDataServerSender {
        private final Socket server;
        private final boolean isValidLoginData;

        public LoginDataServerSender(Socket server, boolean isValidLoginData) {
            this.server = server;
            this.isValidLoginData = isValidLoginData;
        }
        public void send() {
            try {
                OutputStream sout = server.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);
                out.writeUTF("" + isValidLoginData);
                out.flush();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    class ServerReceiver implements Runnable {
        private Socket client;

        ServerReceiver(Socket client) {
            this.client = client;
        }

        public void run(){
            System.out.println("started ClientReciever");
            String line;

            String roomNumber = null;
            String userName = null;
            String msg = null;

            while (true) {
                try{
                    System.out.println("in while");
                    InputStream sin = client.getInputStream();
                    DataInputStream in = new DataInputStream(sin);
                    line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                    System.out.println("Got from client : " + line);
                    String[] clientArgs = line.split(":");
                    roomNumber = clientArgs[2];
                    userName = clientArgs[1];
                    msg = clientArgs[0];
                    ChatObj chatObj = new ChatObj(msg, userName, roomNumber, 1);
                    chatObj.getUsers().add(userName);
                    objs.add(chatObj);
                    int roomCounts = 0;
                    if (rooms.get(roomNumber) != null) {
                        roomCounts = rooms.get(roomNumber);
                    }
                    if (roomCounts == 0) {
                        rooms.put(roomNumber, 1);
                    } else {
                        int newCount = rooms.get(roomNumber) + 1;
                        rooms.put(roomNumber, newCount);
                    }


                    System.out.println("objs=" + objs);



                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }





        }
    }


    class ServerSender implements Runnable {

        private final String userName;
        private final String roomNumber;
        private final Socket server;

        public ServerSender(String userName, String roomNumber, Socket server) {
            this.userName = userName;
            this.roomNumber = roomNumber;
            this.server = server;
        }

        public void run() {
            /* 1. Check each in objs and find not sended for this userName
             * 2. Send msg to user if needed
             * 3. Add user to list of users thar already received msg
             * 4. Check if a msg was received to everyone. If it is delete msg from objs
             */

            while (true) {

                try {
                    ChatObj objToDelete = null;

                    for (ChatObj obj : objs) {
                        // 1.1 get users
                        List<String> sendedUsers = obj.getUsers();
                        // 1.2 check if not exists current user
                        if (sendedUsers.isEmpty() || !sendedUsers.contains(userName)) {
                            // 2 send msg
                            OutputStream sout = server.getOutputStream();
                            DataOutputStream out = new DataOutputStream(sout);
                            out.writeUTF("msg from " + obj.getUserName() + ": " + obj.getMsg());
                            out.flush();
                            // 3 add user to list
                            sendedUsers.add(userName);
                        } else {
                            // 4 check if this object must be deleted
                            if (rooms.get(roomNumber) > 1 && sendedUsers.size() == rooms.get(roomNumber)) {
                                objToDelete = obj;
                                break;
                            }
                        }

                    }

                    if (objToDelete != null) {
                        objs.remove(objToDelete);
                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                    continue;
                }

            }
        }
    }


    class ChatObj {
        private final String msg;
        private final String userName;
        private final String roomNumber;
        private final int msgCnt;
        private final CopyOnWriteArrayList<String> users;

        public ChatObj(String msg, String userName, String roomNumber, int msgCnt) {
            this.msg = msg;
            this.userName = userName;
            this.roomNumber = roomNumber;
            this.msgCnt = msgCnt;
            this.users = new CopyOnWriteArrayList<>();
        }

        public String getMsg() {
            return msg;
        }

        public String getUserName() {
            return userName;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public int getMsgCnt() {
            return msgCnt;
        }

        public CopyOnWriteArrayList<String> getUsers() {
            return users;
        }
    }
}
