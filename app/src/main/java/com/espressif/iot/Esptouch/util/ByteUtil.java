package com.espressif.iot.Esptouch.util;

//import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsConstants;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class ByteUtil {
    public static final String ESPTOUCH_ENCODING_CHARSET = "ISO-8859-1";

    public static void putString2bytes(byte[] destbytes, String srcString, int destOffset, int srcOffset, int count) {
        for (int i = 0; i < count; i++) {
            destbytes[count + i] = srcString.getBytes()[i];
        }
    }

    public static byte convertUint8toByte(char uint8) {
        if (uint8 <= 'ÿ') {
            return (byte) uint8;
        }
        throw new RuntimeException("Out of Boundary");
    }

    public static char convertByte2Uint8(byte b) {
        return (char) (b & 255);
    }

    public static char[] convertBytes2Uint8s(byte[] bytes) {
        int len = bytes.length;
        char[] uint8s = new char[len];
        for (int i = 0; i < len; i++) {
            uint8s[i] = convertByte2Uint8(bytes[i]);
        }
        return uint8s;
    }

    public static void putbytes2Uint8s(char[] destUint8s, byte[] srcBytes, int destOffset, int srcOffset, int count) {
        for (int i = 0; i < count; i++) {
            destUint8s[destOffset + i] = convertByte2Uint8(srcBytes[srcOffset + i]);
        }
    }

    public static String convertByte2HexString(byte b) {
        return Integer.toHexString(convertByte2Uint8(b));
    }

    public static String convertU8ToHexString(char u8) {
        return Integer.toHexString(u8);
    }

    public static byte[] splitUint8To2bytes(char uint8) {
        if (uint8 < '\u0000' || uint8 > 'ÿ') {
            throw new RuntimeException("Out of Boundary");
        }
        byte high;
        byte low;
        String hexString = Integer.toHexString(uint8);
        if (hexString.length() > 1) {
            high = (byte) Integer.parseInt(hexString.substring(0, 1), 16);
            low = (byte) Integer.parseInt(hexString.substring(1, 2), 16);
        } else {
            high = (byte) 0;
            low = (byte) Integer.parseInt(hexString.substring(0, 1), 16);
        }
        return new byte[]{high, low};
    }

    public static byte combine2bytesToOne(byte high, byte low) {
        if (high >= (byte) 0 && high <= (byte) 15 && low >= (byte) 0 && low <= (byte) 15) {
            return (byte) ((high << 4) | low);
        }
        throw new RuntimeException("Out of Boundary");
    }

    public static char combine2bytesToU16(byte high, byte low) {
        return (char) ((convertByte2Uint8(high) << 8) | convertByte2Uint8(low));
    }

    private static byte randomByte() {
        return (byte) (127 - new Random().nextInt(256));
    }

    public static byte[] randomBytes(char len) {
        byte[] data = new byte[len];
        for (char i = '\u0000'; i < len; i++) {
            data[i] = randomByte();
        }
        return data;
    }

    public static byte[] genSpecBytes(char len) {
        byte[] data = new byte[len];
        for (char i = '\u0000'; i < len; i++) {
            data[i] = (byte) 49;
        }
        return data;
    }

    public static byte[] randomBytes(byte len) {
        return randomBytes(convertByte2Uint8(len));
    }

    public static byte[] genSpecBytes(byte len) {
        return genSpecBytes(convertByte2Uint8(len));
    }

    public static String parseBssid(byte[] bssidBytes, int offset, int count) {
        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) {
            bytes[i] = bssidBytes[i + offset];
        }
        return parseBssid(bytes);
    }

    public static String parseBssid(byte[] bssidBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bssidBytes) {
            String str;
            int k = b & 255;
            String hexK = Integer.toHexString(k);
            if (k < 16) {
                str = AppEventsConstants.EVENT_PARAM_VALUE_NO + hexK;
            } else {
                str = hexK;
            }
            System.out.println(str);
            sb.append(str);
        }
        return sb.toString();
    }

    public static byte[] getBytesByString(String string) {
        try {
            return string.getBytes(ESPTOUCH_ENCODING_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("the charset is invalid");
        }
    }

    private static void test_splitUint8To2bytes() {
        byte[] result = splitUint8To2bytes('\u0014');
        if (result[0] == (byte) 1 && result[1] == (byte) 4) {
            System.out.println("test_splitUint8To2bytes(): pass");
        } else {
            System.out.println("test_splitUint8To2bytes(): fail");
        }
    }

    private static void test_combine2bytesToOne() {
        if (combine2bytesToOne((byte) 1, (byte) 4) == (byte) 20) {
            System.out.println("test_combine2bytesToOne(): pass");
        } else {
            System.out.println("test_combine2bytesToOne(): fail");
        }
    }

    private static void test_convertChar2Uint8() {
        if (convertByte2Uint8((byte) 97) == 'a' && convertByte2Uint8(Byte.MIN_VALUE) == '' && convertByte2Uint8((byte) -1) == 'ÿ') {
            System.out.println("test_convertChar2Uint8(): pass");
        } else {
            System.out.println("test_convertChar2Uint8(): fail");
        }
    }

    private static void test_convertUint8toByte() {
        if (convertUint8toByte('a') == (byte) 97 && convertUint8toByte('') == Byte.MIN_VALUE && convertUint8toByte('ÿ') == (byte) -1) {
            System.out.println("test_convertUint8toByte(): pass");
        } else {
            System.out.println("test_convertUint8toByte(): fail");
        }
    }

    private static void test_parseBssid() {
        if (parseBssid(new byte[]{(byte) 15, (byte) -2, (byte) 52, (byte) -102, (byte) -93, (byte) -60}).equals("0ffe349aa3c4")) {
            System.out.println("test_parseBssid(): pass");
        } else {
            System.out.println("test_parseBssid(): fail");
        }
    }

    public static void main(String[] args) {
        test_convertUint8toByte();
        test_convertChar2Uint8();
        test_splitUint8To2bytes();
        test_combine2bytesToOne();
        test_parseBssid();
    }
}
