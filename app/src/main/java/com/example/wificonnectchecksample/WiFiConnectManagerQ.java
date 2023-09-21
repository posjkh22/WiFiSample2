package com.example.wificonnectchecksample;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class WiFiConnectManagerQ extends ConnectivityManager.NetworkCallback {

    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;

    private String TAG = "NetworkCheck";


    public WiFiConnectManagerQ(Context context){
        this.context = context;
        networkRequest =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void connect(String ssid, String pw) {
        final WifiNetworkSpecifier wifiNetworkSpecifier;
        wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(pw)
                .build();

        final NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier)
                .build();

        this.connectivityManager.requestNetwork(networkRequest, this);
    }
}
