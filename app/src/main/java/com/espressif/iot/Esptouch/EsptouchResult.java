package com.espressif.iot.Esptouch;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class EsptouchResult implements IEsptouchResult {
    private final String mBssid;
    private final InetAddress mInetAddress;
    private AtomicBoolean mIsCancelled = new AtomicBoolean(false);
    private final boolean mIsSuc;

    public EsptouchResult(boolean isSuc, String bssid, InetAddress inetAddress) {
        this.mIsSuc = isSuc;
        this.mBssid = bssid;
        this.mInetAddress = inetAddress;
    }

    public boolean isSuc() {
        return this.mIsSuc;
    }

    public String getBssid() {
        return this.mBssid;
    }

    public boolean isCancelled() {
        return this.mIsCancelled.get();
    }

    public void setIsCancelled(boolean isCancelled) {
        this.mIsCancelled.set(isCancelled);
    }

    public InetAddress getInetAddress() {
        return this.mInetAddress;
    }
}
