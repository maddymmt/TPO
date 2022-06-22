import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class LanguageServer {

    private static String language;
    private static Map<String, String> dictionary;
    private static int port;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Socket socket;
    private static ServerSocket serverSocket;

    public LanguageServer(String language, int port) {
        LanguageServer.port = port;
        LanguageServer.language = language;
        try {
            dictionary = Files.lines(new File("src/zad1/Dictionaries/" + language + ".txt").toPath(), StandardCharsets.UTF_8)
                    .collect(Collectors.toMap(
                            line -> line.split(",")[0],
                            line -> line.split(",")[1]
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        main(null); // start server
    }

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(language+ " server started");
            while (true) {
                socket = serverSocket.accept();
                ClientHandler clientSock = new ClientHandler(socket);
                new Thread(clientSock).start();

//                new Thread(() -> {
//                    try {
//                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    out = new PrintWriter(socket.getOutputStream(), true);
//                    System.out.println("Client connected to " + language + " server");
//                    String line;
//                    while ((line = in.readLine()) != null) {
//                        String[] words = line.split(",");
//                        System.out.println(Arrays.toString(words));
//                        Socket languageSocket = new Socket(words[1], Integer.parseInt(words[2]));
//                        PrintWriter languageOut = new PrintWriter(languageSocket.getOutputStream(), true);
//                        languageOut.println(dictionary.getOrDefault(words[0].toLowerCase(), "Not found"));
//                        languageOut.close();
//                    }} catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    out.close();
                    in.close();
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
            BufferedReader in = null;
            PrintWriter out = null;
            PrintWriter languageOut = null;

            try{
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                out = new PrintWriter(clientSocket.getOutputStream(), true);
                System.out.println("Client connected to " + language + " server");
                String line;
                while ((line = in.readLine()) != null) {
                    String[] words = line.split(",");
                    Socket languageSocket = new Socket(words[1], Integer.parseInt(words[2]));
                    languageOut = new PrintWriter(languageSocket.getOutputStream(), true);
                    languageOut.println(dictionary.getOrDefault(words[0].toLowerCase(), "Not found"));
                    languageOut.close();
                    languageSocket.close();
                    System.out.println(language + "server client disconnected");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
//                    if (out != null) {
////                        languageOut.close();
//                        System.out.println(language + "server client disconnected");
//                        out.close();
//                    }
                    if (in != null) {
                        System.out.println(language + "server client disconnected");
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


//Server otrzymal zapytanie od gui
//Lang server otrzymal zapytanie od server
//Gui otrzymal zapytanie od lang server
//
//Server otrzymal zapytanie od gui
//Lang server otrzymal zapytanie od server
