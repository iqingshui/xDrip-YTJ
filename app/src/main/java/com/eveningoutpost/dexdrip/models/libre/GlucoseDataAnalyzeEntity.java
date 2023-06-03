package com.eveningoutpost.dexdrip.models.libre;


import java.util.List;

/* loaded from: classes.dex */
public class GlucoseDataAnalyzeEntity {
    private List<GlucoseData> history;
    private long timeStart;
    private List<GlucoseData> trend;

    public List<GlucoseData> getTrend() {
        return this.trend;
    }

    public void setTrend(List<GlucoseData> list) {
        this.trend = list;
    }

    public List<GlucoseData> getHistory() {
        return this.history;
    }

    public void setHistory(List<GlucoseData> list) {
        this.history = list;
    }

    public long getTimeStart() {
        return this.timeStart;
    }

    public void setTimeStart(long j) {
        this.timeStart = j;
    }
}