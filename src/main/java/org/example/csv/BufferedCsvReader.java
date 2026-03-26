package org.example.csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class BufferedCsvReader implements CsvReader {

    @Override
    public Stream<CsvRecord> read(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), "UTF-8"), 8 * 1024 * 1024);
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

    private CsvRecord parseLine(String line) {
        int i1 = line.indexOf(',');
        int i2 = line.indexOf(',', i1 + 1);
        int i3 = line.indexOf(',', i2 + 1);
        int i4 = line.indexOf(',', i3 + 1);
        int i5 = line.indexOf(',', i4 + 1);

        return new CsvRecord(
                line.substring(0, i1),
                Long.parseLong(line.substring(i2 + 1, i3)),
                Long.parseLong(line.substring(i3 + 1, i4)),
                Double.parseDouble(line.substring(i4 + 1, i5)),
                Long.parseLong(line.substring(i5 + 1))
        );
    }
}
