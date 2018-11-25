package com.example.wifizhilian.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseOpenHelper extends SQLiteOpenHelper {
    private static String dbname = "wifizhilian.db";
    private static int version = 4;

    public DataBaseOpenHelper(Context context) {
        super(context, dbname, null, version);
    }

    public DataBaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        CreateTable(db);
    }

    private void CreateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE binddev (id INTEGER PRIMARY KEY,netid INTEGER,chipid TEXT,mac TEXT,userid TEXT,product TEXT,ssid TEXT,bindtime TEXT,state INTEGER,updatecode varchar(20),nikename TEXT,mtoptime INTEGER,isgroup INTEGER);");
        db.execSQL("CREATE TABLE devset (id INTEGER PRIMARY KEY,netid INTEGER,chipid TEXT,day INTEGER,state INTEGER,data TEXT,nikename TEXT);");
        db.execSQL("CREATE TABLE devtran (id INTEGER PRIMARY KEY,netid INTEGER,chipid TEXT,seti INTEGER,state INTEGER,startact INTEGER,period INTEGER,endact INTEGER,nikename TEXT);");
        db.execSQL("CREATE TABLE devgroup (id INTEGER PRIMARY KEY,mainnetid INTEGER,mainchipid TEXT,seti INTEGER,sonchipid TEXT);");
        db.execSQL("CREATE TABLE routes (id INTEGER PRIMARY KEY,ssid TEXT,pass TEXT);");
    }

    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS binddev");
        db.execSQL("DROP TABLE IF EXISTS devset");
        db.execSQL("DROP TABLE IF EXISTS routes");
        CreateTable(db);
    }
}
