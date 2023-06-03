package com.eveningoutpost.dexdrip.models.libre;

import static com.eveningoutpost.dexdrip.models.LibreOOPAlgorithm.getDateTimeStr;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.impl.TimeUtil;

/* loaded from: classes.dex */
public class LibreDataParser {
    private static String TAG = "Ble";
    private int amountOfValuesToCompare = 4;
    private long sensorStartTimeInMilliseconds;

    public Date dateOfMostRecentHistoryValue(long j, int i, Date date) {
        double d;
        long j2 = j - 3;
        long j3 = (j2 % 15) + 3;
        long time = date.getTime();
        if ((j2 / 15) % 32 == i) {
            d = time;
        } else {
            d = time;
            j3 -= 15;
        }
        return new Date((long) (d - ((j3 * 60.0d) * 1000.0d)));
    }

    public long getDeviceOpenTimeLength(byte[] bArr) {
        return Math.abs(((bArr[317] & 255) * 256) + (bArr[316] & 255));
    }

    public GlucoseDataAnalyzeEntity parseLibre1Data(byte[] bArr, Libre1DerivedAlgorithmParameters libre1DerivedAlgorithmParameters) {
        GlucoseDataAnalyzeEntity glucoseDataAnalyzeEntity = new GlucoseDataAnalyzeEntity();
        Log.i(TAG, "运行iOS解析算法");
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        int i = bArr[26] & 255;
        int i2 = bArr[27] & 255;
        long deviceOpenTimeLength = getDeviceOpenTimeLength(bArr);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" 运行时长：" + deviceOpenTimeLength);
        stringBuffer.append(" 当前时间：" + getDateTimeStr(date.getTime()));
        this.sensorStartTimeInMilliseconds = date.getTime() - ((60 * deviceOpenTimeLength) * 1000);
        stringBuffer.append(StringUtils.LF);
        stringBuffer.append(" 计算的开启时间：" + getDateTimeStr(this.sensorStartTimeInMilliseconds));

        List<GlucoseData> trendData = analyzeDataToBloodSugar(bArr, libre1DerivedAlgorithmParameters, 16, i, 28, deviceOpenTimeLength, false, this.sensorStartTimeInMilliseconds);
        if (trendData == null || trendData.size() == 0) {
            Log.e(TAG, "瞬感数据解析，趋势数据不存在");
            Log.i(TAG, stringBuffer.toString());
            return glucoseDataAnalyzeEntity;
        }
        long time = dateOfMostRecentHistoryValue(deviceOpenTimeLength, i2, date).getTime();
        long j = this.sensorStartTimeInMilliseconds;
        List<GlucoseData> historyData = analyzeDataToBloodSugar(bArr, libre1DerivedAlgorithmParameters, 32, i2, 124, (int) ((time - j) / 1000), true, j);
        glucoseDataAnalyzeEntity.setTrend(trendData);
        glucoseDataAnalyzeEntity.setHistory(historyData);
        glucoseDataAnalyzeEntity.setTimeStart(this.sensorStartTimeInMilliseconds);
        return glucoseDataAnalyzeEntity;
    }

    public long timeInSecondsCalculator(long j, int i) {
        return Math.abs((long) (Math.max(0L, j - i) * 60.0d));
    }

    public long timeInSecondsCalculatorHistory(long j, int i) {
        return Math.abs((long) Math.max(0.0d, j - (i * 900.0d)));
    }

    public List<GlucoseData> analyzeDataToBloodSugar(byte[] bArr, Libre1DerivedAlgorithmParameters libre1DerivedAlgorithmParameters, int i, int i2, int i3, long j, boolean z, long j2) {
        long timeInSecondsCalculator;
        ArrayList arrayList = new ArrayList();
        if (i2 == 255) {
            Log.i(TAG, "数据是空的时候index解析是255，超长索引导致崩溃");
            return arrayList;
        }
        for (int i4 = 0; i4 < i; i4++) {
            int i5 = (i2 - i4) - 1;
            if (i5 < 0) {
                i5 += i;
            }
            if (z) {
                timeInSecondsCalculator = timeInSecondsCalculatorHistory(j, i4);
            } else {
                timeInSecondsCalculator = timeInSecondsCalculator(j, i4);
            }
            int i6 = (i5 * 6) + i3;
            int i7 = i6 + 5;
            if (i7 > bArr.length) {
                Log.i(TAG, "解析瞬感失败: 情况1 firstByteToAppend：" + i3);
            } else {
                byte[] bArr2 = {bArr[i6], bArr[i6 + 1], bArr[i6 + 2], bArr[i6 + 3], bArr[i6 + 4], bArr[i7]};
                Date date = new Date(j2 + (timeInSecondsCalculator * 1000));
                if (arrayList.size() > 0 && date.getTime() >= ((GlucoseData) arrayList.get(arrayList.size() - 1)).timeStamp.getTime()) {
                    Log.i(TAG, "解析瞬感失败: 情况2");
                } else {
                    GlucoseData glucoseData = new GlucoseData();
                    if (libre1DerivedAlgorithmParameters != null) {
                        glucoseData.timeStamp = date;
                        glucoseData.glucoseLevelRaw = new LibreMeasurement(bArr2, 0.1d, 0.0d, 0, date, libre1DerivedAlgorithmParameters).temperatureAlgorithmGlucose;
                        glucoseData.cData = bArr2;
                    } else {
                        double doubleValue = Double.valueOf((((bArr2[1] & 255) * 256) + (bArr2[0] & 255)) & 8191).doubleValue();
                        if (doubleValue > 0.0d) {
                            glucoseData.timeStamp = date;
                            glucoseData.glucoseLevelRaw = doubleValue * ConstantsBloodGlucose.libreMultiplier;
                            glucoseData.cData = bArr2;
                        }
                    }
                    arrayList.add(glucoseData);
                }
            }
        }
        return arrayList;
    }
}
