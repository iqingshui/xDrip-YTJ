package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.AlarmConfiguration;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.ApplicationRegion;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.NonActionableConfiguration;

/* loaded from: classes.dex */
public interface DataProcessing {
    public static final int BASE_YEAR = 2010;

    boolean getNeedsReaderInfoForActivation();

    MemoryRegion getNextRegionToRead(byte[] bArr, int i);

    int getProductFamily();

    int getTotalMemorySize();

    int getUnlockCode();

    void initialize(Object obj);

    boolean isPatchSupported(byte[] bArr, ApplicationRegion applicationRegion);

    DataProcessingOutputs processScan(AlarmConfiguration alarmConfiguration, NonActionableConfiguration nonActionableConfiguration, byte[] bArr, int i, int i2, int i3, byte[] bArr2) throws DataProcessingException;
}
