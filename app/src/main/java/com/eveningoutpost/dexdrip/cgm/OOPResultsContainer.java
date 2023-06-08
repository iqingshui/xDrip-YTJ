package com.eveningoutpost.dexdrip.cgm;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.TrendArrow;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.GlucoseValue;
import com.google.gson.GsonBuilder;
import java.io.Serializable;
import java.util.List;

/* loaded from: classes.dex */
public class OOPResultsContainer {
    String Message;
    OOPResults[] oOPResultsArray = new OOPResults[0];
    int version = 1;

    OOPResultsContainer() {
    }

    String toGson() {
        return new GsonBuilder().create().toJson(this);
    }

    /* loaded from: classes.dex */
    public static class HistoricBg implements Serializable {
        public double bg;
        public int quality;
        public int time;

        HistoricBg(GlucoseValue glucoseValue) {
            this.quality = glucoseValue.getDataQuality() == 0 ? 0 : 1;
            this.time = glucoseValue.getId();
            this.bg = glucoseValue.getValue();
        }
    }

    /* loaded from: classes.dex */
    public static class OOPResults implements Serializable {
        public int currenTrend;
        public double currentBg;
        public int currentTime;
        public HistoricBg[] historicBg;
        public String serialNumber;
        public long timestamp;

        public OOPResults(long j, int i, int i2, TrendArrow trendArrow) {
            this.currentBg = i;
            if (trendArrow != null) {
                this.currenTrend = trendArrow.ordinal();
            }
            this.timestamp = j;
            this.currentTime = i2;
            this.serialNumber = "";
        }

        public String toGson() {
            return new GsonBuilder().create().toJson(this);
        }

        public double getCurrentBg() {
            return this.currentBg;
        }

        public void setCurrentBg(double d) {
            this.currentBg = d;
        }

        public int getCurrentTime() {
            return this.currentTime;
        }

        public void setCurrentTime(int i) {
            this.currentTime = i;
        }

        public int getCurrenTrend() {
            return this.currenTrend;
        }

        public void setCurrenTrend(int i) {
            this.currenTrend = i;
        }

        public HistoricBg[] getHistoricBg() {
            return this.historicBg;
        }

        public void setHistoricBg(List<GlucoseValue> list) {
            if (list == null) {
                return;
            }
            this.historicBg = new HistoricBg[list.size()];
            for (int i = 0; i < list.size(); i++) {
                this.historicBg[i] = new HistoricBg(list.get(i));
            }
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(long j) {
            this.timestamp = j;
        }

        public String getSerialNumber() {
            return this.serialNumber;
        }

        public void setSerialNumber(String str) {
            this.serialNumber = str;
        }
    }
}