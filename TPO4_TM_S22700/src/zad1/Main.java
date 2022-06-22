package zad1;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        SwingUtilities.invokeLater(() -> {
            try {
                new Admin();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        SwingUtilities.invokeLater(() -> {
            try {
                new KlientGUI();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}