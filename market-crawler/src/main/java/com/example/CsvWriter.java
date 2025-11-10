package com.example;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class CsvWriter {

    private static final Path folder = Paths.get("data");

    public static void saveToCsv(Map<String, List<Double>> data) throws IOException {
        if (!Files.exists(folder)) Files.createDirectories(folder);

        for (String key : data.keySet()) {
            Path file = folder.resolve(key + ".csv");
            boolean append = Files.exists(file); // 이미 있으면 이어쓰기

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), append))) {
                if (!append) writer.write("Timestamp,Price\n"); // 헤더 한 번만 작성

                List<Double> values = data.get(key);
                for (Double price : values) {
                    writer.write(LocalDateTime.now() + "," + price + "\n");
                }
            }
        }
    }
}
