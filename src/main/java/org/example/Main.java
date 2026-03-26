package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import org.example.csv.*;
import org.example.aggregator.DataAggregator;
import org.example.aggregator.HashMapDataAggregator;

public class Main {

    public static void main(String[] args) throws IOException {
        String inputPath = null;
        String outputDir = null;

        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "--input" -> inputPath = args[++i];
                case "--output" -> outputDir = args[++i];
            }
        }

        // Nếu không có --input, sử dụng file từ resources
        if (inputPath == null) {
        }

        // Nếu không có --output, sử dụng folder output mặc định
        if (outputDir == null) {
            outputDir = "output";
        }

        Files.createDirectories(Path.of(outputDir));

        CsvReader reader = new BufferedCsvReader();
        DataAggregator aggregator = new HashMapDataAggregator();
        CsvWriter writer = new CsvWriter();

        // Measure execution time
        long startRead = System.nanoTime();
        Stream<CsvRecord> records = reader.read(inputPath);
        long endRead = System.nanoTime();
        long readTime = endRead - startRead;

        long startProcess = System.nanoTime();
        try (Stream<CsvRecord> recordsToProcess = records) {
            aggregator.process(recordsToProcess);
        }
        long endProcess = System.nanoTime();
        long processTime = endProcess - startProcess;

        // Print execution time
        System.out.println("Time to read CSV: " + readTime / 1_000_000.0 + " ms");
        System.out.println("Time to process: " + processTime / 1_000_000.0 + " ms");
        System.out.println("Total time: " + (readTime + processTime) / 1_000_000.0 + " ms");

        writer.write(Path.of(outputDir, "top10_ctr.csv"), aggregator.topByCtr(10));
        writer.write(Path.of(outputDir, "top10_cpa.csv"), aggregator.topByCpa(10));

    }
}
