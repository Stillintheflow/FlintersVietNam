package org.example.csv;

import java.io.IOException;
import java.util.stream.Stream;

public interface CsvReader {
    Stream<CsvRecord> read(String filePath) throws IOException;
}
