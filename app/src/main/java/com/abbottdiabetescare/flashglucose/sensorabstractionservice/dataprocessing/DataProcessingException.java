package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

/* loaded from: classes.dex */
public final class DataProcessingException extends Exception {
    private static final long serialVersionUID = 1;
    private final DataProcessingResult result;

    public DataProcessingException(DataProcessingResult dataProcessingResult) {
        super(dataProcessingResult.toString());
        this.result = dataProcessingResult;
    }

    public DataProcessingResult getResult() {
        return this.result;
    }
}
