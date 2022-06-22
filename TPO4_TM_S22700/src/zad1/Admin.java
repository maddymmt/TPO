package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class Admin extends JFrame {
    private JButton dodajTematButton;
    private JButton zatwierdzWyslijButton;
    private JButton usunTematButton;
    private JButton poinformujButton;
    private JPanel tematPanel;
    private HashMap<String, Temat> tematy;
    private GridBagConstraints gbc;
    private GridBagConstraints gbcTematy;
    private ClientAdmin client;
    private java.util.List<String> tematyNiewyslane;
    private SocketChannel socketChannel;

    public Admin() throws IOException {
        super("TPO4_TM_S22700");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 900));
        setLayout(new GridBagLayout());
        setResizable(false);
        this.client = new ClientAdmin("localhost", 8888);

        dodajTematButton = new JButton("Dodaj nowy temat");
        usunTematButton = new JButton("Usuń temat");
        poinformujButton = new JButton("Poinformuj o zmianach tematów");
        zatwierdzWyslijButton = new JButton("Zatwierdź i wyślij posty");

        tematy = new HashMap<>();

        tematPanel = new JPanel();
        tematPanel.setLayout(new GridBagLayout());
        gbcTematy = new GridBagConstraints();
        gbcTematy.gridx = 0;
        gbcTematy.gridy = 0;
        gbcTematy.insets = new Insets(5, 10, 5, 10);
        gbcTematy.fill = GridBagConstraints.HORIZONTAL;

        dodajTematButton.addActionListener(e -> dodajTemat());
        usunTematButton.addActionListener(e -> usunTemat());
        poinformujButton.addActionListener(e -> poinformujTematy());
        zatwierdzWyslijButton.addActionListener(e -> zatwierdzWyslij());

        JScrollPane scroll = new JScrollPane(tematPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(760, 710));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(dodajTematButton, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 5, 10);
        add(usunTematButton, gbc);
        gbc.gridy++;
        add(poinformujButton, gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        add(scroll, gbc);
        gbc.weighty = 0;

        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(zatwierdzWyslijButton, gbc);

        tematyNiewyslane = new ArrayList<>();

        pack();
        setVisible(true);
    }

    public void dodajTemat() {
        String name = JOptionPane.showInputDialog("Podaj nazwę tematu", null);
        if ((name != null) && (name.length() > 0)) {
            if(tematy.containsKey(name)) {
                JOptionPane.showMessageDialog(null, "Temat o takiej nazwie już istnieje");
                return;
            }
            Temat temat = new Temat(name);
            tematy.put(name, temat);
            tematPanel.add(temat, gbcTematy);
            tematyNiewyslane.add(name);
            gbcTematy.gridy++;
            revalidate();
        }
    }

    public void usunTemat() {
        String name = JOptionPane.showInputDialog("Podaj nazwę tematu do usunięcia", null);
        if ((name != null) && (name.length() > 0)) {
            tematPanel.remove(tematy.get(name));
            tematy.remove(name);
            tematyNiewyslane.remove(name);
            revalidate();

            if (!tematyNiewyslane.contains(name)) {
                client.sendMessage("deleteTopic,"+name+",Usunieto temat " + name);
            }
        }
    }

    public void poinformujTematy() {
        if (tematyNiewyslane.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : tematyNiewyslane) {
                sb.append(s).append(" ");
            }
            client.sendMessage("addTopics,"+sb+",Dodano nowe tematy: ");
        } else if (tematyNiewyslane.size() == 1) {
            client.sendMessage("addTopic,"+tematyNiewyslane.get(0)+",Dodano nowy temat " + tematyNiewyslane.get(0));
        } else {
            JOptionPane.showMessageDialog(null, "Brak tematów do wysłania");
        }
        tematyNiewyslane = new ArrayList<>();
    }

    public void zatwierdzWyslij() {
        for (Temat temat : tematy.values()) {
            for (JPanel p : temat.getPosty().keySet()) {
                JButton zatwierdzButton1 = (JButton) p.getComponent(0);
                if (zatwierdzButton1.getText().equals("Zatwierdź")) {
                    JOptionPane.showMessageDialog(null, "Nie wszystkie tematy zostały zatwierdzone");
                    return;
                }
            }
        }

        for (Temat temat : tematy.values()) {
            temat.getPosty().forEach((post, czyWyslany) -> {
                if (!czyWyslany) {
                    JTextArea textArea = (JTextArea) post.getComponent(0);
                    client.sendMessage("sendPost,"+temat.getNazwaLabel().getText()+","+textArea.getText()+",Wysłano post " + textArea.getText());
                    temat.setPosty(post, true);
                }
            });
        }
    }
}
