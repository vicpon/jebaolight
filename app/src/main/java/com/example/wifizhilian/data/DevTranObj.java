package com.example.wifizhilian.data;

public class DevTranObj {
    private String ChipID;
    private int DevID;
    private int EndAct;
    private int ID;
    private String NickName;
    private int Period;
    private int SetIndex;
    private int StartAct;
    private int State;

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

    public int getState() {
        return this.State;
    }

    public void setState(int state) {
        this.State = state;
    }

    public String getNickName() {
        return this.NickName;
    }

    public void setNickName(String nickName) {
        this.NickName = nickName;
    }

    public int getSetIndex() {
        return this.SetIndex;
    }

    public void setSetIndex(int setIndex) {
        this.SetIndex = setIndex;
    }

    public int getStartAct() {
        return this.StartAct;
    }

    public void setStartAct(int startAct) {
        this.StartAct = startAct;
    }

    public int getPeriod() {
        return this.Period;
    }

    public void setPeriod(int period) {
        this.Period = period;
    }

    public int getEndAct() {
        return this.EndAct;
    }

    public void setEndAct(int endAct) {
        this.EndAct = endAct;
    }
}
