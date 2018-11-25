package com.espressif.iot.Esptouch.protocol;

import com.espressif.iot.Esptouch.task.ICodeData;
import com.espressif.iot.Esptouch.util.ByteUtil;
import com.espressif.iot.Esptouch.util.CRC8;
import com.espressif.iot.Esptouch.util.EspNetUtil;
//import com.facebook.appevents.AppEventsConstants;
import java.net.InetAddress;

public class DatumCode implements ICodeData {
    private static final int EXTRA_HEAD_LEN = 5;
    private static final int EXTRA_LEN = 40;
    private final DataCode[] mDataCodes;

    public DatumCode(String apSsid, String apBssid, String apPassword, InetAddress ipAddress, boolean isSsidHiden) {
        int i;
        char totalLen;
        char apPwdLen = (char) ByteUtil.getBytesByString(apPassword).length;
        CRC8 crc = new CRC8();
        crc.update(ByteUtil.getBytesByString(apSsid));
        char apSsidCrc = (char) ((int) crc.getValue());
        crc.reset();
        crc.update(EspNetUtil.parseBssid2bytes(apBssid));
        char apBssidCrc = (char) ((int) crc.getValue());
        char apSsidLen = (char) ByteUtil.getBytesByString(apSsid).length;
        String[] ipAddrStrs = ipAddress.getHostAddress().split("\\.");
        int ipLen = ipAddrStrs.length;
        char[] ipAddrChars = new char[ipLen];
        for (i = 0; i < ipLen; i++) {
            ipAddrChars[i] = (char) Integer.parseInt(ipAddrStrs[i]);
        }
        char _totalLen = (char) (((ipLen + 5) + apPwdLen) + apSsidLen);
        if (isSsidHiden) {
            totalLen = (char) (((ipLen + 5) + apPwdLen) + apSsidLen);
        } else {
            totalLen = (char) ((ipLen + 5) + apPwdLen);
        }
        this.mDataCodes = new DataCode[totalLen];
        this.mDataCodes[0] = new DataCode(_totalLen, 0);
        char totalXor = (char) ('\u0000' ^ _totalLen);
        this.mDataCodes[1] = new DataCode(apPwdLen, 1);
        totalXor = (char) (totalXor ^ apPwdLen);
        this.mDataCodes[2] = new DataCode(apSsidCrc, 2);
        totalXor = (char) (totalXor ^ apSsidCrc);
        this.mDataCodes[3] = new DataCode(apBssidCrc, 3);
        totalXor = (char) (totalXor ^ apBssidCrc);
        this.mDataCodes[4] = null;
        for (i = 0; i < ipLen; i++) {
            this.mDataCodes[i + 5] = new DataCode(ipAddrChars[i], i + 5);
            totalXor = (char) (ipAddrChars[i] ^ totalXor);
        }
        byte[] apPwdBytes = ByteUtil.getBytesByString(apPassword);
        char[] apPwdChars = new char[apPwdBytes.length];
        for (i = 0; i < apPwdBytes.length; i++) {
            apPwdChars[i] = ByteUtil.convertByte2Uint8(apPwdBytes[i]);
        }
        for (i = 0; i < apPwdChars.length; i++) {
            this.mDataCodes[(i + 5) + ipLen] = new DataCode(apPwdChars[i], (i + 5) + ipLen);
            totalXor = (char) (apPwdChars[i] ^ totalXor);
        }
        byte[] apSsidBytes = ByteUtil.getBytesByString(apSsid);
        char[] apSsidChars = new char[apSsidBytes.length];
        for (i = 0; i < apSsidBytes.length; i++) {
            apSsidChars[i] = ByteUtil.convertByte2Uint8(apSsidBytes[i]);
            totalXor = (char) (apSsidChars[i] ^ totalXor);
        }
        if (isSsidHiden) {
            for (i = 0; i < apSsidChars.length; i++) {
                this.mDataCodes[((i + 5) + ipLen) + apPwdLen] = new DataCode(apSsidChars[i], ((i + 5) + ipLen) + apPwdLen);
            }
        }
        this.mDataCodes[4] = new DataCode(totalXor, 4);
    }

    public byte[] getBytes() {
        byte[] datumCode = new byte[(this.mDataCodes.length * 6)];
        for (int i = 0; i < this.mDataCodes.length; i++) {
            System.arraycopy(this.mDataCodes[i].getBytes(), 0, datumCode, i * 6, 6);
        }
        return datumCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        byte[] dataBytes = getBytes();
        for (byte convertByte2HexString : dataBytes) {
            String hexString = ByteUtil.convertByte2HexString(convertByte2HexString);
            sb.append("0x");
//            if (hexString.length() == 1) {
//                sb.append(AppEventsConstants.EVENT_PARAM_VALUE_NO);
//            }
            sb.append(hexString).append(" ");
        }
        return sb.toString();
    }

    public char[] getU8s() {
        byte[] dataBytes = getBytes();
        int len = dataBytes.length / 2;
        char[] dataU8s = new char[len];
        for (int i = 0; i < len; i++) {
            dataU8s[i] = (char) (ByteUtil.combine2bytesToU16(dataBytes[i * 2], dataBytes[(i * 2) + 1]) + 40);
        }
        return dataU8s;
    }
}
