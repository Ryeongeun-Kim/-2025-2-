package com.example;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.*;

public class ExchangeWindow extends JFrame {

    private final List<String> symbols = new ArrayList<>();
    private final Map<String, List<Double>> priceHistory = new HashMap<>();
    private final Map<String, String> displayNames = new HashMap<>();

    private final ChartUtil chartUtil = new ChartUtil();

    public ExchangeWindow() {

        setTitle("Realtime Exchange Rate Viewer");
        setSize(800, 400);
        setLayout(new BorderLayout());
        setLocation(300, 80);

        String symbol = "USDKRW=X";
        String display = "환율 USD→KRW";

        symbols.add(symbol);
        priceHistory.put(symbol, new ArrayList<>());
        displayNames.put(symbol, display);

        chartUtil.addSeries(symbol, display);

        add(chartUtil.getChartPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    public void startFetching() {
        FetcherThread th = new FetcherThread(symbols, priceHistory, displayNames, chartUtil);
        th.setDaemon(true);
        th.start();
    }
}
