package com.example.wificonnectchecksample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class WiFiConnectQ extends ConnectivityManager.NetworkCallback {

    private Context context;
    private ConnectivityManager connectivityManager;

    private String TAG = "WiFiConnectQ";

    public WiFiConnectQ(Context context){
        this.context = context;
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
