package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

/* loaded from: classes.dex */
public enum DataProcessingType {
    APOLLO_PG1(1095774552),
    APOLLO_PG2(1095774808),
    ATHENA_PG1_8HOUR(1345411139),
    ATHENA_PG1_14DAY(1345411157),
    ATHENA_ONE_MINUTE(1096044365),
    ATHENA_SUB_MINUTE(1096045389),
    ATHENA_FOUR_SECOND(1096042067);
    
    private final DataProcessing dataProcessing;

    DataProcessingType(int i) {
        this.dataProcessing = new DataProcessingNative(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataProcessing getDataProcessing() {
        return this.dataProcessing;
    }
}
