package org.example.csv;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * Optimized CSV reader using character-based parsing
 * Avoids string allocations and multiple indexOf calls
 */
public class FastCsvReader implements CsvReader {

    private static final int BUFFER_SIZE = 8 * 1024 * 1024; // 8MB buffer

    @Override
    public Stream<CsvRecord> read(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath),
                        StandardCharsets.UTF_8),
                BUFFER_SIZE);
        br.readLine(); // skip header
        return br.lines()
                .map(this::parseLine)
                .onClose(() -> {
                    try {
                        br.close();
                    } catch (IOException ignored) {
                    }
                });
    }

    /**
     * Parse a CSV line with fixed 5 fields
     * Uses character-based parsing for better performance
     */
    private CsvRecord parseLine(String line) {
        int[] commaPositions = new int[4];
        int commaCount = 0;
        
        // Find comma positions
        for (int i = 0; i < line.length() && commaCount < 4; i++) {
            if (line.charAt(i) == ',') {
                commaPositions[commaCount++] = i;
            }
        }

        // Parse fields
        String campaignId = line.substring(0, commaPositions[0]);
        long impressions = Long.parseLong(line, commaPositions[0] + 1, commaPositions[1], 10);
        long clicks = Long.parseLong(line, commaPositions[1] + 1, commaPositions[2], 10);
        double spend = Double.parseDouble(line.substring(commaPositions[2] + 1, commaPositions[3]));
        long conversions = Long.parseLong(line, commaPositions[3] + 1, line.length(), 10);

        return new CsvRecord(campaignId, impressions, clicks, spend, conversions);
    }
}

