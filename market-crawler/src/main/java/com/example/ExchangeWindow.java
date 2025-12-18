package com.example;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.*;

public class ExchangeWindow extends JFrame {

    private final List<String> symbols = new ArrayList<>();
    private final Map<String, List<Double>> priceHistory = new HashMap<>();
    private final Map<String, List<Date>> timeHistory = new HashMap<>();
    private final Map<String, String> displayNames = new HashMap<>();

    private final ChartUtil chartUtil = new ChartUtil();

    public ExchangeWindow() {

        setTitle("Realtime Exchange Rate Viewer");
        setSize(800, 400);
        setLayout(new BorderLayout());

        String symbol = "USDKRW=X";
        symbols.add(symbol);
        priceHistory.put(symbol, new ArrayList<>());
        timeHistory.put(symbol, new ArrayList<>());
        displayNames.put(symbol, "환율 USD→KRW");

        chartUtil.addSeries(symbol, "환율 USD→KRW");
        add(chartUtil.getChartPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    public void startFetching() {
        FetcherThread th = new FetcherThread(
                symbols,
                priceHistory,
                timeHistory,
                displayNames,
                chartUtil
        );
        th.setDaemon(true);
        th.start();
    }
}
