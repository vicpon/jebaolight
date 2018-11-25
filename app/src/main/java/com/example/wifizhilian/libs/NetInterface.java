package com.example.wifizhilian.libs;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.data.AppConfig;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.data.DevSetObj;
import com.example.wifizhilian.data.DevTranObj;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.share.internal.ShareConstants;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

public class NetInterface {
    public static final int Net_AddNew_Dev = 20007;
    public static final int Net_DataSubmit_FAIL = 20014;
    public static final int Net_DataSubmit_OK = 20003;
    public static final int Net_Data_Error = 20019;
    public static final int Net_EMailExist_Error = 20012;
    public static final int Net_EditDevSon_OK = 20008;
    public static final int Net_EditRunTask_OK = 20009;
    public static final int Net_GetBindList_OK = 20004;
    public static final int Net_GetDevPara_OK = 20062;
    public static final int Net_GetDevSet_OK = 20005;
    public static final int Net_GetDevSon_OK = 20006;
    public static final int Net_GetNewVer = 20050;
    public static final int Net_NamePwd_Error = 20011;
    public static final int Net_NoNewVer = 20051;
    public static final int Net_RunTaskData_OK = 20010;
    public static final int Net_SaveDevPara_OK = 20061;
    public static final int Net_UnHerbindDev = 20041;
    public static final int Net_UnKnown_Error = 20018;
    public static final int Net_UnbindDev = 20040;
    public static final int Net_UserLogin_OK = 20001;
    public static final int Net_UserRegedit_FAIL = 20013;
    public static final int Net_UserRegedit_OK = 20002;
    public static final String ServerUrl = "http://iot.ekewe.net";
    private static final String TAG = "NetInterface";
    public static final int UnAllRelation = 20042;
    private Context _context = null;
    private Handler _handler = null;

    /* renamed from: com.example.wifizhilian.libs.NetInterface$8 */
    class C03128 implements Runnable {
        C03128() {
        }

        public void run() {
            AppConfig _config = SysApp.getMe().getConfig();
            Map<String, String> para = new HashMap();
            para.put("AppKey", _config.getLocalMacAddress());
            para.put("SKey", _config.getSKey());
            para.put("SHash", _config.getSHash());
            para.put("VeriKey", HttpClient.getPostVerify(para));
            try {
                JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/GetDevList", para));
                String codeStr = root.getString("statusCode");
                if (ComUtils.StrIsEmpty(codeStr) || codeStr.equals("300")) {
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    return;
                }
                JSONArray devlist = root.getJSONArray("devlist");
                SysApp.getMe().getDevices().clear();
                if (devlist.length() > 0) {
                    for (int ii = 0; ii < devlist.length(); ii++) {
                        JSONObject dev = devlist.getJSONObject(ii);
                        BindDevObj obj = new BindDevObj();
                        obj.setChipid(dev.getString("DevKey"));
                        obj.setMAC(dev.getString("DevKey2"));
                        obj.setNetID(Integer.parseInt(dev.getString("BindDevID")));
                        obj.setProduct(dev.getString("ProductName"));
                        obj.setDevType(dev.getInt("DevType"));
                        obj.setSSID(dev.getString("SSID"));
                        obj.setNickName(dev.getString("NickName"));
                        obj.setMtoPTime(ComUtils.StrToInt(dev.getString("HandStopTime"), 0));
                        obj.setUserID(SysApp.getMe().getConfig().getAccount());
                        obj.setState(1);
                        obj.setUpdateCode(AppEventsConstants.EVENT_PARAM_VALUE_NO);
                        obj.setGroup(ComUtils.StrToInt(dev.getString("isGroup"), 0));
                        SysApp.getMe().getDevices().add(obj);
                    }
                }
                NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_GetBindList_OK);
            } catch (Exception e) {
                System.out.print(e.toString());
                NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
            }
        }
    }

    public NetInterface(Context context, Handler pHandler) {
        this._context = context;
        this._handler = pHandler;
    }

    private void sendDataError(String msginfo) {
        Message msg = new Message();
        msg.what = Net_Data_Error;
        msg.obj = msginfo;
        this._handler.sendMessage(msg);
    }

    private void sendMessageData(int code, Object msginfo) {
        if (this._handler != null) {
            Message msg = new Message();
            msg.what = code;
            msg.obj = msginfo;
            this._handler.sendMessage(msg);
        }
    }

    private void sendMessageData(int code) {
        if (this._handler != null) {
            this._handler.sendEmptyMessage(code);
        }
    }

    public void Login(final String Account, final String PassWord, final boolean isRemember) {
        new Thread(new Runnable() {
            public void run() {
                String appTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));
                TimeZone tz = TimeZone.getDefault();
                AppConfig _config = SysApp.getMe().getConfig();
                String reStr = _config.GetDeviceInfo();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("Account", Account);
                para.put("PassWord", PassWord);
                para.put("TimeZone", String.valueOf(tz.getRawOffset() / 3600000));
                para.put("AppTime", appTime);
                para.put(ShareConstants.WEB_DIALOG_PARAM_DATA, "BX#" + DataCrypto.Base64Encode(reStr));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/Login", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_NamePwd_Error);
                        return;
                    }
                    String SHash = root.getString("SHash");
                    String SKey = root.getString("SKey");
                    int Uid = root.getInt("AccID");
                    if (ComUtils.StrIsEmpty(SHash) || ComUtils.StrIsEmpty(SKey)) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_UnKnown_Error);
                        return;
                    }
                    _config.setSHash(SHash);
                    _config.setAccID(Uid);
                    _config.setSKey(SKey);
                    if (isRemember) {
                        _config.setRemember(isRemember);
                        _config.setAccount(Account);
                        _config.setPassword(PassWord);
                    }
                    JSONObject userInfo = root.getJSONObject("UserInfo");
                    _config.setEMail(userInfo.getString("Email"));
                    _config.setPhone(userInfo.getString("Phone"));
                    _config.setNickName(userInfo.getString("NickName"));
                    NetInterface.this._handler.sendEmptyMessage(20001);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void Regedit(final String NickName, final String PassWord, final String EMail) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                String reStr = _config.GetDeviceInfo();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("NickName", NickName);
                para.put("PassWord", PassWord);
                para.put("Email", EMail);
                para.put(ShareConstants.WEB_DIALOG_PARAM_DATA, "BX#" + DataCrypto.Base64Encode(reStr));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/Regedit", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_UserRegedit_FAIL);
                        return;
                    }
                    String SHash = root.getString("SHash");
                    String SKey = root.getString("SKey");
                    String Account = root.getString("Account");
                    int Uid = root.getInt("AccID");
                    if (ComUtils.StrIsEmpty(SHash) || ComUtils.StrIsEmpty(SKey) || ComUtils.StrIsEmpty(Account)) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_UnKnown_Error);
                        return;
                    }
                    _config.setSHash(SHash);
                    _config.setSKey(SKey);
                    _config.setRemember(true);
                    _config.setAutoLogin(true);
                    _config.setAccID(Uid);
                    _config.setAccount(Account);
                    _config.setPassword(PassWord);
                    _config.setNickName(NickName);
                    NetInterface.this._handler.sendEmptyMessage(20002);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void SaveInfo(String NickName, String PassWord, String EMail, String Phone) {
        final String str = NickName;
        final String str2 = PassWord;
        final String str3 = EMail;
        final String str4 = Phone;
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("NickName", str);
                para.put("PassWord", str2);
                para.put("Email", str3);
                para.put("Phone", str4);
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/SaveUserInfo", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                        return;
                    }
                    if (!ComUtils.StrIsEmpty(str2)) {
                        _config.setPassword(str2);
                    }
                    _config.setNickName(str);
                    _config.setEMail(str3);
                    _config.setPhone(str4);
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_OK);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void BindDevice(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                int timz = NetInterface.getCurrentTimeZone();
                xLog.m5i(NetInterface.TAG, "=======TimeZone:" + timz);
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SSID", obj.getSSID());
                para.put("ProductName", obj.getProduct());
                para.put("TimeZone", String.valueOf(timz));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/BindDev", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || codeStr.equals("300")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                        return;
                    }
                    if (obj.getNetID() <= 0) {
                        obj.setNetID(Integer.parseInt(root.getString(ShareConstants.WEB_DIALOG_PARAM_MESSAGE)));
                    }
                    obj.setState(1);
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_AddNew_Dev);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this.sendDataError("BindDevice");
                }
            }
        }).start();
    }

    public void UnBindDevice(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/UnBindDev", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || codeStr.equals("300")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_UnbindDev);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this.sendDataError("BindDevice");
                }
            }
        }).start();
    }

    public void UnHerBindDevice(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/UnHerBindDev", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || codeStr.equals("300")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_UnHerbindDev);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this.sendDataError("BindDevice");
                }
            }
        }).start();
    }

    public void UnAllRelation(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/UnAllRelation", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || codeStr.equals("300")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.UnAllRelation);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this.sendDataError("UnAllRelation");
                }
            }
        }).start();
    }

    public void GetDevList() {
        new Thread(new C03128()).start();
    }

    public void getSonDevlist(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SSID", obj.getSSID());
                para.put("ProductName", obj.getProduct());
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/GetSonDevList", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || codeStr.equals("300")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                        return;
                    }
                    JSONArray devlist = root.getJSONArray("devlist");
                    if (devlist.length() > 0) {
                        for (int ii = 0; ii < devlist.length(); ii++) {
                            JSONObject dev = devlist.getJSONObject(ii);
                            BindDevObj obj = new BindDevObj();
                            obj.setChipid(dev.getString("DevKey"));
                            obj.setMAC(dev.getString("DevKey2"));
                            obj.setNetID(Integer.parseInt(dev.getString("ID")));
                            obj.setProduct(dev.getString("ProductName"));
                            obj.setSSID(dev.getString("SSID"));
                            obj.setNickName(dev.getString("NickName"));
                            obj.setUserID(SysApp.getMe().getConfig().getAccount());
                            obj.setState(1);
                            obj.setUpdateCode(AppEventsConstants.EVENT_PARAM_VALUE_NO);
                            SysApp.getMe().addNewDevice(obj);
                        }
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_GetDevSon_OK);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    private String getDevType(String devName) {
        if (devName.startsWith("MultiLED_2")) {
            return AppEventsConstants.EVENT_PARAM_VALUE_YES;
        }
        if (devName.startsWith("MultiLED_6")) {
            return "2";
        }
        if (devName.startsWith("WaterPump_a")) {
            return "3";
        }
        return String.valueOf(0);
    }

    public void SaveFeedback(final String Title, final String Content) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("Title", Title);
                para.put("Content", Content);
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/SaveFeedback", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_OK);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void getDevSetData(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SSID", obj.getSSID());
                para.put("ProductName", obj.getProduct());
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/GetDevTaskList", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                        return;
                    }
                    int ii;
                    JSONArray tlist = root.getJSONArray("tasklist");
                    if (tlist.length() > 0) {
                        for (ii = 0; ii < tlist.length(); ii++) {
                            JSONObject setObj = tlist.getJSONObject(ii);
                            DevSetObj mDevSetObj = new DevSetObj();
                            mDevSetObj.setID(setObj.getInt("ID"));
                            mDevSetObj.setChipID(obj.getChipid());
                            mDevSetObj.setDevID(obj.getNetID());
                            mDevSetObj.setDay(setObj.getInt("SetIndex"));
                            mDevSetObj.setNickName(setObj.getString("RuleName"));
                            mDevSetObj.setState(1);
                            if (setObj.getInt("State") == 2) {
                                mDevSetObj.setRun(true);
                            }
                            mDevSetObj.setData(setObj.getString("Parameter1"));
                            obj.addSet(mDevSetObj);
                        }
                    }
                    tlist = root.getJSONArray("tranlist");
                    if (tlist.length() > 0) {
                        for (ii = 0; ii < tlist.length(); ii++) {
                            JSONObject Obj = tlist.getJSONObject(ii);
                            DevTranObj tranObj = new DevTranObj();
                            tranObj.setID(Obj.getInt("ID"));
                            tranObj.setChipID(obj.getChipid());
                            tranObj.setDevID(obj.getNetID());
                            tranObj.setSetIndex(Obj.getInt("SetIndex"));
                            tranObj.setNickName(Obj.getString("TranName"));
                            tranObj.setStartAct(Obj.getInt("StartAct"));
                            tranObj.setPeriod(Obj.getInt("Period"));
                            tranObj.setEndAct(Obj.getInt("EndAct"));
                            tranObj.setState(1);
                            obj.addTran(tranObj);
                        }
                    }
                    tlist = root.getJSONArray("grouplist");
                    if (tlist.length() > 0) {
                        boolean isGetSon = false;
                        for (ii = 0; ii < tlist.length(); ii++) {
                            int sonID = tlist.getJSONObject(ii).getInt("SonDevID");
                            obj.addGroup(sonID);
                            if (SysApp.getMe().getDevByNetid(sonID) == null) {
                                isGetSon = true;
                            }
                        }
                        if (isGetSon) {
                            NetInterface.this.getSonDevlist(obj);
                        }
                    }
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_GetDevSet_OK);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void saveDevSetData(final BindDevObj obj, final DevSetObj devset) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SSID", obj.getSSID());
                para.put("ProductName", obj.getProduct());
                para.put("SetIndex", String.valueOf(devset.getDay()));
                para.put("RuleName", devset.getNickName());
                para.put("Parameter1", devset.getData());
                para.put("Parameter2", "");
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/SaveDevTaskInfo", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                        return;
                    }
                    String chipid = root.getString("chipid");
                    String ruleday = root.getString("ruleday");
                    if (!(ComUtils.StrIsEmpty(chipid) || ComUtils.StrIsEmpty(ruleday))) {
                        DevSetObj mDevSetObj = obj.getSetByIndex(Integer.parseInt(ruleday));
                        if (mDevSetObj != null) {
                            mDevSetObj.setState(1);
                        }
                    }
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_OK);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void setRunTask(final BindDevObj obj, final int curIndex) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SSID", obj.getSSID());
                para.put("ProductName", obj.getProduct());
                para.put("SetIndex", String.valueOf(curIndex));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/SetRunTask", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                        return;
                    }
                    String chipid = root.getString("chipid");
                    String ruleday = root.getString("ruleday");
                    if (!(ComUtils.StrIsEmpty(chipid) || ComUtils.StrIsEmpty(ruleday))) {
                        DevSetObj mDevSetObj = obj.getSetByIndex(Integer.parseInt(ruleday));
                        if (mDevSetObj != null) {
                            mDevSetObj.setState(1);
                        }
                    }
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_RunTaskData_OK);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void deleDevSetData(final BindDevObj obj, final String index) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SSID", obj.getSSID());
                para.put("ProductName", obj.getProduct());
                para.put("setIndex", index);
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/DelDevTaskInfo", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_OK);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void saveDevExtInfo(final BindDevObj obj, final String Name, final int mtopTime) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("NickName", Name);
                para.put("MtoPTime", String.valueOf(mtopTime));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/SaveBindDevExtInfo", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_OK);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void modiDevGroup(final BindDevObj obj, final String setSonIDs, final String delSonIDs) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SetSonIDs", setSonIDs);
                para.put("DelSonIDs", delSonIDs);
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/SaveDevGroup", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_EditDevSon_OK);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public static int getCurrentTimeZone() {
        return TimeZone.getDefault().getRawOffset() / 3600000;
    }

    public void getNewVer(final int curVer) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("CurVer", String.valueOf(curVer));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/getNewVer", para));
                    String codeStr = JsonGet.getStr(root, "statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this.sendMessageData(NetInterface.Net_Data_Error);
                        return;
                    }
                    int newVer = JsonGet.getInt(root, "VerCode");
                    xLog.m5i(NetInterface.TAG, "curVer:" + curVer + "  newVer:" + newVer);
                    if (newVer > curVer) {
                        NetInterface.this.sendMessageData(NetInterface.Net_GetNewVer, root);
                    } else {
                        NetInterface.this.sendMessageData(NetInterface.Net_NoNewVer);
                    }
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_EditDevSon_OK);
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void getDevPara(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    JSONObject root = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/GetDevPara", para));
                    String codeStr = root.getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this.sendMessageData(NetInterface.Net_GetDevPara_OK, root);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void setDevPara(final BindDevObj obj) {
        new Thread(new Runnable() {
            public void run() {
                AppConfig _config = SysApp.getMe().getConfig();
                Map<String, String> para = new HashMap();
                para.put("AppKey", _config.getLocalMacAddress());
                para.put("SKey", _config.getSKey());
                para.put("SHash", _config.getSHash());
                para.put("DevID", String.valueOf(obj.getNetID()));
                para.put("Key1", obj.getChipid());
                para.put("Key2", obj.getMAC());
                para.put("DevType", NetInterface.this.getDevType(obj.getProduct()));
                para.put("SSID", obj.getSSID());
                para.put("ProductName", obj.getProduct());
                para.put("NickName", obj.getNickName());
                para.put("Parameter", obj.getPara().toString());
                para.put("VeriKey", HttpClient.getPostVerify(para));
                try {
                    String codeStr = new JSONObject(HttpClient.postFromHttpClient("http://iot.ekewe.net/IAppHtml/SaveDevPara", para)).getString("statusCode");
                    if (ComUtils.StrIsEmpty(codeStr) || !codeStr.equals("200")) {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_DataSubmit_FAIL);
                    } else {
                        NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_SaveDevPara_OK);
                    }
                } catch (Exception e) {
                    System.out.print(e.toString());
                    NetInterface.this._handler.sendEmptyMessage(NetInterface.Net_Data_Error);
                }
            }
        }).start();
    }

    public void downFile(final String url) {
        new Thread(new Runnable() {
            public void run() {
                String SDRoot = NetInterface.this._context.getExternalCacheDir().getAbsolutePath();
                String downUrl = url;
                if (!downUrl.startsWith("http://")) {
                    if (downUrl.startsWith("/")) {
                        downUrl = NetInterface.ServerUrl + downUrl;
                    } else {
                        downUrl = "http://iot.ekewe.net/" + downUrl;
                    }
                }
                String tmpPath = downUrl.substring(downUrl.indexOf("/", "http://".length()), downUrl.lastIndexOf("/"));
                String fileName = downUrl.substring(downUrl.lastIndexOf("/") + 1);
                if (ComUtils.StrIsEmpty(fileName)) {
                    NetInterface.this.sendMessageData(NetInterface.Net_Data_Error);
                    return;
                }
                if (!ComUtils.StrIsEmpty(tmpPath)) {
                    File p = new File(SDRoot + tmpPath);
                    if (!p.exists()) {
                        p.mkdirs();
                    }
                }
                String loclPath = SDRoot + tmpPath + fileName;
                xLog.m5i(NetInterface.TAG, "mImgPath:" + loclPath + " Url:" + downUrl);
                File f = new File(loclPath);
                if (f.exists()) {
                    f.delete();
                }
                HttpClient.DownFile(downUrl, loclPath, NetInterface.this._handler);
            }
        }).start();
    }
}
