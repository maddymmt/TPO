package zad1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Window extends JFrame {

    private Gson gson;
    private Service service;
    private String weatherJson;
    private Double rate1;
    private Double rate2;
    private String currencyGiven;
    private JsonObject jsonWeather;
    private WebView webView;
    private JFXPanel jfxPanel;

    public Window(Service s, String weatherJson, Double rate1, Double rate2) {
        super("TPO2_TM_S22700");
        JFrame.setDefaultLookAndFeelDecorated(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        setResizable(true);

        this.service = s;
        this.weatherJson = weatherJson;
        this.rate1 = rate1;
        this.rate2 = rate2;
        currencyGiven = "USD";

        getContentPane().add(upperPanel(), BorderLayout.NORTH);
        getContentPane().add(lowerPanel(), BorderLayout.CENTER);
        setVisible(true);
        pack();
        setSize(screenSize.width,screenSize.height);
    }


    JPanel upperPanel() {
        gson = new Gson();
        jsonWeather = gson.fromJson(weatherJson, JsonObject.class);

        JPanel upperPanel = new JPanel();
        JTextArea textWeather = new JTextArea();
        JTextArea textCurrencyRate = new JTextArea();
        JTextArea textCurrencyPln = new JTextArea();
        JPanel inputPanel = new JPanel();
        CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 10, 1, 1, new Color(51, 0, 102)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        upperPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        upperPanel.setLayout(new GridLayout(1, 4, 10, 0));
        upperPanel.setBackground(new Color(204, 229, 255));

        textWeather.setBorder(border);
        textWeather.setMargin(new Insets(5, 5, 5, 5));
        textWeather.setLineWrap(true);
        textWeather.setEditable(false);
        textWeather.setText(weatherInformation(jsonWeather));

        textCurrencyRate.setBorder(border);
        textCurrencyRate.setLineWrap(true);
        textCurrencyRate.setEditable(false);
        textCurrencyRate.setText(currencyRateBaseLoc(service.getCountryCurrency(), currencyGiven, rate1));

        textCurrencyPln.setBorder(border);
        textCurrencyPln.setLineWrap(true);
        textCurrencyPln.setEditable(false);
        textCurrencyPln.setText(currencyPlnLoc(rate2));

        inputPanel.setBorder(border);
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel labelCountry = new JLabel("Country:");
        JTextField inputCountry = new JTextField(service.getLocation());
        JLabel labelCity = new JLabel("City:");
        JTextField inputCity = new JTextField(jsonWeather.get("name").getAsString());
        JLabel labelCurrency = new JLabel("Currency Code:");
        JTextField inputCurrency = new JTextField("USD");
        JButton button = new JButton("OK");
        button.addActionListener(e -> {
            service = new Service(inputCountry.getText());
            weatherJson = service.getWeather(inputCity.getText());
            rate1 = service.getRateFor(inputCurrency.getText());
            rate2 = service.getNBPRate();

            textWeather.setText(weatherInformation(gson.fromJson(weatherJson, JsonObject.class)));
            textCurrencyRate.setText(currencyRateBaseLoc(service.getCountryCurrency(), inputCurrency.getText(), rate1));
            textCurrencyPln.setText(currencyPlnLoc(rate2));

            Platform.runLater(() -> reloadPage(inputCity.getText()));
        });

        inputPanel.add(labelCountry);
        inputPanel.add(inputCountry);
        inputPanel.add(labelCity);
        inputPanel.add(inputCity);
        inputPanel.add(labelCurrency);
        inputPanel.add(inputCurrency);
        inputPanel.add(button);

        upperPanel.add(textWeather);
        upperPanel.add(textCurrencyRate);
        upperPanel.add(textCurrencyPln);
        upperPanel.add(inputPanel);

        return upperPanel;
    }

    String weatherInformation(JsonObject jsonWeather) {
        return "Location: " + jsonWeather.get("name").getAsString() +
                "\nSky: " + jsonWeather.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString() +
                "\nTemperature: " + jsonWeather.getAsJsonObject("main").get("temp") +
                "\nPressure: " + jsonWeather.getAsJsonObject("main").get("pressure") +
                "\nHumidity: " + jsonWeather.getAsJsonObject("main").get("humidity") +
                "\nWind: " + jsonWeather.getAsJsonObject("wind").get("speed");
    }

    String currencyRateBaseLoc(String currencyLocation, String currencyBase, Double rate1) {
        return "\" The " + currencyLocation + " exchange rate agains the " + currencyBase + "\" is: " +
                "\n" + rate1 + ", if 1 " + currencyLocation + " = " + rate1 + " " + currencyBase +
                "\n(1 " + currencyBase + " = " + 1 / rate1 + " " + currencyLocation + ")";
    }

    String currencyPlnLoc(Double rate) {
        return "The PLN exchange rate against the currency of the given country is: \n" + rate;
    }

    JFXPanel lowerPanel() {
//        JPanel lowerPanel = new JPanel();
        jfxPanel = new JFXPanel();
        Platform.runLater(this::createJfxContent);
//        lowerPanel.add(jfxPanel);
        return jfxPanel;
    }

    void createJfxContent() {
        Group root  =  new  Group();
        webView = new WebView();
        webView.getEngine().load("https://wikipedia.org/wiki/" + jsonWeather.get("name").getAsString());
        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);
    }

    void reloadPage(String city) {
        webView.getEngine().load("https://wikipedia.org/wiki/" + city);
    }
}
