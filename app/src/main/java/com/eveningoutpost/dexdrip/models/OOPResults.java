package com.eveningoutpost.dexdrip.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OOPResults {
    double currentBg;
    int currentTime;
    int currentTrend;
    HistoricBg[] historicBg;
    long timestamp;
    String serialNumber;

    String toGson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
