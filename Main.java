package com.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        List<String> symbols = Arrays.asList("USDKRW=X", "AAPL");
        Map<String, List<Double>> priceHistory = new HashMap<>();
        for (String s : symbols) priceHistory.put(s, new ArrayList<>());

        for (int i = 0; i < 3; i++) {
            System.out.println("==== Fetch round " + (i + 1) + " ====");
            for (String symbol : symbols) {
                double price = DataFetcher.fetch(symbol);
                if (price > 0) {
                    priceHistory.get(symbol).add(price);
                    System.out.printf("%s | %s = %.2f%n", LocalDateTime.now(), symbol, price);
                } else {
                    System.out.printf("%s | %s = (fetch 실패)%n", LocalDateTime.now(), symbol);
                }
            }
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
        }

        // ✅ CSV로 저장
        CsvWriter.saveToCsv(priceHistory);

        // ✅ 차트로 저장
        ChartUtil.saveChart(priceHistory);

        System.out.println("✅ Data saved to /data and charts created in /charts");
    }
}
