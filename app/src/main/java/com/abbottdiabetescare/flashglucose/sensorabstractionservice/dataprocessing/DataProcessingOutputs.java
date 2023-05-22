package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

/* loaded from: classes.dex */
public final class DataProcessingOutputs {
    private final AlgorithmResults algorithmResults;
    private final int estimatedSensorEndTimestamp;
    private final int estimatedSensorStartTimestamp;
    private final boolean insertionIsConfirmed;
    private final byte[] newState;
    private final boolean sensorHasBeenRemoved;

    public DataProcessingOutputs(int i, int i2, boolean z, boolean z2, byte[] bArr, AlgorithmResults algorithmResults) {
        this.estimatedSensorStartTimestamp = i;
        this.estimatedSensorEndTimestamp = i2;
        this.insertionIsConfirmed = z;
        this.sensorHasBeenRemoved = z2;
        this.newState = bArr;
        this.algorithmResults = algorithmResults;
    }

    public int getEstimatedSensorStartTimestamp() {
        return this.estimatedSensorStartTimestamp;
    }

    public int getEstimatedSensorEndTimestamp() {
        return this.estimatedSensorEndTimestamp;
    }

    public boolean getInsertionIsConfirmed() {
        return this.insertionIsConfirmed;
    }

    public boolean getSensorHasBeenRemoved() {
        return this.sensorHasBeenRemoved;
    }

    public byte[] getNewState() {
        return this.newState;
    }

    public AlgorithmResults getAlgorithmResults() {
        return this.algorithmResults;
    }
}
