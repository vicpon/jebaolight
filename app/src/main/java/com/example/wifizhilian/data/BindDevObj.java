package com.example.wifizhilian.data;

import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.data.AppConfig.EnumNetType;
import com.example.wifizhilian.libs.ComUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class BindDevObj {
    private Date Bindtime = new Date();
    private String Chipid = "";
    private int DevType = 1;
    private List<DevGroupObj> GroupList = new ArrayList();
    private int ID;
    private String IpAddr;
    private int IpPort;
    private int IsGroup;
    private String MAC = "";
    private int MtoPTime;
    private int NetID;
    private String NickName = "";
    private int PackNum = 0;
    private String Product = "";
    private long RXtime = 0;
    private String SSID = "";
    private List<DevSetObj> SetList = new ArrayList();
    private int State;
    private List<DevTranObj> TranList = new ArrayList();
    private String UpdateCode = "";
    private String UserID = "";
    private byte[] WordPara = new byte[]{(byte) 0, (byte) 0, (byte) 0};
    private EnumNetType mNetType = EnumNetType.None;
    private JSONObject mSetPara = new JSONObject();

    public int getID() {
        return this.ID;
    }

    public void setID(int devid) {
        this.ID = devid;
    }

    public int getNetID() {
        return this.NetID;
    }

    public void setNetID(int netID) {
        this.NetID = netID;
    }

    public String getChipid() {
        return this.Chipid;
    }

    public void setChipid(String chipid) {
        this.Chipid = chipid;
    }

    public String getMAC() {
        return this.MAC;
    }

    public void setMAC(String mAC) {
        this.MAC = mAC;
    }

    public String getUserID() {
        return this.UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getProduct() {
        return this.Product;
    }

    public void setProduct(String product) {
        this.Product = product;
    }

    public String getSSID() {
        return this.SSID;
    }

    public void setSSID(String sSID) {
        this.SSID = sSID;
    }

    public Date getBindtime() {
        return this.Bindtime;
    }

    public void setBindtime(Date bindtime) {
        this.Bindtime = bindtime;
    }

    public int getState() {
        return this.State;
    }

    public void setState(int state) {
        this.State = state;
    }

    public byte[] getWordPara() {
        return this.WordPara;
    }

    public void setWordPara(byte[] wordPara) {
        this.WordPara = wordPara;
    }

    public String getIpAddr() {
        return this.IpAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.IpAddr = ipAddr;
    }

    public long getRXtime() {
        return this.RXtime;
    }

    public void setRXtime(long rXtime) {
        this.RXtime = rXtime;
    }

    public int getIpPort() {
        return this.IpPort;
    }

    public void setIpPort(int ipPort) {
        this.IpPort = ipPort;
    }

    public int getPackNum() {
        return this.PackNum;
    }

    public void setPackNum(int packNum) {
        this.PackNum = packNum;
    }

    public String getUpdateCode() {
        return this.UpdateCode;
    }

    public void setUpdateCode(String updateCode) {
        this.UpdateCode = updateCode;
    }

    public String getNickName() {
        if (ComUtils.StrIsEmpty(this.NickName)) {
            return SysApp.getMe().getDevName(getProduct(), getSSID());
        }
        return this.NickName;
    }

    public int getDevType() {
        return this.DevType;
    }

    public void setDevType(int devType) {
        this.DevType = devType;
    }

    public void setNickName(String nickName) {
        this.NickName = nickName;
    }

    public int getMtoPTime() {
        return this.MtoPTime;
    }

    public void setMtoPTime(int mtoPTime) {
        this.MtoPTime = mtoPTime;
    }

    public int isGroup() {
        return this.IsGroup;
    }

    public void setGroup(int isGroup) {
        this.IsGroup = isGroup;
    }

    public List<DevGroupObj> getGroups() {
        return this.GroupList;
    }

    public DevGroupObj getGroup(int sonID) {
        for (int ii = 0; ii < this.GroupList.size(); ii++) {
            if (((DevGroupObj) this.GroupList.get(ii)).getSonID() == sonID) {
                return (DevGroupObj) this.GroupList.get(ii);
            }
        }
        return null;
    }

    public void addGroup(int sonID) {
        int ii = 0;
        while (ii < this.GroupList.size()) {
            if (((DevGroupObj) this.GroupList.get(ii)).getSonID() != sonID) {
                ii++;
            } else {
                return;
            }
        }
        DevGroupObj obj = new DevGroupObj();
        obj.setChipid(this.Chipid);
        obj.setMainNetid(this.NetID);
        obj.setSonID(sonID);
        this.GroupList.add(obj);
    }

    public void delGroupBySonID(int sonID) {
        for (int ii = this.GroupList.size() - 1; ii >= 0; ii--) {
            if (((DevGroupObj) this.GroupList.get(ii)).getSonID() == sonID) {
                this.GroupList.remove(ii);
            }
        }
    }

    public boolean checkGroupSonID(int sonID) {
        for (int ii = 0; ii < this.GroupList.size(); ii++) {
            if (((DevGroupObj) this.GroupList.get(ii)).getSonID() == sonID) {
                return true;
            }
        }
        return false;
    }

    public void initGroupUpdate() {
        for (int ii = 0; ii < this.GroupList.size(); ii++) {
            ((DevGroupObj) this.GroupList.get(ii)).setUpdate(false);
        }
    }

    public List<DevTranObj> getTran() {
        return this.TranList;
    }

    public void addTran(DevTranObj obj) {
        for (int ii = 0; ii < this.TranList.size(); ii++) {
            if (((DevTranObj) this.TranList.get(ii)).getSetIndex() == obj.getSetIndex()) {
                this.TranList.set(ii, obj);
                return;
            }
        }
        this.TranList.add(obj);
    }

    public DevTranObj getTranByIndex(int index) {
        for (int ii = 0; ii < this.TranList.size(); ii++) {
            if (((DevTranObj) this.TranList.get(ii)).getSetIndex() == index) {
                return (DevTranObj) this.TranList.get(ii);
            }
        }
        return null;
    }

    public void delTranByIndex(int index) {
        for (int ii = this.TranList.size() - 1; ii >= 0; ii--) {
            if (((DevTranObj) this.TranList.get(ii)).getSetIndex() == index) {
                this.TranList.remove(ii);
            }
        }
    }

    public List<DevSetObj> getSetList() {
        return this.SetList;
    }

    public void addSet(DevSetObj obj) {
        for (int ii = 0; ii < this.SetList.size(); ii++) {
            if (((DevSetObj) this.SetList.get(ii)).getDay() == obj.getDay()) {
                this.SetList.set(ii, obj);
                return;
            }
        }
        this.SetList.add(obj);
    }

    public DevSetObj getSetByIndex(int index) {
        for (int ii = 0; ii < this.SetList.size(); ii++) {
            if (((DevSetObj) this.SetList.get(ii)).getDay() == index) {
                return (DevSetObj) this.SetList.get(ii);
            }
        }
        return null;
    }

    public void delSetByIndex(int index) {
        for (int ii = this.SetList.size() - 1; ii >= 0; ii--) {
            if (((DevSetObj) this.SetList.get(ii)).getDay() == index) {
                this.SetList.remove(ii);
            }
        }
    }

    public void setOffLine() {
        setNetType(EnumNetType.None);
        setRXtime(System.currentTimeMillis() - 60000);
    }

    public EnumNetType getNetType() {
        return this.mNetType;
    }

    public void setNetType(EnumNetType mNetType) {
        this.mNetType = mNetType;
    }

    public JSONObject getPara() {
        return this.mSetPara;
    }

    public void setPara(String val) {
        try {
            this.mSetPara = new JSONObject(val);
        } catch (JSONException e) {
            System.out.print(e.toString());
        }
    }

    public void setPara(JSONObject val) {
        this.mSetPara = val;
    }
}
