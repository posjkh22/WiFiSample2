package com.example.wificonnectchecksample;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.S)
public class WiFiStatusReceiverS extends ConnectivityManager.NetworkCallback {

    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;

    private String TAG = "WiFiStatusReceiverS";


    public WiFiStatusReceiverS(Context context){
        super(ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO);
        this.context = context;
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

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("type", "wifi_connected_info");
        intent.putExtra("ssid", "Connected");
        intent.putExtra("bssid", "");
        startActivity(this.context, intent, null);
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.d(TAG, "[onLost] network disconnected");

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("type", "wifi_connected_info");
        intent.putExtra("ssid", "Disconnected");
        intent.putExtra("bssid", "");
        startActivity(this.context, intent, null);
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {

        WifiInfo wifiInfo =  (WifiInfo) networkCapabilities.getTransportInfo();

        if (wifiInfo != null)
        {
            String ssid = wifiInfo.getSSID().replace("\"",  "" );
            String bssid = wifiInfo.getBSSID();
            Log.d(TAG, "[onCapabilitiesChanged] ssid: " +ssid + "/ bssid: "+ bssid);

            Intent intent = new Intent(this.context, MainActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("type", "wifi_connected_info");
            intent.putExtra("ssid", ssid);
            intent.putExtra("bssid", bssid);
            startActivity(this.context, intent, null);
        }
    }
}

