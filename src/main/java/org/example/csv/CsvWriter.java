package org.example.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.example.model.CampaignStats;

public class CsvWriter {

    private static final String CSV_HEADER = "campaign_id,total_impressions,total_clicks,total_spend,total_conversions,CTR,CPA";

    public void write(Path path, List<CampaignStats> stats) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(CSV_HEADER);
            writer.newLine();
            for (CampaignStats s : stats) {
                writer.write(s.toCsvLine());
                writer.newLine();
            }
        }
        System.out.println("Written: " + path);
    }
}
