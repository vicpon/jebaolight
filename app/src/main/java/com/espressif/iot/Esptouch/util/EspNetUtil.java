package com.espressif.iot.Esptouch.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EspNetUtil {
    public static InetAddress getLocalInetAddress(Context context) {
        InetAddress localInetAddr = null;
        try {
            localInetAddr = InetAddress.getByName(__formatString(((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getIpAddress()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return localInetAddr;
    }

    private static String __formatString(int value) {
        String strValue = "";
        byte[] ary = __intToByteArray(value);
        for (int i = ary.length - 1; i >= 0; i--) {
            strValue = strValue + (ary[i] & 255);
            if (i > 0) {
                strValue = strValue + ".";
            }
        }
        return strValue;
    }

    private static byte[] __intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) ((value >>> (((b.length - 1) - i) * 8)) & 255);
        }
        return b;
    }

    public static InetAddress parseInetAddr(byte[] inetAddrBytes, int offset, int count) {
        InetAddress inetAddress = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(Integer.toString(inetAddrBytes[offset + i] & 255));
            if (i != count - 1) {
                sb.append('.');
            }
        }
        try {
            inetAddress = InetAddress.getByName(sb.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress;
    }

    public static byte[] parseBssid2bytes(String bssid) {
        String[] bssidSplits = bssid.split(":");
        byte[] result = new byte[bssidSplits.length];
        for (int i = 0; i < bssidSplits.length; i++) {
            result[i] = (byte) Integer.parseInt(bssidSplits[i], 16);
        }
        return result;
    }
}
