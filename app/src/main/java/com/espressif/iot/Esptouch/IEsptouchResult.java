package com.espressif.iot.Esptouch;

import java.net.InetAddress;

public interface IEsptouchResult {
    String getBssid();

    InetAddress getInetAddress();

    boolean isCancelled();

    boolean isSuc();
}
