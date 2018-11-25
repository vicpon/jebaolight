package com.example.wifizhilian.libs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonGet {
    public static JSONObject getPathObj(JSONObject data, String path) {
        if (data == null) {
            return null;
        }
        JSONObject reObj = data;
        String[] ps = path.split("/");
        for (int ii = 0; ii < ps.length; ii++) {
            if (!data.isNull(ps[ii])) {
                return null;
            }
            reObj = getObject(reObj, ps[ii]);
        }
        return reObj;
    }

    public static boolean putPathObj(JSONObject data, String path, Object val) {
        if (data == null) {
            data = new JSONObject();
        }
        JSONObject reObj = data;
        String[] ps = path.split("/");
        for (int ii = 0; ii < ps.length - 1; ii++) {
            if (data.isNull(ps[ii])) {
                reObj = getObject(reObj, ps[ii]);
            } else {
                try {
                    reObj = reObj.put(ps[ii], new JSONObject());
                } catch (JSONException e) {
                    return false;
                }
            }
        }
        try {
            reObj.put(getEndPointName(path), val);
        } catch (JSONException e2) {
        }
        return true;
    }

    private static String getEndPointName(String path) {
        if (path.lastIndexOf(47) >= 0) {
            return path.substring(path.lastIndexOf(47) + 1);
        }
        return path;
    }

    private static boolean isBranch(String path) {
        if (path.lastIndexOf(47) >= 0) {
            return true;
        }
        return false;
    }

    public static int getInt(JSONObject data, String key) {
        int i = 0;
        if (data != null) {
            try {
                if (!data.isNull(key)) {
                    i = data.getInt(key);
                }
            } catch (JSONException e) {
                System.out.print(e.toString());
            }
        }
        return i;
    }

    public static Double getDouble(JSONObject data, String key) {
        if (data != null) {
            try {
                if (!data.isNull(key)) {
                    return Double.valueOf(data.getDouble(key));
                }
            } catch (JSONException e) {
                System.out.print(e.toString());
                return Double.valueOf(0.0d);
            }
        }
        return Double.valueOf(0.0d);
    }

    public static String getStr(JSONObject data, String key) {
        if (data != null) {
            try {
                if (!data.isNull(key)) {
                    return data.getString(key);
                }
            } catch (JSONException e) {
                System.out.print(e.toString());
                return "";
            }
        }
        return "";
    }

    public static String getUStr(JSONObject data, String key) {
        if (data != null) {
            try {
                if (!data.isNull(key)) {
                    return ComUtils.unicodeToString(data.getString(key));
                }
            } catch (JSONException e) {
                System.out.print(e.toString());
                return "";
            }
        }
        return "";
    }

    public static JSONArray getArray(JSONObject data, String key) {
        JSONArray jSONArray = null;
        if (data != null) {
            try {
                if (!data.isNull(key)) {
                    jSONArray = data.getJSONArray(key);
                }
            } catch (JSONException e) {
                System.out.print(e.toString());
            }
        }
        return jSONArray;
    }

    public static JSONObject getObject(JSONObject data, String key) {
        JSONObject jSONObject = null;
        if (data != null) {
            try {
                if (!data.isNull(key)) {
                    jSONObject = data.getJSONObject(key);
                }
            } catch (JSONException e) {
                System.out.print(e.toString());
            }
        }
        return jSONObject;
    }

    public static void pubData(JSONObject data, String key, int val) {
        try {
            data.put(key, val);
        } catch (JSONException e) {
            System.out.print(e.toString());
        }
    }

    public static void pubData(JSONObject data, String key, Double val) {
        try {
            data.put(key, val);
        } catch (JSONException e) {
            System.out.print(e.toString());
        }
    }

    public static void pubData(JSONObject data, String key, String val) {
        try {
            data.put(key, val);
        } catch (JSONException e) {
            System.out.print(e.toString());
        }
    }

    public static void pubData(JSONObject data, String key, JSONObject val) {
        try {
            data.put(key, val);
        } catch (JSONException e) {
            System.out.print(e.toString());
        }
    }

    public static void pubData(JSONObject data, String key, JSONArray val) {
        try {
            data.put(key, val);
        } catch (JSONException e) {
            System.out.print(e.toString());
        }
    }
}
