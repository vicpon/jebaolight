package com.example.wifizhilian;

import android.app.Application;
import android.content.Intent;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.BindDevObj;
import java.util.ArrayList;
import java.util.List;

public class SysApp extends Application {
    public static final boolean IsDebug = true;
    private static SysApp mMe;
    private AppConfig mConfig;
    private List<BindDevObj> mDevices = new ArrayList();
    private Intent sysSerIntent;

    public void onCreate() {
        mMe = this;
        this.sysSerIntent = new Intent(this, SysServer.class);
        startService(this.sysSerIntent);
        this.mConfig = new AppConfig(this);
        super.onCreate();
    }

    public void onTerminate() {
        if (this.sysSerIntent != null) {
            stopService(this.sysSerIntent);
        }
        super.onTerminate();
    }

    public static SysApp getMe() {
        return mMe;
    }

    public AppConfig getConfig() {
        return this.mConfig;
    }

    public String getDevName(String Product, String SSID) {
        if (Product.startsWith("MultiLED_63")) {
            return getResources().getString(R.string.str_dev_tongdongaljac) + SSID.substring(SSID.indexOf("-") + 1);
        }
        if (Product.startsWith("MultiLED_62")) {
            return getResources().getString(R.string.str_dev_tongdongal150) + SSID.substring(SSID.indexOf("-") + 1);
        }
        if (Product.startsWith("MultiLED_6")) {
            return getResources().getString(R.string.str_dev_tongdong6l) + SSID.substring(SSID.indexOf("-") + 1);
        }
        if (Product.startsWith("WaterPump_a")) {
            return getResources().getString(R.string.str_dev_waterpumpa) + SSID.substring(SSID.indexOf("-") + 1);
        }
        if (Product.startsWith("MultiLED_2") || Product.startsWith("WifiZ")) {
            return getResources().getString(R.string.str_dev_tongdong) + SSID.substring(SSID.indexOf("-") + 1);
        }
        return Product + "_" + SSID.substring(SSID.indexOf("-") + 1);
    }

    public List<BindDevObj> getDevices() {
        return this.mDevices;
    }

    public void addNewDevice(BindDevObj dev) {
        int ii = 0;
        while (ii < this.mDevices.size()) {
            if (!((BindDevObj) this.mDevices.get(ii)).getChipid().equals(dev.getChipid())) {
                ii++;
            } else {
                return;
            }
        }
        this.mDevices.add(dev);
    }

    public BindDevObj getDevByChipid(String cid) {
        for (int ii = 0; ii < this.mDevices.size(); ii++) {
            if (((BindDevObj) this.mDevices.get(ii)).getChipid().equals(cid)) {
                return (BindDevObj) this.mDevices.get(ii);
            }
        }
        return null;
    }

    public BindDevObj getDevByNetid(int nid) {
        for (int ii = 0; ii < this.mDevices.size(); ii++) {
            if (((BindDevObj) this.mDevices.get(ii)).getNetID() == nid) {
                return (BindDevObj) this.mDevices.get(ii);
            }
        }
        return null;
    }
}
