package com.eveningoutpost.dexdrip.models.libre;

/* loaded from: classes.dex */
public class Libre1DerivedAlgorithmParameters {
    public double extraOffset;
    public double extraSlope;
    public int isValidForFooterWithReverseCRCs;
    public double offset_offset;
    public double offset_slope;
    public String serialNumber;
    public double slope_offset;
    public double slope_slope;

    public Libre1DerivedAlgorithmParameters(double d, double d2, double d3, double d4, int i, double d5, double d6, String str) {
        this.extraSlope = 1.0d;
        this.extraOffset = 0.0d;
        this.slope_slope = d;
        this.slope_offset = d2;
        this.offset_slope = d3;
        this.offset_offset = d4;
        this.isValidForFooterWithReverseCRCs = i;
        this.extraSlope = d5;
        this.extraOffset = d6;
        this.serialNumber = str;
    }

    public Libre1DerivedAlgorithmParameters(byte[] bArr, String str) {
        this.extraSlope = 1.0d;
        this.extraOffset = 0.0d;
        this.serialNumber = str;
        LibreAlgorithmThresholds libreAlgorithmThresholds = new LibreAlgorithmThresholds(1000, 3000, 6000, 9000, 49778);
        LibreCalibrationInfo libreCalibrationInfo = new LibreCalibrationInfo(bArr);
        double roundedGlucoseValueFromRaw2 = new LibreMeasurement(libreAlgorithmThresholds.glucoseLowerThreshold, libreAlgorithmThresholds.temperatureLowerThreshold).roundedGlucoseValueFromRaw2(libreCalibrationInfo);
        double roundedGlucoseValueFromRaw22 = new LibreMeasurement(libreAlgorithmThresholds.glucoseUpperThreshold, libreAlgorithmThresholds.temperatureLowerThreshold).roundedGlucoseValueFromRaw2(libreCalibrationInfo);
        double doubleValue = (roundedGlucoseValueFromRaw22 - roundedGlucoseValueFromRaw2) / (Double.valueOf(libreAlgorithmThresholds.glucoseUpperThreshold).doubleValue() - Double.valueOf(libreAlgorithmThresholds.glucoseLowerThreshold).doubleValue());
        double doubleValue2 = roundedGlucoseValueFromRaw22 - (Double.valueOf(libreAlgorithmThresholds.glucoseUpperThreshold).doubleValue() * doubleValue);
        double roundedGlucoseValueFromRaw23 = new LibreMeasurement(libreAlgorithmThresholds.glucoseLowerThreshold, libreAlgorithmThresholds.temperatureUpperThreshold).roundedGlucoseValueFromRaw2(libreCalibrationInfo);
        double roundedGlucoseValueFromRaw24 = new LibreMeasurement(libreAlgorithmThresholds.glucoseUpperThreshold, libreAlgorithmThresholds.temperatureUpperThreshold).roundedGlucoseValueFromRaw2(libreCalibrationInfo);
        double doubleValue3 = (roundedGlucoseValueFromRaw24 - roundedGlucoseValueFromRaw23) / (Double.valueOf(libreAlgorithmThresholds.glucoseUpperThreshold).doubleValue() - Double.valueOf(libreAlgorithmThresholds.glucoseLowerThreshold).doubleValue());
        double doubleValue4 = roundedGlucoseValueFromRaw24 - (Double.valueOf(libreAlgorithmThresholds.glucoseUpperThreshold).doubleValue() * doubleValue3);
        double doubleValue5 = (doubleValue - doubleValue3) / (Double.valueOf(libreAlgorithmThresholds.temperatureLowerThreshold).doubleValue() - Double.valueOf(libreAlgorithmThresholds.temperatureUpperThreshold).doubleValue());
        this.slope_slope = doubleValue5;
        this.offset_slope = doubleValue - (doubleValue5 * Double.valueOf(libreAlgorithmThresholds.temperatureLowerThreshold).doubleValue());
        double doubleValue6 = (doubleValue2 - doubleValue4) / (Double.valueOf(libreAlgorithmThresholds.temperatureLowerThreshold).doubleValue() - Double.valueOf(libreAlgorithmThresholds.temperatureUpperThreshold).doubleValue());
        this.slope_offset = doubleValue6;
        this.offset_offset = doubleValue4 - (doubleValue6 * Double.valueOf(libreAlgorithmThresholds.temperatureUpperThreshold).doubleValue());
        byte[] bArr2 = new byte[24];
        System.arraycopy(bArr, 320, bArr2, 0, 24);
        this.isValidForFooterWithReverseCRCs = (bArr2[1] << 8) | bArr2[0];
        this.extraSlope = 1.0d;
        this.extraOffset = 0.0d;
    }
}
