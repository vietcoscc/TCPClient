package com.example.ominext.tcpclient.socket;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {
    private static final String HOST = "192.168.4.1";
    private static final int PORT = 12000;
    private static final int TIME_OUT = 2000;
    private Socket socket;
    private InputStream is = null;
    private OutputStream os = null;
    private boolean isAlive;
    private ConnectionCallBack mConnectionCallBack;
    private OnDataListener mOnDataListener;
    private boolean isListening = true;
    private Handler handler;
    private HandlerThread handlerThread;

    public SocketClient() {
        handlerThread = new HandlerThread("SocketClient");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        connect();
    }

    private void connect() {
        Thread thread = new Thread(this.runnable);
        thread.start();
    }

    private void initSocket() throws IOException {
        if (socket != null) {
            socket.close();
            socket = null;
            is = null;
            os = null;
            isAlive = false;
            isListening = false;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                initSocket();
                socket = new Socket();
                InetSocketAddress endPoint = new InetSocketAddress(HOST, PORT);
                socket.connect(endPoint, TIME_OUT);
                is = socket.getInputStream();
                os = socket.getOutputStream();
                isAlive = true;
                isListening = true;
                if (mConnectionCallBack != null) {
                    mConnectionCallBack.onAlive("Socket is alive");
                }
                while (isListening) {
                    byte b[] = new byte[1024 * 10];
                    int length = is.read(b);
                    if (length > 0) {
                        mOnDataListener.onReceiveData(b, length);
                    }
                }

            } catch (IOException e) {
                isAlive = false;
                if (mConnectionCallBack != null) {
                    mConnectionCallBack.onException(e.getMessage());
                }
            }
        }
    };

    public void sendData(final byte b[]) {
        runInBackground(new Runnable() {
            @Override
            public void run() {
                if (isAlive && os != null) {
                    try {
                        os.write(b);
                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SocketClient.this.mConnectionCallBack.onException(e.getMessage());
                    }
                }
            }
        });
    }

    private void runInBackground(Runnable runnable) {
        if (handler != null) {
            handler.post(runnable);
        }
    }

    public void setConnectionCallBack(ConnectionCallBack mConnectionCallBack) {
        this.mConnectionCallBack = mConnectionCallBack;
    }

    public void setOnReceiveDataListener(OnDataListener mOnDataListener) {
        this.mOnDataListener = mOnDataListener;
    }
}
