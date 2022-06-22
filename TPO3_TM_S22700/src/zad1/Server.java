import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class Server {

    private static Map<String, Integer> map = new HashMap<>();
    private static ServerSocket serverSocket;
    private static Socket socket;

    public static void main(String[] args) {
        map.put("en", 1111);
        map.put("pl", 12122);
        map.put("de", 12222);
        map.put("fr", 11221);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            new Thread(()-> {
                LanguageServer languageServer = new LanguageServer(entry.getKey(), entry.getValue());
            }).start();
        }

        try {
            serverSocket = new ServerSocket(11111);
            System.out.println("Main server started");
            serverSocket.setReuseAddress(true);
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Main server new client connected " + socket.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(socket);
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serverSocket != null) {
                try {
                    socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;


        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            BufferedReader inClient = null;
            PrintWriter outClient = null;
            try {
                inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outClient = new PrintWriter(clientSocket.getOutputStream(), true);
                PrintWriter outLan = null;
                String line;
                while ((line = inClient.readLine()) != null) {
                    String[] respParts = line.split(",");
                    Socket socketLan = new Socket();
                    if (map.containsKey(respParts[1].toLowerCase())) {
                        socketLan.connect(new InetSocketAddress("localhost", map.get(respParts[1].toLowerCase())), 500);
                        outLan = new PrintWriter(socketLan.getOutputStream(), true);
                        outLan.println(respParts[0] + "," + clientSocket.getInetAddress().getHostAddress() + "," + respParts[2]);
                        outClient.println("OK");
                        outLan.close();
                    } else {
                        outClient.println("Language not supported");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outClient != null) {
                        System.out.println("Main server client disconnected");
                        outClient.close();
                    }
                    if (inClient != null) {
                        inClient.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}