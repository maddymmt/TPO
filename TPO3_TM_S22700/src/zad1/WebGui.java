import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WebGui extends JFrame {
    private JTextField languageCode;
    private JTextField wordField;
    private JTextArea translationArea;
    private JButton translateButton;
    private JLabel languageLabel;
    private JLabel wordLabel;
    private JPanel mainPanel;
    private JLabel messageLabel;
    private static Socket client;
    private static ServerSocket serverSocketClient;
    private static String message;

    public WebGui() {
        super("TPO3_TM_S22700");
        setSize(600, 280);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        languageCode.setText("en");
        wordField.setText("myszka");
        translateButton.addActionListener(e -> getTranslationFromServer());


        new Thread(() -> {
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

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                if (serverSocketClient != null) {
                    try {
                        serverSocketClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void getTranslationFromServer() {
        if (wordField.getText().length() == 0) {
            translationArea.setText("Podaj słowo do przetłumaczenia");
            return;
        }
        if (languageCode.getText().length() == 0) {
            translationArea.setText("Podaj kod języka docelowego");
            return;
        }
        if (wordField.getText().length() > 0 && languageCode.getText().length() > 0) {
            Socket socketServer = null;
            PrintWriter out = null; //Obiekt do wystawiania danych w strone serwera
            BufferedReader in = null;   //Obiekt do pobierania danych od serwera
            try {
                socketServer = new Socket(); //server
                socketServer.connect(new InetSocketAddress("127.0.0.1", 11111), 500); //Socket TCP na konkretny adres i port
                in = new BufferedReader(new InputStreamReader(socketServer.getInputStream())); //Od serwera
                out = new PrintWriter(socketServer.getOutputStream(), true);  //W stronę serwera
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                System.out.println((wordField.getText() + "," + languageCode.getText() + "," + serverSocketClient.getLocalPort()));
                out.println(wordField.getText() + "," + languageCode.getText() + "," + serverSocketClient.getLocalPort());
                message = in.readLine();
                System.out.println(message);
                messageLabel.setText(message);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        System.out.println("Client main server disconnected");
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        socketServer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WebGui::new);
    }


    private class ClientHandler implements Runnable {
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
                    WebGui.this.translationArea.setText(message);
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
}