package org.example.aggregator;

import java.util.List;
import java.util.stream.Stream;

import org.example.csv.CsvRecord;
import org.example.model.CampaignStats;

public interface DataAggregator {
    void process(Stream<CsvRecord> records);
    List<CampaignStats> topByCtr(int limit);
    List<CampaignStats> topByCpa(int limit);
}
