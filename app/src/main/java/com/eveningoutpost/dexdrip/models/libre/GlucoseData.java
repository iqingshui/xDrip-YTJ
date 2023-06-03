package com.eveningoutpost.dexdrip.models.libre;

import static com.eveningoutpost.dexdrip.models.LibreOOPAlgorithm.getDateTimeStr;

import java.math.BigDecimal;
import java.util.Date;

/* loaded from: classes.dex */
public class GlucoseData {
    byte[] cData;
    double glucoseLevelRaw;
    Date timeStamp;

    public long sensorAge;
    public static float getValueFormFloat(float f) {
        return new BigDecimal(f).setScale(1, 4).floatValue();
    }


    public GlucoseData(Date date, double d, byte[] bArr) {
        this.timeStamp = date;
        this.glucoseLevelRaw = getValueFormFloat((float) d);
        this.cData = bArr;
    }

    public GlucoseData(Date date, double d) {
        this.timeStamp = date;
        this.glucoseLevelRaw = d;
    }

    public GlucoseData() {
    }

    public String getPrint() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("时间：");
        stringBuffer.append(getDateTimeStr(this.timeStamp.getTime()));
        stringBuffer.append(" 血糖值：");
        stringBuffer.append(getBloodSugar());
        stringBuffer.append(" 原始血糖值：");
        stringBuffer.append(getValueFormFloat((float) this.glucoseLevelRaw));
        return stringBuffer.toString();
    }

    public float getBloodSugar() {
        return getValueFormFloat((float) (this.glucoseLevelRaw / 18.0d));
    }

    public boolean checkBloodSugarError() {
        return getBloodSugar() <= 2.2f || getBloodSugar() >= 27.8f;
    }
}