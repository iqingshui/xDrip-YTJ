package com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.AlarmConfiguration;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.ApplicationRegion;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.NonActionableConfiguration;

/* loaded from: classes.dex */
public class DataProcessingNative implements DataProcessing {
    private final int parserType;

    private native boolean getNeedsReaderInfoForActivation(int i);

    private native MemoryRegion getNextRegionToRead(int i, byte[] bArr, int i2);

    private native int getProductFamily(int i);

    private native int getTotalMemorySize(int i);

    private native int getUnlockCode(int i);

    private native boolean isPatchSupported(int i, byte[] bArr, ApplicationRegion applicationRegion);

    private native DataProcessingResult processScan(int i, AlarmConfiguration alarmConfiguration, NonActionableConfiguration nonActionableConfiguration, byte[] bArr, int i2, int i3, int i4, byte[] bArr2, Out<Integer> out, Out<Integer> out2, Out<Boolean> out3, Out<Boolean> out4, Out<byte[]> out5, Out<AlgorithmResults> out6);

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public native void initialize(Object obj);

    static {
        System.loadLibrary("DataProcessing");
    }

    public DataProcessingNative(int i) {
        this.parserType = i;
    }

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public boolean isPatchSupported(byte[] bArr, ApplicationRegion applicationRegion) {
        return isPatchSupported(this.parserType, bArr, applicationRegion);
    }

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public int getProductFamily() {
        return getProductFamily(this.parserType);
    }

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public int getUnlockCode() {
        return getUnlockCode(this.parserType);
    }

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public int getTotalMemorySize() {
        return getTotalMemorySize(this.parserType);
    }

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public boolean getNeedsReaderInfoForActivation() {
        return getNeedsReaderInfoForActivation(this.parserType);
    }

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public MemoryRegion getNextRegionToRead(byte[] bArr, int i) {
        return getNextRegionToRead(this.parserType, bArr, i);
    }

    @Override // com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessing
    public DataProcessingOutputs processScan(AlarmConfiguration alarmConfiguration, NonActionableConfiguration nonActionableConfiguration, byte[] bArr, int i, int i2, int i3, byte[] bArr2) throws DataProcessingException {
        Out<Integer> out = new Out<>();
        Out<Integer> out2 = new Out<>();
        Out<Boolean> out3 = new Out<>();
        Out<Boolean> out4 = new Out<>();
        Out<byte[]> out5 = new Out<>();
        Out<AlgorithmResults> out6 = new Out<>();
        DataProcessingResult processScan = processScan(this.parserType, alarmConfiguration, nonActionableConfiguration, bArr, i, i2, i3, bArr2, out, out2, out3, out4, out5, out6);
        if (processScan == DataProcessingResult.SUCCESS) {
            return new DataProcessingOutputs(out.value().intValue(), out2.value().intValue(), out3.value().booleanValue(), out4.value().booleanValue(), out5.value(), out6.value());
        }
        throw new DataProcessingException(processScan);
    }
}
