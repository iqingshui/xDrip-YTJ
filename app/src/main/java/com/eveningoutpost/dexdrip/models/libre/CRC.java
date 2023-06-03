package com.eveningoutpost.dexdrip.models.libre;

import android.util.Log;

/* loaded from: classes.dex */
public class CRC {
    private static String TAG = "CRC";
    private static int[] crc16table = {0, 4489, 8978, 12955, 17956, 22445, 25910, 29887, 35912, 40385, 44890, 48851, 51820, 56293, 59774, 63735, 4225, 264, 13203, 8730, 22181, 18220, 30135, 25662, 40137, 36160, 49115, 44626, 56045, 52068, 63999, 59510, 8450, 12427, 528, 5017, 26406, 30383, 17460, 21949, 44362, 48323, 36440, 40913, 60270, 64231, 51324, 55797, 12675, 8202, 4753, 792, 30631, 26158, 21685, 17724, 48587, 44098, 40665, 36688, 64495, 60006, 55549, 51572, 16900, 21389, 24854, 28831, 1056, 5545, 10034, 14011, 52812, 57285, 60766, 64727, 34920, 39393, 43898, 47859, 21125, 17164, 29079, 24606, 5281, 1320, 14259, 9786, 57037, 53060, 64991, 60502, 39145, 35168, 48123, 43634, 25350, 29327, 16404, 20893, 9506, 13483, 1584, 6073, 61262, 65223, 52316, 56789, 43370, 47331, 35448, 39921, 29575, 25102, 20629, 16668, 13731, 9258, 5809, 1848, 65487, 60998, 56541, 52564, 47595, 43106, 39673, 35696, 33800, 38273, 42778, 46739, 49708, 54181, 57662, 61623, 2112, 6601, 11090, 15067, 20068, 24557, 28022, 31999, 38025, 34048, 47003, 42514, 53933, 49956, 61887, 57398, 6337, 2376, 15315, 10842, 24293, 20332, 32247, 27774, 42250, 46211, 34328, 38801, 58158, 62119, 49212, 53685, 10562, 14539, 2640, 7129, 28518, 32495, 19572, 24061, 46475, 41986, 38553, 34576, 62383, 57894, 53437, 49460, 14787, 10314, 6865, 2904, 32743, 28270, 23797, 19836, 50700, 55173, 58654, 62615, 32808, 37281, 41786, 45747, 19012, 23501, 26966, 30943, 3168, 7657, 12146, 16123, 54925, 50948, 62879, 58390, 37033, 33056, 46011, 41522, 23237, 19276, 31191, 26718, 7393, 3432, 16371, 11898, 59150, 63111, 50204, 54677, 41258, 45219, 33336, 37809, 27462, 31439, 18516, 23005, 11618, 15595, 3696, 8185, 63375, 58886, 54429, 50452, 45483, 40994, 37561, 33584, 31687, 27214, 22741, 18780, 15843, 11370, 7921, 3960};

    private static int crc16(byte[] bArr) {
        Log.i(TAG, "data: " + ByteUtil.getStringFromByteFormatHex(bArr));
        Log.i(TAG, "data: " + bArr.length);
        int i = 65535;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            i = (i >> 8) ^ crc16table[((byte) (ByteUtil.getIntFromByte(bArr[i2]) ^ i)) & 255];
        }
        int i3 = 0;
        for (int i4 = 0; i4 < 16; i4++) {
            i3 = (i3 << 1) | (i & 1);
            i >>= 1;
        }
        return i3;
    }

    public static boolean computeCRC16(byte[] bArr, int i, int i2) {
        int i3;
        int i4;
        if (bArr == null || bArr.length < i + i2) {
            i3 = -1;
            i4 = 0;
        } else {
            byte[] bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
            Log.i(TAG, ByteUtil.getStringFromByteFormatHex(bArr2));
            int i5 = i2 - 2;
            byte[] bArr3 = new byte[i5];
            System.arraycopy(bArr2, 2, bArr3, 0, i5);
            i4 = crc16(bArr3);
            i3 = ((bArr2[1] & 255) << 8) | (bArr2[0] & 255);
        }
        Log.i(TAG, "reverseCrc: " + i4);
        Log.i(TAG, "normalCrc: " + i3);
        return i3 == i4;
    }
}
