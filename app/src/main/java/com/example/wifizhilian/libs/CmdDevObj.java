package com.example.wifizhilian.libs;

import java.nio.ByteBuffer;

public class CmdDevObj {
    private static final String TAG = "CmdDevObj";
    private int Code;
    private int DevID;
    private String Header = "FFFD";
    private int PackNum;
    private int Type;
    private int ValLength;
    private byte[] Value;
    private byte Verify;
    private boolean isValid = false;

    public CmdDevObj(String data) {
        if (data.length() < 28) {
            this.isValid = false;
        } else if (data.startsWith(this.Header)) {
            this.Type = ComUtils.byte2Unint(ComUtils.hexStringToBytes(data.substring(4, 8)));
            this.DevID = ComUtils.byte2Unint(ComUtils.hexStringToBytes(data.substring(8, 16)));
            this.PackNum = ComUtils.byte2Unint(ComUtils.hexStringToBytes(data.substring(16, 20)));
            this.Code = ComUtils.byte2Unint(ComUtils.hexStringToBytes(data.substring(20, 22)));
            this.ValLength = ComUtils.byte2Unint(ComUtils.hexStringToBytes(data.substring(22, 26)));
            if (data.length() < (this.ValLength * 2) + 28) {
                this.isValid = false;
                return;
            }
            this.Verify = ComUtils.hexStringToBytes(data.substring((this.ValLength * 2) + 26, (this.ValLength * 2) + 28))[0];
            if (this.ValLength > 0) {
                this.Value = ComUtils.hexStringToBytes(data.substring(26, (this.ValLength * 2) + 26));
            } else {
                this.Value = new byte[0];
            }
            if (getVerifyCode() == this.Verify) {
                this.isValid = true;
            }
        } else {
            this.isValid = false;
        }
    }

    public byte[] getCommand() {
        ByteBuffer buf = ByteBuffer.allocate((this.Value == null ? 0 : this.Value.length) + 14);
        buf.put(ComUtils.hexStringToBytes(this.Header));
        buf.put(ComUtils.int2ByteArr(this.Type, 2));
        buf.put(ComUtils.int2ByteArr(this.DevID, 4));
        buf.put(ComUtils.int2ByteArr(this.PackNum, 2));
        buf.put(ComUtils.int2ByteArr(this.Code, 1));
        buf.put(ComUtils.int2ByteArr(this.ValLength, 2));
        if (this.Value != null) {
            buf.put(this.Value);
        }
        buf.put(getVerifyCode(buf.array()));
        return buf.array();
    }

    private byte getVerifyCode(byte[] data) {
        byte reVal = (byte) 0;
        for (int ii = 2; ii < data.length - 1; ii++) {
            reVal = (byte) (data[ii] + reVal);
        }
        return reVal;
    }

    private byte getVerifyCode() {
        int valLen;
        if (this.Value == null) {
            valLen = 0;
        } else {
            valLen = this.Value.length;
        }
        ByteBuffer buf = ByteBuffer.allocate(valLen + 13);
        buf.put(ComUtils.hexStringToBytes(this.Header));
        buf.put(ComUtils.int2ByteArr(this.Type, 2));
        buf.put(ComUtils.int2ByteArr(this.DevID, 4));
        buf.put(ComUtils.int2ByteArr(this.PackNum, 2));
        buf.put(ComUtils.int2ByteArr(this.Code, 1));
        buf.put(ComUtils.int2ByteArr(this.ValLength, 2));
        if (this.Value != null) {
            buf.put(this.Value);
        }
        byte[] tmpVer = buf.array();
        byte reVal = (byte) 0;
        for (int ii = 2; ii < tmpVer.length; ii++) {
            reVal = (byte) (tmpVer[ii] + reVal);
        }
        return reVal;
    }

    public int getType() {
        return this.Type;
    }

    public void setType(int type) {
        this.Type = type;
    }

    public int getDevID() {
        return this.DevID;
    }

    public void setDevID(int devID) {
        this.DevID = devID;
    }

    public int getPackNum() {
        return this.PackNum;
    }

    public void setPackNum(int packNum) {
        this.PackNum = packNum;
    }

    public int getCode() {
        return this.Code;
    }

    public void setCode(int code) {
        this.Code = code;
    }

    public int getValLength() {
        return this.ValLength;
    }

    public void setValLength(int valLength) {
        this.ValLength = valLength;
    }

    public byte[] getValue() {
        return this.Value;
    }

    public void setValue(byte[] value) {
        this.Value = value;
    }

    public byte getVerify() {
        return this.Verify;
    }

    public void setVerify(byte verify) {
        this.Verify = verify;
    }

    public boolean isValid() {
        return this.isValid;
    }
}
