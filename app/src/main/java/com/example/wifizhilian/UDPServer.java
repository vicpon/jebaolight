package com.example.wifizhilian;

import android.os.Handler;
import com.example.wifizhilian.data.AppConfig.EnumNetType;
import com.example.wifizhilian.data.BindDevObj;
import com.example.wifizhilian.libs.CmdAppFactory;
import com.example.wifizhilian.libs.CmdAppObj;
import com.example.wifizhilian.libs.CmdDevObj;
import com.example.wifizhilian.libs.ComUtils;
import com.example.wifizhilian.libs.HttpClient;
import com.example.wifizhilian.libs.xLog;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

public class UDPServer {
    private static final String SERVER_DOMAIN = "iot.ekewe.net";
    private static final int ServerPort = 8961;
    public static final String TAG = "UDPServer";
    private static final int WIFIDevPort = 9980;
    private static final int[] localPort = new int[]{9981, 9982, 9983, 9984, 9985, 9986, 9987, 9988, 9989, 9990, 9991, 9992, 9993, 9994, 9995, 9996, 9997, 9998, 9999};
    private static Map<Integer, BindDevObj> mDevList = new HashMap();
    private boolean isWork = true;
    private Thread mBroadcastThread;
    private Thread mConnectWlanThread;
    private Handler mHandler = new Handler();
    private Thread mReceiveThread;
    private InetAddress mSerIpaddr = null;
    private DatagramSocket mUDPSocket;
    Runnable runBroadcastThread = new C02882();
    Runnable runUDPReceiveThread = new C02871();

    /* renamed from: com.example.wifizhilian.UDPServer$1 */
    class C02871 implements Runnable {
        C02871() {
        }

        public void run() {
            UDPServer.this.mReceiveThread = new Thread(new UDPReceiveThread());
            UDPServer.this.mReceiveThread.start();
        }
    }

    /* renamed from: com.example.wifizhilian.UDPServer$2 */
    class C02882 implements Runnable {
        C02882() {
        }

        public void run() {
            UDPServer.this.mBroadcastThread = new Thread(new UDPReceiveThread());
            UDPServer.this.mBroadcastThread.start();
        }
    }

    class BroadcastThread implements Runnable {
        BroadcastThread() {
        }

        public void run() {
            String message = ComUtils.bytesToHexString(CmdAppFactory.Broadcast()).toUpperCase();
            while (UDPServer.this.isWork) {
                try {
                    Thread.sleep(1000);
                    if (!UDPServer.this.mUDPSocket.isClosed()) {
                        InetAddress local = InetAddress.getByName("255.255.255.255");
                        UDPServer.this.mUDPSocket.send(new DatagramPacket(message.getBytes(), message.length(), local, UDPServer.WIFIDevPort));
                    }
                } catch (Exception e) {
                    UDPServer.this.mHandler.postDelayed(UDPServer.this.runBroadcastThread, 1000);
                    System.out.println(e.toString());
                }
            }
        }
    }

    class ConnectWlanThread implements Runnable {
        ConnectWlanThread() {
        }

        public void run() {
            try {
                UDPServer.this.mSerIpaddr = InetAddress.getByName(UDPServer.SERVER_DOMAIN);
            } catch (UnknownHostException e1) {
                System.out.print(e1.toString());
            }
            while (UDPServer.this.isWork) {
                try {
                    Iterator iterator = UDPServer.mDevList.keySet().iterator();
                    while (!UDPServer.this.mUDPSocket.isClosed() && iterator.hasNext()) {
                        Integer key = (Integer) iterator.next();
                        if (UDPServer.mDevList.containsKey(key)) {
                            BindDevObj dev = (BindDevObj) UDPServer.mDevList.get(key);
                            if (dev.getNetType().equals(EnumNetType.Lan)) {
                                if (System.currentTimeMillis() - dev.getRXtime() > 3000) {
                                    dev.setOffLine();
                                }
                            } else if (dev.getNetType().equals(EnumNetType.P2P)) {
                                if (System.currentTimeMillis() - dev.getRXtime() > 5000) {
                                    dev.setOffLine();
                                }
                            } else if (dev.getNetType().equals(EnumNetType.Wan) && System.currentTimeMillis() - dev.getRXtime() > 10000) {
                                dev.setOffLine();
                            }
                            InetAddress SendIp = null;
                            int SendPort = 0;
                            String SendMsg = "";
                            if (dev.getNetType().equals(EnumNetType.None)) {
                                dev.setIpAddr("");
                                dev.setIpPort(0);
                                SendIp = UDPServer.this.mSerIpaddr;
                                SendPort = UDPServer.ServerPort;
                                SendMsg = ComUtils.bytesToHexString(CmdAppFactory.AskDevIP((BindDevObj) UDPServer.mDevList.get(key))).toUpperCase();
                            } else if (dev.getNetType().equals(EnumNetType.P2P)) {
                                SendIp = InetAddress.getByName(((BindDevObj) UDPServer.mDevList.get(key)).getIpAddr());
                                SendPort = ((BindDevObj) UDPServer.mDevList.get(key)).getIpPort();
                                SendMsg = ComUtils.bytesToHexString(CmdAppFactory.GetState((BindDevObj) UDPServer.mDevList.get(key))).toUpperCase();
                            } else if (dev.getNetType().equals(EnumNetType.Wan)) {
                                SendIp = UDPServer.this.mSerIpaddr;
                                SendPort = UDPServer.ServerPort;
                                SendUdpCommand(SendIp, UDPServer.ServerPort, ComUtils.bytesToHexString(CmdAppFactory.AskDevIP((BindDevObj) UDPServer.mDevList.get(key))).toUpperCase());
                                SendMsg = ComUtils.bytesToHexString(CmdAppFactory.GetState((BindDevObj) UDPServer.mDevList.get(key))).toUpperCase();
                            }
                            SendUdpCommand(SendIp, SendPort, SendMsg);
                        }
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void SendUdpCommand(InetAddress Ip, int Port, String Msg) {
            if (Ip != null && !ComUtils.StrIsEmpty(Msg)) {
                xLog.m5i(UDPServer.TAG, "UDP SendUdpCommand SendMsg:" + Msg + "  IP:" + Ip.toString() + "  Port:" + Port);
                try {
                    UDPServer.this.mUDPSocket.send(new DatagramPacket(Msg.getBytes(), Msg.length(), Ip, Port));
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    class UDPReceiveThread implements Runnable {
        UDPReceiveThread() {
        }

        public void run() {
            byte[] message = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(message, message.length);
            while (UDPServer.this.isWork) {
                try {
                    UDPServer.this.mUDPSocket.receive(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ipAddr = datagramPacket.getAddress().getHostAddress().toString();
                String recData = new String(datagramPacket.getData()).substring(0, datagramPacket.getLength());
                xLog.m5i(UDPServer.TAG, "UDPReceiveThread  IP:" + ipAddr + "   Port:" + datagramPacket.getPort() + "   RecData:" + recData);
                if (recData.startsWith("FFFD")) {
                    CmdDevObj devcmd = new CmdDevObj(recData);
                    if (devcmd.isValid()) {
                        UDPServer.this.analyseDevCmd(devcmd, ipAddr, datagramPacket.getPort());
                    }
                } else {
                    try {
                        if (recData.startsWith("FFFF")) {
                            CmdAppObj sercmd = new CmdAppObj(recData);
                            if (sercmd.isValid()) {
                                UDPServer.this.analyseSerCmd(sercmd);
                            }
                        }
                    } catch (Exception e) {
                        UDPServer.this.mHandler.postDelayed(UDPServer.this.runUDPReceiveThread, 1000);
                        System.out.print(e.toString());
                        return;
                    }
                }
            }
        }
    }

    public void Start() {
        StartServer();
        this.mReceiveThread = new Thread(new UDPReceiveThread());
        this.mReceiveThread.start();
        this.mBroadcastThread = new Thread(new BroadcastThread());
        this.mBroadcastThread.start();
        this.mConnectWlanThread = new Thread(new ConnectWlanThread());
        this.mConnectWlanThread.start();
    }

    private void StartServer() {
        int[] iArr = localPort;
        int length = iArr.length;
        int i = 0;
        while (i < length) {
            int port = iArr[i];
            try {
                this.mUDPSocket = new DatagramSocket(port);
                xLog.m5i(TAG, "UDPSocket port:" + port);
                return;
            } catch (IOException ex) {
                this.isWork = false;
                System.out.print(ex.toString());
                i++;
            }
        }
    }

    public boolean isRun() {
        return (this.mUDPSocket == null || this.mUDPSocket.isClosed()) ? false : true;
    }

    public void Stop() {
        this.isWork = false;
        if (this.mReceiveThread != null) {
            this.mReceiveThread.interrupt();
            this.mReceiveThread = null;
        }
        if (this.mBroadcastThread != null) {
            this.mBroadcastThread.interrupt();
            this.mBroadcastThread = null;
        }
        if (this.mConnectWlanThread != null) {
            this.mConnectWlanThread.interrupt();
            this.mConnectWlanThread = null;
        }
        if (this.mUDPSocket != null) {
            this.mUDPSocket.close();
            this.mUDPSocket = null;
        }
    }

    public void updateBindDev(List<BindDevObj> devList) {
        xLog.m3d(TAG, "updateBindDev size:" + devList.size());
        synchronized (this) {
            for (Integer key : mDevList.keySet()) {
                if (mDevList.containsKey(key) && !((BindDevObj) mDevList.get(key)).getNetType().equals(EnumNetType.Lan)) {
                    mDevList.remove(key);
                }
            }
            for (int ii = 0; ii < devList.size(); ii++) {
                if (!mDevList.containsKey(Integer.valueOf(((BindDevObj) devList.get(ii)).getNetID()))) {
                    mDevList.put(Integer.valueOf(((BindDevObj) devList.get(ii)).getNetID()), devList.get(ii));
                }
            }
        }
    }

    public void SendCmdToDev(final int devID, final int code, final byte[] data) {
        new Thread() {
            public void run() {
                try {
                    if (UDPServer.mDevList.containsKey(Integer.valueOf(devID))) {
                        BindDevObj obj = (BindDevObj) UDPServer.mDevList.get(Integer.valueOf(devID));
                        String message = "";
                        switch (code) {
                            case 3:
                                message = ComUtils.bytesToHexString(CmdAppFactory.GetState(obj)).toUpperCase();
                                break;
                            case 10:
                                message = ComUtils.bytesToHexString(CmdAppFactory.SetState(obj, data)).toUpperCase();
                                break;
                            case 11:
                                message = ComUtils.bytesToHexString(CmdAppFactory.UpdateTask(obj, data)).toUpperCase();
                                break;
                            case 12:
                                message = ComUtils.bytesToHexString(CmdAppFactory.HandStop(obj, data)).toUpperCase();
                                break;
                            case 13:
                                message = ComUtils.bytesToHexString(CmdAppFactory.SetSonIDs(obj, data)).toUpperCase();
                                break;
                            case 21:
                                message = ComUtils.bytesToHexString(CmdAppFactory.SetOnOff(obj, data)).toUpperCase();
                                break;
                        }
                        if (!ComUtils.StrIsEmpty(message)) {
                            InetAddress sendIp = null;
                            int sendPort = 0;
                            if (obj.getNetType().equals(EnumNetType.Lan) || obj.getNetType().equals(EnumNetType.P2P)) {
                                sendIp = InetAddress.getByName(obj.getIpAddr());
                                sendPort = obj.getIpPort();
                            } else if (obj.getNetType().equals(EnumNetType.Wan) && UDPServer.this.mSerIpaddr != null) {
                                sendIp = UDPServer.this.mSerIpaddr;
                                sendPort = UDPServer.ServerPort;
                            }
                            xLog.m5i(UDPServer.TAG, "Send Cmd:" + message + "   to IP:" + sendIp.toString() + "   Port:" + sendPort);
                            if (sendIp != null) {
                                UDPServer.this.mUDPSocket.send(new DatagramPacket(message.getBytes(), message.length(), sendIp, sendPort));
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }.start();
    }

    public void SendData(final String ipAdd, final int port, final byte[] data) {
        new Thread() {
            public void run() {
                try {
                    String message = ComUtils.bytesToHexString(data).toUpperCase();
                    if (!ComUtils.StrIsEmpty(ipAdd) && !ComUtils.StrIsEmpty(message)) {
                        InetAddress sendIp = InetAddress.getByName(ipAdd);
                        UDPServer.this.mUDPSocket.send(new DatagramPacket(message.getBytes(), message.length(), sendIp, port));
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }.start();
    }

    public static Map<Integer, BindDevObj> getDevList() {
        return mDevList;
    }

    public static boolean checkChipID(String id) {
        for (Integer key : mDevList.keySet()) {
            if (mDevList.containsKey(key) && ((BindDevObj) mDevList.get(key)).getChipid().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static BindDevObj getByChipID(String id) {
        for (Integer key : mDevList.keySet()) {
            if (mDevList.containsKey(key) && ((BindDevObj) mDevList.get(key)).getChipid().equals(id)) {
                return (BindDevObj) mDevList.get(key);
            }
        }
        return null;
    }

    private void getDevInfo(final String ipAddr) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject device = ((JSONObject) new JSONTokener(HttpClient.get("http://" + ipAddr + "/client?command=info", "UTF8")).nextValue()).getJSONObject("Device");
                    int netID = device.getInt("netid");
                    int chipid = device.getInt("chipid");
                    String mac = device.getString("mac");
                    String ssid = device.getString("ssid");
                    String product = device.getString("product");
                    if (UDPServer.mDevList.containsKey(Integer.valueOf(netID))) {
                        ((BindDevObj) UDPServer.mDevList.get(Integer.valueOf(netID))).setChipid(String.valueOf(chipid));
                        ((BindDevObj) UDPServer.mDevList.get(Integer.valueOf(netID))).setMAC(mac);
                        ((BindDevObj) UDPServer.mDevList.get(Integer.valueOf(netID))).setProduct(product);
                        ((BindDevObj) UDPServer.mDevList.get(Integer.valueOf(netID))).setSSID(ssid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addDevList(CmdDevObj cmd, String Ipaddr, int port) {
        BindDevObj dev = new BindDevObj();
        dev.setNetID(cmd.getDevID());
        dev.setIpPort(port);
        dev.setIpAddr(Ipaddr);
        dev.setWordPara(cmd.getValue());
        dev.setRXtime(System.currentTimeMillis());
        dev.setPackNum(cmd.getPackNum());
        dev.setState(0);
        if (cmd.getCode() == 19) {
            dev.setNetType(EnumNetType.Lan);
        } else if (cmd.getCode() == 3) {
            dev.setNetType(EnumNetType.P2P);
        } else {
            dev.setNetType(EnumNetType.Wan);
        }
        mDevList.put(Integer.valueOf(cmd.getDevID()), dev);
    }

    private void analyseDevCmd(CmdDevObj devcmd, String ipAddr, int ipPort) {
        if (mDevList.containsKey(Integer.valueOf(devcmd.getDevID()))) {
            ((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).setIpPort(ipPort);
            ((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).setIpAddr(ipAddr);
            ((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).setWordPara(devcmd.getValue());
            ((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).setPackNum(devcmd.getPackNum());
            ((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).setRXtime(System.currentTimeMillis());
            switch (devcmd.getCode()) {
                case 3:
                    ((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).setNetType(EnumNetType.P2P);
                    break;
                case 19:
                    ((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).setNetType(EnumNetType.Lan);
                    break;
            }
        }
        addDevList(devcmd, ipAddr, ipPort);
        if (((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).getNetType().equals(EnumNetType.Lan) && ComUtils.StrIsEmpty(((BindDevObj) mDevList.get(Integer.valueOf(devcmd.getDevID()))).getSSID())) {
            getDevInfo(ipAddr);
        }
    }

    private void analyseSerCmd(CmdAppObj sercmd) {
        if (mDevList.containsKey(Integer.valueOf(sercmd.getDevID()))) {
            switch (sercmd.getCode()) {
                case 3:
                    ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).setWordPara(ComUtils.hexStringToBytes(ComUtils.byte2AccStr(sercmd.getValue())));
                    ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).setRXtime(System.currentTimeMillis());
                    return;
                case 5:
                    String ipInfo = ComUtils.byte2AccStr(sercmd.getValue());
                    if (ipInfo.startsWith("0.0.0.0") || ipInfo.indexOf(":") <= 0) {
                        ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).setOffLine();
                        return;
                    }
                    String[] ip = ipInfo.split(":");
                    ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).setIpAddr(ip[0]);
                    ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).setIpPort(Integer.parseInt(ip[1]));
                    ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).setRXtime(System.currentTimeMillis());
                    ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).setNetType(EnumNetType.Wan);
                    try {
                        InetAddress sendIp = InetAddress.getByName(((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).getIpAddr());
                        String message = ComUtils.bytesToHexString(CmdAppFactory.GetState((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID())))).toUpperCase();
                        this.mUDPSocket.send(new DatagramPacket(message.getBytes(), message.length(), sendIp, ((BindDevObj) mDevList.get(Integer.valueOf(sercmd.getDevID()))).getIpPort()));
                        return;
                    } catch (UnknownHostException e) {
                        System.out.print(e.toString());
                        return;
                    } catch (IOException e2) {
                        System.out.print(e2.toString());
                        return;
                    }
                default:
                    return;
            }
        }
    }
}
