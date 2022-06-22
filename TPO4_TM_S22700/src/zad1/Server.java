package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;


public class Server {

    private HashMap<String, List<String>> tematy;
    private HashMap<SocketChannel, List<String>> clients;

    public static void main(String[] args) throws IOException {
        new Server();
    }

    Server() throws IOException {
        String host = "localhost";
        int port = 8888;
        tematy = new HashMap<>();
        clients = new HashMap<>();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(host, port));
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Serwer: czekam ... ");

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) { // połaczenie klienta gotowe do akceptacji
                    System.out.println("Serwer: ktoś się połączył ..., akceptuję go ... ");
                    SocketChannel cc = serverChannel.accept();
                    cc.configureBlocking(false);
                    cc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    clients.put(cc, new ArrayList<>());
                    continue;
                }
                if (key.isReadable()) {  // któryś z kanałów gotowy do czytania
                    SocketChannel cc = (SocketChannel) key.channel();
                    serviceRequest(cc);
                    continue;
                }
                if (key.isWritable()) {  // któryś z kanałów gotowy do pisania
                    continue;
                }

            }
        }

    }


    private static Charset charset = Charset.forName("ISO-8859-2");
    private static final int BSIZE = 1024;
    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);
    private StringBuffer reqString = new StringBuffer();


    private void serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return; // jeżeli kanał zamknięty

        System.out.print("Serwer: czytam komunikat od klienta ... ");
        reqString.setLength(0);
        bbuf.clear();

        try {
            int n = sc.read(bbuf);   // nie natrafimy na koniec wiersza
            if (n > 0) {
                bbuf.flip();
                CharBuffer cbuf = charset.decode(bbuf);
                while (cbuf.hasRemaining()) {
                    char c = cbuf.get();
                    reqString.append(c);
                }
            }

            ArrayList<String> requestContent = new ArrayList<>(Arrays.asList(reqString.toString().split(",")));
            String cmd = requestContent.get(0);

            String topicName;
            String information;
            String[] topicsArray;

            switch (cmd) {
                case "deleteTopic":
                    topicName = requestContent.get(1);
                    information = requestContent.get(2);
                    tematy.remove(topicName);
                    sendMessage(sc, "deleteTopic," + topicName + "," + information);
                case "addTopic":
                    topicName = requestContent.get(1);
                    information = requestContent.get(2);
                    tematy.put(topicName, new LinkedList<>());
                    sendMessage(sc, "addTopic," + topicName + "," + information);
                case "addTopics":
                    topicsArray = requestContent.get(1).split(";");
                    information = requestContent.get(2);
//                    StringBuilder sb = new StringBuilder();
                    for (String s : topicsArray) {
                        tematy.put(s, new LinkedList<>());
//                        sb.append(s).append(",");
                    }
//                    information += sb.toString();
                    sendMessage(sc, "addTopics," + requestContent.get(1) + "," + information);
                case "sendPost":
                    topicName = requestContent.get(1);
                    String post = requestContent.get(2);
                    tematy.get(topicName).add(post);
                    for (List<String> list : clients.values()) {
                        if(list.contains(topicName)) {
                            sendMessage(sc, "sendPost," + topicName + "," + post);
                        }
                    }
                    //od klienta
                case "dostepneTematy":
                    String dostepneTematy = tematy.keySet().toString();
                    sendMessage(sc, dostepneTematy);
                case "wybraneTematy":
                    topicsArray = requestContent.get(1).split(" ");
                    clients.replace(sc, Arrays.stream(topicsArray).toList());
                    sendMessage(sc, "empty");

            }
        } catch (Exception exc) { // przerwane polączenie?
            exc.printStackTrace();
            try {
                sc.close();
                sc.socket().close();
            } catch (Exception e) {
            }
        }
    }

    private void sendMessage(SocketChannel client, String message) {
        try {
            client.write(charset.encode(CharBuffer.wrap(message)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
