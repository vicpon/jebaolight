package com.example.wifizhilian.libs;

import com.facebook.appevents.AppEventsConstants;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DataCrypto {
    private static String DESKey = "9f55807f";
    private static char[] base64_table = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    public static String DESDeCode(String message, String key) throws Exception {
        if (key.length() > 8) {
            key = key.substring(0, 8);
        }
        byte[] bytesrc = convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(2, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes("UTF-8"))), new IvParameterSpec(key.getBytes("UTF-8")));
        return new String(cipher.doFinal(bytesrc));
    }

    public static String DESEnCode(String message, String key) throws Exception {
        if (key.length() > 8) {
            key = key.substring(0, 8);
        }
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(1, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes("UTF-8"))), new IvParameterSpec(key.getBytes("UTF-8")));
        return toHexString(cipher.doFinal(message.getBytes("UTF-8")));
    }

    public static byte[] convertHexString(String ss) {
        byte[] digest = new byte[(ss.length() / 2)];
        for (int i = 0; i < digest.length; i++) {
            digest[i] = (byte) Integer.parseInt(ss.substring(i * 2, (i * 2) + 2), 16);
        }
        return digest;
    }

    public static String toHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (byte b2 : b) {
            String plainText = Integer.toHexString(b2 & 255);
            if (plainText.length() < 2) {
                plainText = AppEventsConstants.EVENT_PARAM_VALUE_NO + plainText;
            }
            hexString.append(plainText);
        }
        return hexString.toString();
    }

    public static String MD5Encrypts(String info) {
        try {
            byte[] res = info.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] result = md.digest(res);
            for (byte update : result) {
                md.update(update);
            }
            byte[] hash = md.digest();
            StringBuffer d = new StringBuffer("");
            for (byte update2 : hash) {
                int v = update2 & 255;
                if (v < 16) {
                    d.append(AppEventsConstants.EVENT_PARAM_VALUE_NO);
                }
                d.append(Integer.toString(v, 16).toUpperCase());
            }
            return d.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(encryption[i] & 255).length() == 1) {
                    strBuf.append(AppEventsConstants.EVENT_PARAM_VALUE_NO).append(Integer.toHexString(encryption[i] & 255));
                } else {
                    strBuf.append(Integer.toHexString(encryption[i] & 255));
                }
            }
            return strBuf.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String Base64Encode(String str) {
        if (str == null || str.equals("")) {
            return "";
        }
        byte[] a = str.getBytes();
        int totalBits = a.length * 8;
        int nn = totalBits % 6;
        StringBuffer toReturn = new StringBuffer();
        for (int curPos = 0; curPos < totalBits; curPos += 6) {
            int bytePos = curPos / 8;
            switch (curPos % 8) {
                case 0:
                    toReturn.append(base64_table[(a[bytePos] & 252) >> 2]);
                    break;
                case 2:
                    toReturn.append(base64_table[a[bytePos] & 63]);
                    break;
                case 4:
                    if (bytePos != a.length - 1) {
                        toReturn.append(base64_table[(((a[bytePos] & 15) << 2) | ((a[bytePos + 1] & 192) >> 6)) & 63]);
                        break;
                    }
                    toReturn.append(base64_table[((a[bytePos] & 15) << 2) & 63]);
                    break;
                case 6:
                    if (bytePos != a.length - 1) {
                        toReturn.append(base64_table[(((a[bytePos] & 3) << 4) | ((a[bytePos + 1] & 240) >> 4)) & 63]);
                        break;
                    }
                    toReturn.append(base64_table[((a[bytePos] & 3) << 4) & 63]);
                    break;
                default:
                    break;
            }
        }
        if (nn == 2) {
            toReturn.append("==");
        } else if (nn == 4) {
            toReturn.append("=");
        }
        return toReturn.toString();
    }
}
