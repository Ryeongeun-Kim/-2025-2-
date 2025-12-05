package com.example;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.None;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartUtil {

    private final XYChart chart;
    private final JPanel chartPanel;

    public ChartUtil() {

        chart = new XYChartBuilder()
                .width(900).height(600)
                .title("Realtime Market Chart")
                .xAxisTitle("Fetch #")
                .yAxisTitle("Price")
                .build();

        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setYAxisDecimalPattern("#,###.##");

        chartPanel = new XChartPanel<>(chart);
    }

    public JPanel getChartPanel() {
        return chartPanel;
    }

    private String getUnit(String symbol) {
        if (symbol.equals("USDKRW=X")) return "₩";
        if (symbol.endsWith(".KS") || symbol.endsWith(".KQ")) return "₩";
        return "$";
    }

    private String seriesKey(String symbol, String displayName) {
        return displayName + " (" + getUnit(symbol) + ")";
    }

    public void addSeries(String symbol, String displayName) {
        SwingUtilities.invokeLater(() -> {
            try {
                String key = seriesKey(symbol, displayName);
                chart.addSeries(key, new double[]{}, new double[]{}).setMarker(new None());
                chartPanel.revalidate();
                chartPanel.repaint();
            } catch (Exception ignored) {}
        });
    }

    public void removeSeries(String symbol) {
        SwingUtilities.invokeLater(() -> {
            try {
                chart.getSeriesMap().entrySet().removeIf(e -> e.getKey().contains(symbol));
                chartPanel.revalidate();
                chartPanel.repaint();
            } catch (Exception ignored) {}
        });
    }

    public void updateSeries(String symbol, String displayName, List<Double> values) {

        SwingUtilities.invokeLater(() -> {
            try {
                double[] x = new double[values.size()];
                double[] y = new double[values.size()];

                for (int i = 0; i < values.size(); i++) {
                    x[i] = i + 1;
                    y[i] = values.get(i);
                }

                String key = seriesKey(symbol, displayName);

                if (chart.getSeriesMap().containsKey(key)) {
                    chart.updateXYSeries(key, x, y, null);
                } else {
                    chart.addSeries(key, x, y).setMarker(new None());
                }

                chartPanel.revalidate();
                chartPanel.repaint();

            } catch (Exception ignored) {}
        });
    }

    public void saveChartImage() {
        try {
            File folder = new File("charts");
            if (!folder.exists()) folder.mkdirs();

            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File file = new File(folder, "chart_" + time + ".png");

            BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);

            JOptionPane.showMessageDialog(null,
                    "차트 이미지 저장 완료:\n" + file.getAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "저장 실패: " + e.getMessage());
        }
    }
}
