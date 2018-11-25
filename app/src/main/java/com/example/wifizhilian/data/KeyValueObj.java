package com.example.wifizhilian.data;

public class KeyValueObj {
    public int IntVal = 0;
    public int Key = 0;
    public int ResID = 0;
    public String StringVal = "";

    public KeyValueObj() {
    }
    public KeyValueObj(int i, String s, int k) {
        this.IntVal = i;
        this.StringVal = s;
        this.Key = k;
    }
}
