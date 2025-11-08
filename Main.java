package com.example;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        List<String> symbols = Arrays.asList("USDKRW=X", "AAPL");
        Map<String, List<Double>> priceHistory = new HashMap<>();
        for (String s : symbols) priceHistory.put(s, new ArrayList<>());

        int rounds = 5;                 // 총 fetch 횟수
        int intervalMillis = 5 * 60_000; // 5분 = 300,000ms

        for (int i = 0; i < rounds; i++) {
            System.out.println("==== Fetch round " + (i + 1) + " ====");
            
            // 🔹 모든 심볼 동시에 fetch
            for (String symbol : symbols) {
                double price = DataFetcher.fetch(symbol);
                if (price > 0) {
                    priceHistory.get(symbol).add(price);
                    System.out.printf("%s | %s = %.2f%n", LocalDateTime.now(), symbol, price);
                } else {
                    System.out.printf("%s | %s = (fetch 실패)%n", LocalDateTime.now(), symbol);
                }
            }

            // CSV와 차트 갱신
            CsvWriter.saveToCsv(priceHistory);
            ChartUtil.saveChart(priceHistory);

            // 마지막 round 전까지만 대기
            if (i < rounds - 1) {
                try {
                    System.out.printf("다음 fetch까지 5분 대기...\n");
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException ignored) {}
            }
        }

        System.out.println("✅ 모든 fetch 완료, CSV 및 차트 저장됨");
    }
}
