package com.example;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CsvWriter {

    private static final Path FOLDER = Paths.get("data");
    private static final SimpleDateFormat TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void saveAll(Map<String, List<Date>> timeHistory,
                               Map<String, List<Double>> priceHistory) {

        try {
            if (!Files.exists(FOLDER)) Files.createDirectories(FOLDER);

            for (String symbol : priceHistory.keySet()) {

                Path file = FOLDER.resolve(symbol + ".csv");

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {
                    bw.write("timestamp,price\n");

                    List<Date> times = timeHistory.get(symbol);
                    List<Double> prices = priceHistory.get(symbol);

                    if (times == null || prices == null) continue;

                    int n = Math.min(times.size(), prices.size());
                    for (int i = 0; i < n; i++) {
                        bw.write(TS.format(times.get(i)) + "," + prices.get(i) + "\n");
                    }
                }
            }

            // CSV 저장 성공 팝업
            JOptionPane.showMessageDialog(
                    null,
                    "CSV 저장 완료\n폴더 위치: " + FOLDER.toAbsolutePath(),
                    "CSV 저장 성공",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            // CSV 저장 실패 팝업
            JOptionPane.showMessageDialog(
                    null,
                    "CSV 저장 실패: " + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
