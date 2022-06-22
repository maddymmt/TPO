/**
 * @author Turczyn Magdalena S22700
 */

package zad1;


import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Service {

    private String location;
    private String locationCode;
    private String countryCode;
    private String locationCurrencyCode;
    private String countryCurrency;
    private Gson gson;

    public Service(String location) {
        this.location = location;
//
//        StringBuilder json = new StringBuilder();
//        gson = new Gson();
//
//        try {
//            URL url = new URL("http://country.io/names.json");
//            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
//                String line;
//                while ((line = in.readLine()) != null)
//                    json.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JsonObject obj = gson.fromJson(String.valueOf(json), JsonObject.class);
//        locationCode = obj.get("BD").getAsString();
    }

    public String getWeather(String city) {
        String ApiKey = "632171a052c6f66a7af16113bd199969";
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + ApiKey + "&units=metric");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null)
                    result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        gson = new Gson();
        countryCode = gson.fromJson(String.valueOf(result), JsonObject.class).getAsJsonObject("sys").get("country").getAsString();

        return result.toString();
    }


    public Double getRateFor(String currencyCode) {

        StringBuilder result = new StringBuilder();
        StringBuilder jsonCurrency = new StringBuilder();
        gson = new Gson();

        try {
            URL url = new URL("http://country.io/currency.json");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null)
                    jsonCurrency.append(line);
            }
            JsonObject jsonObjectCurrencies = gson.fromJson(String.valueOf(jsonCurrency), JsonObject.class);
            countryCurrency = jsonObjectCurrencies.get(countryCode).getAsString();
            url = new URL("https://api.exchangerate.host/latest?base=" + countryCurrency);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null)
                    result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonObject jsonObject = gson.fromJson(String.valueOf(result), JsonObject.class);
        String rate = jsonObject.getAsJsonObject("rates").get(currencyCode).getAsString();

        return Double.parseDouble(rate);
    }


    public Double getNBPRate() {

        StringBuilder xmlA = new StringBuilder();
        StringBuilder xmlB = new StringBuilder();
        gson = new Gson();

        try {
            URL url = new URL("https://www.nbp.pl/kursy/xml/a064z220401.xml");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null)
                    xmlA.append(line);
            }
            url = new URL("https://www.nbp.pl/kursy/xml/b013z220330.xml");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null)
                    xmlB.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObjectA = XML.toJSONObject(xmlA.toString());
        JSONObject jsonObjectB = XML.toJSONObject(xmlB.toString());
        JSONArray jsonArray = new JSONArray();
        jsonArray.putAll(jsonObjectA.getJSONObject("tabela_kursow").get("pozycja"));
        jsonArray.putAll(jsonObjectB.getJSONObject("tabela_kursow").get("pozycja"));
        double rate = 0.0;

        for (int i = 0; i < jsonArray.length(); i++) {
            if (countryCurrency.equals("PLN")) {
                rate = 1.0;
                break;
            } else if (jsonArray.getJSONObject(i).get("kod_waluty").equals(countryCurrency)) {
                String rateValue =jsonArray.getJSONObject(i).get("kurs_sredni").toString();
                rateValue = rateValue.replaceAll(",",".");
                rate = Double.parseDouble(rateValue);
                break;
            }

        }
        return rate;
    }

    public String getLocation() {
        return location;
    }

    public String getCountryCurrency() {
        return countryCurrency;
    }

}
