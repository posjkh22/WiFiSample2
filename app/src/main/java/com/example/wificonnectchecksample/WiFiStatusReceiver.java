package com.example.wificonnectchecksample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiStatusReceiver extends BroadcastReceiver {

    final String TAG = "WiFiStatusReceiver";

    private WiFiStatus status;

    public WiFiStatusReceiver(WiFiStatus status) {
        this.status = status;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if(info != null && info.isConnected()) {

            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            String bssid = wifiInfo.getBSSID();
            Log.d(TAG, "ssid: " +ssid + "/ bssid: "+ bssid);
            this.status.postValue(ssid);
        }
    }
}
