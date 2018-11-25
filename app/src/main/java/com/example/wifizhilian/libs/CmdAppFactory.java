package com.example.wifizhilian.libs;

import com.example.wifizhilian.SysApp;
import com.example.wifizhilian.data.BindDevObj;

public class CmdAppFactory {
    public static final int CODE_AppSendTask = 8;
    public static final int CODE_AskDevIP = 5;
    public static final int CODE_Broadcast = 19;
    public static final int CODE_CheckTime = 2;
    public static final int CODE_Clean = 255;
    public static final int CODE_DevInit = 153;
    public static final int CODE_GetID = 1;
    public static final int CODE_HandStopTime = 12;
    public static final int CODE_Report = 3;
    public static final int CODE_RunBore = 6;
    public static final int CODE_SerSendTask = 7;
    public static final int CODE_SetOnOff = 21;
    public static final int CODE_SetSSID = 20;
    public static final int CODE_SetSonID = 13;
    public static final int CODE_SetState = 10;
    public static final int CODE_TelBore = 4;
    public static final int CODE_UpdateTask = 11;

    public static byte[] Broadcast() {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(0);
        com.setDevID(0);
        com.setPackNum(0);
        com.setCode(19);
        com.setValLength(0);
        com.setValue(null);
        return com.getCommand();
    }

    public static byte[] SetState(BindDevObj dev, byte[] data) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(10);
        com.setValLength(data.length);
        com.setValue(data);
        return com.getCommand();
    }

    public static byte[] AskDevIP(BindDevObj dev) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(5);
        com.setValLength(0);
        com.setValue(null);
        return com.getCommand();
    }

    public static byte[] GetState(BindDevObj dev) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(3);
        com.setValLength(0);
        com.setValue(null);
        return com.getCommand();
    }

    public static byte[] SetSonIDs(BindDevObj dev, byte[] sonIDs) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(13);
        com.setValLength(sonIDs.length);
        com.setValue(sonIDs);
        return com.getCommand();
    }

    public static byte[] UpdateTask(BindDevObj dev, byte[] data) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(11);
        com.setValLength(data.length);
        com.setValue(data);
        return com.getCommand();
    }

    public static byte[] HandStop(BindDevObj dev, byte[] data) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(12);
        com.setValLength(data.length);
        com.setValue(data);
        return com.getCommand();
    }

    public static byte[] SetSSID(BindDevObj dev, byte[] data) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(20);
        com.setValLength(data.length);
        com.setValue(data);
        return com.getCommand();
    }

    public static byte[] SetOnOff(BindDevObj dev, byte[] data) {
        CmdAppObj com = new CmdAppObj();
        com.setAccount(SysApp.getMe().getConfig().getAccID());
        com.setDevID(dev.getNetID());
        com.setPackNum(dev.getPackNum());
        com.setCode(21);
        com.setValLength(data.length);
        com.setValue(data);
        return com.getCommand();
    }
}
