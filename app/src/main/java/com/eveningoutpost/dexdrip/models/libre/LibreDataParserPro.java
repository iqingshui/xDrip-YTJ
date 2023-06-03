package com.eveningoutpost.dexdrip.models.libre;


import static com.eveningoutpost.dexdrip.models.LibreOOPAlgorithm.getDateTimeStr;

import android.util.Log;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

/* loaded from: classes.dex */
public class LibreDataParserPro {
    private static String TAG = BloodByteDataAnalyzeUtil.TAG;

    public static GlucoseDataAnalyzeEntity parseFRAM(byte[] bArr) {
        int i;
        int i2;
        int i3 = 0;
        long j;
        GlucoseDataAnalyzeEntity glucoseDataAnalyzeEntity = new GlucoseDataAnalyzeEntity();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Log.i(TAG, "开始解析医疗版瞬感数据");
        long currentTimeMillis = System.currentTimeMillis();
        if (bArr.length < 176) {
            Log.i(TAG, "数据长度不够");
            return glucoseDataAnalyzeEntity;
        }
        int i4 = 0;
        int ByteArreyToUnsignedShort = ByteUtil.ByteArreyToUnsignedShort(bArr, 74, false);
        long j2 = currentTimeMillis - ((ByteArreyToUnsignedShort * 60) * 1000);
        Log.i(TAG, "佩戴时间：" + getDateTimeStr(j2));
        int ByteArreyToUnsignedShort2 = ByteUtil.ByteArreyToUnsignedShort(bArr, 76, false);
        int ByteArreyToUnsignedShort3 = ByteUtil.ByteArreyToUnsignedShort(bArr, 78, false);
        Log.i(TAG, "trendIndex: " + ByteArreyToUnsignedShort2);
        Log.i(TAG, "historyIndex: " + ByteArreyToUnsignedShort3);
        Log.i(TAG, "解析趋势数据 ");
        int i5 = 0;
        while (true) {
            i = 14;
            if (i5 >= 15) {
                break;
            }
            int i6 = (ByteArreyToUnsignedShort2 - 1) - i5;
            if (i6 < 0) {
                i6 += 16;
            }
            long j3 = j2;
            GlucoseData glucoseData = new GlucoseData(new Date(currentTimeMillis - ((i5 * 60) * 1000)), readBits(bArr, (i6 * 6) + 80, i4, 14) & 8191, new byte[0]);
            Log.i(TAG, glucoseData.getPrint());
            arrayList.add(glucoseData);
            i5++;
            j2 = j3;
            i4 = 0;
        }
        long j4 = j2;
        int i7 = ByteArreyToUnsignedShort - 3;
        long j5 = (i7 % 15) + 3;
        long j6 = (i7 / 15) % 32 == ByteArreyToUnsignedShort3 ? currentTimeMillis + (DateUtils.MILLIS_PER_MINUTE * j5) : currentTimeMillis + ((j5 - 15) * DateUtils.MILLIS_PER_MINUTE);
        Log.i(TAG, "解析历史数据 ");
        int i8 = 0;
        while (i8 < 31) {
            int i9 = (((ByteArreyToUnsignedShort3 - 1) - i8) * 6) + 176;
            if (bArr.length >= i9 + 6 || (i9 = (((((bArr.length - 176) / 6) - 1) - i8) * 6) + 176) >= 176) {
                i2 = ByteArreyToUnsignedShort3;
                double readBits = readBits(bArr, i9, 0, i) & 8191;
                j = j5;
                long j7 = ((int) ((((long) ByteArreyToUnsignedShort) - j5) - ((long) (i8 * 15)))) > -1 ? j6 - ((i3 * 60) * 1000) : j4;
                if (readBits > 0.0d) {
                    GlucoseData glucoseData2 = new GlucoseData(new Date(j7), readBits, new byte[0]);
                    Log.i(TAG, glucoseData2.getPrint());
                    arrayList2.add(glucoseData2);
                    i8++;
                    ByteArreyToUnsignedShort3 = i2;
                    j5 = j;
                    i = 14;
                }
            } else {
                j = j5;
                i2 = ByteArreyToUnsignedShort3;
            }
            i8++;
            ByteArreyToUnsignedShort3 = i2;
            j5 = j;
            i = 14;
        }
        Log.i(TAG, "历史数据的个数: " + arrayList2.size());
        glucoseDataAnalyzeEntity.setTrend(arrayList);
        glucoseDataAnalyzeEntity.setHistory(arrayList2);
        glucoseDataAnalyzeEntity.setTimeStart(j4);
        return glucoseDataAnalyzeEntity;
    }

    private static int readBits(byte[] bArr, int i, int i2, int i3) {
        if (i3 == 0) {
            return 0;
        }
        int i4 = 0;
        for (int i5 = 0; i5 < i3; i5++) {
            int i6 = (i * 8) + i2 + i5;
            int floor = (int) Math.floor(i6 / 8);
            int i7 = i6 % 8;
            if (floor >= bArr.length) {
                return 0;
            }
            if (i6 >= 0 && ((bArr[floor] >> i7) & 1) == 1) {
                i4 |= 1 << i5;
            }
        }
        return i4 / 10;
    }
}