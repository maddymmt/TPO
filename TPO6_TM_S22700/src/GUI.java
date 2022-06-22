import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static JTextField textField;
    private static JTextArea textArea;
    private static JButton btnSend;
    private static JButton btnConnect;
    private static String username;
    private Client client;

    public GUI() {
        setTitle("Chat");
        setSize(520, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        textField = new JTextField();
        textField.setBounds(10, 450, 390, 20);
        add(textField);

        btnSend = new JButton("Send");
        btnSend.setBounds(410, 450, 80, 20);
        btnSend.setEnabled(false);
        add(btnSend);

        textArea = new JTextArea();
        textArea.setBounds(10, 40, 480, 400);
        textArea.setEditable(false);
        add(textArea);

        btnConnect = new JButton("Connect");
        btnConnect.setBounds(10, 10, 120, 20);
        add(btnConnect);

        btnSend.addActionListener(e -> {
            if (textField.getText().length() > 0) {
                client.sendMessage(textField.getText());
                textField.setText("");
            }
        });

        btnConnect.addActionListener(e -> {
            if (username == null) {
                username = JOptionPane.showInputDialog("Enter your username");
                if (!username.equals("")) {
                    client = new Client(username, this);
                    btnSend.setEnabled(true);
                    btnConnect.setEnabled(false);
                    client.sendMessage("1");
                }
            } else {
                client.initialize();
                btnSend.setEnabled(true);
                btnConnect.setEnabled(false);
                client.sendMessage("2");
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setVisible(true);
    }

    public void getMessage(String message) {
        textArea.append(message + "\n");
    }
    public void setButtonEnabled(boolean enabled) {
        btnConnect.setEnabled(enabled);
    }
    public void setButtonSendEnabled(boolean enabled) {
        btnSend.setEnabled(enabled);
    }

    public static void main(String[] args) {
        new GUI();
    }


}
