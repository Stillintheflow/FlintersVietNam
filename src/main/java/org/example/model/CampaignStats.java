package org.example.model;

public class CampaignStats {
    String campaignId;
    long totalImpressions;
    long totalClicks;
    double totalSpend;
    public long totalConversions;

    public CampaignStats(String campaignId) {
        this.campaignId = campaignId;
    }

    public void accumulate(long impressions, long clicks, double spend, long conversions) {
        totalImpressions += impressions;
        totalClicks += clicks;
        totalSpend += spend;
        totalConversions += conversions;
    }

    public static CampaignStats merge(CampaignStats a, CampaignStats b) {
        a.totalImpressions += b.totalImpressions;
        a.totalClicks += b.totalClicks;
        a.totalSpend += b.totalSpend;
        a.totalConversions += b.totalConversions;
        return a;
    }

    public double ctr() {
        return totalImpressions == 0 ? 0 : (double) totalClicks / totalImpressions;
    }

    public Double cpa() {
        return totalConversions == 0 ? null : totalSpend / totalConversions;
    }

    public String toCsvLine() {
        return String.format("%s,%d,%d,%.2f,%d,%.4f,%s",
                campaignId, totalImpressions, totalClicks, totalSpend,
                totalConversions, ctr(),
                cpa() == null ? "" : String.format("%.2f", cpa()));
    }
}
