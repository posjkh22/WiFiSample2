package com.example.wificonnectchecksample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class WiFiStatusReceiverQ extends ConnectivityManager.NetworkCallback {

    final private String TAG = "WiFiStatusReceiverQ";

    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;
    private WiFiStatus status;

    public WiFiStatusReceiverQ(Context context, WiFiStatus status){
        this.context = context;
        this.status = status;
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void register() { this.connectivityManager.registerNetworkCallback(networkRequest, this);}

    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.d(TAG, "[onAvailable] network connected");

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        String bssid = wifiInfo.getBSSID();

        Log.d(TAG, "[onAvailable] ssid: " +ssid + "/ bssid: "+ bssid);

        this.status.postValue(ssid);

    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.d(TAG, "[onLost] network disconnected");

        this.status.postValue("Disconnected");
    }
}

