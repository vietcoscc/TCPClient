package com.example.ominext.tcpclient;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ominext.tcpclient.socket.ConnectionCallBack;
import com.example.ominext.tcpclient.socket.OnDataListener;
import com.example.ominext.tcpclient.socket.SocketClient;

public class MainActivity extends AppCompatActivity implements ConnectionCallBack, OnDataListener {
    public static final String TAG = "MainActivity";
    private Button mBtnSend;
    private EditText mEdtMessage;
    private NetWorkReceiver netWorkReceiver = new NetWorkReceiver();
    private SocketClient mSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerNetWorkReceiver();
        initViews();

    }

    private void initViews() {
        mBtnSend = findViewById(R.id.btnSend);
        mEdtMessage = findViewById(R.id.edtMessage);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSocketClient.sendData(mEdtMessage.getText().toString().getBytes());
            }
        });
    }

    private void registerNetWorkReceiver() {
        netWorkReceiver.setOnWifiTarget(new NetWorkReceiver.OnWifiTarget() {
            @Override
            public void onConnected() {
                mSocketClient = new SocketClient();
                mSocketClient.setConnectionCallBack(MainActivity.this);
                mSocketClient.setOnReceiveDataListener(MainActivity.this);
                Log.i(TAG, "WIFI target connected");
                Toast.makeText(MainActivity.this, "WIFI target connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisnected() {
                Log.i(TAG, "WIFI target not connected");
                Toast.makeText(MainActivity.this, "WIFI target not connected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onPause() {
        unregisterReceiver(netWorkReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, intentFilter);
        super.onResume();
    }


    public void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onAlive(String message) {
        Log.i(TAG, message);
        toast(message);
    }

    @Override
    public void onException(String message) {
        toast(message);
        Log.i(TAG, message);
    }

    @Override
    public void onReceiveData(byte[] b, int length) {
        String data = new String(b, 0, length);
        Log.i(TAG, data);
        Log.i(TAG, Thread.currentThread().getName());
        toast(data);
    }
}
