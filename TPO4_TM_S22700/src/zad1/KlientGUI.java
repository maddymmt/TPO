package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KlientGUI extends JFrame {

    private JScrollPane scrollPane;
    private GridBagConstraints gbc;
    private GridBagConstraints gbcLista;
    private GridBagConstraints gbcTematy;
    private GridBagConstraints gbcSub;
    private JLabel subskrybujLabel;
    private JButton button;
    private JPanel tematPanel;
    private JPanel subskrybujPanel;
    private List<String> tematyWybrane;
    private Client client;
    private String message;


    public KlientGUI() throws IOException {
        super("TPO4_TM_S22700");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 900));
        setLayout(new GridBagLayout());
        setResizable(false);
        client = new Client("localhost", 8888, this);

        subskrybujPanel = new JPanel();
        subskrybujLabel = new JLabel("Subskrybuj");
        button = new JButton("Zatwierdz");
        tematPanel = new JPanel();
        scrollPane = new JScrollPane(tematPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(700, 710));
        subskrybujPanel.setPreferredSize(new Dimension(700, 710));
        tematPanel.setLayout(new GridBagLayout());
        subskrybujPanel.setLayout(new GridBagLayout());

        gbcLista = new GridBagConstraints();
        gbcLista.insets = new Insets(5, 10, 5, 10);
        gbcLista.gridx = 0;
        gbcLista.gridy = 0;
        gbcLista.weighty = 1;
        gbcLista.weightx = 1;
        gbcLista.fill = GridBagConstraints.HORIZONTAL;

        gbcTematy = new GridBagConstraints();
        gbcTematy.gridx = 0;
        gbcTematy.gridy = 0;
        gbcTematy.weightx = 1;
        gbcTematy.insets = new Insets(5, 10, 5, 10);
        gbcTematy.fill = GridBagConstraints.HORIZONTAL;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        button.addActionListener(e -> wyslijTematy());

        add(subskrybujLabel, gbc);
        gbc.gridy++;

        dodajDostepneTematy();
        add(subskrybujPanel, gbc);


        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        add(button, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.weighty = 1;
        gbc.gridy++;
        add(scrollPane, gbc);

        pack();
        setVisible(true);
    }

    private void dodajDostepneTematy() {
        client.sendMessage("dostepneTematy");
        if (client.getResponse() != null) {
            String[] tematy = client.getResponse().split(",");
            for (String temat : tematy) {
                JCheckBox checkBox = new JCheckBox(temat);
                subskrybujPanel.add(checkBox, gbcLista);
                gbcLista.gridy++;
            }
            revalidate();
        } else {
            JOptionPane.showMessageDialog(null, "Nie połaczono z serwerem");
        }
    }


    private void dodajZasubskrybowaneTematy() {
        for (String temat : tematyWybrane) {
            tematPanel.add(tematSubskrypcja(temat), gbcTematy);
            gbcTematy.gridy++;
        }
        revalidate();
    }

    private void wyslijTematy() { //  wysylamy zaznaczone tematy do serwera
        tematyWybrane = new ArrayList<>();
        for (Component c : subskrybujPanel.getComponents()) {
            if (((JCheckBox) c).isSelected()) {
                tematyWybrane.add(((JCheckBox) c).getText());
            } else {
                tematyWybrane.remove(((JCheckBox) c).getText());
            }
        }
        dodajZasubskrybowaneTematy();
        client.sendMessage("wybraneTematy," + String.join(";", tematyWybrane));
    }

    private JPanel tematSubskrypcja(String topic) {
        JPanel panel = new JPanel();
        JLabel nazwaLabel = new JLabel(topic);

        panel.setLayout(new GridBagLayout());
        panel.setVisible(true);

        gbcSub = new GridBagConstraints();
        gbcSub.gridx = 0;
        gbcSub.gridy = 0;
        gbcSub.weightx = 1;
        gbcSub.weighty = 1;
        gbcSub.fill = GridBagConstraints.HORIZONTAL;

        panel.add(nazwaLabel, gbcSub);
        gbcSub.gridy++;

        return panel;
    }

    public void dodajPost(String topic, String tresc) {
        Component[] components = tematPanel.getComponents();

        JPanel panel = new JPanel();
        JTextArea tekst = new JTextArea();
        tekst.setEditable(false);
        tekst.setSize(new Dimension(740, 200));
        tekst.setText(tresc);

        for (Component c : components) {
            if (((JPanel) c).getComponent(0).getClass().getName().equals("javax.swing.JLabel")) {
                if (((JLabel) ((JPanel) c).getComponent(0)).getText().equals(topic)) {
                    ((JPanel) c).add(panel,gbcSub);
                }
            }
        }
        gbcSub.gridy++;
        revalidate();
    }


    public void setMessage(String message) {
        this.message = message;
        JOptionPane.showMessageDialog(null, message);

    }

    public void usunCheckbox(String temat) {
        Component checkBox[] = subskrybujPanel.getComponents();
        for (Component c : checkBox) {
            if (((JCheckBox) c).getText().equals(temat)) {
                subskrybujPanel.remove(c);
            }
        }
        revalidate();
    }

    public void dodajCheckbox(String temat) {
        JCheckBox checkBox = new JCheckBox(temat);
        subskrybujPanel.add(checkBox, gbcLista);
        gbcLista.gridy++;
        revalidate();
    }

    public JPanel getTematPanel() {
        return tematPanel;
    }
}
