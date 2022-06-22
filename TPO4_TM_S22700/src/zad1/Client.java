package zad1;

import com.sun.source.tree.Scope;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class Client {

    SocketChannel socketChannel;
    InetSocketAddress serverAddress;
    Selector selector;
    Charset charset;
    ByteBuffer buffer;
    CharBuffer charBuffer;
    static String response;
    boolean isConnected;
    KlientGUI gui;

    public Client(String host, int port, KlientGUI gui) throws UnknownHostException, IOException {
        serverAddress = new InetSocketAddress(host, port);
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        charset = StandardCharsets.UTF_8;
        buffer = ByteBuffer.allocate(1024);
        charBuffer = CharBuffer.allocate(1024);
        isConnected = false;
        this.gui = gui;
        reconnect();
    }

//        ArrayList<String> requestContent = new ArrayList<>(Arrays.asList(response.split(",")));
//        String cmd = requestContent.get(0);
//        String topicName;
//        String information;
//        switch (cmd) {
//            case "deleteTopic":
//                topicName = requestContent.get(1);
//                information = requestContent.get(2);
//                gui.setMessage(information);
//                gui.usunCheckbox(topicName);
//                break;
//            case "addTopic":
//                topicName = requestContent.get(1);
//                information = requestContent.get(2);
//                gui.setMessage(information);
//                gui.dodajCheckbox(topicName);
//                break;
//            case "addTopics":
//                List<String> topics = Arrays.asList(requestContent.get(1).split(";"));
//                information = requestContent.get(2);
//                gui.setMessage(information + String.join(", ", topics));
//                for (String topic : topics) {
//                    gui.dodajCheckbox(topic);
//                }
//                break;
//            case "sendPost":
//                topicName = requestContent.get(1);
//                String post = requestContent.get(2);
//                gui.dodajPost(topicName, post);
//                break;


//    public Client() {
//        charset = StandardCharsets.UTF_8;
//        buffer = ByteBuffer.allocate(1024);
//        charBuffer = charset.decode(buffer);
//        isConnected = false;
//        connect("localhost", 8888);
//    }
//

    public void getMessage() {
        String message = "";

        if (!socketChannel.isConnected()) {
            System.out.println("Client disconnected");
        }
        while (true) {
            try {
                buffer.clear();
                int read = socketChannel.read(buffer);
                if (read == 0)
                    continue;
                else if (read == -1)
                    break;
                else {
                    buffer.flip();
                    charBuffer = charset.decode(buffer);
                    message = charBuffer.toString();
                    System.out.println("Client received: " + message);
                    charBuffer.clear();


                    ArrayList<String> requestContent = new ArrayList<>(Arrays.asList(response.split(",")));
                    String cmd = requestContent.get(0);
                    String topicName;
                    String information;

                    switch (cmd) {
                        case "deleteTopic" -> {
                            topicName = requestContent.get(1);
                            information = requestContent.get(2);
                            gui.setMessage(information);
                            gui.usunCheckbox(topicName);
                        }
                        case "addTopic" -> {
                            topicName = requestContent.get(1);
                            information = requestContent.get(2);
                            gui.setMessage(information);
                            gui.dodajCheckbox(topicName);
                        }
                        case "addTopics" -> {
                            List<String> topics = Arrays.asList(requestContent.get(1).split(";"));
                            information = requestContent.get(2);
                            gui.setMessage(information + String.join(", ", topics));
                            for (String topic : topics) {
                                gui.dodajCheckbox(topic);
                            }
                        }
                        case "sendPost" -> {
                            topicName = requestContent.get(1);
                            String post = requestContent.get(2);
                            gui.dodajPost(topicName, post);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Message receiving failed");
            }
        }
    }

    public void sendMessage(String message) {
        System.out.println("Client sending: " + message);
        ByteBuffer msg = charset.encode(CharBuffer.wrap(message));
        while (true) {
            try {
                socketChannel.write(msg);
                getMessage();
                return;
            } catch (Exception e) {
//                connect("localhost", 8888);
                System.out.println("Client disconnected");
            }
        }
    }

    public String getResponse() {
        return response;
    }

    private void reconnect() {
        while(true) {
            if(socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (Exception ignored) {}
            }

            try {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress("localhost", 8888));
                while (!socketChannel.finishConnect()) { }
                System.out.println("Client connected");
                return;
            } catch (Exception ignored) {}
        }
    }
}