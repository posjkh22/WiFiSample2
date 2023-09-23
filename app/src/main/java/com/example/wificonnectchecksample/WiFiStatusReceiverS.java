package com.example.wificonnectchecksample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.S)
public class WiFiStatusReceiverS extends ConnectivityManager.NetworkCallback {

    final private String TAG = "WiFiStatusReceiverS";

    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;
    private WiFiStatus status;

    public WiFiStatusReceiverS(Context context, WiFiStatus status){
        super(ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO);
        this.status = status;
        this.context = context;
        networkRequest = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void register() {
        Log.d(TAG, "register");
        this.connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    public void unregister() {
        Log.d(TAG, "unregister");
        this.connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.d(TAG, "onAvailable");
        this.status.postValue("Connected");
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.d(TAG, "onLost");
        this.status.postValue("Disconnected");
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {

        WifiInfo wifiInfo =  (WifiInfo) networkCapabilities.getTransportInfo();
        if (wifiInfo != null)
        {
            String ssid = wifiInfo.getSSID().replace("\"",  "" );
            String bssid = wifiInfo.getBSSID();
            Log.d(TAG, "[onCapabilitiesChanged] ssid: " +ssid + "/ bssid: "+ bssid);

            this.status.postValue(ssid);
        }
    }
}

