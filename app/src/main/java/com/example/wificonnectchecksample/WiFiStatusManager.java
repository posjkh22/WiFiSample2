package com.example.wificonnectchecksample;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

public class WiFiStatusManager {

    private Context context;
    private  WiFiStatusReceiver wiFiStatusReceiver;
    private  WiFiStatusReceiverS wiFiStatusReceiverS;
    private  WiFiStatusReceiverQ wiFiStatusReceiverQ;

    final String TAG = "WiFiStatusManager";

    public WiFiStatusManager(Context context) {
        this.context = context;
    }

    public void register() {

        Log.d(TAG, "register WiFiStatusReceiver");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (wiFiStatusReceiverS == null) {
                wiFiStatusReceiverS = new WiFiStatusReceiverS(this.context);
                wiFiStatusReceiverS.register();
            }
        }
        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (wiFiStatusReceiverQ == null) {
                wiFiStatusReceiverQ = new WiFiStatusReceiverQ(this.context);
                wiFiStatusReceiverQ.register();
            }
        }
        else {
            if (wiFiStatusReceiver == null) {
                wiFiStatusReceiver = new WiFiStatusReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                this.context.registerReceiver(wiFiStatusReceiver, intentFilter);
            }
        }
    }
    public void unregister() {

        Log.d(TAG, "unregister WiFiStatusReceiver");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (wiFiStatusReceiverS != null) {
                wiFiStatusReceiverS.unregister();
                wiFiStatusReceiverS = null;
            }
        }
        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (wiFiStatusReceiverQ != null) {
                wiFiStatusReceiverQ.unregister();
                wiFiStatusReceiverQ = null;
            }
        }
        else {
            if (wiFiStatusReceiver != null) {
                this.context.unregisterReceiver(wiFiStatusReceiver);
            }
        }
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public void setWifiEnabled() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Intent intent = new Intent(Settings.Panel.ACTION_WIFI);
            this.context.startActivity(intent);
        }
        else {
            WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
        }
    }

}
