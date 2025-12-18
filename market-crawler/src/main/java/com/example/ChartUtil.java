package com.example;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.None;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChartUtil {

    private final XYChart chart;
    private final JPanel chartPanel;

    public ChartUtil() {

        chart = new XYChartBuilder()
                .width(900)
                .height(600)
                .title("Realtime Market Chart")
                .xAxisTitle("Time")
                .yAxisTitle("Price")
                .build();

        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setDatePattern("HH:mm:ss");
        chart.getStyler().setXAxisLabelRotation(45);
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
    }

    public void removeSeries(String symbol, String displayName) {
        SwingUtilities.invokeLater(() -> {
            try {
                String key = seriesKey(symbol, displayName);
                chart.getSeriesMap().remove(key);
                chartPanel.revalidate();
                chartPanel.repaint();
            } catch (Exception e) {
                System.err.println("Failed to remove series: " + e.getMessage());
            }
        });
    }

    public void updateSeries(String symbol,
                             String displayName,
                             List<Date> times,
                             List<Double> prices) {

        SwingUtilities.invokeLater(() -> {
            try {
                if (times == null || prices == null) return;
                if (times.isEmpty() || prices.isEmpty()) return;

                String key = seriesKey(symbol, displayName);

                if (chart.getSeriesMap().containsKey(key)) {
                    chart.updateXYSeries(key, times, prices, null);
                } else {
                    XYSeries s = chart.addSeries(key, times, prices);
                    s.setMarker(new None());
                }

                chartPanel.revalidate();
                chartPanel.repaint();

            } catch (Exception e) {
                System.err.println("Chart update failed: " + e.getMessage());
            }
        });
    }

    public void saveChartImage() {
        try {
            File folder = new File("charts");
            if (!folder.exists()) folder.mkdirs();

            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File file = new File(folder, "chart_" + time + ".png");

            BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);

            JOptionPane.showMessageDialog(
                    null,
                    "차트 이미지 저장 완료\n폴더 위치: " + file.getAbsolutePath(),
                    "저장 성공",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "저장 실패: " + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
