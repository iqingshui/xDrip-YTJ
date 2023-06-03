package com.eveningoutpost.dexdrip.models.libre;

import android.text.TextUtils;
import android.util.Log;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ByteUtil {
    public static byte[] IntToByteArrey(int i, boolean z) {
        byte[] bArr = new byte[4];
        if (z) {
            bArr[3] = (byte) (i & 255);
            bArr[2] = (byte) ((i >> 8) & 255);
            bArr[1] = (byte) ((i >> 16) & 255);
            bArr[0] = (byte) ((i >> 24) & 255);
        } else {
            bArr[0] = (byte) (i & 255);
            bArr[1] = (byte) ((i >> 8) & 255);
            bArr[2] = (byte) ((i >> 16) & 255);
            bArr[3] = (byte) ((i >> 24) & 255);
        }
        return bArr;
    }

    public static byte[] ShortToByteArrey(short s, boolean z) {
        byte[] bArr = new byte[2];
        if (z) {
            bArr[1] = (byte) (s & 255);
            bArr[0] = (byte) ((s >> 8) & 255);
        } else {
            bArr[0] = (byte) (s & 255);
            bArr[1] = (byte) ((s >> 8) & 255);
        }
        return bArr;
    }

    public static byte[] getByteDataFromUInt16(short s, boolean z) {
        byte[] bArr = new byte[2];
        if (z) {
            bArr[1] = (byte) (s & 255);
            bArr[0] = (byte) ((s >> 8) & 255);
        } else {
            bArr[0] = (byte) (s & 255);
            bArr[1] = (byte) ((s >> 8) & 255);
        }
        return bArr;
    }

    public static byte[] getByteDataFromUInt32(long j, boolean z) {
        byte[] bArr = new byte[4];
        if (z) {
            bArr[3] = (byte) (j & 255);
            bArr[2] = (byte) ((j >> 8) & 255);
            bArr[1] = (byte) ((j >> 16) & 255);
            bArr[0] = (byte) ((j >> 24) & 255);
        } else {
            bArr[0] = (byte) (j & 255);
            bArr[1] = (byte) ((j >> 8) & 255);
            bArr[2] = (byte) ((j >> 16) & 255);
            bArr[3] = (byte) ((j >> 24) & 255);
        }
        return bArr;
    }

    public static int getIntFromByte(byte b) {
        return (((b >> 4) & 15) * 16) + (b & 15);
    }

    public static byte[] getByteFromStringFormatUTF8(String str) {
        if (TextUtils.isEmpty(str)) {
            return new byte[0];
        }
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] getByteFromStringFormat(String str) {
        if (TextUtils.isEmpty(str)) {
            return new byte[7];
        }
        int length = str.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static String getStringFromByteFormatHex(byte[] bArr) {
        return bArr == null ? "null" : bytesToHexString(bArr, 0, bArr.length);
    }

    public static String getStringFromByteFormatHex(byte b) {
        return getStringFromByteFormatHex(new byte[]{b});
    }

    public static String getStringFromByteFormatNormal(byte[] bArr) {
        if (bArr == null || bArr.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int length = bArr.length;
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02X ", Byte.valueOf(bArr[i])));
        }
        return sb.toString();
    }

    public static String getStringFromByteFormatNormal(byte b) {
        return getStringFromByteFormatHex(new byte[]{b});
    }

    public static String bytesToHexString(byte[] bArr, int i, int i2) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0 || i2 <= 0) {
            return null;
        }
        for (int i3 = i; i3 < i + i2; i3++) {
            String hexString = Integer.toHexString(bArr[i3] & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            sb.append(hexString.toUpperCase());
        }
        return sb.toString();
    }

    public static int byteToInt(byte b) {
        return getIntFromByte(b);
    }


    public static int ByteArreyToInt(byte[] bArr, int i, boolean z) {
        int i2;
        byte b;
        if (!z) {
            i2 = (bArr[i] & 255) | (65280 & (bArr[i + 1] << 8)) | (16711680 & (bArr[i + 2] << 16));
            b = bArr[i + 3];
        } else {
            i2 = (bArr[i + 3] & 255) | (65280 & (bArr[i + 2] << 8)) | (16711680 & (bArr[i + 1] << 16));
            b = bArr[i];
        }
        return ((b << 24) & ViewCompat.MEASURED_STATE_MASK) | i2;
    }

    public static int ByteArreyToUnsignedShort(byte[] bArr, int i, boolean z) {
        int i2;
        byte b;
        if (!z) {
            i2 = bArr[i] & 255;
            b = bArr[i + 1];
        } else {
            i2 = bArr[i + 1] & 255;
            b = bArr[i];
        }
        return (((b << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | i2) & 65535;
    }

    public static byte getByteUINT16FromInt(int i) {
        String hexString = Integer.toHexString(i);
        if (hexString.length() <= 1) {
            hexString = "0" + hexString;
        }
        return Integer.valueOf(hexString, 16).byteValue();
    }

    public static byte[] getByteDataFromTypeToByte(byte b, byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return new byte[0];
        }
        HashMap hashMap = new HashMap();
        int i = 0;
        do {
            int i2 = bArr[i];
            int i3 = i + 2;
            i = i + i2 + 1;
            int i4 = i2 - 1;
            byte[] bArr2 = new byte[i4];
            System.arraycopy(bArr, i3, bArr2, 0, i4);
            hashMap.put(Byte.valueOf(bArr[i3 - 1]), bArr2);
            if (bArr[i] <= 0) {
                break;
            }
        } while (i < bArr.length);
        byte[] bArr3 = (byte[]) hashMap.get(Byte.valueOf(b));
        return bArr3 == null ? new byte[0] : bArr3;
    }

    public static String getBleAppearanceValue(byte[] bArr, String str) {
        HashMap hashMap = new HashMap();
        int i = 0;
        while (true) {
            int i2 = bArr[i];
            if (i2 > 0) {
                String bytesToHexString = bytesToHexString(bArr, i + 1, 1);
                int i3 = i2 - 1;
                byte[] bArr2 = new byte[i3];
                System.arraycopy(bArr, i + 2, bArr2, 0, i3);
                hashMap.put(bytesToHexString, bytesToHexString(bArr2, 0, i3));
                i = i + i2 + 1;
            } else {
                Log.i("szy", "type: " + str + " 数据：" + ((String) hashMap.get(str)));
                return (String) hashMap.get(str);
            }
        }
    }

    public static int getCRC32(byte[] bArr) {
        int[] iArr = {0, 1996959894, -301047508, -1727442502, 124634137, 1886057615, -379345611, -1637575261, 249268274, 2044508324, -522852066, -1747789432, 162941995, 2125561021, -407360249, -1866523247, 498536548, 1789927666, -205950648, -2067906082, 450548861, 1843258603, -187386543, -2083289657, 325883990, 1684777152, -43845254, -1973040660, 335633487, 1661365465, -99664541, -1928851979, 997073096, 1281953886, -715111964, -1570279054, 1006888145, 1258607687, -770865667, -1526024853, 901097722, 1119000684, -608450090, -1396901568, 853044451, 1172266101, -589951537, -1412350631, 651767980, 1373503546, -925412992, -1076862698, 565507253, 1454621731, -809855591, -1195530993, 671266974, 1594198024, -972236366, -1324619484, 795835527, 1483230225, -1050600021, -1234817731, 1994146192, 31158534, -1731059524, -271249366, 1907459465, 112637215, -1614814043, -390540237, 2013776290, 251722036, -1777751922, -519137256, 2137656763, 141376813, -1855689577, -429695999, 1802195444, 476864866, -2056965928, -228458418, 1812370925, 453092731, -2113342271, -183516073, 1706088902, 314042704, -1950435094, -54949764, 1658658271, 366619977, -1932296973, -69972891, 1303535960, 984961486, -1547960204, -725929758, 1256170817, 1037604311, -1529756563, -740887301, 1131014506, 879679996, -1385723834, -631195440, 1141124467, 855842277, -1442165665, -586318647, 1342533948, 654459306, -1106571248, -921952122, 1466479909, 544179635, -1184443383, -832445281, 1591671054, 702138776, -1328506846, -942167884, 1504918807, 783551873, -1212326853, -1061524307, -306674912, -1698712650, 62317068, 1957810842, -355121351, -1647151185, 81470997, 1943803523, -480048366, -1805370492, 225274430, 2053790376, -468791541, -1828061283, 167816743, 2097651377, -267414716, -2029476910, 503444072, 1762050814, -144550051, -2140837941, 426522225, 1852507879, -19653770, -1982649376, 282753626, 1742555852, -105259153, -1900089351, 397917763, 1622183637, -690576408, -1580100738, 953729732, 1340076626, -776247311, -1497606297, 1068828381, 1219638859, -670225446, -1358292148, 906185462, 1090812512, -547295293, -1469587627, 829329135, 1181335161, -882789492, -1134132454, 628085408, 1382605366, -871598187, -1156888829, 570562233, 1426400815, -977650754, -1296233688, 733239954, 1555261956, -1026031705, -1244606671, 752459403, 1541320221, -1687895376, -328994266, 1969922972, 40735498, -1677130071, -351390145, 1913087877, 83908371, -1782625662, -491226604, 2075208622, 213261112, -1831694693, -438977011, 2094854071, 198958881, -2032938284, -237706686, 1759359992, 534414190, -2118248755, -155638181, 1873836001, 414664567, -2012718362, -15766928, 1711684554, 285281116, -1889165569, -127750551, 1634467795, 376229701, -1609899400, -686959890, 1308918612, 956543938, -1486412191, -799009033, 1231636301, 1047427035, -1362007478, -640263460, 1088359270, 936918000, -1447252397, -558129467, 1202900863, 817233897, -1111625188, -893730166, 1404277552, 615818150, -1160759803, -841546093, 1423857449, 601450431, -1285129682, -1000256840, 1567103746, 711928724, -1274298825, -1022587231, 1510334235, 755167117};
        int length = bArr.length;
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = length - 1;
            if (length == 0) {
                return i & (-1);
            }
            i = (iArr[((i / 256) ^ bArr[i2]) & 255] ^ (i << 8)) & (-1);
            i2++;
            length = i3;
        }
    }

    public static byte[] getCRC16MonBusLow(byte[] bArr) {
        int i = 65535;
        for (byte b : bArr) {
            i ^= b & 255;
            for (int i2 = 0; i2 < 8; i2++) {
                i = (i & 1) != 0 ? (i >> 1) ^ 40961 : i >> 1;
            }
        }
        String upperCase = Integer.toHexString(i).toUpperCase();
        if (upperCase.length() != 4) {
            upperCase = new StringBuffer("0000").replace(4 - upperCase.length(), 4, upperCase).toString();
        }
        return getByteFromStringFormat(upperCase.substring(2, 4) + upperCase.substring(0, 2));
    }

    public static long getShorUInt16FromByteData(byte[] bArr, int i, boolean z) {
        int i2;
        byte b;
        if (!z) {
            i2 = bArr[i] & 255;
            b = bArr[i + 1];
        } else {
            i2 = bArr[i + 1] & 255;
            b = bArr[i];
        }
        return (((b << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | i2) & 65535;
    }

    public static int getIntUInt32FromByteData(byte[] bArr, int i, boolean z) {
        int i2;
        byte b;
        if (!z) {
            i2 = (bArr[i] & 255) | (65280 & (bArr[i + 1] << 8)) | (16711680 & (bArr[i + 2] << 16));
            b = bArr[i + 3];
        } else {
            i2 = (bArr[i + 3] & 255) | (65280 & (bArr[i + 2] << 8)) | (16711680 & (bArr[i + 1] << 16));
            b = bArr[i];
        }
        return ((b << 24) & ViewCompat.MEASURED_STATE_MASK) | i2;
    }

}
