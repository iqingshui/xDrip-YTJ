package com.eveningoutpost.dexdrip.cgm;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* compiled from: AlgorithmRunner.java */
/* loaded from: classes.dex */
class MyContextWrapper extends ContextWrapper {
    static final String TAG = "OOPAlgorithm";
    Context mBase;

    public MyContextWrapper(Context context) {
        super(context);
        this.mBase = context;
        Log.e(TAG, "MyContextWrapper.MyContextWrapper() called ");
    }

    public static String getPackageCodePathNoCreate(Context context) {
        return context.getFilesDir().getPath() + "base111.apk";
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public String getPackageCodePath() {
        Log.e(TAG, "MyContextWrapper.getPackageCodePath() called mBase.getPackageCodePath() = " + this.mBase.getPackageCodePath());
        String packageCodePathNoCreate = getPackageCodePathNoCreate(this.mBase);
        Log.e(TAG, "MyContextWrapper newpath = " + packageCodePathNoCreate);
        File file = new File(packageCodePathNoCreate);
        if (file.exists() && !file.isDirectory()) {
            Log.e(TAG, "MyContextWrapper apk exists, returning it = " + packageCodePathNoCreate);
            return packageCodePathNoCreate;
        }
        try {
            InputStream openRawResource = getResources().openRawResource(getResources().getIdentifier("original_apk", "raw", getPackageName()));
            FileOutputStream fileOutputStream = new FileOutputStream(packageCodePathNoCreate);
            byte[] bArr = new byte[1048576];
            while (true) {
                int read = openRawResource.read(bArr);
                if (read < 0) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
                Log.e(TAG, "MyContextWrapper succesfully read  = " + read);
            }
            Log.e(TAG, "MyContextWrapper succesfully wrote file  = " + packageCodePathNoCreate);
            fileOutputStream.close();
            openRawResource.close();
        } catch (IOException e) {
            Log.e(TAG, "Error: reading resource file", e);
        }
        return packageCodePathNoCreate;
    }
}
