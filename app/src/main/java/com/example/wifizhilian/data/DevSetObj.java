package com.example.wifizhilian.data;

public class DevSetObj {
    private String ChipID;
    private String Data;
    private int Day;
    private int DevID;
    private int ID;
    private String NickName;
    private String SetPara;
    private int State;
    private boolean isRun;

    public int getID() {
        return this.ID;
    }

    public void setID(int iD) {
        this.ID = iD;
    }

    public int getDevID() {
        return this.DevID;
    }

    public void setDevID(int devID) {
        this.DevID = devID;
    }

    public String getChipID() {
        return this.ChipID;
    }

    public void setChipID(String chipID) {
        this.ChipID = chipID;
    }

    public int getDay() {
        return this.Day;
    }

    public void setDay(int day) {
        this.Day = day;
    }

    public int getState() {
        return this.State;
    }

    public void setState(int state) {
        this.State = state;
    }

    public String getData() {
        return this.Data;
    }

    public void setData(String data) {
        this.Data = data;
    }

    public String getNickName() {
        return this.NickName;
    }

    public void setNickName(String nickName) {
        this.NickName = nickName;
    }

    public boolean isRun() {
        return this.isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public String getPara() {
        return this.SetPara;
    }

    public void setPara(String setPara) {
        this.SetPara = setPara;
    }
}
