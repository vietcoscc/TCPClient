package com.example.ominext.tcpclient.socket;

public interface ConnectionCallBack {
    void onAlive(String message);

    void onException(String message);
}
