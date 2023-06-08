package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

/* loaded from: classes.dex */
public final class DataProcessingFactory {
    private DataProcessingFactory() {
    }

    public static DataProcessing getDataProcessing(DataProcessingType dataProcessingType) {
        return dataProcessingType.getDataProcessing();
    }
}
