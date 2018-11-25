package com.example.wifizhilian.libs;

import java.util.ArrayList;

public class SimpleXml {
    private static final String TAG = "SimpleXml";

    public static String getXmlValue(String sourstr, String key) {
        if (ComUtils.StrIsEmpty(sourstr) || ComUtils.StrIsEmpty(key)) {
            return "";
        }
        String skey = "<" + key;
        String ekey = "</" + key + ">";
        int start = sourstr.indexOf(skey);
        if (start < 0) {
            return "";
        }
        start = sourstr.indexOf(">", start) + 1;
        if (start < 1) {
            return "";
        }
        int end = sourstr.indexOf(ekey, start);
        if (end < start) {
            return "";
        }
        return sourstr.substring(start, end);
    }

    public static ArrayList<String> getXmlNodes(String sourstr, String key) {
        ArrayList<String> nodes = new ArrayList();
        if (!ComUtils.StrIsEmpty(sourstr) && !ComUtils.StrIsEmpty(key)) {
            int endnum = 0;
            int tnum = 0;
            String skey = "<" + key;
            String ekey = "</" + key + ">";
            while (true) {
                int startnum = sourstr.indexOf(skey, endnum);
                if (startnum < 0) {
                    break;
                }
                endnum = sourstr.indexOf(ekey, skey.length() + startnum);
                if (endnum <= startnum) {
                    break;
                }
                nodes.add(sourstr.substring(startnum, ekey.length() + endnum));
                tnum++;
            }
        }
        return nodes;
    }

    public static String getXMLNode(String sourstr, String key) {
        if (ComUtils.StrIsEmpty(sourstr) || ComUtils.StrIsEmpty(key)) {
            return "";
        }
        String skey = "<" + key;
        String ekey = "</" + key + ">";
        int startnum = sourstr.indexOf(skey);
        if (startnum < 0) {
            return "";
        }
        int endnum = sourstr.indexOf(ekey, skey.length() + startnum);
        if (endnum > startnum) {
            return sourstr.substring(startnum, ekey.length() + endnum);
        }
        return "";
    }

    public static String getXmlAttrib(String sourstr, String attrib) {
        if (ComUtils.StrIsEmpty(sourstr) || ComUtils.StrIsEmpty(attrib)) {
            return "";
        }
        String reStr = "";
        String skey = attrib + "=";
        int start = sourstr.indexOf(skey);
        if (start < 0) {
            return "";
        }
        sourstr = sourstr.substring(skey.length() + start);
        sourstr.trim();
        if (sourstr.startsWith("\"")) {
            return sourstr.substring(1, sourstr.indexOf("\"", 1));
        }
        int endnum = sourstr.indexOf(" ", 1);
        if (endnum < 0) {
            endnum = sourstr.indexOf(">", 1);
        }
        if (endnum < 0) {
            return "";
        }
        return sourstr.substring(0, endnum);
    }
}
