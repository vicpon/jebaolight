package com.example.wifizhilian.libs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
    public static byte[] readInputStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        while (true) {
            int len = inputStream.read(buffer);
            if (len != -1) {
                outSteam.write(buffer, 0, len);
            } else {
                outSteam.close();
                inputStream.close();
                return outSteam.toByteArray();
            }
        }
    }
}
