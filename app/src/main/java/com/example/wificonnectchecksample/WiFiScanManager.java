package com.example.wificonnectchecksample;

import static android.content.Context.WIFI_SERVICE;
import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.io.Serializable;
import java.util.List;

public class WiFiScanManager {

    final private String TAG = "WiFiScan";

    private Context context;
    private BroadcastReceiver mReceiver = null;
    private WifiManager wifiManager = null;

    public WiFiScanManager(Context context){
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public void startScan() {
        boolean success = wifiManager.startScan();
        if (!success) {
            scanFailure();
        }
    }

    public void register(Context context) {
        if(this.mReceiver != null) {
            Log.d(TAG, "registered already.");
            return;
        }
        Log.d(TAG, "register");

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(this.mReceiver, intentFilter);
    }

    public void unregister() {
        if(mReceiver != null){
            this.context.unregisterReceiver(mReceiver);
            this.mReceiver = null;
        }
    }

    public void scanSuccess() {
        Log.d(TAG, "scanSuccess");

        @SuppressLint("MissingPermission")
        List<ScanResult> results = wifiManager.getScanResults();

        Log.d(TAG, "wifi scan result: " +results.toString());
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("type", "wifi_scanned_info");
        intent.putExtra("scanList", (Serializable) results);
        startActivity(this.context, intent, null);
    }

    public void scanFailure() {
        Log.d(TAG, "scanFailure");

        @SuppressLint("MissingPermission")
        List<ScanResult> results = wifiManager.getScanResults();

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("type", "wifi_scanned_info");
        intent.putExtra("scanList", (Serializable) results);
        startActivity(this.context, intent, null);
    }
}
