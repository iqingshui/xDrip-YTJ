//package com.eveningoutpost.dexdrip.cgm;
//
//import android.content.Context;
//import android.util.Log;
//import com.abbottdiabetescare.flashglucose.sensorabstractionservice.AlarmConfiguration;
//import com.abbottdiabetescare.flashglucose.sensorabstractionservice.ApplicationRegion;
//import com.abbottdiabetescare.flashglucose.sensorabstractionservice.NonActionableConfiguration;
//import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingException;
//import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingNative;
//import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.DataProcessingOutputs;
//import com.eveningoutpost.dexdrip.localeTasker.TaskerPlugin;
//import com.eveningoutpost.dexdrip.models.OOPResults;
//import com.eveningoutpost.dexdrip.models.OOPResultsContainer;
//
//import java.util.Arrays;
//
///* loaded from: classes.dex */
//public class AlgorithmRunner {
//    static final String TAG = "OOPAlgorithm";
//
//    public static OOPResults RunAlgorithm(long timestamp, Context context, byte[] bArr, byte[] bArr2) {
//        byte[] bArr3;
//        DataProcessingNative dataProcessingNative = new DataProcessingNative(1095774808);
//        dataProcessingNative.initialize(new MyContextWrapper(context));
//        boolean isPatchSupported = dataProcessingNative.isPatchSupported(new byte[]{-33, 0, 0, 1, 1, 2}, ApplicationRegion.LEVEL_1);
//        Log.e(TAG, "data_processing_native.isPatchSupported11 returned " + isPatchSupported);
//        if (!isPatchSupported) {
//            return new OOPResults(timestamp, -1, 0, null);
//        }
//        AlarmConfiguration alarmConfiguration = new AlarmConfiguration(70, 180);
//        NonActionableConfiguration nonActionableConfiguration = new NonActionableConfiguration(true, true, 0, 40, 500, -2.0d, 2.0d);
//        if (bArr2 == null) {
//            byte[] bArr4 = {-1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//            bArr3 = Arrays.copyOf(bArr4, bArr4.length);
//        } else {
//            bArr3 = bArr2;
//        }
//        try {
//            DataProcessingOutputs processScan = dataProcessingNative.processScan(alarmConfiguration, nonActionableConfiguration, bArr, 236458825, 236734356, TaskerPlugin.Setting.REQUESTED_TIMEOUT_MS_NEVER, bArr3);
//            if (processScan == null) {
//                return new OOPResults(timestamp, -3, 0, null);
//            }
//            OOPResults oOPResults = new OOPResults(timestamp, processScan.getAlgorithmResults().getRealTimeGlucose().getValue(), processScan.getAlgorithmResults().getRealTimeGlucose().getId(), processScan.getAlgorithmResults().getTrendArrow());
//            if (processScan.getAlgorithmResults().getHistoricGlucose() != null) {
//                oOPResults.setHistoricBg(processScan.getAlgorithmResults().getHistoricGlucose());
//            } else {
//                Log.e(TAG, "getAlgorithmResults.getHistoricGlucose() returned null");
//            }
//            return oOPResults;
//        } catch (DataProcessingException unused) {
//            return new OOPResults(timestamp, -2, 0, null);
//        }
//    }
//
//    public static String getPackageCodePathNoCreate(Context context) {
//        return MyContextWrapper.getPackageCodePathNoCreate(context);
//    }
//}