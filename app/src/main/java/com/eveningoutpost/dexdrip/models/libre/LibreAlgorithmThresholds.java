package com.eveningoutpost.dexdrip.models.libre;

/* loaded from: classes.dex */
public class LibreAlgorithmThresholds {
    public int forSensorIdentifiedBy;
    public int glucoseLowerThreshold;
    public int glucoseUpperThreshold;
    public int temperatureLowerThreshold;
    public int temperatureUpperThreshold;

    public LibreAlgorithmThresholds(int i, int i2, int i3, int i4, int i5) {
        this.glucoseLowerThreshold = i;
        this.glucoseUpperThreshold = i2;
        this.temperatureLowerThreshold = i3;
        this.temperatureUpperThreshold = i4;
        this.forSensorIdentifiedBy = i5;
    }
}
