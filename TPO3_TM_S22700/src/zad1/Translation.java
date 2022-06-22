import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Translation {

    private static Socket client;
    public static ServerSocket serverSocketClient;
    public static String message;

    public Translation(){
        main(null);
    }

    public static void main(String[] args) {
        serverSocketClient = null;
        try {
            serverSocketClient = new ServerSocket(2222);
            serverSocketClient.setReuseAddress(true);

            while (true) {
                client = serverSocketClient.accept();
                System.out.println("New client connected to WebGui server " + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (serverSocketClient != null) {
                try {
                    serverSocketClient.close();
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
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true); //nic nie wysylamy do clienta
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Wiadomosc od klienta: " + line);
                    message = line;
                    out.println(line);
//                    Parent.this.translationArea.setText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        System.out.println("The client exited");
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getMessage(){
        return message;
    }

}
