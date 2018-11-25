package com.example.wifizhilian.data;

public class DevGroupObj {
    private String Chipid;
    private int ID;
    private int MainNetid;
    private int SonID;
    private boolean isUpdate = false;

    public int getID() {
        return this.ID;
    }

    public void setID(int devid) {
        this.ID = devid;
    }

    public int getMainNetid() {
        return this.MainNetid;
    }

    public void setMainNetid(int netID) {
        this.MainNetid = netID;
    }

    public String getChipid() {
        return this.Chipid;
    }

    public void setChipid(String chipid) {
        this.Chipid = chipid;
    }

    public int getSonID() {
        return this.SonID;
    }

    public void setSonID(int val) {
        this.SonID = val;
    }

    public boolean isUpdate() {
        return this.isUpdate;
    }

    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }
}
