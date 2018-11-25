package com.espressif.iot.Esptouch.util;

import java.util.zip.Checksum;

public class CRC8 implements Checksum {
    private static final short CRC_INITIAL = (short) 0;
    private static final short CRC_POLYNOM = (short) 140;
    private static final short[] crcTable = new short[256];
    private final short init = CRC_INITIAL;
    private short value = CRC_INITIAL;

    static {
        for (int dividend = 0; dividend < 256; dividend++) {
            int remainder = dividend;
            for (int bit = 0; bit < 8; bit++) {
                if ((remainder & 1) != 0) {
                    remainder = (remainder >>> 1) ^ 140;
                } else {
                    remainder >>>= 1;
                }
            }
            crcTable[dividend] = (short) remainder;
        }
    }

    public void update(byte[] buffer, int offset, int len) {
        for (int i = 0; i < len; i++) {
            this.value = (short) (crcTable[(buffer[offset + i] ^ this.value) & 255] ^ (this.value << 8));
        }
    }

    public void update(byte[] buffer) {
        update(buffer, 0, buffer.length);
    }

    public void update(int b) {
        update(new byte[]{(byte) b}, 0, 1);
    }

    public long getValue() {
        return (long) (this.value & 255);
    }

    public void reset() {
        this.value = this.init;
    }
}
