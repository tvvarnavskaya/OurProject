package com.tasl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tvarnavskaya on 26.08.2016.
 */

class SampleServer extends Thread {
    Socket s;
    int num;

    public static void main(String args[]) {
        try {
            int i = 0; // счётчик подключений

            // привинтить сокет на локалхост, порт 3128
            ServerSocket server = new ServerSocket(3128, 0, InetAddress.getByName("localhost"));

            System.out.println("server is started");
            System.out.println("Waiting for a client...");

//            CopyOnWriteArrayList<Socket> socketsForRoom = new CopyOnWriteArrayList<>();
            // комната и список сокетов клиентов
            ConcurrentHashMap<Integer, CopyOnWriteArrayList> sockets = new ConcurrentHashMap<>();

            // комната и очередь с сообщениями
            ConcurrentHashMap<Integer, LinkedBlockingQueue> queues = new ConcurrentHashMap<>();
            LinkedBlockingQueue<MyObject> queue = new LinkedBlockingQueue<>();

            String line = null;
            while(true) {
                Socket socket = server.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
                // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
                InputStream sin = socket.getInputStream();


                // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                DataInputStream in = new DataInputStream(sin);

                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                System.out.println("Got from client : " + line);
                String[] clientArgs = line.split(":");
                int roomNumber = Integer.parseInt(clientArgs[2]);
                MyObject myObject = new MyObject(clientArgs[0], clientArgs[1], roomNumber);
                if (sockets.contains(roomNumber)) {
                    CopyOnWriteArrayList<Socket> socketsForRoom = sockets.get(roomNumber);
                    if (!socketsForRoom.contains(socket)) {
                        socketsForRoom.add(socket);
                    }
                } else {
                    CopyOnWriteArrayList<Socket> socketsForRoom = new CopyOnWriteArrayList<>();
                    socketsForRoom.add(socket);
                    sockets.put(roomNumber, socketsForRoom);
                }
                queue.offer(myObject);
//                if (queues.contains(roomNumber)) {
//                    LinkedBlockingQueue<MyObject> queue = new LinkedBlockingQueue();
//                    queue.add(myObject);
//                } else {
//                    LinkedBlockingQueue<MyObject> queue = queues.get(roomNumber);
//                    queue.add(myObject);
//                }
                if (!queue.isEmpty()) {
                    MyObject obj = queue.poll();
                    int room = obj.getRoom();
                    for (Object o : sockets.get(room)) {
                        Socket s = (Socket) o;
                        OutputStream sout = s.getOutputStream();
                        DataOutputStream out = new DataOutputStream(sout);
                        out.writeUTF(obj.getMsg()); // отсылаем клиентам этой комнаты текст, но не тому, кто прислал.
                        out.flush(); // заставляем поток закончить передачу данных.
                    }

                }

                System.out.println("Waiting for a client...");
                System.out.println();
            }
        }
        catch(Exception e) {
            System.out.println("init error: "+e);
        } // вывод исключений
    }

    static class MyObject {
        String msg;
        String clientName;
        int room;

        public MyObject(String msg, String clientName, int room) {
            this.msg = msg;
            this.clientName = clientName;
            this.room = room;
        }

        public String getMsg() {
            return msg;
        }

        public String getClientName() {
            return clientName;
        }

        public int getRoom() {
            return room;
        }

    }

    public SampleServer(int num, Socket s) {
        // копируем данные
        this.num = num;
        this.s = s;

        // и запускаем новый вычислительный поток (см. ф-ю run())
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run() {
        try {
            // из сокета клиента берём поток входящих данных
            InputStream is = s.getInputStream();
            // и оттуда же - поток данных от сервера к клиенту
            OutputStream os = s.getOutputStream();

            // буффер данных в 64 килобайта
            byte buf[] = new byte[64*1024];
            // читаем 64кб от клиента, результат - кол-во реально принятых данных
            int r = is.read(buf);

            // создаём строку, содержащую полученную от клиента информацию
            String data = new String(buf, 0, r);

            // добавляем данные об адресе сокета:
            data = ""+num+": "+"\n"+data;

            // выводим данные:
            os.write(data.getBytes());

            // завершаем соединение
            s.close();
        }
        catch(Exception e) {
            System.out.println("init error: "+e);
        } // вывод исключений
    }
}