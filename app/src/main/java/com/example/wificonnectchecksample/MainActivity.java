package com.example.wificonnectchecksample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private TextView tvWifiStatus;
    private TextView tvScan;

    private TextView tvConnectedWifi;
    private TextView tvScenarioStatus;


    private Button btnScan;
    private Button btnRun;
    private Button btnConnect;

    private EditText editSsid;

    private EditText editPw;


    private String currentSSID = "";
    private String currentBSSID = "";

    private List<ScanResult> currentScanList;

    private String[] permission = new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_NETWORK_STATE,
            android.Manifest.permission.WRITE_SETTINGS,
    };

    WiFiStatus wiFiStatus = null;

    WiFiScanManager wifiScanManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        initUiContents();

        observeWiFiStatus();
    }

    private void observeWiFiStatus() {

        registerWifiManager();
        wiFiStatus.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String ssid) {
                Log.d(TAG, "observe: " + ssid);
                currentSSID = ssid;
                tvConnectedWifi.setText(ssid);
                registerConnectedLog(ssid);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        registerWifiManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterWifiManager();
    }

    private void setWifiEnabled() {
        if (wiFiStatus == null) {
            wiFiStatus = new WiFiStatus(this);
        }
        if (!wiFiStatus.isWifiEnabled()) {
            wiFiStatus.setWifiEnabled();
        }
    }

    private void registerWifiManager() {
        if (wiFiStatus != null) {
            wiFiStatus.register();
        }
        else {
            wiFiStatus = new WiFiStatus(this);
            wiFiStatus.register();
        }
        if (wifiScanManager == null) {
            wifiScanManager = new WiFiScanManager(this);
            wifiScanManager.register(this);
        }
    }
    private void unregisterWifiManager() {
        if (wiFiStatus != null) {
            wiFiStatus.unregister();
        }
        else {
            wiFiStatus = new WiFiStatus(this);
            wiFiStatus.unregister();
        }
        if (wifiScanManager != null) {
            wifiScanManager.unregister();
            wifiScanManager = null;
        }
    }

    private void initUiContents() {
        tvWifiStatus = findViewById(R.id.tvWifiStatus);
        tvWifiStatus.setText("");

        tvScan = findViewById(R.id.scanList);
        tvScan.setText("");

        btnRun = findViewById(R.id.btnRun);
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick - btnScan");
                run();
            }
        });

        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick - btnScan");
                scan();
            }
        });

        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick - btnConnect");
                connect();
            }
        });

        editSsid = findViewById(R.id.edit_ssid);
        editPw = findViewById(R.id.edit_pw);

        editSsid.setText("KH");
        editPw.setText("12345678");

        tvConnectedWifi = findViewById(R.id.tvConnectedWifi);
        tvScenarioStatus = findViewById(R.id.tvScenarioStatus);
    }

    private void run() {
        testScenario(10 * 1000, 1000);
    }

    private void scan() {
        tvScan.setText("");
        if (wifiScanManager != null) {
            wifiScanManager.startScan();
        }
    }

    private void connect() {
        final String ssid = editSsid.getText().toString();
        final String pw = editPw.getText().toString();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            WiFiConnectQ cm  = new WiFiConnectQ(this);
            cm.connect(ssid, pw);
        }
        else {
            WiFiConnect cm = new WiFiConnect(this);
            cm.connect(ssid, pw);
        }
    }

    private void registerConnectedLog(String text) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
        String timeString = dateFormat.format(now);
        String log = String.format("[%s] %s\n", timeString, text);
        tvWifiStatus.append(log);
    }

    private void registerScanLog(String text) {
        String log = String.format("%s\n", text);
        tvScan.append(log);
    }

    private void registerConnectLog(String text) {
        String log = String.format("%s\n", text);
        tvWifiStatus.append(log);
    }


    private boolean checkPermission(){
        int result;
        List<String> listPermission = new ArrayList<>();
        for(String p : permission){
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED){
                listPermission.add(p);
            }
        }
        if(!listPermission.isEmpty()){
            ActivityCompat.requestPermissions(MainActivity.this,listPermission.toArray(new String[listPermission.size()]),0);
            return false;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String type = intent.getStringExtra("type");

        switch(type) {

            case "wifi_scanned_info": {
                currentScanList = (List<ScanResult>) intent.getSerializableExtra("scanList");
                for (ScanResult item : currentScanList) {
                    String ssid = item.SSID;
                    String bssid = item.BSSID;
                    this.registerScanLog(ssid + " [" + bssid + "]");
                }
            }
                break;

            default:
                break;
        }
    }


    private void testScenario(int msTimeout, int msPolling) {

        if (currentSSID.equals(editSsid.getText().toString())) {
            tvScenarioStatus.setText("PASS");
            return;
        }

        connect();

        new Thread(new Runnable() {

            int msDelay = 0;
            @Override
            public void run() {
                while(true) {

                    // Timeout NG
                    if (msDelay >= msTimeout) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvScenarioStatus.setText("NG");
                            }
                        });
                        break;
                    }

                    // PASS
                    if (currentSSID.equals(editSsid.getText().toString())) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvScenarioStatus.setText("PASS");
                            }
                        });
                        break;
                    }

                    try {
                        Thread.sleep(msPolling);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    msDelay += msPolling;
                }
            }
        }).start();
    }
}