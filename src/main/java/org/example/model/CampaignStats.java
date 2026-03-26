package org.example.model;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class CampaignStats {
    final String campaignId;
    final LongAdder totalImpressions = new LongAdder();
    final LongAdder totalClicks      = new LongAdder();
    public final LongAdder totalConversions = new LongAdder();
    // LongAdder tốt hơn AtomicLong khi contention cao (nhiều thread ghi cùng lúc)

    // double không có Adder tương đương → dùng trick qua AtomicLong
    private final AtomicLong totalSpendBits = new AtomicLong(
            Double.doubleToLongBits(0.0)
    );

    public CampaignStats(String campaignId) {
        this.campaignId = campaignId;
    }

    public void accumulate(long impressions, long clicks, double spend, long conversions) {
        totalImpressions.add(impressions);
        totalClicks.add(clicks);
        totalConversions.add(conversions);
        addSpend(spend);
    }

    private void addSpend(double delta) {
        long prev, next;
        do {
            prev = totalSpendBits.get();
            next = Double.doubleToLongBits(Double.longBitsToDouble(prev) + delta);
        } while (!totalSpendBits.compareAndSet(prev, next));
    }

    public double totalSpend() {
        return Double.longBitsToDouble(totalSpendBits.get());
    }

    public double ctr() {
        long imp = totalImpressions.sum();
        return imp == 0 ? 0 : (double) totalClicks.sum() / imp;
    }

    public Double cpa() {
        long conv = totalConversions.sum();
        return conv == 0 ? null : totalSpend() / conv;
    }

    public String toCsvLine() {
        Double cpa = cpa();
        return String.format("%s,%d,%d,%.2f,%d,%.4f,%s",
                campaignId,
                totalImpressions.sum(),
                totalClicks.sum(),
                totalSpend(),
                totalConversions.sum(),
                ctr(),
                cpa == null ? "" : String.format("%.2f", cpa));
    }
}