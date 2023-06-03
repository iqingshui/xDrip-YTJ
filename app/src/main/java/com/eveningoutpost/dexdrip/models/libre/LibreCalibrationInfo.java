package com.eveningoutpost.dexdrip.models.libre;

/* loaded from: classes.dex */
public class LibreCalibrationInfo {
    int i1;
    int i2;
    double i3;
    double i4;
    double i5;
    double i6;

    public LibreCalibrationInfo(byte[] bArr) {
        this.i1 = readBits(bArr, 2, 0, 3);
        this.i2 = readBits(bArr, 2, 3, 10);
        this.i3 = readBits(bArr, 336, 0, 8);
        if (readBits(bArr, 336, 33, 1) != 0) {
            this.i3 = -this.i3;
        }
        this.i4 = readBits(bArr, 336, 8, 14);
        this.i5 = readBits(bArr, 336, 40, 12) << 2;
        this.i6 = readBits(bArr, 336, 52, 12) << 2;
    }

    public static int readBits(byte[] bArr, int i, int i2, int i3) {
        if (i3 == 0) {
            return 0;
        }
        int i4 = 0;
        for (int i5 = 0; i5 < i3; i5++) {
            int i6 = (i * 8) + i2 + i5;
            int intValue = Integer.valueOf((int) Math.floor(Float.valueOf(i6).floatValue() / 8.0f)).intValue();
            int i7 = i6 % 8;
            if (i6 >= 0 && ((bArr[intValue] >> i7) & 1) == 1) {
                i4 |= 1 << i5;
            }
        }
        return i4;
    }
}
