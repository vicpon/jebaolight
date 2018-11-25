package com.espressif.iot.Esptouch.udp;

import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPSocketClient {
    private static final String TAG = "UDPSocketClient";
    private volatile boolean mIsClosed;
    private volatile boolean mIsStop;
    private DatagramSocket mSocket;

    public UDPSocketClient() {
        try {
            this.mSocket = new DatagramSocket();
            this.mIsStop = false;
            this.mIsClosed = false;
        } catch (SocketException e) {
            Log.e(TAG, "SocketException");
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void interrupt() {
        Log.i(TAG, "USPSocketClient is interrupt");
        this.mIsStop = true;
    }

    public synchronized void close() {
        if (!this.mIsClosed) {
            this.mSocket.close();
            this.mIsClosed = true;
        }
    }

    public void sendData(byte[][] data, String targetHostName, int targetPort, long interval) {
        sendData(data, 0, data.length, targetHostName, targetPort, interval);
    }

    public void sendData(byte[][] data, int offset, int count, String targetHostName, int targetPort, long interval) {
        if (data == null || data.length <= 0) {
            Log.e(TAG, "sendData(): data == null or length <= 0");
            return;
        }
        int i = offset;
        while (!this.mIsStop && i < offset + count) {
            if (data[i].length != 0) {
                try {
                    this.mSocket.send(new DatagramPacket(data[i], data[i].length, InetAddress.getByName(targetHostName), targetPort));
                } catch (UnknownHostException e) {
                    Log.e(TAG, "sendData(): UnknownHostException");
                    e.printStackTrace();
                    this.mIsStop = true;
                } catch (IOException e2) {
                    Log.e(TAG, "sendData(): IOException, but just ignore it");
                }
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                    Log.e(TAG, "sendData is Interrupted");
                    this.mIsStop = true;
                }
            }
            i++;
        }
        if (this.mIsStop) {
            close();
        }
    }
}
