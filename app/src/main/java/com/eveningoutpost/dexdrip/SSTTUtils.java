package com.eveningoutpost.dexdrip;

import static com.eveningoutpost.dexdrip.utilitymodels.Constants.MMOLL_TO_MGDL;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SSTTUtils {
    public static String SERIAL = "";
    public static String readSN(Context context) {
        if (!SERIAL.equals("")) {
            Log.d("SM", "Readed SN:" + SERIAL);
            return SERIAL;
        }

        if (Build.MODEL.equalsIgnoreCase("S30") || Build.MODEL.equalsIgnoreCase("S10") || Build.MODEL.equalsIgnoreCase("S31")) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Log.d("SM", "Readed IMEI SN:" + telephonyManager.getDeviceId());
                SERIAL = telephonyManager.getDeviceId();
                return telephonyManager.getDeviceId();
            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
        }

        if (Build.SERIAL.equalsIgnoreCase("unknown") || Build.SERIAL.startsWith("0123456789")) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/prefix.txt");
                FileInputStream inputStream = new FileInputStream(file);

                byte[] arr = new byte[100];
                int cnt = inputStream.read(arr);
                String msg = new String(arr, 0, cnt).trim();

                Log.d("硅基", "Msg:" + msg);
                SERIAL = msg;
                return msg;

            } catch (Exception e) {

                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).trim();
                SERIAL = androidId;
                return androidId;
//                e.printStackTrace();
            }
        }

        SERIAL = Build.SERIAL;
        return Build.SERIAL;
    }
    public static Handler handler = new Handler(Looper.myLooper());
    public static int mLastTx;
    public static int mDownX;
    public static int mLastTy;
    public static int mDownY;
    public static TextView tvClock = null;
    public static TextView tvGlucose = null;
    public static ImageView ivArrow = null;
    public static String sha1(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            byte[] digest = messageDigest.digest();
            int length = digest.length;
            char[] charArray = "0123456789abcdef".toCharArray();
            char[] cArr = new char[length * 2];
            int i = 0;
            for (byte b : digest) {
                int i2 = i + 1;
                cArr[i] = charArray[(b >>> 4) & 15];
                i = i2 + 1;
                cArr[i2] = charArray[b & 15];
            }
            return new String(cArr);
        } catch (Exception e) {
            return "";
        }
    }

    public static String readPrefix() {

        String idStr = SERIAL;
        final String string = idStr.substring(5) + sha1(idStr).substring(0, 5);
        return string;
    }

    public static String readPw() {
        String idStr = SERIAL;
        String code = sha1(idStr.substring(5) + "YouBitch").substring(0, 6);
        return code;
    }

    public static void showAlert(Context context, double glucose, long timestamp, String trend) {
        context = context.getApplicationContext();
        context.startForegroundService(new Intent(context.getApplicationContext(), MyService.class));

        if (System.currentTimeMillis() - timestamp > 1000 * 60 * 4) {
            return;
        }

//
//        final int[] iArr = {R.drawable.arrow_bs_rd,
//                R.drawable.arrow_bs_sd,
//                R.drawable.arrow_bs_stable,
//                R.drawable.arrow_bs_syrg,
//                R.drawable.arrow_bs_rr};

        int finalTrend = 0;

        if(trend.equals("DoubleUp") || trend.equals("SingleUp")) {
            finalTrend = R.drawable.arrow_bs_rr;
        }

        if(trend.equals("FortyFiveUp")) {
            finalTrend = R.drawable.arrow_bs_syrg;
        }

        if(trend.equals("Flat")) {
            finalTrend = R.drawable.arrow_bs_stable;
        }

        if(trend.equals("FortyFiveDown")) {
            finalTrend = R.drawable.arrow_bs_sd;
        }

        if(trend.equals("SingleDown") || trend.equals("DoubleDown") ) {
            finalTrend = R.drawable.arrow_bs_sd;
        }

        final Context finalContext1 = context;
        int finalTrend1 = finalTrend;
        handler.post(new Runnable() { // from class: com.ShowGlu$1
            @SuppressLint("WrongConstant")
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ((KeyguardManager) finalContext1.getSystemService(Context.KEYGUARD_SERVICE))
                            .newKeyguardLock("tagName").disableKeyguard();
                    if (!Settings.canDrawOverlays(finalContext1)) {
                        return;
                    }


                    if (tvGlucose == null) {

                        SharedPreferences sharedPreferences = finalContext1.getSharedPreferences("alert", Context.MODE_PRIVATE);

                        int x = sharedPreferences.getInt("x", 0);
                        int y = sharedPreferences.getInt("y", 0);

                        final LinearLayout linearLayout = new LinearLayout(finalContext1);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams.gravity = 17;
                        LinearLayout linearLayout2 = new LinearLayout(finalContext1);
                        linearLayout2.setLayoutParams(layoutParams);
                        tvClock = new TextView(finalContext1);
                        tvClock.setGravity(17);
                        tvClock.setTextColor(Color.parseColor("#000000"));

                        GradientDrawable gradientDrawable = new GradientDrawable();
                        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                        gradientDrawable.setCornerRadius(5.0f);
                        gradientDrawable.setColor(Color.parseColor("#88FFFFFF"));
                        tvClock.setBackground(gradientDrawable);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams2.gravity = 17;
                        tvClock.setLayoutParams(layoutParams2);
                        linearLayout2.setOrientation(LinearLayout.VERTICAL);
                        linearLayout2.addView(tvClock);
                        linearLayout.addView(linearLayout2);
                        LinearLayout linearLayout3 = new LinearLayout(finalContext1);
                        GradientDrawable gradientDrawable2 = new GradientDrawable();
                        gradientDrawable2.setShape(GradientDrawable.RECTANGLE);
                        gradientDrawable2.setCornerRadius(20.0f);
                        gradientDrawable2.setColor(Color.parseColor("#FF7E1717"));
                        linearLayout3.setBackground(gradientDrawable2);
                        tvGlucose = new TextView(finalContext1);
                        ivArrow = new ImageView(finalContext1);
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(35, 35);
//                        ivArrow.setLayoutParams(params);
                        ivArrow.setForegroundGravity(17);
                        ivArrow.setColorFilter(-1);
                        LinearLayout.LayoutParams ivLayoutParameters = new LinearLayout.LayoutParams(35, 35);
                        ivLayoutParameters.gravity = 17;
                        ivLayoutParameters.setMargins(0, 0, 0, 0);
                        ivArrow.setLayoutParams(ivLayoutParameters);
                        linearLayout3.addView(tvGlucose);
                        linearLayout3.addView(ivArrow);
                        tvGlucose.setTextSize(45);
                        tvGlucose.setTextColor(Color.parseColor("#FFFFFF"));
                        linearLayout.addView(linearLayout3);
                        final WindowManager windowManager = (WindowManager) finalContext1.getSystemService(Context.WINDOW_SERVICE);
                        final WindowManager.LayoutParams layoutParams4 = new WindowManager.LayoutParams();
                        layoutParams4.type = Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
                        layoutParams4.format = 1;
                        layoutParams4.flags = 524296;
                        layoutParams4.alpha = 1.0f;
                        layoutParams4.gravity = 51;
                        layoutParams4.x = x;
                        layoutParams4.y = y;
                        layoutParams4.width = -2;
                        layoutParams4.height = -2;
                        windowManager.addView(linearLayout, layoutParams4);
                        linearLayout.setOnTouchListener(new View.OnTouchListener() { // from class: com.ShowGlu$1.1
                            @Override // android.view.View.OnTouchListener
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                int action = motionEvent.getAction();
                                if (action == MotionEvent.ACTION_DOWN) {
                                    mDownX = mLastTx = (int) motionEvent.getRawX();
                                    mDownY = mLastTy = (int) motionEvent.getRawY();
                                    return true;
                                }
                                if (action == MotionEvent.ACTION_MOVE) {
                                    float rawX = motionEvent.getRawX() - mLastTx;
                                    float rawY = motionEvent.getRawY() - mLastTy;
                                    mLastTx = (int) motionEvent.getRawX();
                                    mLastTy = (int) motionEvent.getRawY();
                                    WindowManager.LayoutParams layoutParams5 = layoutParams4;
                                    layoutParams5.x = (int) (layoutParams5.x + rawX);
                                    WindowManager.LayoutParams layoutParams6 = layoutParams4;
                                    layoutParams6.y = (int) (layoutParams6.y + rawY);
                                    windowManager.updateViewLayout(linearLayout, layoutParams4);
                                }
                                if (action == MotionEvent.ACTION_UP) {
                                    Log.d("TAG", "action up");
                                    if (Math.abs(motionEvent.getRawX() - mDownX) < 10 && Math.abs(motionEvent.getRawY() - mDownY) < 10) {

                                        Intent intent = new Intent(finalContext1, Home.class);
                                        finalContext1.startActivity(intent);


                                    } else {
                                        SharedPreferences sharedPreferences = finalContext1.getSharedPreferences("alert", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("x", mLastTx);
                                        editor.putInt("x", mLastTx);
                                        editor.commit();
                                    }
                                }
                                return true;
                            }
                        });

                    }
                    tvGlucose.setText(String.format("%.1f", glucose / MMOLL_TO_MGDL));
                    ivArrow.setImageResource(finalTrend1);
                    tvClock.setText(new SimpleDateFormat("  HH:mm  ").format(new Date(timestamp)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
