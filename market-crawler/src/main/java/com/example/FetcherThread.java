package com.example;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FetcherThread extends Thread {

    private final List<String> symbols;
    private final Map<String, List<Double>> priceHistory;
    private final Map<String, List<Date>> timeHistory;
    private final Map<String, String> displayNames;
    private final ChartUtil chartUtil;

    private final DecimalFormat df = new DecimalFormat("#,###.##");

    public FetcherThread(List<String> symbols,
                         Map<String, List<Double>> priceHistory,
                         Map<String, List<Date>> timeHistory,
                         Map<String, String> displayNames,
                         ChartUtil chartUtil) {

        this.symbols = symbols;
        this.priceHistory = priceHistory;
        this.timeHistory = timeHistory;
        this.displayNames = displayNames;
        this.chartUtil = chartUtil;
    }

    private String formatPrice(String symbol, double price) {
        if (symbol.equals("USDKRW=X")) return "₩" + df.format(price) + " per 1 USD";
        if (symbol.endsWith(".KS") || symbol.endsWith(".KQ")) return "₩" + df.format(price);
        return "$" + df.format(price);
    }

    @Override
    public void run() {

        while (true) {

            System.out.println("==== Fetch round ====");

            for (String symbol : symbols) {

                double price = DataFetcher.fetch(symbol);

                if (price > 0) {
                    Date now = new Date();

                    // 시간 + 가격 동시 저장(시계열)
                    priceHistory.get(symbol).add(price);
                    timeHistory.get(symbol).add(now);

                    String displayName = displayNames.get(symbol);

                    // 차트 업데이트(시간축)
                    chartUtil.updateSeries(
                            symbol,
                            displayName,
                            timeHistory.get(symbol),
                            priceHistory.get(symbol)
                    );

                    System.out.println(LocalDateTime.now() + " | " +
                            displayName + " = " + formatPrice(symbol, price));

                } else {
                    System.out.println(LocalDateTime.now() + " | " + symbol + " = (fetch 실패)");
                }
            }

            try { Thread.sleep(10_000); }
            catch (InterruptedException ignored) {}
        }
    }
}
