package com.eveningoutpost.dexdrip;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Date;

public class MyService extends Service {

    public static PowerManager.WakeLock mWakeLock = null;
    static long lastSetTs = 0;

    boolean isFirst = true;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "xDrip";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "xDrip",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("xDrip")
                    .setContentText("xDrip").build();

            startForeground(1, notification);
        }
    }

    public MyService() {
    }

    private void cancelExistingAndSetNew(boolean currentWakeLock, long next) {


        if (next == 0) {
            next = System.currentTimeMillis() + 5 * 60 * 1000 - 5 * 1000;
        } else {
            if (!isFirst) {
                return;
            }
            isFirst = false;
        }

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.setReferenceCounted(true);
            mWakeLock.release();
        }


        if (currentWakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "xDrip:血糖上传");
            mWakeLock.acquire(20 * 1000);
        } else {
            Log.d("xDrip", "Cancel Lock");
        }

        Log.d("xDrip", "上传！！！！");

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        intent.putExtra("wakeup", "wakeup");
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getService(
                    getApplicationContext(),
                    10,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Log.d("xDrip", "下一个时刻：" + new Date(next).toString());
            AlarmManager.AlarmClockInfo alarmClockInfo =
                    new AlarmManager.AlarmClockInfo(next, pendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        }


        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //最大分配内存
        int memory = activityManager.getMemoryClass();
        //最大分配内存获取方法2
        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        //剩余内存
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        Log.d("xDrip", "memory: " + memory + ",maxMemory: " + maxMemory + ",totalMemory: " + totalMemory + ",freeMemory: " + freeMemory);
    }

    long mLastTs = 99999;
    long mLastNumOfUnreceived = System.currentTimeMillis();
    Handler handler = new Handler();

    Runnable callback = () -> {
        if (System.currentTimeMillis() - mLastTs > 2 * 1000 * 60) {
            try {
                Log.d("xDrip", "Runnable Urgent Service to Load Data!!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(intent != null)
//            cancelExistingAndSetNew(!intent.getBooleanExtra("cancelLock", false));
//        else

        long next = 0;
        if (intent != null && intent.hasExtra("next")) {
            next = intent.getLongExtra("next", 0);
        }
        cancelExistingAndSetNew(true, next);


        if (intent != null && intent.hasExtra("ts")) {
            mLastTs = intent.getLongExtra("ts", 0);
            mLastNumOfUnreceived = intent.getIntExtra("unreceived", 0);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}