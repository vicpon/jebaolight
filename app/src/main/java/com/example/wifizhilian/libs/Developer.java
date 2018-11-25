package com.example.wifizhilian.libs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.ServerProtocol;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Developer {
    private static final String WebServer = "http://app.ekewe.com/Interface.aspx";
    private String TAG = "Developer";
    private Runnable checkRun = new C03032();
    private boolean isDestroy = false;
    private Context mContext;
    private String mDeviceID = "";
    private Handler mHandler = new Handler();
    private String mLableName = "";
    private String mPackageName = "";
    private SharedPreferences mSharePre;
    private String mVerName = "";
    private Runnable regeditRun = new C03011();
    private Runnable runError = new C03043();

    /* renamed from: com.example.wifizhilian.libs.Developer$1 */
    class C03011 implements Runnable {

        /* renamed from: com.example.wifizhilian.libs.Developer$1$1 */
        class C03001 implements Runnable {
            C03001() {
            }

            public void run() {
                String key = Developer.this.getIMEI();
                if (ComUtils.StrIsEmpty(key)) {
                    key = Developer.this.getLocalMacAddress();
                }
                Map<String, String> params = new HashMap();
                params.put("ACT", "REGEDIT");
                params.put("MAC", key);
                params.put("SoftName", Developer.this.mPackageName);
                params.put("SoftVer", Developer.this.mVerName);
                params.put("SysVer", VERSION.SDK);
                params.put("DW", String.valueOf(Developer.this.mContext.getResources().getDisplayMetrics().widthPixels));
                params.put("DH", String.valueOf(Developer.this.mContext.getResources().getDisplayMetrics().heightPixels));
                params.put("OInfo", "BX#" + DataCrypto.Base64Encode(Developer.this.getSysInfo()));
                params.put("VeriKey", HttpClient.getPostVerify(params));
                try {
                    String reStr = HttpClient.postFromHttpClient(Developer.WebServer, params);
                    if (!ComUtils.StrIsEmpty(reStr)) {
                        String tmpStr = SimpleXml.getXmlValue(reStr, ServerProtocol.DIALOG_PARAM_STATE);
                        if (ComUtils.StrIsEmpty(tmpStr)) {
                            Developer.this.mHandler.postDelayed(Developer.this.regeditRun, 86400000);
                        } else if (tmpStr.equals(AppEventsConstants.EVENT_PARAM_VALUE_NO)) {
                            tmpStr = SimpleXml.getXmlValue(reStr, NotificationCompat.CATEGORY_MESSAGE);
                            if (!ComUtils.StrIsEmpty(tmpStr)) {
                                Developer.this.mDeviceID = tmpStr;
                                if (Developer.this.mSharePre != null) {
                                    Editor editor = Developer.this.mSharePre.edit();
                                    editor.putString("DeveloperID", tmpStr);
                                    editor.commit();
                                }
                                Developer.this.mHandler.postDelayed(Developer.this.checkRun, 86400000);
                            }
                        } else {
                            System.out.print("注册错误");
                            System.out.print(SimpleXml.getXmlValue(reStr, NotificationCompat.CATEGORY_MESSAGE));
                        }
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                }
            }
        }

        C03011() {
        }

        public void run() {
            new Thread(new C03001()).start();
        }
    }

    /* renamed from: com.example.wifizhilian.libs.Developer$2 */
    class C03032 implements Runnable {

        /* renamed from: com.example.wifizhilian.libs.Developer$2$1 */
        class C03021 implements Runnable {
            C03021() {
            }

            public void run() {
                Map<String, String> params = new HashMap();
                params.put("ACT", "CHECK");
                params.put("DeviceID", Developer.this.mDeviceID);
                params.put("MAC", Developer.this.getIMEI());
                params.put("SoftName", Developer.this.mPackageName);
                params.put("SoftVer", Developer.this.mVerName);
                params.put("SysVer", VERSION.SDK);
                params.put("OInfo", "BX#" + DataCrypto.Base64Encode(Developer.this.getRunInfo()));
                params.put("VeriKey", HttpClient.getPostVerify(params));
                try {
                    String reStr = HttpClient.postFromHttpClient(Developer.WebServer, params);
                    if (!ComUtils.StrIsEmpty(reStr)) {
                        String tmpStr = SimpleXml.getXmlValue(reStr, ServerProtocol.DIALOG_PARAM_STATE);
                        if (!ComUtils.StrIsEmpty(tmpStr)) {
                            if (!tmpStr.equals(AppEventsConstants.EVENT_PARAM_VALUE_NO) && !tmpStr.equals(AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                                if (tmpStr.equals("2")) {
                                    System.out.print("有新的版");
                                } else if (tmpStr.equals("3")) {
                                    System.out.print("非法版本" + SimpleXml.getXmlValue(reStr, NotificationCompat.CATEGORY_MESSAGE));
                                } else if (tmpStr.equals("4")) {
                                    System.out.print("没有软件信息");
                                } else if (tmpStr.equals("5")) {
                                    Editor editor = Developer.this.mSharePre.edit();
                                    editor.remove("DeveloperID");
                                    editor.commit();
                                    Developer.this.mHandler.postDelayed(Developer.this.regeditRun, 10000);
                                    return;
                                } else if (tmpStr.equals("6")) {
                                    Developer.this.mHandler.postDelayed(Developer.this.runError, (long) ((new Random().nextInt(120) + 30) * 1000));
                                    return;
                                } else if (tmpStr.equals("7")) {
                                    System.exit(0);
                                }
                            } else {
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                }
                if (!Developer.this.isDestroy) {
                    Developer.this.mHandler.postDelayed(Developer.this.checkRun, 86400000);
                }
            }
        }

        C03032() {
        }

        public void run() {
            new Thread(new C03021()).start();
        }
    }

    /* renamed from: com.example.wifizhilian.libs.Developer$3 */
    class C03043 implements Runnable {
        C03043() {
        }

        public void run() {
            int ii = 0;
            while (true) {
                ii++;
            }
        }
    }

    public Developer(Context context) {
        this.mContext = context;
        this.mSharePre = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        this.mDeviceID = this.mSharePre.getString("DeveloperID", "");
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(this.mContext.getPackageName(), 0);
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mContext.getPackageName(), 0);
            this.mVerName = packInfo.versionName;
            this.mLableName = (String) packageManager.getApplicationLabel(applicationInfo);
            this.mPackageName = packInfo.packageName;
        } catch (NameNotFoundException e) {
            System.out.print(e.toString());
        }
        this.mHandler.removeCallbacks(this.regeditRun);
        this.mHandler.removeCallbacks(this.checkRun);
        if (ComUtils.StrIsEmpty(this.mDeviceID)) {
            this.mHandler.postDelayed(this.regeditRun, 20000);
        } else {
            this.mHandler.postDelayed(this.checkRun, 20000);
        }
    }

    @TargetApi(9)
    private String getSysInfo() {
        return (((("" + "SotName:" + this.mLableName + "\r\n") + "density:" + this.mContext.getResources().getDisplayMetrics().density + "\r\n") + "densityDpi:" + this.mContext.getResources().getDisplayMetrics().densityDpi + "\r\n") + "SERIAL:" + Build.SERIAL + "\r\n") + "MODEL:" + Build.MODEL + "\r\n";
    }

    private String getRunInfo() {
        String reStr = "";
        try {
            String tmpStr;
            BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
            while (true) {
                tmpStr = br.readLine();
                if (ComUtils.StrIsEmpty(tmpStr)) {
                    break;
                }
                reStr = reStr + tmpStr + "\r\n";
            }
            br = new BufferedReader(new FileReader("/proc/meminfo"));
            while (true) {
                tmpStr = br.readLine();
                if (ComUtils.StrIsEmpty(tmpStr)) {
                    break;
                }
                reStr = reStr + tmpStr + "\r\n";
            }
        } catch (FileNotFoundException e) {
            System.out.print(e.toString());
        } catch (IOException e2) {
            System.out.print(e2.toString());
        }
        return reStr;
    }

    public String getIMEI() {
        try {
            return ((TelephonyManager) this.mContext.getSystemService("phone")).getDeviceId();
        } catch (Exception e) {
            return "";
        }
    }

    public String getLocalMacAddress() {
        return ((WifiManager) this.mContext.getSystemService("wifi")).getConnectionInfo().getMacAddress();
    }

    public void Destroy() {
        this.isDestroy = true;
        this.mHandler.removeCallbacks(this.checkRun);
        this.mHandler.removeCallbacks(this.regeditRun);
    }
}
