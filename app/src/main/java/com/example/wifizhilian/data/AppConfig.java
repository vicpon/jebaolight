package com.example.wifizhilian.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.DataCrypto;
import com.example.wifizhilian.libs.xLog;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class AppConfig {
    public static final String ADD_NEW_DEVICE = "com.example.wifizhilian.ADD_NEW_DEVICE";
    public static final String DEVICE_LIST_CHANG = "com.example.wifizhilian.DEVICE_LIST_CHANG";
    public static final String LOGIN_APP_OK = "com.example.wifizhilian.LOGIN_APP_OK";
    public static final String SEND_CMD_TODEV = "com.example.wifizhilian.SEND_CMD_TODEV";
    public static final String SEND_DATA_TOUDP = "com.example.wifizhilian.SEND_DATA_TOUDP";
    private static final String TAG = "SysConfig";
    private static final String defaultDatePattern = "yyyy-MM-dd ";
    private final String CONFIG_FNAME = "appcon.dat";
    private final String DESKEY = "WIFI";
    private float _density = 0.0f;
    private int _densityDpi = 0;
    private int _desktopH = 0;
    private int _desktopW = 0;
    private String _deviceMAC = null;
    private String _sysKey = null;
    private float _xdpi = 0.0f;
    private float _ydip = 0.0f;
    private int mAccID = 0;
    private String mAccount = "";
    private boolean mAutoLogin = false;
    private Context mContext;
    private String mEMail = "";
    private String mNickName = "";
    private String mPassword = "";
    private String mPhone = "";
    private boolean mRemember = false;
    private String mSHash = "";
    private String mSKey = "";
    private SharedPreferences mSharePre;

    public enum EnumNetType {
        None,
        Lan,
        P2P,
        Wan
    }

    public AppConfig(Context context) {
        this.mContext = context;
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        this.mSharePre = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        this._desktopW = dm.widthPixels;
        this._desktopH = dm.heightPixels;
        this._density = dm.density;
        this._densityDpi = dm.densityDpi;
        this._xdpi = dm.xdpi;
        this._ydip = dm.ydpi;
        this._deviceMAC = getLocalMacAddress();
    }

    public void saveData(String key, String val) {
        if (this.mSharePre != null) {
            Editor editor = this.mSharePre.edit();
            editor.putString(key, val);
            editor.commit();
        }
    }

    public void saveData(String key, int val) {
        if (this.mSharePre != null) {
            Editor editor = this.mSharePre.edit();
            editor.putInt(key, val);
            editor.commit();
        }
    }

    public void saveData(String key, boolean val) {
        if (this.mSharePre != null) {
            Editor editor = this.mSharePre.edit();
            editor.putBoolean(key, val);
            editor.commit();
        }
    }

    public void saveData(String key, float val) {
        if (this.mSharePre != null) {
            Editor editor = this.mSharePre.edit();
            editor.putFloat(key, val);
            editor.commit();
        }
    }

    public String getData(String key) {
        String reData = "";
        if (ComUtils.StrIsEmpty(key) || this.mSharePre == null) {
            return reData;
        }
        return this.mSharePre.getString(key, "");
    }

    public String getMacAddr() {
        return this._deviceMAC;
    }

    public int GetDesktopHeight() {
        return this._desktopH;
    }

    public int GetDesktopWidth() {
        return this._desktopW;
    }

    public String getDESKey() {
        return DataCrypto.MD5Encrypts("WIFI");
    }

    public String getLocalMacAddress() {
        return ((WifiManager) this.mContext.getSystemService("wifi")).getConnectionInfo().getMacAddress();
    }

    public String[] getVersion() {
        String[] version = new String[]{"null", "null", "null", "null"};
        try {
            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/version"), 8192);
            version[0] = localBufferedReader.readLine().split("\\s+")[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        version[1] = VERSION.RELEASE;
        version[2] = Build.MODEL;
        version[3] = Build.DISPLAY;
        return version;
    }

    public String getVersionName() {
        try {
            return this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public int getVersionCode() {
        int i = 0;
        try {
            return this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return i;
        }
    }

    public long[] getSDCardMemory() {
        long[] sdCardInfo = new long[2];
        if ("mounted".equals(Environment.getExternalStorageState())) {
            StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bSize = (long) sf.getBlockSize();
            long availBlocks = (long) sf.getAvailableBlocks();
            sdCardInfo[0] = bSize * ((long) sf.getBlockCount());
            sdCardInfo[1] = bSize * availBlocks;
        }
        return sdCardInfo;
    }

    public long[] getRomMemroy() {
        long[] romInfo = new long[2];
        romInfo[0] = getTotalInternalMemorySize();
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        romInfo[1] = ((long) stat.getBlockSize()) * ((long) stat.getAvailableBlocks());
        return romInfo;
    }

    public long getTotalInternalMemorySize() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) stat.getBlockCount()) * ((long) stat.getBlockSize());
    }

    public void getTotalMemory() {
        String str2 = "";
        try {
            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            while (true) {
                str2 = localBufferedReader.readLine();
                if (str2 != null) {
                    xLog.m5i(TAG, "---" + str2);
                } else {
                    return;
                }
            }
        } catch (IOException e) {
        }
    }

    public String getMaxCpuFreq() {
        String result = "";
        try {
            InputStream in = new ProcessBuilder(new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"}).start().getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    public String getMinCpuFreq() {
        String result = "";
        try {
            InputStream in = new ProcessBuilder(new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"}).start().getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    public String getCurCpuFreq() {
        String result = "N/A";
        try {
            result = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq")).readLine().trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public String getCpuName() {
        try {
            String[] array = new BufferedReader(new FileReader("/proc/cpuinfo")).readLine().split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public String getSysKey() {
        if (this._sysKey == null) {
            this._sysKey = this.mSharePre.getString("SystemKey", "");
        }
        if (!ComUtils.StrIsEmpty(this._sysKey)) {
            return this._sysKey;
        }
        this._sysKey = getIMEI();
        if (ComUtils.StrIsEmpty(this._sysKey)) {
            this._sysKey = getLocalMacAddress().replace(":", "");
        }
        saveData("SystemKey", this._sysKey);
        return this._sysKey;
    }

    public String getIMEI() {
        try {
            return ((TelephonyManager) this.mContext.getSystemService("phone")).getDeviceId();
        } catch (Exception e) {
            return "";
        }
    }

    public String GetDeviceInfo() {
        String reStr = ("" + "IMEI:" + getIMEI() + "\r\n") + "MAC:" + this._deviceMAC + "\r\n";
        String[] tmpArr = getVersion();
        return (((((((((((((reStr + "AppVersion:" + getVersionName() + "\r\n") + "AppVersionCode:" + getVersionCode() + "\r\n") + "KernelVersion:" + tmpArr[0] + "\r\n") + "Firmwareversion:" + tmpArr[1] + "\r\n") + "ModelVersion:" + tmpArr[2] + "\r\n") + "SystemVersion:" + tmpArr[3] + "\r\n") + "ScreenSize:" + this._desktopW + "x" + this._desktopH + "\r\n") + "Density:" + this._density + "\r\n") + "DensityDpi:" + this._densityDpi + "\r\n") + "Xdpi*Ydpi:" + this._xdpi + "x" + this._ydip + "\r\n") + "CpuName:" + getCpuName() + "\r\n") + "MaxCpuFreq:" + getMaxCpuFreq() + "\r\n") + "RomMemroy:" + getRomMemroy()[0] + "\r\n") + "SDCardMemory:" + getSDCardMemory()[0] + "\r\n";
    }

    public String getSHash() {
        return this.mSHash;
    }

    public void setSHash(String mSHash) {
        this.mSHash = mSHash;
    }

    public String getSKey() {
        return this.mSKey;
    }

    public void setSKey(String mSKey) {
        this.mSKey = mSKey;
    }

    public String getAccount() {
        if (ComUtils.StrIsEmpty(this.mAccount) && this.mSharePre != null) {
            this.mAccount = this.mSharePre.getString("Account", "");
        }
        return this.mAccount;
    }

    public void setAccount(String mAccount) {
        if (this.mAccount != mAccount) {
            this.mAccount = mAccount;
            saveData("Account", mAccount);
        }
    }

    public String getPassword() {
        if (ComUtils.StrIsEmpty(this.mPassword) && this.mSharePre != null) {
            this.mPassword = this.mSharePre.getString("Password", "");
        }
        return this.mPassword;
    }

    public void setPassword(String mPassword) {
        if (this.mPassword != mPassword) {
            this.mPassword = mPassword;
            saveData("Password", mPassword);
        }
    }

    public boolean isRemember() {
        if (!(this.mRemember || this.mSharePre == null)) {
            this.mRemember = this.mSharePre.getBoolean("Remember", true);
        }
        return this.mRemember;
    }

    public void setRemember(boolean mRemember) {
        if (this.mRemember != mRemember) {
            this.mRemember = mRemember;
            saveData("Remember", mRemember);
        }
    }

    public String getNickName() {
        return this.mNickName;
    }

    public void setNickName(String mNickName) {
        this.mNickName = mNickName;
    }

    public String getPhone() {
        return this.mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getEMail() {
        return this.mEMail;
    }

    public void setEMail(String mEMail) {
        this.mEMail = mEMail;
    }

    public boolean isAutoLogin() {
        if (!(this.mAutoLogin || this.mSharePre == null)) {
            this.mAutoLogin = this.mSharePre.getBoolean("AutoLogin", true);
        }
        return this.mAutoLogin;
    }

    public void setAutoLogin(boolean mAutoLogin) {
        if (this.mAutoLogin != mAutoLogin) {
            this.mAutoLogin = mAutoLogin;
            saveData("AutoLogin", mAutoLogin);
        }
    }

    public int getAccID() {
        return this.mAccID;
    }

    public void setAccID(int mAccID) {
        this.mAccID = mAccID;
    }
}
