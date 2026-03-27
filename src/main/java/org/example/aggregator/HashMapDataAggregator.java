package org.example.aggregator;

import org.example.csv.CsvRecord;
import org.example.model.CampaignStats;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class HashMapDataAggregator implements DataAggregator {


    private final Map<String, CampaignStats> map = new HashMap<>();

    @Override
    public void process(Stream<CsvRecord> records) {
        // each parallel thread builds its own local map, then merge — no contention
        Map<String, CampaignStats> merged = records.parallel().collect(
                () -> new HashMap<String, CampaignStats>(1024),
                (localMap, r) -> localMap.computeIfAbsent(r.campaignId(), k -> new CampaignStats(r.campaignId()))
                        .accumulate(r.impressions(), r.clicks(), r.spend(), r.conversions()),
                (m1, m2) -> m2.forEach((k, v) -> m1.merge(k, v, CampaignStats::merge))
        );
        map.putAll(merged);
    }

    @Override
    public List<CampaignStats> topByCtr(int limit) {
        return topK(map.values(), limit, Comparator.comparingDouble(CampaignStats::ctr));
    }

    @Override
    public List<CampaignStats> topByCpa(int limit) {
        List<CampaignStats> withConversions = map.values().stream()
                .filter(c -> c.totalConversions > 0)
                .toList();
        return topK(withConversions, limit, Comparator.comparingDouble(CampaignStats::cpa).reversed());
    }

    private List<CampaignStats> topK(Collection<CampaignStats> data,
                                     int k,
                                     Comparator<CampaignStats> minComparator) {
        PriorityQueue<CampaignStats> heap = new PriorityQueue<>(k + 1, minComparator);
        for (CampaignStats s : data) {
            heap.offer(s);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        List<CampaignStats> result = new ArrayList<>(heap);
        result.sort(minComparator.reversed());
        return result;
    }
}
