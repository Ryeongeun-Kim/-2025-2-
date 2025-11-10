package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;

public class DataFetcher {

    public static double fetch(String symbol) {
        try {
            String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol;

            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .timeout(7000)
                    .get();

            JSONObject json = new JSONObject(doc.text());
            JSONObject meta = json.getJSONObject("chart")
                    .getJSONArray("result")
                    .getJSONObject(0)
                    .getJSONObject("meta");

            return meta.getDouble("regularMarketPrice");

        } catch (Exception e) {
            System.err.println("Failed to fetch " + symbol + ": " + e.getMessage());
            return -1;
        }
    }
}
