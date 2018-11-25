package com.espressif.iot.Esptouch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.espressif.iot.Esptouch.util.ByteUtil;
import java.lang.reflect.Field;

public class EspWifiAdminSimple {
    private final Context mContext;

    public EspWifiAdminSimple(Context context) {
        this.mContext = context;
    }

    public String getWifiConnectedSsid() {
        WifiInfo mWifiInfo = getConnectionInfo();
        if (mWifiInfo == null || !isWifiConnected()) {
            return null;
        }
        int len = mWifiInfo.getSSID().length();
        if (mWifiInfo.getSSID().startsWith("\"") && mWifiInfo.getSSID().endsWith("\"")) {
            return mWifiInfo.getSSID().substring(1, len - 1);
        }
        return mWifiInfo.getSSID();
    }

    public String getWifiConnectedSsidAscii(String ssid) {
        String ssidAscii = ssid;
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        wifiManager.startScan();
        boolean isBreak = false;
        long start = System.currentTimeMillis();
        String ssidAscii2 = ssidAscii;
        while (true) {
            try {
                Thread.sleep(20);
                for (ScanResult scanResult : wifiManager.getScanResults()) {
                    if (scanResult.SSID != null && scanResult.SSID.equals(ssid)) {
                        isBreak = true;
                        try {
                            Field wifiSsidfield = ScanResult.class.getDeclaredField("wifiSsid");
                            wifiSsidfield.setAccessible(true);
                            Class<?> wifiSsidClass = wifiSsidfield.getType();
                            Object wifiSsid = wifiSsidfield.get(scanResult);
                            ssidAscii = new String((byte[]) wifiSsidClass.getDeclaredMethod("getOctets", new Class[0]).invoke(wifiSsid, new Object[0]), ByteUtil.ESPTOUCH_ENCODING_CHARSET);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            ssidAscii = ssidAscii2;
                        }
                    }
                }
                ssidAscii = ssidAscii2;
                if (System.currentTimeMillis() - start >= 100 || isBreak) {
                    return ssidAscii;
                }
                ssidAscii2 = ssidAscii;
            } catch (InterruptedException e2) {
                return ssidAscii2;
            }
        }
    }

    public String getWifiConnectedBssid() {
        WifiInfo mWifiInfo = getConnectionInfo();
        if (mWifiInfo == null || !isWifiConnected()) {
            return null;
        }
        return mWifiInfo.getBSSID();
    }

    private WifiInfo getConnectionInfo() {
        return ((WifiManager) this.mContext.getSystemService("wifi")).getConnectionInfo();
    }

    private boolean isWifiConnected() {
        NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isConnected();
        }
        return false;
    }

    private NetworkInfo getWifiNetworkInfo() {
        return ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getNetworkInfo(1);
    }
}
