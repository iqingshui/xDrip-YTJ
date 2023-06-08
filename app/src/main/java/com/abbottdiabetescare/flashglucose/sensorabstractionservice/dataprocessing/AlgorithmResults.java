package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.Alarm;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.TrendArrow;
import java.util.List;

/* loaded from: classes.dex */
public final class AlgorithmResults {
    private final Alarm alarm;
    private final List<GlucoseValue> historicGlucose;
    private final boolean isActionable;
    private final GlucoseValue realTimeGlucose;
    private final TrendArrow trendArrow;

    public AlgorithmResults(List<GlucoseValue> list, GlucoseValue glucoseValue, TrendArrow trendArrow, Alarm alarm, boolean z) {
        this.historicGlucose = list;
        this.realTimeGlucose = glucoseValue;
        this.trendArrow = trendArrow;
        this.alarm = alarm;
        this.isActionable = z;
    }

    public List<GlucoseValue> getHistoricGlucose() {
        return this.historicGlucose;
    }

    public GlucoseValue getRealTimeGlucose() {
        return this.realTimeGlucose;
    }

    public TrendArrow getTrendArrow() {
        return this.trendArrow;
    }

    public Alarm getAlarm() {
        return this.alarm;
    }

    public boolean getIsActionable() {
        return this.isActionable;
    }
}
