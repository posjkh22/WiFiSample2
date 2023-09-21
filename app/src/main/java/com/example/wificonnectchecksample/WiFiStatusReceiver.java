package com.example.wificonnectchecksample;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiStatusReceiver extends BroadcastReceiver {

    final String TAG = "WiFiStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if(info != null && info.isConnected()) {

            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            String bssid = wifiInfo.getBSSID();

            Log.d(TAG, "ssid: " +ssid + "/ bssid: "+ bssid);

            Intent it = new Intent(context, MainActivity.class);
            it.addFlags(
                    it.FLAG_ACTIVITY_SINGLE_TOP |
                    it.FLAG_ACTIVITY_CLEAR_TOP);

            it.putExtra("type", "wifi_connected_info");
            it.putExtra("ssid", ssid.substring(1, ssid.length() - 1));
            it.putExtra("bssid", bssid);
            startActivity(context, it, null);

        }
    }
}
