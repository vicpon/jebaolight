package com.example.wifizhilian.libs;

import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class HttpClient {
    public static final int Net_DownFile_Error = 20093;
    public static final int Net_DownFile_GetSize = 20092;
    public static final int Net_DownFile_Over = 20094;
    public static final int Net_DownFile_Progress = 20095;
    private static final String TAG = "HttpClient";

    public static class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }

    public static byte[] get(String url) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet method = new HttpGet(url);
        HttpParams params = new BasicHttpParams();
        params.setIntParameter("http.connection.timeout", 10000);
        httpClient.setParams(params);
        HttpResponse resp = httpClient.execute(method);
        if (resp.getStatusLine().getStatusCode() != 200) {
            return null;
        }
        return StreamTool.readInputStream(resp.getEntity().getContent());
    }

    public static String get(String url, String encode) throws Exception {
        xLog.m5i(TAG, "Http get Path:" + url);
        HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
        httpResponse.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(httpResponse.getEntity(), encode);
        }
        return "";
    }

    public static String postJson(String actionUrl, StringEntity JsonStr) {
        String reStr = "";
        HttpPost request = new HttpPost(actionUrl);
        request.setEntity(JsonStr);
        try {
            reStr = EntityUtils.toString(new DefaultHttpClient().execute(request).getEntity());
        } catch (ClientProtocolException e) {
            System.out.print(e.toString());
        } catch (IOException e2) {
            System.out.print(e2.toString());
        }
        return reStr;
    }

    public static String post(String actionUrl, Map<String, String> params, File[] files) {
        try {
            String BOUNDARY = "---------------------------7da2137580612";
            String endline = "--" + BOUNDARY + "--\r\n";
            HttpURLConnection conn = (HttpURLConnection) new URL(actionUrl).openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data" + "; boundary=" + BOUNDARY);
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : params.entrySet()) {
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + ((String) entry.getKey()) + "\"\r\n\r\n");
                sb.append((String) entry.getValue());
                sb.append("\r\n");
            }
            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.write(sb.toString().getBytes());
            int length = files.length;
            int i = 0;
            while (i < length) {
                File file = files[i];
                if (file.isFile()) {
                    StringBuilder split = new StringBuilder();
                    split.append("--");
                    split.append(BOUNDARY);
                    split.append("\r\n");
                    split.append("Content-Disposition: form-data;name=\"" + file.getName() + "\";filename=\"" + file.getName() + "\"\r\n");
                    dataOutputStream.write(split.toString().getBytes());
                    FileInputStream filestream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int len = filestream.read(buffer);
                        if (len == -1) {
                            break;
                        }
                        dataOutputStream.write(buffer, 0, len);
                    }
                    filestream.close();
                    dataOutputStream.write("\r\n".getBytes());
                    i++;
                } else {
                    throw new RuntimeException("文件读取失败");
                }
            }
            dataOutputStream.write(("--" + BOUNDARY + "--\r\n").getBytes());
            dataOutputStream.flush();
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("请求url 失败");
            }
            InputStream is = conn.getInputStream();
            StringBuilder b = new StringBuilder();
            while (true) {
                int ch = is.read();
                if (ch != -1) {
                    b.append((char) ch);
                } else {
                    dataOutputStream.close();
                    conn.disconnect();
                    return b.toString();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String post(String actionUrl, Map<String, String> params, File file) {
        return post(actionUrl, (Map) params, new File[]{file});
    }

    public static String postFromHttpClient(String path, Map<String, String> params) throws Exception {
        xLog.m5i(TAG, "Post Url:" + path);
        String reStr = EncodingUtils.getString(postFromHttpClient(path, params, "UTF-8"), "UTF-8");
        xLog.m5i(TAG, "Post Revert Data:" + reStr);
        return reStr;
    }

    public static byte[] postFromHttpClientForB(String path, Map<String, String> params) throws Exception {
        String tmpStr = "?";
        for (Entry<String, String> entry : params.entrySet()) {
            String key = ((String) entry.getKey()).toString();
            tmpStr = tmpStr + key + "=" + ((String) entry.getValue()).toString() + "&";
        }
        return postFromHttpClient(path, params, "UTF-8");
    }

    public static byte[] postFromHttpClient(String path, Map<String, String> params, String encode) throws Exception {
        List<NameValuePair> formparams = new ArrayList();
        if (params != null) {
            for (Entry<String, String> entry : params.entrySet()) {
                formparams.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, encode);
        HttpPost httppost = new HttpPost(path);
        httppost.setEntity(entity);
        return StreamTool.readInputStream(new DefaultHttpClient().execute(httppost).getEntity().getContent());
    }

    public static String postFile(String webUrl, String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return "";
        }
        String BOUNDARY = "*****";
        String endline = "\r\n";
        String twoHyphens = "--";
        String MULTIPART_FORM_DATA = "multipart/form-data";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(webUrl).openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + BOUNDARY + endline);
            ds.writeBytes("Content-Disposition:form-data;name=\"file1\";filename=\"" + file.getName() + "\"" + endline);
            ds.writeBytes(endline);
            FileInputStream fStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            while (true) {
                int length = fStream.read(buffer);
                if (length == -1) {
                    break;
                }
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(endline);
            ds.writeBytes(twoHyphens + BOUNDARY + twoHyphens + endline);
            fStream.close();
            ds.flush();
            InputStream is = con.getInputStream();
            StringBuffer b = new StringBuffer();
            while (true) {
                int ch = is.read();
                if (ch != -1) {
                    b.append((char) ch);
                } else {
                    ds.close();
                    con.disconnect();
                    return b.toString();
                }
            }
        } catch (MalformedURLException e) {
            System.out.print("MalformedURLException upfile error:" + e.toString());
            return "";
        } catch (ProtocolException e2) {
            System.out.print("ProtocolException upfile error:" + e2.toString());
            return "";
        } catch (IOException e3) {
            System.out.print("IOException upfile error:" + e3.toString());
            return "";
        }
    }

    public static byte[] post(String path, Map<String, String> params, String encode) throws Exception {
        StringBuilder parambuilder = new StringBuilder("");
        if (!(params == null || params.isEmpty())) {
            for (Entry<String, String> entry : params.entrySet()) {
                parambuilder.append((String) entry.getKey()).append("=").append(URLEncoder.encode((String) entry.getValue(), encode)).append("&");
            }
            parambuilder.deleteCharAt(parambuilder.length() - 1);
        }
        byte[] data = parambuilder.toString().getBytes();
        HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg,image/pjpeg, application/x-shockwave-flash, application/xaml+xml,application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application,application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0;Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("Connection", "Keep-Alive");
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(data);
        outStream.flush();
        outStream.close();
        if (conn.getResponseCode() == 200) {
            return StreamTool.readInputStream(conn.getInputStream());
        }
        return null;
    }

    public static String DownFile(String url, String fileName) {
        return DownFile(url, fileName, null);
    }

    public static String DownFile(String url, String fileName, Handler reHandler) {
        String FileName = "";
        if (fileName == null || fileName == "") {
            FileName = url.substring(url.lastIndexOf("/") + 1);
        } else {
            FileName = fileName;
        }
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            long fileSize = (long) conn.getContentLength();
            if (fileSize <= 0) {
                if (reHandler != null) {
                    reHandler.sendEmptyMessage(Net_DownFile_Error);
                }
                return "";
            } else if (is == null) {
                if (reHandler != null) {
                    reHandler.sendEmptyMessage(Net_DownFile_Error);
                }
                return "";
            } else {
                Message msg;
                if (reHandler != null) {
                    msg = new Message();
                    msg.what = Net_DownFile_GetSize;
                    msg.obj = Long.valueOf(fileSize);
                    reHandler.sendMessage(msg);
                }
                FileOutputStream FOS = new FileOutputStream(FileName);
                byte[] buf = new byte[262144];
                int downSize = 0;
                long setNum = System.currentTimeMillis();
                while (true) {
                    int numread = is.read(buf);
                    if (numread == -1) {
                        break;
                    }
                    FOS.write(buf, 0, numread);
                    downSize += numread;
                    if (reHandler != null && System.currentTimeMillis() - setNum > 1000) {
                        setNum = System.currentTimeMillis();
                        msg = new Message();
                        msg.what = Net_DownFile_Progress;
                        msg.obj = Integer.valueOf((int) (((long) (downSize * 100)) / fileSize));
                        reHandler.sendMessage(msg);
                    }
                }
                if (reHandler == null) {
                    return FileName;
                }
                msg = new Message();
                msg.what = Net_DownFile_Over;
                msg.obj = FileName;
                reHandler.sendMessage(msg);
                return FileName;
            }
        } catch (MalformedURLException e) {
            xLog.m5i(TAG, e.toString());
            if (reHandler != null) {
                reHandler.sendEmptyMessage(Net_DownFile_Error);
            }
            return null;
        } catch (IOException e2) {
            xLog.m5i(TAG, e2.toString());
            if (reHandler != null) {
                reHandler.sendEmptyMessage(Net_DownFile_Error);
            }
            return null;
        }
    }

    public static String getPostVerify(Map<String, String> params) {
        String tmpStr = "";
        if (params == null || params.isEmpty()) {
            return "";
        }
        Map<String, String> sortMap = new TreeMap(new MapKeyComparator());
        sortMap.putAll(params);
        for (Entry<String, String> entry : sortMap.entrySet()) {
            if (!ComUtils.StrIsEmpty(tmpStr)) {
                tmpStr = tmpStr + "&";
            }
            tmpStr = tmpStr + ((String) entry.getKey()) + "=" + (entry.getValue() == null ? "" : (String) entry.getValue());
        }
        return DataCrypto.getMD5(tmpStr);
    }
}
