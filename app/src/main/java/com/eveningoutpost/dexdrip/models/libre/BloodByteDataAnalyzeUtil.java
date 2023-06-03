package com.eveningoutpost.dexdrip.models.libre;

import static com.eveningoutpost.dexdrip.NFCReaderX.verifyTime;
import static com.eveningoutpost.dexdrip.models.LibreOOPAlgorithm.getDateTimeStr;

import android.util.Log;

import com.eveningoutpost.dexdrip.LibreAlarmReceiver;
import com.eveningoutpost.dexdrip.models.ReadingData;
import com.eveningoutpost.dexdrip.models.UserError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;

/* loaded from: classes.dex */
public class BloodByteDataAnalyzeUtil {
    public static String TAG = "BleBloodAnalyze";
    private static long sensorStartTimeInMilliseconds;

    public static void analyzeBloodByteData(byte[] bArr, boolean z) {
        GlucoseDataAnalyzeEntity parseLibre1Data;
        if (bArr == null || bArr.length == 0) {
            return;
        }
        Log.i(TAG, "血糖字节数据算法解析");
        String str = TAG;
        Log.i(str, "解析字节的长度: " + bArr.length);
        String str2 = TAG;
        Log.i(str2, "字节内容: " + ByteUtil.getStringFromByteFormatHex(bArr));


        boolean checkDeviceDataProHCRC = LibreUtils.checkDeviceDataProHCRC(bArr);
        if (checkDeviceDataProHCRC) {
            Log.i(TAG, "使用医疗版瞬感算法进行解析");
            parseLibre1Data = LibreDataParserPro.parseFRAM(bArr);
        } else {
            Log.i(TAG, "使用用户版瞬感算法进行解析");
            parseLibre1Data = new LibreDataParser().parseLibre1Data(bArr, new Libre1DerivedAlgorithmParameters(bArr, "123"));
        }
        if (parseLibre1Data == null || parseLibre1Data.getTrend() == null || parseLibre1Data.getTrend().size() == 0) {
            Log.i(TAG, "趋势数据解析为null");
            return;
        }
        List<GlucoseData> trend = parseLibre1Data.getTrend();
        List<GlucoseData> history = parseLibre1Data.getHistory();
        if (checkDeviceDataProHCRC && !z) {
            history = null;
        }
        if (trend == null || trend.size() == 0) {
            Log.i(TAG, "没有趋势数据无法进行解析");
        } else {
            callBackToXDrip(parseLibre1Data.getTimeStart(), history, trend);
        }
    }


    public static List<GlucoseData> GranularData(List<GlucoseData> list, long j) {
        if (list == null) {
            return new ArrayList();
        }
        if (list == null || list.size() < 3) {
            Log.d(TAG, "数据太少无法进行颗粒度");
            Log.i(TAG, "数据太少无法进行颗粒度");
            return list;
        }
        double[] dArr = new double[list.size()];
        double[] dArr2 = new double[list.size()];
        Collections.sort(list, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.5
            @Override // java.util.Comparator
            public int compare(GlucoseData glucoseData, GlucoseData glucoseData2) {
                return (int) (glucoseData.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
            }
        });
        long time = list.get(0).timeStamp.getTime();
        long time2 = list.get(list.size() - 1).timeStamp.getTime();
        int i = 0;
        double d = 0.0d;
        while (i < list.size()) {
            GlucoseData glucoseData = list.get(i);
            if (glucoseData.checkBloodSugarError()) {
                if (d <= 0.0d) {
                    d = 2.200000047683716d;
                }
                glucoseData.glucoseLevelRaw = d;
            }
            long time3 = glucoseData.timeStamp.getTime();
            dArr[i] = time3;
            dArr2[i] = glucoseData.glucoseLevelRaw;
            double d2 = glucoseData.glucoseLevelRaw;
            if (time2 < time3) {
                time2 = time3;
            }
            i++;
            d = d2;
        }
        if (j <= time2) {
            time2 = j;
        }
        if (list != null) {
            String str = "数据个数：" + list.size() + " 开始时间：" + getDateTimeStr(time) + " 结束时间：" + getDateTimeStr(time2);
            Log.i(TAG, str);
        }
        ArrayList arrayList = new ArrayList();
        try {
            PolynomialSplineFunction interpolate = new SplineInterpolator().interpolate(dArr, dArr2);
            while (time2 >= time) {
                arrayList.add(new GlucoseData(new Date(time2), (int) interpolate.value(time2), new byte[0]));
                time2 -= 300000L;
            }
            return arrayList;
        } catch (NonMonotonicSequenceException e) {
            Log.i(TAG, "异常信息：" + e.getMessage());
            Log.e(TAG, "血糖数据颗粒化异常,历史数据不再进行颗粒化处理");
            return list;
        }
    }

    public static GlucoseData getBloodSugarMean(List<GlucoseData> list, int i) {
        long currentTimeMillis = System.currentTimeMillis();
        GlucoseData glucoseData = new GlucoseData();
        glucoseData.timeStamp = new Date(currentTimeMillis - (((i * 5) * 60) * 1000));
        Collections.sort(list, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.4
            @Override // java.util.Comparator
            public int compare(GlucoseData glucoseData2, GlucoseData glucoseData3) {
                return (int) (glucoseData3.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
            }
        });
        int i2 = 0;
        double d = 0.0d;
        double d2 = 0.0d;
        for (GlucoseData glucoseData2 : list) {
            if (!glucoseData2.checkBloodSugarError()) {
                double d3 = glucoseData2.glucoseLevelRaw;
                if (d2 == 0.0d) {
                    d2 = d3;
                }
                if (Math.abs(d2 - d3) < 10.799999999999999d) {
                    d += glucoseData2.glucoseLevelRaw;
                    i2++;
                }
            }
        }
        if (i2 > 0) {
            glucoseData.glucoseLevelRaw = d / i2;
        }
        if (glucoseData.glucoseLevelRaw <= 0.0d) {
            glucoseData.glucoseLevelRaw = 37.79999923706055d;
        }
        return glucoseData;
    }

    private static void getBloodSugarDataFromGlucoseData(List<GlucoseData> list) {
//        ArrayList arrayList = new ArrayList();
//        if (list == null) {
//            Log.i(TAG, "血糖数据不存在");
//            Log.d(TAG, "血糖数据集合不存在,无法进行解析转化为数据");
//            return;
//        }
//        int i = 0;
//        float f = 0.0f;
//        for (GlucoseData glucoseData : list) {
//            LotanEntity transformBloodEntityFromGlucoseData = transformBloodEntityFromGlucoseData(glucoseData.timeStamp.getTime(), glucoseData.glucoseLevelRaw);
//            if (transformBloodEntityFromGlucoseData != null && transformBloodEntityFromGlucoseData.getBloodSugar() > 0.0f) {
//                if (transformBloodEntityFromGlucoseData.getPackageNumber() > 480 && !transformBloodEntityFromGlucoseData.checkBloodSugarDataError()) {
//                    if (f == 0.0f) {
//                        f = transformBloodEntityFromGlucoseData.getOriginalBloodSugar();
//                    }
//                    if (Math.abs(f - transformBloodEntityFromGlucoseData.getOriginalBloodSugar()) > 3.0f) {
//                        Log.i(TAG, "8小时内相邻的两条数据差值大于3则不能进行保存认为该数据异常");
//                        BluetoothLogUtil.writeHintMessage("8小时内相邻的两条数据差值大于3则不能进行保存认为该数据异常，数据剔除不进行保存，bloodSugarLast：" + f + " bloodSugar：" + transformBloodEntityFromGlucoseData.getOriginalBloodSugar());
//                        i++;
//                        if (i < 5) {
//                        }
//                    }
//                    f = transformBloodEntityFromGlucoseData.getOriginalBloodSugar();
//                }
//                arrayList.add(transformBloodEntityFromGlucoseData);
//            } else {
//                Log.i(TAG, "血糖转化后，血糖模型为空");
//                DataServiceYAMI.getInstance().hideUserReadBluetoothData();
//                BluetoothLogUtil.writeHintMessage("血糖转化后，血糖模型为空,");
//            }
//        }
//        LotanDbHandler.insertDeviceAbbottData(arrayList);
    }


    public static void saveBloodSugarToDB(long j, List<GlucoseData> readHistoryData, List<GlucoseData> readTrendData) {
        if (readHistoryData == null) {
            readHistoryData = new ArrayList<>();
        }
        if (readTrendData == null) {
            readTrendData = new ArrayList<>();
        }
        Log.d(TAG, "将解析的数据保存到数据库中");
        ArrayList<GlucoseData> arrayList = new ArrayList<>();
        sensorStartTimeInMilliseconds = j;
        List<GlucoseData> GranularData = GranularData(readHistoryData, System.currentTimeMillis() - 900000L);
        arrayList.addAll(GranularData);
        Collections.sort(arrayList, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.2
            @Override // java.util.Comparator
            public int compare(GlucoseData glucoseData, GlucoseData glucoseData2) {
                return (int) (glucoseData.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
            }
        });
        if (readTrendData.size() > 14) {
            arrayList.add(getBloodSugarMean(readTrendData.subList(0, 4), 0));
            arrayList.add(getBloodSugarMean(readTrendData.subList(5, 9), 1));
            arrayList.add(getBloodSugarMean(readTrendData.subList(10, 14), 2));
        }
        if (readTrendData.size() == 7) {
            arrayList.add(getBloodSugarMean(readTrendData.subList(0, 1), 0));
            arrayList.add(getBloodSugarMean(readTrendData.subList(2, 3), 1));
            arrayList.add(getBloodSugarMean(readTrendData.subList(5, 6), 2));
        }
        Collections.sort(arrayList, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.3
            @Override // java.util.Comparator
            public int compare(GlucoseData glucoseData, GlucoseData glucoseData2) {
                return (int) (glucoseData.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
            }
        });
        getBloodSugarDataFromGlucoseData(arrayList);
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < readTrendData.size(); i++) {
            stringBuffer.append(StringUtils.LF);
            stringBuffer.append("趋势数据：" + readTrendData.get(i).getPrint());
        }
        for (int i2 = 0; i2 < readHistoryData.size(); i2++) {
            stringBuffer.append(StringUtils.LF);
            stringBuffer.append("原始历史数据：" + readHistoryData.get(i2).getPrint());
        }
        for (int i3 = 0; i3 < GranularData.size(); i3++) {
            stringBuffer.append(StringUtils.LF);
            stringBuffer.append("颗粒历史数据：" + GranularData.get(i3).getPrint());
        }
        stringBuffer.append(StringUtils.LF);
        stringBuffer.append(" 历史数据的个数: " + readHistoryData.size() + " 颗粒化后的历史数据个数：" + GranularData.size());
        Log.i("BleLibre2", stringBuffer.toString());


        ReadingData readingData = new ReadingData();

        readingData.trend = new ArrayList<com.eveningoutpost.dexdrip.models.GlucoseData>();

        for (GlucoseData iData : readTrendData) {
            // Add the first object, that is the current time
            com.eveningoutpost.dexdrip.models.GlucoseData glucoseData = new com.eveningoutpost.dexdrip.models.GlucoseData();
            glucoseData.sensorTime = (int) ((iData.timeStamp.getTime() - sensorStartTimeInMilliseconds) / 1000 / 60);
            glucoseData.realDate = iData.timeStamp.getTime();
            glucoseData.glucoseLevel = (int) iData.glucoseLevelRaw;
            glucoseData.glucoseLevelRaw = (int) iData.glucoseLevelRaw;

            verifyTime(glucoseData.sensorTime, "LibreOOPAlgorithm", null);
            readingData.trend.add(glucoseData);
        }
        // TODO: Add here data of last 10 minutes or whatever.


        // Add the historic data
        readingData.history = new ArrayList<com.eveningoutpost.dexdrip.models.GlucoseData>();
        for (GlucoseData historicBg : GranularData) {
            com.eveningoutpost.dexdrip.models.GlucoseData glucoseData = new com.eveningoutpost.dexdrip.models.GlucoseData();
            glucoseData.realDate = historicBg.timeStamp.getTime();
            glucoseData.glucoseLevel = (int) historicBg.glucoseLevelRaw;
            glucoseData.glucoseLevelRaw = (int) historicBg.glucoseLevelRaw;
            readingData.history.add(glucoseData);
        }

        // Add the current point again. This is needed in order to have the last gaps closed.
        // TODO: Base this on real BG values.
//        glucoseData = new com.eveningoutpost.dexdrip.models.GlucoseData();
//        glucoseData.realDate = oOPResults.timestamp;
//        glucoseData.glucoseLevel = (int) (oOPResults.currentBg);
//        glucoseData.glucoseLevelRaw = (int) (oOPResults.currentBg);
//        readingData.history.add(glucoseData);

        UserError.Log.d(TAG, "handleData Created the following object " + readingData.toString());
        LibreAlarmReceiver.CalculateFromDataTransferObject(readingData, false, false);

    }


    public static void callBackToXDrip(long timeStart, List<GlucoseData> history, List<GlucoseData> trend) {
        Log.d(TAG, "开始时间TimeStamp：" + timeStart);
        Log.d(TAG, "开始时间：" + getDateTimeStr(timeStart));
        saveBloodSugarToDB(timeStart, history, trend);
    }
//
//    public static void saveBloodSugarDataFromDexCom(List<GlucoseData> list) {
//        Collections.sort(list, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.1
//            @Override // java.util.Comparator
//            public int compare(GlucoseData glucoseData, GlucoseData glucoseData2) {
//                return (int) (glucoseData2.timeStamp.getTime() - glucoseData.timeStamp.getTime());
//            }
//        });
//        ArrayList arrayList = new ArrayList();
//        long j = 0;
//        for (GlucoseData glucoseData : list) {
//            if (Math.abs(glucoseData.timeStamp.getTime() - j) > 258000.0d) {
//                j = glucoseData.timeStamp.getTime();
//                arrayList.add(glucoseData);
//            }
//        }
//        getBloodSugarDataFromGlucoseData(arrayList);
//    }
//
//    public static void saveBloodSugarToDB(long j, List<GlucoseData> list, List<GlucoseData> list2) {
//        if (list == null) {
//            list = new ArrayList<>();
//        }
//        if (list2 == null) {
//            list2 = new ArrayList<>();
//        }
//        BluetoothLogUtil.writeHintMessage("将解析的数据保存到数据库中");
//        ArrayList arrayList = new ArrayList();
//        sensorStartTimeInMilliseconds = j;
//        List<GlucoseData> GranularData = GranularData(list, System.currentTimeMillis() - PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS);
//        arrayList.addAll(GranularData);
//        Collections.sort(arrayList, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.2
//            @Override // java.util.Comparator
//            public int compare(GlucoseData glucoseData, GlucoseData glucoseData2) {
//                return (int) (glucoseData.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
//            }
//        });
//        if (list2.size() > 14) {
//            arrayList.add(getBloodSugarMean(list2.subList(0, 4), 0));
//            arrayList.add(getBloodSugarMean(list2.subList(5, 9), 1));
//            arrayList.add(getBloodSugarMean(list2.subList(10, 14), 2));
//        }
//        if (list2.size() == 7) {
//            arrayList.add(getBloodSugarMean(list2.subList(0, 1), 0));
//            arrayList.add(getBloodSugarMean(list2.subList(2, 3), 1));
//            arrayList.add(getBloodSugarMean(list2.subList(5, 6), 2));
//        }
//        Collections.sort(arrayList, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.3
//            @Override // java.util.Comparator
//            public int compare(GlucoseData glucoseData, GlucoseData glucoseData2) {
//                return (int) (glucoseData.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
//            }
//        });
//        getBloodSugarDataFromGlucoseData(arrayList);
//        StringBuffer stringBuffer = new StringBuffer();
//        for (int i = 0; i < list2.size(); i++) {
//            stringBuffer.append(StringUtils.LF);
//            stringBuffer.append("趋势数据：" + list2.get(i).getPrint());
//        }
//        for (int i2 = 0; i2 < list.size(); i2++) {
//            stringBuffer.append(StringUtils.LF);
//            stringBuffer.append("原始历史数据：" + list.get(i2).getPrint());
//        }
//        for (int i3 = 0; i3 < GranularData.size(); i3++) {
//            stringBuffer.append(StringUtils.LF);
//            stringBuffer.append("颗粒历史数据：" + GranularData.get(i3).getPrint());
//        }
//        if (list != null) {
//            stringBuffer.append(StringUtils.LF);
//            stringBuffer.append(" 历史数据的个数: " + list.size() + " 颗粒化后的历史数据个数：" + GranularData.size());
//        }
//        Log.i("BleLibre2", stringBuffer.toString());
//    }
//
//    public static boolean checkBloodDeviceAbbottTimeOverdue() {
//        DeviceEntity deviceInfo = Global.getDeviceInfo();
//        if (deviceInfo != null) {
//            int expired_libre_pay = Global.getUserInfo().getCloud_control().getExpired_libre_pay();
//            long real_sensor_start_time = deviceInfo.getReal_sensor_start_time() * 1000;
//            int use_expired_sensor = deviceInfo.getUse_expired_sensor();
//            long currentTimeMillis = System.currentTimeMillis();
//            String str = TAG;
//            Log.i(str, "time: " + TimeUtil.getDateTimeStr(currentTimeMillis));
//            String str2 = TAG;
//            Log.i(str2, "realSensorStartTime: " + TimeUtil.getDateTimeStr(real_sensor_start_time));
//            String str3 = TAG;
//            Log.i(str3, "expiredLibrePay: " + expired_libre_pay);
//            String str4 = TAG;
//            Log.i(str4, "useExpiredSensor: " + use_expired_sensor);
//            if (expired_libre_pay == 1 && use_expired_sensor == 0 && currentTimeMillis - real_sensor_start_time > 1296000000) {
//                Log.i(TAG, "用户未开通使用过期瞬感探头服务，并且当前探头过期了");
//                return true;
//            }
//            return false;
//        }
//        return false;
//    }

    public static boolean checkBloodDeviceStatusStart(byte[] bArr) {
        return bArr == null || bArr.length < 176 || bArr[4] != 1;
    }
//
//    public static GlucoseData getBloodSugarMean(List<GlucoseData> list, int i) {
//        long currentTimeMillis = System.currentTimeMillis();
//        GlucoseData glucoseData = new GlucoseData();
//        glucoseData.timeStamp = new Date(currentTimeMillis - (((i * 5) * 60) * 1000));
//        Collections.sort(list, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.4
//            @Override // java.util.Comparator
//            public int compare(GlucoseData glucoseData2, GlucoseData glucoseData3) {
//                return (int) (glucoseData3.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
//            }
//        });
//        int i2 = 0;
//        double d = 0.0d;
//        double d2 = 0.0d;
//        for (GlucoseData glucoseData2 : list) {
//            if (!glucoseData2.checkBloodSugarError()) {
//                double d3 = glucoseData2.glucoseLevelRaw;
//                if (d2 == 0.0d) {
//                    d2 = d3;
//                }
//                if (Math.abs(d2 - d3) < 10.799999999999999d) {
//                    d += glucoseData2.glucoseLevelRaw;
//                    i2++;
//                }
//            }
//        }
//        if (i2 > 0) {
//            glucoseData.glucoseLevelRaw = d / i2;
//        }
//        if (glucoseData.glucoseLevelRaw <= 0.0d) {
//            glucoseData.glucoseLevelRaw = 37.79999923706055d;
//        }
//        return glucoseData;
//    }
//
//    public static List<GlucoseData> GranularData(List<GlucoseData> list, long j) {
//        if (list == null) {
//            return new ArrayList();
//        }
//        if (list == null || list.size() < 3) {
//            BluetoothLogUtil.writeHintMessage("数据太少无法进行颗粒度");
//            Log.i(TAG, "数据太少无法进行颗粒度");
//            return list;
//        }
//        double[] dArr = new double[list.size()];
//        double[] dArr2 = new double[list.size()];
//        Collections.sort(list, new Comparator<GlucoseData>() { // from class: bloodByteAnalyze.BloodByteDataAnalyzeUtil.5
//            @Override // java.util.Comparator
//            public int compare(GlucoseData glucoseData, GlucoseData glucoseData2) {
//                return (int) (glucoseData.timeStamp.getTime() - glucoseData2.timeStamp.getTime());
//            }
//        });
//        long time = list.get(0).timeStamp.getTime();
//        long time2 = list.get(list.size() - 1).timeStamp.getTime();
//        int i = 0;
//        double d = 0.0d;
//        while (i < list.size()) {
//            GlucoseData glucoseData = list.get(i);
//            if (glucoseData.checkBloodSugarError()) {
//                if (d <= 0.0d) {
//                    d = 2.200000047683716d;
//                }
//                glucoseData.glucoseLevelRaw = d;
//            }
//            long time3 = glucoseData.timeStamp.getTime();
//            dArr[i] = time3;
//            dArr2[i] = glucoseData.glucoseLevelRaw;
//            double d2 = glucoseData.glucoseLevelRaw;
//            if (time2 < time3) {
//                time2 = time3;
//            }
//            i++;
//            d = d2;
//        }
//        if (j <= time2) {
//            time2 = j;
//        }
//        if (list != null) {
//            String str = "数据个数：" + list.size() + " 开始时间：" + TimeUtil.getDateTimeStr(time) + " 结束时间：" + TimeUtil.getDateTimeStr(time2);
//            Log.i(TAG, str);
//            BluetoothLogUtil.writeHintMessage(str);
//        }
//        ArrayList arrayList = new ArrayList();
//        try {
//            PolynomialSplineFunction interpolate = new SplineInterpolator().interpolate(dArr, dArr2);
//            while (time2 >= time) {
//                arrayList.add(new GlucoseData(new Date(time2), (int) interpolate.value(time2), new byte[0]));
//                time2 -= PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS;
//            }
//            return arrayList;
//        } catch (NonMonotonicSequenceException e) {
//            Log.i(TAG, "异常信息：" + e.getMessage());
//            BluetoothLogUtil.writeHintMessage("血糖数据颗粒化异常,历史数据不再进行颗粒化处理");
//            DataServiceYAMI.getInstance().hideUserReadBluetoothData();
//            return list;
//        }
//    }
//
//    private static void getBloodSugarDataFromGlucoseData(List<GlucoseData> list) {
//        ArrayList arrayList = new ArrayList();
//        if (list == null) {
//            Log.i(TAG, "血糖数据不存在");
//            BluetoothLogUtil.writeHintMessage("血糖数据集合不存在,无法进行解析转化为糖动数据");
//            DataServiceYAMI.getInstance().hideUserReadBluetoothData();
//            return;
//        }
//        int i = 0;
//        float f = 0.0f;
//        for (GlucoseData glucoseData : list) {
//            LotanEntity transformBloodEntityFromGlucoseData = transformBloodEntityFromGlucoseData(glucoseData.timeStamp.getTime(), glucoseData.glucoseLevelRaw);
//            if (transformBloodEntityFromGlucoseData != null && transformBloodEntityFromGlucoseData.getBloodSugar() > 0.0f) {
//                if (transformBloodEntityFromGlucoseData.getPackageNumber() > 480 && !transformBloodEntityFromGlucoseData.checkBloodSugarDataError()) {
//                    if (f == 0.0f) {
//                        f = transformBloodEntityFromGlucoseData.getOriginalBloodSugar();
//                    }
//                    if (Math.abs(f - transformBloodEntityFromGlucoseData.getOriginalBloodSugar()) > 3.0f) {
//                        Log.i(TAG, "8小时内相邻的两条数据差值大于3则不能进行保存认为该数据异常");
//                        BluetoothLogUtil.writeHintMessage("8小时内相邻的两条数据差值大于3则不能进行保存认为该数据异常，数据剔除不进行保存，bloodSugarLast：" + f + " bloodSugar：" + transformBloodEntityFromGlucoseData.getOriginalBloodSugar());
//                        i++;
//                        if (i < 5) {
//                        }
//                    }
//                    f = transformBloodEntityFromGlucoseData.getOriginalBloodSugar();
//                }
//                arrayList.add(transformBloodEntityFromGlucoseData);
//            } else {
//                Log.i(TAG, "血糖转化后，血糖模型为空");
//                DataServiceYAMI.getInstance().hideUserReadBluetoothData();
//                BluetoothLogUtil.writeHintMessage("血糖转化后，血糖模型为空,");
//            }
//        }
//        LotanDbHandler.insertDeviceAbbottData(arrayList);
//    }
//
//    public static LotanEntity transformBloodEntityFromGlucoseData(long j, double d) {
//        long currentTimeMillis = System.currentTimeMillis();
//        LotanEntity lotanEntity = new LotanEntity();
//        if (j > currentTimeMillis) {
//            Log.i(TAG, "血糖数据产生时间大于当前时间");
//            BluetoothLogUtil.writeHintMessage("将血糖数据进行解析时，血糖数据产生时间大于当前时间，血糖数据时间：" + TimeUtil.getDateTimeStr(j) + " glucoseLevel：" + d + "  timeNew：" + TimeUtil.getDateTimeStr(currentTimeMillis));
//            DataServiceYAMI.getInstance().hideUserReadBluetoothData();
//            return null;
//        }
//        lotanEntity.setCreateTime(j / 1000);
//        lotanEntity.setPeriodId(Global.getPeriodId());
//        long deviceLibreBindTime = Global.getDeviceLibreBindTime();
//        if (deviceLibreBindTime <= 0) {
//            deviceLibreBindTime = sensorStartTimeInMilliseconds;
//        }
//        int i = (int) ((j - deviceLibreBindTime) / DateUtils.MILLIS_PER_MINUTE);
//        lotanEntity.setPackageNumber(i);
//        float convertGlucoseUnitsValue = convertGlucoseUnitsValue(d);
//        lotanEntity.setBloodSugar(convertGlucoseUnitsValue);
//        lotanEntity.setOriginalBloodSugar(convertGlucoseUnitsValue);
//        lotanEntity.setUserId(Global.getUserId());
//        lotanEntity.setCommand(4);
//        lotanEntity.setCurrent((int) d);
//        lotanEntity.setOriginalCurrent(lotanEntity.getCurrent());
//        if (i < 60) {
//            lotanEntity.setDataType(1);
//        } else {
//            lotanEntity.setDataType(2);
//        }
//        return lotanEntity;
//    }
//
//    public static float convertGlucoseUnitsValue(double d) {
//        return CommonUtil.getValueFormFloat((float) (d / 18.0d));
//    }
//
//    public static List<LotanEntity> smootherBloodSugar(List<LotanEntity> list) {
//        if (PrefManager.getInstance().getBloodSugarLineChartSmooth()) {
//            if (list == null) {
//                return null;
//            }
//            SavitzkyGolaySmoothUtil.SavitzkyGolaySmoothable[] savitzkyGolaySmoothableArr = new SavitzkyGolaySmoothUtil.SavitzkyGolaySmoothable[list.size()];
//            for (int i = 0; i < list.size(); i++) {
//                savitzkyGolaySmoothableArr[i] = new SavitzkyGolaySmoothUtil.SavitzkyGolaySmoothable(list.get(i).getBloodSugar());
//            }
//            SavitzkyGolaySmoothUtil.smoothSavitzkyGolayQuaDratic(savitzkyGolaySmoothableArr);
//            for (int i2 = 0; i2 < list.size(); i2++) {
//                list.get(i2).setBloodSugar(CommonUtil.getValueFormFloat((float) savitzkyGolaySmoothableArr[i2].getValue()));
//            }
//            return list;
//        }
//        return list;
//    }
//
//    public static void testBloodAnalyze() {
//        analyzeBloodByteData(ByteUtil.getByteFromStringFormat("A01B000003000000000000000000000000000000000000004A454D413132392D54313738384EE512C9AA10005C0CC04E140396805A00EDA6127EDA1D052A4C738202CB1B0000000072A52B380B00BE032AC61DE5590126C61D115A0122C61D155A011CC61D255A010FC61D355A0106C61D415A01F5C51D1D5A01EBC51D095A01DCC51D155A01D5C51DF15901C5C51D195A0166C61D6D99015AC61D9159014DC61DB9590145C61DCD590137C61DE9590148C41D755A01C0C51DA95A018EC71DE9990171C81DB559012CC91D69990148C91D8D9901BDC91D51590118CA1D1D9901A7CA1DF59801C9CA1D159901C2CA1D2199019CCA1D5599017CCA1DE9980165CA1D8999019FC91D5D9A01EBC81DD19901DBC71D315B0148C61D3D5B0140C51D695A01CBC41D499A016DC41D291C010EC41DAD1C0143C41D4D1C0144C41DB91C01C0C31DF11C0131C31D751B01D0C21D895A01B4C21D015A01FBC21DD1590176C31DE9590176C31DD55901BCC21D79990130C21D7999011DC21DA15901FCC11D69"), true);
//    }
//
//    private static void logMessage(String str) {
//        String str2 = TAG;
//        Log.i(str2, str + StringUtils.LF);
//    }
}
