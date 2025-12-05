package com.example;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CsvWriter {

    private static final Path FOLDER = Paths.get("data");

    public static void saveAll(Map<String, List<Double>> history) {

        try {
            if (!Files.exists(FOLDER)) Files.createDirectories(FOLDER);

            for (String symbol : history.keySet()) {

                Path file = FOLDER.resolve(symbol + ".csv");

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {

                    bw.write("timestamp,price\n");

                    List<Double> values = history.get(symbol);

                    for (Double price : values) {
                        bw.write(LocalDateTime.now() + "," + price + "\n");
                    }
                }
            }

            //CSV 저장 성공 팝업
            JOptionPane.showMessageDialog(
                    null,
                    "CSV 저장 완료!\n폴더 위치: " + FOLDER.toAbsolutePath(),
                    "CSV 저장 성공",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            //오류 시 팝업
            JOptionPane.showMessageDialog(
                    null,
                    "CSV 저장 실패: " + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
