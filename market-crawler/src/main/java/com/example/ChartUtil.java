package com.example;

import org.knowm.xchart.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ChartUtil {

    private static final Path folder = Paths.get("charts");

    public static void saveChart(Map<String, List<Double>> data) throws IOException {
        if (!Files.exists(folder)) Files.createDirectories(folder);

        for (String key : data.keySet()) {
            List<Double> values = data.get(key);
            List<Integer> index = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) index.add(i + 1);

            XYChart chart = new XYChartBuilder().width(600).height(400)
                    .title(key + " Trend")
                    .xAxisTitle("Fetch Count")
                    .yAxisTitle("Price")
                    .build();
            chart.addSeries(key, index, values);

            Path file = folder.resolve(key + ".png");
            BitmapEncoder.saveBitmap(chart, file.toString(), BitmapEncoder.BitmapFormat.PNG);
        }
    }
}
