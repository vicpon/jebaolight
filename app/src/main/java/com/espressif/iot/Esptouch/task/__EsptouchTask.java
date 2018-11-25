package com.espressif.iot.Esptouch.task;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.espressif.iot.Esptouch.EsptouchResult;
import com.espressif.iot.Esptouch.IEsptouchListener;
import com.espressif.iot.Esptouch.IEsptouchResult;
import com.espressif.iot.Esptouch.protocol.EsptouchGenerator;
import com.espressif.iot.Esptouch.udp.UDPSocketClient;
import com.espressif.iot.Esptouch.udp.UDPSocketServer;
import com.espressif.iot.Esptouch.util.ByteUtil;
import com.espressif.iot.Esptouch.util.EspNetUtil;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class __EsptouchTask implements __IEsptouchTask {
    private static final String ESPTOUCH_VERSION = "v0.3.4.6";
    private static final int ONE_DATA_LEN = 3;
    private static final String TAG = "EsptouchTask";
    private final String mApBssid;
    private final String mApPassword;
    private final String mApSsid;
    private volatile Map<String, Integer> mBssidTaskSucCountMap;
    private final Context mContext;
    private IEsptouchListener mEsptouchListener;
    private volatile List<IEsptouchResult> mEsptouchResultList;
    private AtomicBoolean mIsCancelled;
    private volatile boolean mIsExecuted = false;
    private volatile boolean mIsInterrupt = false;
    private final boolean mIsSsidHidden;
    private volatile boolean mIsSuc = false;
    private IEsptouchTaskParameter mParameter;
    private final UDPSocketClient mSocketClient;
    private final UDPSocketServer mSocketServer;
    private Thread mTask;

    public __EsptouchTask(String apSsid, String apBssid, String apPassword, Context context, IEsptouchTaskParameter parameter, boolean isSsidHidden) {
        Log.i(TAG, "Welcome Esptouch v0.3.4.6");
        if (TextUtils.isEmpty(apSsid)) {
            throw new IllegalArgumentException("the apSsid should be null or empty");
        }
        if (apPassword == null) {
            apPassword = "";
        }
        this.mContext = context;
        this.mApSsid = apSsid;
        this.mApBssid = apBssid;
        this.mApPassword = apPassword;
        this.mIsCancelled = new AtomicBoolean(false);
        this.mSocketClient = new UDPSocketClient();
        this.mParameter = parameter;
        this.mSocketServer = new UDPSocketServer(this.mParameter.getPortListening(), this.mParameter.getWaitUdpTotalMillisecond(), context);
        this.mIsSsidHidden = isSsidHidden;
        this.mEsptouchResultList = new ArrayList();
        this.mBssidTaskSucCountMap = new HashMap();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void __putEsptouchResult(boolean r11, java.lang.String r12, java.net.InetAddress r13) {
        /*
        r10 = this;
        r5 = 0;
        r6 = r10.mEsptouchResultList;
        monitor-enter(r6);
        r4 = 0;
        r7 = r10.mBssidTaskSucCountMap;	 Catch:{ all -> 0x00a8 }
        r0 = r7.get(r12);	 Catch:{ all -> 0x00a8 }
        r0 = (java.lang.Integer) r0;	 Catch:{ all -> 0x00a8 }
        if (r0 != 0) goto L_0x0014;
    L_0x000f:
        r7 = 0;
        r0 = java.lang.Integer.valueOf(r7);	 Catch:{ all -> 0x00a8 }
    L_0x0014:
        r7 = r0.intValue();	 Catch:{ all -> 0x00a8 }
        r7 = r7 + 1;
        r0 = java.lang.Integer.valueOf(r7);	 Catch:{ all -> 0x00a8 }
        r7 = "EsptouchTask";
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00a8 }
        r8.<init>();	 Catch:{ all -> 0x00a8 }
        r9 = "__putEsptouchResult(): count = ";
        r8 = r8.append(r9);	 Catch:{ all -> 0x00a8 }
        r8 = r8.append(r0);	 Catch:{ all -> 0x00a8 }
        r8 = r8.toString();	 Catch:{ all -> 0x00a8 }
        android.util.Log.d(r7, r8);	 Catch:{ all -> 0x00a8 }
        r7 = r10.mBssidTaskSucCountMap;	 Catch:{ all -> 0x00a8 }
        r7.put(r12, r0);	 Catch:{ all -> 0x00a8 }
        r7 = r0.intValue();	 Catch:{ all -> 0x00a8 }
        r8 = r10.mParameter;	 Catch:{ all -> 0x00a8 }
        r8 = r8.getThresholdSucBroadcastCount();	 Catch:{ all -> 0x00a8 }
        if (r7 < r8) goto L_0x006a;
    L_0x0047:
        r4 = 1;
    L_0x0048:
        if (r4 != 0) goto L_0x006c;
    L_0x004a:
        r5 = "EsptouchTask";
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00a8 }
        r7.<init>();	 Catch:{ all -> 0x00a8 }
        r8 = "__putEsptouchResult(): count = ";
        r7 = r7.append(r8);	 Catch:{ all -> 0x00a8 }
        r7 = r7.append(r0);	 Catch:{ all -> 0x00a8 }
        r8 = ", isn't enough";
        r7 = r7.append(r8);	 Catch:{ all -> 0x00a8 }
        r7 = r7.toString();	 Catch:{ all -> 0x00a8 }
        android.util.Log.d(r5, r7);	 Catch:{ all -> 0x00a8 }
        monitor-exit(r6);	 Catch:{ all -> 0x00a8 }
    L_0x0069:
        return;
    L_0x006a:
        r4 = r5;
        goto L_0x0048;
    L_0x006c:
        r3 = 0;
        r5 = r10.mEsptouchResultList;	 Catch:{ all -> 0x00a8 }
        r5 = r5.iterator();	 Catch:{ all -> 0x00a8 }
    L_0x0073:
        r7 = r5.hasNext();	 Catch:{ all -> 0x00a8 }
        if (r7 == 0) goto L_0x008a;
    L_0x0079:
        r2 = r5.next();	 Catch:{ all -> 0x00a8 }
        r2 = (com.espressif.iot.Esptouch.IEsptouchResult) r2;	 Catch:{ all -> 0x00a8 }
        r7 = r2.getBssid();	 Catch:{ all -> 0x00a8 }
        r7 = r7.equals(r12);	 Catch:{ all -> 0x00a8 }
        if (r7 == 0) goto L_0x0073;
    L_0x0089:
        r3 = 1;
    L_0x008a:
        if (r3 != 0) goto L_0x00a6;
    L_0x008c:
        r5 = "EsptouchTask";
        r7 = "__putEsptouchResult(): put one more result";
        android.util.Log.d(r5, r7);	 Catch:{ all -> 0x00a8 }
        r1 = new com.espressif.iot.Esptouch.EsptouchResult;	 Catch:{ all -> 0x00a8 }
        r1.<init>(r11, r12, r13);	 Catch:{ all -> 0x00a8 }
        r5 = r10.mEsptouchResultList;	 Catch:{ all -> 0x00a8 }
        r5.add(r1);	 Catch:{ all -> 0x00a8 }
        r5 = r10.mEsptouchListener;	 Catch:{ all -> 0x00a8 }
        if (r5 == 0) goto L_0x00a6;
    L_0x00a1:
        r5 = r10.mEsptouchListener;	 Catch:{ all -> 0x00a8 }
        r5.onEsptouchResultAdded(r1);	 Catch:{ all -> 0x00a8 }
    L_0x00a6:
        monitor-exit(r6);	 Catch:{ all -> 0x00a8 }
        goto L_0x0069;
    L_0x00a8:
        r5 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x00a8 }
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.espressif.iot.Esptouch.task.__EsptouchTask.__putEsptouchResult(boolean, java.lang.String, java.net.InetAddress):void");
    }

    private List<IEsptouchResult> __getEsptouchResultList() {
        List<IEsptouchResult> list;
        synchronized (this.mEsptouchResultList) {
            if (this.mEsptouchResultList.isEmpty()) {
                EsptouchResult esptouchResultFail = new EsptouchResult(false, null, null);
                esptouchResultFail.setIsCancelled(this.mIsCancelled.get());
                this.mEsptouchResultList.add(esptouchResultFail);
            }
            list = this.mEsptouchResultList;
        }
        return list;
    }

    private synchronized void __interrupt() {
        if (!this.mIsInterrupt) {
            this.mIsInterrupt = true;
            this.mSocketClient.interrupt();
            this.mSocketServer.interrupt();
            if (this.mTask != null) {
                this.mTask.interrupt();
                this.mTask = null;
            }
        }
    }

    public void interrupt() {
        Log.d(TAG, "interrupt()");
        this.mIsCancelled.set(true);
        __interrupt();
    }

    private void __listenAsyn(final int expectDataLen) {
        this.mTask = new Thread() {
            public void run() {
                Log.d(__EsptouchTask.TAG, "__listenAsyn() start");
                long startTimestamp = System.currentTimeMillis();
                byte expectOneByte = (byte) (ByteUtil.getBytesByString(__EsptouchTask.this.mApSsid + __EsptouchTask.this.mApPassword).length + 9);
                Log.i(__EsptouchTask.TAG, "expectOneByte: " + (expectOneByte + 0));
                while (__EsptouchTask.this.mEsptouchResultList.size() < __EsptouchTask.this.mParameter.getExpectTaskResultCount() && !__EsptouchTask.this.mIsInterrupt) {
                    byte receiveOneByte;
                    byte[] receiveBytes = __EsptouchTask.this.mSocketServer.receiveSpecLenBytes(expectDataLen);
                    if (receiveBytes != null) {
                        receiveOneByte = receiveBytes[0];
                    } else {
                        receiveOneByte = (byte) -1;
                    }
                    if (receiveOneByte == expectOneByte) {
                        Log.i(__EsptouchTask.TAG, "receive correct broadcast");
                        int timeout = (int) (((long) __EsptouchTask.this.mParameter.getWaitUdpTotalMillisecond()) - (System.currentTimeMillis() - startTimestamp));
                        if (timeout < 0) {
                            Log.i(__EsptouchTask.TAG, "esptouch timeout");
                            break;
                        }
                        Log.i(__EsptouchTask.TAG, "mSocketServer's new timeout is " + timeout + " milliseconds");
                        __EsptouchTask.this.mSocketServer.setSoTimeout(timeout);
                        Log.i(__EsptouchTask.TAG, "receive correct broadcast");
                        if (receiveBytes != null) {
                            __EsptouchTask.this.__putEsptouchResult(true, ByteUtil.parseBssid(receiveBytes, __EsptouchTask.this.mParameter.getEsptouchResultOneLen(), __EsptouchTask.this.mParameter.getEsptouchResultMacLen()), EspNetUtil.parseInetAddr(receiveBytes, __EsptouchTask.this.mParameter.getEsptouchResultOneLen() + __EsptouchTask.this.mParameter.getEsptouchResultMacLen(), __EsptouchTask.this.mParameter.getEsptouchResultIpLen()));
                        }
                    } else {
                        Log.i(__EsptouchTask.TAG, "receive rubbish message, just ignore");
                    }
                }
                __EsptouchTask.this.mIsSuc = __EsptouchTask.this.mEsptouchResultList.size() >= __EsptouchTask.this.mParameter.getExpectTaskResultCount();
                __EsptouchTask.this.__interrupt();
                Log.d(__EsptouchTask.TAG, "__listenAsyn() finish");
            }
        };
        this.mTask.start();
    }

    private boolean __execute(IEsptouchGenerator generator) {
        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
        long lastTime = currentTime - this.mParameter.getTimeoutTotalCodeMillisecond();
        byte[][] gcBytes2 = generator.getGCBytes2();
        byte[][] dcBytes2 = generator.getDCBytes2();
        int index = 0;
        while (!this.mIsInterrupt) {
            if (currentTime - lastTime >= this.mParameter.getTimeoutTotalCodeMillisecond()) {
                Log.d(TAG, "send gc code ");
                while (!this.mIsInterrupt && System.currentTimeMillis() - currentTime < this.mParameter.getTimeoutGuideCodeMillisecond()) {
                    this.mSocketClient.sendData(gcBytes2, this.mParameter.getTargetHostname(), this.mParameter.getTargetPort(), this.mParameter.getIntervalGuideCodeMillisecond());
                    if (System.currentTimeMillis() - startTime > ((long) this.mParameter.getWaitUdpSendingMillisecond())) {
                        break;
                    }
                }
                lastTime = currentTime;
            } else {
                this.mSocketClient.sendData(dcBytes2, index, 3, this.mParameter.getTargetHostname(), this.mParameter.getTargetPort(), this.mParameter.getIntervalDataCodeMillisecond());
                index = (index + 3) % dcBytes2.length;
            }
            currentTime = System.currentTimeMillis();
            if (currentTime - startTime > ((long) this.mParameter.getWaitUdpSendingMillisecond())) {
                break;
            }
        }
        return this.mIsSuc;
    }

    private void __checkTaskValid() {
        if (this.mIsExecuted) {
            throw new IllegalStateException("the Esptouch task could be executed only once");
        }
        this.mIsExecuted = true;
    }

    public IEsptouchResult executeForResult() throws RuntimeException {
        return (IEsptouchResult) executeForResults(1).get(0);
    }

    public boolean isCancelled() {
        return this.mIsCancelled.get();
    }

    public List<IEsptouchResult> executeForResults(int expectTaskResultCount) throws RuntimeException {
        __checkTaskValid();
        this.mParameter.setExpectTaskResultCount(expectTaskResultCount);
        Log.d(TAG, "execute()");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("Don't call the esptouch Task at Main(UI) thread directly.");
        }
        InetAddress localInetAddress = EspNetUtil.getLocalInetAddress(this.mContext);
        Log.i(TAG, "localInetAddress: " + localInetAddress);
        IEsptouchGenerator generator = new EsptouchGenerator(this.mApSsid, this.mApBssid, this.mApPassword, localInetAddress, this.mIsSsidHidden);
        __listenAsyn(this.mParameter.getEsptouchResultTotalLen());
        for (int i = 0; i < this.mParameter.getTotalRepeatTime(); i++) {
            if (__execute(generator)) {
                return __getEsptouchResultList();
            }
        }
        if (!this.mIsInterrupt) {
            try {
                Thread.sleep((long) this.mParameter.getWaitUdpReceivingMillisecond());
                __interrupt();
            } catch (InterruptedException e) {
                if (this.mIsSuc) {
                    return __getEsptouchResultList();
                }
                __interrupt();
                return __getEsptouchResultList();
            }
        }
        return __getEsptouchResultList();
    }

    public void setEsptouchListener(IEsptouchListener esptouchListener) {
        this.mEsptouchListener = esptouchListener;
    }
}
