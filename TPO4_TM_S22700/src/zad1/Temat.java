package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Temat extends JPanel {


    private JLabel nazwaLabel;
    private JButton utworzPostButton;
    private GridBagConstraints gbc;
    private HashMap<JPanel, Boolean> posty;

    public Temat(String nazwa) {

        setLayout(new GridBagLayout());
        setVisible(true);
        nazwaLabel = new JLabel(nazwa);
        utworzPostButton = new JButton("Utwórz post");
        posty = new HashMap<>();

        nazwaLabel.setText(nazwa);
        nazwaLabel.setPreferredSize(new Dimension(740, 20));
        utworzPostButton.addActionListener(e -> utworzPost());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(nazwaLabel, gbc);
        gbc.gridy++;
        add(utworzPostButton, gbc);
    }

    public void utworzPost() {
        for (JPanel p : posty.keySet()) {
            JButton zatwierdzButton = (JButton) p.getComponent(0);
            if (zatwierdzButton.getText().equals("Zatwierdź")) {
                JOptionPane.showMessageDialog(null, "Post oczekuje na zatwierdzenie");
                return;
            }
        }
        JPanel post = post();
        ++gbc.gridy;
        add(post, gbc);
        posty.put(post, false);
//        utworzPostButton.setEnabled(false);
        revalidate();
    }

    public void usunPost(JPanel post) {
        remove(post);
        posty.remove(post);
        revalidate();
    }

    public JPanel post() {
        JPanel post = new JPanel();
        JTextArea textArea;
        JButton zatwierdzButton;
        JButton usunButton;

        post.setLayout(new GridBagLayout());
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setSize(new Dimension(740, 200));
        zatwierdzButton = new JButton("Zatwierdź");
        usunButton = new JButton("Usuń");
        textArea.setBackground(new Color(255, 207, 210));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        zatwierdzButton.addActionListener(e -> {
            if (zatwierdzButton.getText().equals("Zatwierdź")) {
                zatwierdzButton.setText("Edytuj");
                textArea.setEditable(false);
                textArea.setBackground(new Color(152, 245, 225));
            } else {
                for (JPanel p : posty.keySet()) {
                    JButton zatwierdzButton1 = (JButton) p.getComponent(0);
                    if (zatwierdzButton1.getText().equals("Zatwierdź")) {
                        JOptionPane.showMessageDialog(null, "Post oczekuje na zatwierdzenie");
                        return;
                    }
                }
                zatwierdzButton.setText("Zatwierdź");
                textArea.setEditable(true);
                textArea.setBackground(new Color(255, 207, 210));
            }
        });
        usunButton.addActionListener(e -> usunPost(post));

        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        post.add(zatwierdzButton, gbc);
        gbc.weightx = 0.5;
        gbc.gridx = 1;
        gbc.gridy = 1;
        post.add(usunButton, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 200;
        post.add(textArea, gbc);

        return post;
    }


    public HashMap<JPanel, Boolean> getPosty() {
        return posty;
    }

    public void setPosty(JPanel post, Boolean czyWyslany) {
        posty.put(post, czyWyslany);
    }
    public JLabel getNazwaLabel() {
        return nazwaLabel;
    }

}
