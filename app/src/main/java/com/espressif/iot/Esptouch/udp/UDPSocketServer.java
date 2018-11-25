package com.espressif.iot.Esptouch.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class UDPSocketServer {
    private static final String TAG = "UDPSocketServer";
    private final byte[] buffer = new byte[64];
    private Context mContext;
    private volatile boolean mIsClosed;
    private MulticastLock mLock;
    private DatagramPacket mReceivePacket = new DatagramPacket(this.buffer, 64);
    private DatagramSocket mServerSocket;

    private synchronized void acquireLock() {
        if (!(this.mLock == null || this.mLock.isHeld())) {
            this.mLock.acquire();
        }
    }

    private synchronized void releaseLock() {
        if (this.mLock != null && this.mLock.isHeld()) {
            try {
                this.mLock.release();
            } catch (Throwable th) {
            }
        }
    }

    public UDPSocketServer(int port, int socketTimeout, Context context) {
        this.mContext = context;
        try {
            this.mServerSocket = new DatagramSocket(port);
            this.mServerSocket.setSoTimeout(socketTimeout);
            this.mIsClosed = false;
            this.mLock = ((WifiManager) this.mContext.getSystemService("wifi")).createMulticastLock("test wifi");
            Log.d(TAG, "mServerSocket is created, socket read timeout: " + socketTimeout + ", port: " + port);
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        }
    }

    public boolean setSoTimeout(int timeout) {
        try {
            this.mServerSocket.setSoTimeout(timeout);
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte receiveOneByte() {
        Log.d(TAG, "receiveOneByte() entrance");
        try {
            acquireLock();
            this.mServerSocket.receive(this.mReceivePacket);
            Log.d(TAG, "receive: " + (this.mReceivePacket.getData()[0] + 0));
            return this.mReceivePacket.getData()[0];
        } catch (IOException e) {
            e.printStackTrace();
            return Byte.MIN_VALUE;
        }
    }

    public byte[] receiveSpecLenBytes(int len) {
        Log.d(TAG, "receiveSpecLenBytes() entrance: len = " + len);
        try {
            acquireLock();
            this.mServerSocket.receive(this.mReceivePacket);
            byte[] recDatas = Arrays.copyOf(this.mReceivePacket.getData(), this.mReceivePacket.getLength());
            Log.d(TAG, "received len : " + recDatas.length);
            for (int i = 0; i < recDatas.length; i++) {
                Log.e(TAG, "recDatas[" + i + "]:" + recDatas[i]);
            }
            Log.e(TAG, "receiveSpecLenBytes: " + new String(recDatas));
            if (recDatas.length == len) {
                return recDatas;
            }
            Log.w(TAG, "received len is different from specific len, return null");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void interrupt() {
        Log.i(TAG, "USPSocketServer is interrupt");
        close();
    }

    public synchronized void close() {
        if (!this.mIsClosed) {
            Log.e(TAG, "mServerSocket is closed");
            this.mServerSocket.close();
            releaseLock();
            this.mIsClosed = true;
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
