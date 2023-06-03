package com.eveningoutpost.dexdrip.models.libre;

import android.text.TextUtils;
import android.util.Log;
import okhttp3.internal.ws.WebSocketProtocol;

/* loaded from: classes.dex */
public class LibreUtils {
    private static final String TAG = "BleLibre";
    public static final byte cmdLibre1H = 112;
    public static final byte cmdLibre1_1 = -33;
    public static final byte cmdLibre1_2 = -94;
    public static final byte cmdLibre2 = -99;
    public static final long[] crc16table = {0, 4489, 8978, 12955, 17956, 22445, 25910, 29887, 35912, 40385, 44890, 48851, 51820, 56293, 59774, 63735, 4225, 264, 13203, 8730, 22181, 18220, 30135, 25662, 40137, 36160, 49115, 44626, 56045, 52068, 63999, 59510, 8450, 12427, 528, 5017, 26406, 30383, 17460, 21949, 44362, 48323, 36440, 40913, 60270, 64231, 51324, 55797, 12675, 8202, 4753, 792, 30631, 26158, 21685, 17724, 48587, 44098, 40665, 36688, 64495, 60006, 55549, 51572, 16900, 21389, 24854, 28831, 1056, 5545, 10034, 14011, 52812, 57285, 60766, 64727, 34920, 39393, 43898, 47859, 21125, 17164, 29079, 24606, 5281, 1320, 14259, 9786, 57037, 53060, 64991, 60502, 39145, 35168, 48123, 43634, 25350, 29327, 16404, 20893, 9506, 13483, 1584, 6073, 61262, 65223, 52316, 56789, 43370, 47331, 35448, 39921, 29575, 25102, 20629, 16668, 13731, 9258, 5809, 1848, 65487, 60998, 56541, 52564, 47595, 43106, 39673, 35696, 33800, 38273, 42778, 46739, 49708, 54181, 57662, 61623, 2112, 6601, 11090, 15067, 20068, 24557, 28022, 31999, 38025, 34048, 47003, 42514, 53933, 49956, 61887, 57398, 6337, 2376, 15315, 10842, 24293, 20332, 32247, 27774, 42250, 46211, 34328, 38801, 58158, 62119, 49212, 53685, 10562, 14539, 2640, 7129, 28518, 32495, 19572, 24061, 46475, 41986, 38553, 34576, 62383, 57894, 53437, 49460, 14787, 10314, 6865, 2904, 32743, 28270, 23797, 19836, 50700, 55173, 58654, 62615, 32808, 37281, 41786, 45747, 19012, 23501, 26966, 30943, 3168, 7657, 12146, 16123, 54925, 50948, 62879, 58390, 37033, 33056, 46011, 41522, 23237, 19276, 31191, 26718, 7393, 3432, 16371, 11898, 59150, 63111, 50204, 54677, 41258, 45219, 33336, 37809, 27462, 31439, 18516, 23005, 11618, 15595, 3696, 8185, 63375, 58886, 54429, 50452, 45483, 40994, 37561, 33584, 31687, 27214, 22741, 18780, 15843, 11370, 7921, 3960};
    public static final int typeLibre1 = 0;
    public static final int typeLibre1H = 1;
    public static final int typeLibre2 = 2;
    public static final int typeUnknown = -1;

//    public static String decodeSerialNumberKey(byte[] bArr) {
//        if (bArr.length < 8) {
//            return "";
//        }
//        byte[] bArr2 = new byte[11];
//        System.arraycopy(bArr, 0, bArr2, 3, 8);
//        String decodeSerialNumber = decodeSerialNumber(bArr2);
//        StringBuffer stringBuffer = new StringBuffer();
//        String appAbbottFirstDataByte = PrefManager.getInstance().getAppAbbottFirstDataByte();
//        String appAbbottSecondDataByte = PrefManager.getInstance().getAppAbbottSecondDataByte();
//        stringBuffer.append(appAbbottFirstDataByte);
//        stringBuffer.append(appAbbottSecondDataByte);
//        byte[] hexStringToByteArray = Utils.hexStringToByteArray(stringBuffer.toString());
//        String pathInfoOfAbbottDevice = PrefManager.getInstance().getPathInfoOfAbbottDevice();
//        boolean checkDeviceDataProHCRC = checkDeviceDataProHCRC(hexStringToByteArray);
//        Log.i(TAG, "是否是医疗版探头: " + checkDeviceDataProHCRC);
//        if (checkDeviceDataProHCRC && !TextUtils.isEmpty(decodeSerialNumber)) {
//            decodeSerialNumber = "1" + decodeSerialNumber.substring(1);
//        }
//        if (TextUtils.isEmpty(pathInfoOfAbbottDevice) || checkDeviceType(ByteUtil.getByteFromStringFormat(pathInfoOfAbbottDevice)) != 2) {
//            return decodeSerialNumber;
//        }
//        return "3" + decodeSerialNumber.substring(1);
//    }

    public static boolean checkDeviceMedical(byte[] bArr) {
        return checkDeviceType(bArr) == 1;
    }

    public static int checkDeviceType(byte[] bArr) {
        if (bArr == null || bArr.length < 2) {
            return -1;
        }
        byte b = bArr[0];
        if (b != -99) {
            if (b == -94 || b == -33) {
                return 0;
            }
            return b != 112 ? -1 : 1;
        }
        return 2;
    }

    public static boolean checkDeviceDataProHCRC(byte[] bArr) {
        Log.i(TAG, "CRC校验医疗版瞬感探头: ");
        boolean z = false;
        if (bArr != null && bArr.length >= 176) {
            boolean computeCRC16 = CRC.computeCRC16(bArr, 0, 40);
            boolean computeCRC162 = CRC.computeCRC16(bArr, 72, 104);
            boolean computeCRC163 = CRC.computeCRC16(bArr, 40, 32);
            if (computeCRC162 && computeCRC163 && computeCRC16) {
                z = true;
            }
            Log.i(TAG, "checkHeader: " + computeCRC16 + " checkData：" + computeCRC162 + " checkFooter：" + computeCRC163);
            StringBuilder sb = new StringBuilder();
            sb.append("CRC校验医疗版瞬感探头: ");
            sb.append(z);
            Log.i(TAG, sb.toString());
        }
        return z;
    }

    public static boolean checkDeviceDataCRC(byte[] bArr) {
        Log.i(TAG, "CRC校验零售版瞬感探头: ");
        boolean z = false;
        if (bArr != null && bArr.length >= 344) {
            boolean computeCRC16 = CRC.computeCRC16(bArr, 0, 24);
            boolean computeCRC162 = CRC.computeCRC16(bArr, 24, 296);
            boolean computeCRC163 = CRC.computeCRC16(bArr, 320, 24);
            if (computeCRC162 && computeCRC163 && computeCRC16) {
                z = true;
            }
            Log.i(TAG, "checkHeader: " + computeCRC16 + " checkData：" + computeCRC162 + " checkFooter：" + computeCRC163);
            StringBuilder sb = new StringBuilder();
            sb.append("CRC校验零售版瞬感探头: ");
            sb.append(z);
            Log.i(TAG, sb.toString());
        }
        return z;
    }

    public static boolean isSensorReady(byte b) {
        String str;
        boolean z = true;
        switch (b) {
            case 1:
                str = "not yet started";
                z = false;
                break;
            case 2:
                str = "starting";
                break;
            case 3:
                str = "ready";
                break;
            case 4:
                str = "expired";
                z = false;
                break;
            case 5:
                str = "shutdown";
                z = false;
                break;
            case 6:
                str = "in failure";
                z = false;
                break;
            default:
                str = "in an unknown state";
                z = false;
                break;
        }
        Log.i(TAG, "Sensor status is: " + str);
        if (!z) {
            Log.e(TAG, "Can't use this sensor as it is " + str);
        }
        return z;
    }

    public static String decodeSerialNumber(byte[] bArr) {
        String[] strArr = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "T", "U", "V", "W", "X", "Y", "Z"};
        byte[] bArr2 = {0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 2; i < 8; i++) {
            bArr2[i - 2] = bArr[10 - i];
        }
        bArr2[6] = 0;
        bArr2[7] = 0;
        String str = "";
        for (int i2 = 0; i2 < 8; i2++) {
            str = str + String.format("%8s", Integer.toBinaryString(bArr2[i2] & 255)).replace(' ', '0');
        }
        char[] cArr = {0, 0, 0, 0, 0};
        String str2 = "0";
        for (int i3 = 0; i3 < 10; i3++) {
            for (int i4 = 0; i4 < 5; i4++) {
                cArr[i4] = str.charAt((i3 * 5) + i4);
            }
            int i5 = ((cArr[0] - '0') * 16) + ((cArr[1] - '0') * 8) + ((cArr[2] - '0') * 4) + ((cArr[3] - '0') * 2) + ((cArr[4] - '0') * 1);
            str2 = str2 + strArr[i5];
        }
        Log.e(TAG, "decodeSerialNumber=" + str2);
        return str2;
    }

    public static boolean verify(byte[] bArr) {
        if (bArr.length < 176) {
            Log.e(TAG, "Must have at least 344 bytes for libre data");
            return false;
        }
        return true;
    }

    static boolean CheckCRC16(byte[] bArr, int i, int i2) {
        return computeCRC16(bArr, i, i2) == ((long) (((bArr[i + 1] & 255) * 256) + (bArr[i] & 255)));
    }

    public static long computeCRC16(byte[] bArr, int i, int i2) {
        long j = 65535;
        for (int i3 = i + 2; i3 < i + i2; i3++) {
            j = crc16table[((int) (j ^ (bArr[i3] & 255))) & 255] ^ (j >> 8);
        }
        long j2 = 0;
        for (int i4 = 0; i4 < 16; i4++) {
            j2 = (j2 << 1) | (1 & j);
            j >>= 1;
        }
        return j2;
    }
}
