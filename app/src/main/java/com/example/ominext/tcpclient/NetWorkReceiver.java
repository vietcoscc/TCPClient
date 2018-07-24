package com.example.ominext.tcpclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class NetWorkReceiver extends BroadcastReceiver {
    private static final String SSID = "\"NANO_HOSPITAL\"";
    private OnWifiTarget mOnWifiTarget;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifiManager != null && connectivityManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (wifiInfo != null && networkInfo != null) {
                    System.out.println("State: " + networkInfo.getState().name());
                    System.out.println("SSID: " + wifiInfo.getSSID());
                    if (wifiInfo.getSSID().equals(SSID) && networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        if (mOnWifiTarget != null) {
                            mOnWifiTarget.onConnected();
                        }
                    } else {
                        if (mOnWifiTarget != null) {
                            mOnWifiTarget.onDisnected();
                        }
                    }
                }
            }
        }
    }

    public void setOnWifiTarget(OnWifiTarget onWifiTarget) {
        this.mOnWifiTarget = onWifiTarget;
    }

    interface OnWifiTarget {
        void onConnected();

        void onDisnected();
    }
}
