package com.espressif.iot.Esptouch.protocol;

import com.espressif.iot.Esptouch.task.IEsptouchGenerator;
import com.espressif.iot.Esptouch.util.ByteUtil;
import java.net.InetAddress;

public class EsptouchGenerator implements IEsptouchGenerator {
    private final byte[][] mDcBytes2;
    private final byte[][] mGcBytes2;

    public EsptouchGenerator(String apSsid, String apBssid, String apPassword, InetAddress inetAddress, boolean isSsidHiden) {
        int i;
        char[] gcU81 = new GuideCode().getU8s();
        this.mGcBytes2 = new byte[gcU81.length][];
        for (i = 0; i < this.mGcBytes2.length; i++) {
            this.mGcBytes2[i] = ByteUtil.genSpecBytes(gcU81[i]);
        }
        char[] dcU81 = new DatumCode(apSsid, apBssid, apPassword, inetAddress, isSsidHiden).getU8s();
        this.mDcBytes2 = new byte[dcU81.length][];
        for (i = 0; i < this.mDcBytes2.length; i++) {
            this.mDcBytes2[i] = ByteUtil.genSpecBytes(dcU81[i]);
        }
    }

    public byte[][] getGCBytes2() {
        return this.mGcBytes2;
    }

    public byte[][] getDCBytes2() {
        return this.mDcBytes2;
    }
}
