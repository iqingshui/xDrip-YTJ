package com.eveningoutpost.dexdrip;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;

import java.util.ArrayList;

public class LauncherActivity extends Activity {
    private final ArrayList<String> permissions = new ArrayList<>();

    public void grantPermission() {
        permissions.clear();

        if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.READ_PHONE_STATE))
            permissions.add(Manifest.permission.READ_PHONE_STATE);

        if (permissions.size() == 0) {
            checkSystemAlert();
        } else {
            requestPermissions(permissions.toArray(new String[0]), 10);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grantPermission();
    }

    boolean mIsChecking;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "resume");
//        checkSystemAlert();
    }

    public void checkSystemAlert() {

//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
//            new AlertDialog.Builder(this)
//                    .setTitle("提示")
//                    .setMessage("硅基动感的持续运行需要将本应用加入到电池优化的忽略名单中，在弹出的对话框内将硅基动感的所有权限设置为不优化")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            mIsChecking = true;
//                            Intent intent = new Intent (Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                            intent.setData(Uri.parse("package:" + getPackageName()));
//                            startActivity(intent);
//                            dialog.dismiss();
//                        }
//                    })
//                    .show();
//        } else {
//        App.SN = ToNs.readSN(this);

        if (!Settings.canDrawOverlays(this)) {

            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION",
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, 100);
        } else {

            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.READ_PHONE_STATE)) {
                grantPermission();
                return;
            }

            SSTTUtils.readSN(this);
            String internalUrl = String.format("https://%s@%s.ns.sstt.top/api/v1/", SSTTUtils.readPw(), SSTTUtils.readPrefix());

            if (!Pref.getString("cloud_storage_api_base", getString(R.string.pref_default_api_url)).contains("sstt.top")) {
                Pref.setString("cloud_storage_api_base", internalUrl);
                Pref.setBoolean("external_blukon_algorithm", true);
                Pref.setBoolean("show_graph_grid_time", false);
                Pref.setBoolean("show_graph_grid_glucose", false);
            }
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            checkSystemAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                grantPermission();
                return;
            }
        }

        checkSystemAlert();
    }
}
