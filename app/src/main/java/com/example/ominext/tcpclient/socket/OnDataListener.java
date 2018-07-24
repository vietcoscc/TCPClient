package com.example.ominext.tcpclient.socket;

public interface OnDataListener {
    void onReceiveData(byte b[], int length);
}
