package com.example.wifizhilian;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.data.DevGroupObj;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.NetInterface;
import com.example.wifizhilian.libs.xLog;
import java.util.List;

public class SysServer extends Service {
    private static final String TAG = "SysServer";
    Handler mHandler = new C02822();
    private UDPServer mUdpServer;
    private BroadcastReceiver updataReceiver = new C02811();
    Runnable waitGetMyBindDevice = new C02844();
    Runnable waitRunAddNewDevice = new C02833();

    /* renamed from: com.example.wifizhilian.SysServer$1 */
    class C02811 extends BroadcastReceiver {
        C02811() {
        }

        public void onReceive(Context context, Intent intent) {
            xLog.m5i(SysServer.TAG, "updataReceiver:" + intent.getAction());
            if (intent.getAction().equals(AppConfig.SEND_CMD_TODEV)) {
                int devid = intent.getIntExtra("DEVID", 0);
                int code = intent.getIntExtra("CODE", 0);
                byte[] data = (byte[]) intent.getSerializableExtra("DATA");
                SysServer.this.mUdpServer.SendCmdToDev(devid, code, data);
                BindDevObj dev = SysApp.getMe().getDevByNetid(devid);
                if (dev != null && dev.isGroup() == 1) {
                    for (int ii = 0; ii < dev.getGroups().size(); ii++) {
                        SysServer.this.mUdpServer.SendCmdToDev(((DevGroupObj) dev.getGroups().get(ii)).getSonID(), code, data);
                    }
                }
            } else if (intent.getAction().equals(AppConfig.SEND_DATA_TOUDP)) {
                SysServer.this.mUdpServer.SendData(intent.getStringExtra("IPADDR"), intent.getIntExtra("PORT", 0), (byte[]) intent.getSerializableExtra("DATA"));
            } else if (intent.getAction().equals(AppConfig.ADD_NEW_DEVICE)) {
                SysServer.this.AddNewDevice();
            } else if (intent.getAction().equals(AppConfig.LOGIN_APP_OK)) {
                SysServer.this.mHandler.postDelayed(SysServer.this.waitGetMyBindDevice, 10);
            } else if (intent.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                System.out.print("Net connect state change");
                if (SysServer.this.checkNetworkConnection()) {
                    SysServer.this.AddNewDevice();
                }
            }
        }
    }

    /* renamed from: com.example.wifizhilian.SysServer$2 */
    class C02822 extends Handler {
        C02822() {
        }

        public void handleMessage(Message msg) {
            String tmpStr = "";
            switch (msg.what) {
                case NetInterface.Net_GetBindList_OK /*20004*/:
                    SysServer.this.sendBroadcast(new Intent(AppConfig.ADD_NEW_DEVICE));
                    SysServer.this.mUdpServer.updateBindDev(SysApp.getMe().getDevices());
                    return;
                case NetInterface.Net_AddNew_Dev /*20007*/:
                    SysServer.this.sendBroadcast(new Intent(AppConfig.DEVICE_LIST_CHANG));
                    return;
                case NetInterface.Net_Data_Error /*20019*/:
                    tmpStr = (String) msg.obj;
                    if (!ComUtils.StrIsEmpty(tmpStr) && tmpStr.equals("BindDevice")) {
                        SysServer.this.mHandler.postDelayed(SysServer.this.waitRunAddNewDevice, 60000);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.example.wifizhilian.SysServer$3 */
    class C02833 implements Runnable {
        C02833() {
        }

        public void run() {
            SysServer.this.AddNewDevice();
        }
    }

    /* renamed from: com.example.wifizhilian.SysServer$4 */
    class C02844 implements Runnable {
        C02844() {
        }

        public void run() {
            SysServer.this.GetMyBindDevice();
        }
    }

    /* renamed from: com.example.wifizhilian.SysServer$5 */
    class C02855 implements Runnable {
        C02855() {
        }

        public void run() {
            List<BindDevObj> list = SysApp.getMe().getDevices();
            for (int ii = 0; ii < list.size(); ii++) {
                if (((BindDevObj) list.get(ii)).getState() == 0) {
                    new NetInterface(SysServer.this, SysServer.this.mHandler).BindDevice((BindDevObj) list.get(ii));
                }
            }
        }
    }

    /* renamed from: com.example.wifizhilian.SysServer$6 */
    class C02866 implements Runnable {
        C02866() {
        }

        public void run() {
            new NetInterface(SysServer.this, SysServer.this.mHandler).GetDevList();
        }
    }

    public class SerialBinder extends Binder {
        public SysServer getService() {
            return SysServer.this;
        }
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.mUdpServer = new UDPServer();
        this.mUdpServer.Start();
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (!this.mUdpServer.isRun()) {
            this.mUdpServer.Stop();
            this.mUdpServer.Start();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConfig.SEND_CMD_TODEV);
        intentFilter.addAction(AppConfig.SEND_DATA_TOUDP);
        intentFilter.addAction(AppConfig.ADD_NEW_DEVICE);
        intentFilter.addAction(AppConfig.LOGIN_APP_OK);
        registerReceiver(this.updataReceiver, intentFilter);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.updataReceiver, mFilter);
        xLog.m5i(TAG, "Serial Server Start.");
    }

    public void onDestroy() {
        unregisterReceiver(this.updataReceiver);
        this.mUdpServer.Stop();
        this.mUdpServer = null;
        super.onDestroy();
    }

    private void AddNewDevice() {
        new Thread(new C02855()).start();
    }

    private void GetMyBindDevice() {
        new Thread(new C02866()).start();
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(1);
        NetworkInfo mobile = connMgr.getNetworkInfo(0);
        if (wifi != null && wifi.isAvailable()) {
            return true;
        }
        if (mobile == null || !mobile.isAvailable()) {
            return false;
        }
        return true;
    }
}
