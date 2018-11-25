package com.example.wifizhilian.libs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComUtils {
    public static boolean StrIsEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    public static int StrToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int StrToInt(String str, int defVal) {
        try {
            defVal = Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defVal;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(byte src) {
        return bytesToHexString(new byte[]{src});
    }

    public static String bytesToHexString(byte[] src, int len) {
        byte[] getDate = new byte[len];
        System.arraycopy(src, 0, getDate, 0, getDate.length);
        return bytesToHexString(getDate);
    }

    public static String getDate(int v) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() + ((long) ((((v * 24) * 60) * 60) * 1000))));
    }

    public static Date strToDate(String str) {
        return strToDate(str, true);
    }

    public static Date strToDate(String str, boolean hasTime) {
        try {
            if (StrIsEmpty(str)) {
                return null;
            }
            SimpleDateFormat sdf;
            if (hasTime) {
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            }
            return sdf.parse(str);
        } catch (ParseException e) {
            System.out.print(e.toString());
            return null;
        }
    }

    public static String dateToStr(Date date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (Exception e) {
            System.out.print(e.toString());
            return "";
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) ((charToByte(hexChars[pos]) << 4) | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static int byteArrayToInt(byte[] b) {
        byte[] tmp;
        int intLen = 4;
        int value = 0;
        if (b.length > 4) {
            tmp = new byte[4];
            System.arraycopy(b, b.length - 4, tmp, 0, tmp.length);
        } else {
            intLen = b.length;
            tmp = new byte[intLen];
            System.arraycopy(b, 0, tmp, 0, tmp.length);
        }
        for (int i = intLen - 1; i >= 0; i--) {
            value += (tmp[i] & 255) << (((intLen - 1) - i) * 8);
        }
        return value;
    }

    public static byte[] intToByteArray(int integer) {
        int i;
        if (integer < 0) {
            i = integer ^ -1;
        } else {
            i = integer;
        }
        int byteNum = (40 - Integer.numberOfLeadingZeros(i)) / 8;
        byte[] byteArray = new byte[4];
        for (int n = 0; n < byteNum; n++) {
            byteArray[3 - n] = (byte) (integer >>> (n * 8));
        }
        return byteArray;
    }

    public static byte[] int2ByteArr(int res, int len) {
        byte[] reVal = new byte[len];
        if (len == 4) {
            reVal[3] = (byte) res;
            reVal[2] = (byte) (res >>> 8);
            reVal[1] = (byte) (res >>> 16);
            reVal[0] = (byte) (res >>> 24);
        } else if (len == 2) {
            reVal[1] = (byte) res;
            reVal[0] = (byte) (res >>> 8);
        } else {
            reVal[0] = (byte) res;
        }
        return reVal;
    }

    public static int byte2Unint(byte[] res) {
        if (res.length == 4) {
            return (((0 + ((res[0] & 255) * 16777216)) + ((res[1] & 255) * 65536)) + ((res[2] & 255) * 256)) + (res[3] & 255);
        }
        if (res.length == 2) {
            return (0 + ((res[0] & 255) * 256)) + (res[1] & 255);
        }
        return 0 + (res[0] & 255);
    }

    public static int byte2Unint(byte res) {
        if (res < (byte) 0) {
            return res + 256;
        }
        return res;
    }

    public static boolean[] byte2Bool(byte b) {
        boolean[] array = new boolean[8];
        for (int i = 0; i < 8; i++) {
            array[i] = (b & 1) == 1;
            b = (byte) (b >> 1);
        }
        return array;
    }

    public static char ascii2Char(int ASCII) {
        return (char) ASCII;
    }

    public static int char2ASCII(char c) {
        return c;
    }

    public static String ascii2String(int[] ASCIIs) {
        StringBuffer sb = new StringBuffer();
        for (int ascii2Char : ASCIIs) {
            sb.append(ascii2Char(ascii2Char));
        }
        return sb.toString();
    }

    public static String byte2AccStr(byte[] ASCIIs) {
        StringBuffer sb = new StringBuffer();
        for (byte byte2Unint : ASCIIs) {
            sb.append(ascii2Char(byte2Unint(byte2Unint)));
        }
        return sb.toString();
    }

    public static String ascii2String(byte[] ASCIIs) {
        StringBuffer sb = new StringBuffer();
        for (byte ascii2Char : ASCIIs) {
            sb.append(ascii2Char(ascii2Char));
        }
        return sb.toString();
    }

    public static String ascii2String(String ASCIIs) {
        String[] ASCIIss = ASCIIs.split(",");
        StringBuffer sb = new StringBuffer();
        for (String parseInt : ASCIIss) {
            sb.append(ascii2Char(Integer.parseInt(parseInt)));
        }
        return sb.toString();
    }

    public static int[] string2ASCII(String s) {
        if (s == null || "".equals(s)) {
            return null;
        }
        char[] chars = s.toCharArray();
        int[] asciiArray = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            asciiArray[i] = char2ASCII(chars[i]);
        }
        return asciiArray;
    }

    public static boolean checkEmail(String maddr) {
        int i1 = maddr.indexOf("@");
        int i2 = maddr.indexOf(".");
        if (i1 <= 0 || i2 <= 0 || i2 <= i1) {
            return false;
        }
        return true;
    }

    public static String unicodeToString(String str) {
        Matcher matcher = Pattern.compile("(\\\\u(\\p{XDigit}{4}))").matcher(str);
        while (matcher.find()) {
            str = str.replace(matcher.group(1), ((char) Integer.parseInt(matcher.group(2), 16)) + "");
        }
        return str;
    }
}
