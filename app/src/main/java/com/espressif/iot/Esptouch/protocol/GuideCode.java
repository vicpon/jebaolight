package com.espressif.iot.Esptouch.protocol;

import com.espressif.iot.Esptouch.task.ICodeData;
import com.espressif.iot.Esptouch.util.ByteUtil;
//import com.facebook.appevents.AppEventsConstants;

public class GuideCode implements ICodeData {
    public static final int GUIDE_CODE_LEN = 4;

    public byte[] getBytes() {
        throw new RuntimeException("DataCode don't support getBytes()");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        char[] dataU8s = getU8s();
        for (int i = 0; i < 4; i++) {
            String hexString = ByteUtil.convertU8ToHexString(dataU8s[i]);
            sb.append("0x");
//            if (hexString.length() == 1) {
//                sb.append(AppEventsConstants.EVENT_PARAM_VALUE_NO);
//            }
            sb.append(hexString).append(" ");
        }
        return sb.toString();
    }

    public char[] getU8s() {
        return new char[]{'ȃ', 'Ȃ', 'ȁ', 'Ȁ'};
    }
}
