package org.example.aggregator;

import org.example.csv.CsvRecord;
import org.example.model.CampaignStats;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class HashMapDataAggregator implements DataAggregator {


    private final Map<String, CampaignStats> map = new ConcurrentHashMap<>(1 << 14); // 16384, power of 2

    @Override
    public void process(Stream<CsvRecord> records) {
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        try {
            pool.submit(() ->
                    records.parallel().forEach(r ->
                            map.computeIfAbsent(r.campaignId(), CampaignStats::new)
                                    .accumulate(r.impressions(), r.clicks(), r.spend(), r.conversions())
                    )
            ).get();
        } catch (Exception e) {
            throw new RuntimeException("Processing failed", e.getCause());
        } finally {
            pool.shutdown();
        }
    }

    @Override
    public List<CampaignStats> topByCtr(int limit) {
        return topK(map.values(), limit, Comparator.comparingDouble(CampaignStats::ctr));
    }

    @Override
    public List<CampaignStats> topByCpa(int limit) {
        List<CampaignStats> withConversions = map.values().stream()
                .filter(c -> c.totalConversions.intValue() > 0)
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
