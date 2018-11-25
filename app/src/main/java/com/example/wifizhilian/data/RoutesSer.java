package com.example.wifizhilian.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class RoutesSer {
    private DataBaseOpenHelper dbOpenHelper;

    public RoutesSer(Context context) {
        this.dbOpenHelper = new DataBaseOpenHelper(context);
    }

    public String getPass(String ssid) {
        String reStr = "";
        SQLiteDatabase database = this.dbOpenHelper.getReadableDatabase();
        database.beginTransaction();
        Cursor cursor = database.rawQuery("select pass from routes where ssid=?", new String[]{ssid});
        if (cursor.moveToNext()) {
            reStr = cursor.getString(0);
        }
        cursor.close();
        database.endTransaction();
        database.close();
        return reStr;
    }

    public void Update(String ssid, String pass) {
        SQLiteDatabase database = this.dbOpenHelper.getReadableDatabase();
        database.beginTransaction();
        Cursor cursor = database.rawQuery("select id from routes where ssid=?", new String[]{ssid});
        if (cursor.moveToNext()) {
            database.execSQL("update routes set pass=? where ssid=?", new Object[]{pass, ssid});
        } else {
            database.execSQL("insert into routes (ssid,pass) values (?,?)", new Object[]{ssid, pass});
        }
        cursor.close();
        database.endTransaction();
        database.close();
    }

    public int save(RoutesObj obj) {
        int curID = 0;
        SQLiteDatabase database = this.dbOpenHelper.getWritableDatabase();
        database.beginTransaction();
        database.execSQL("insert into routes(ssid,pass)values(?,?)", new Object[]{obj.getSSID(), obj.getPassWord()});
        database.setTransactionSuccessful();
        Cursor cursor = database.rawQuery("select max(id) from routes", null);
        if (cursor.moveToNext()) {
            curID = cursor.getInt(0);
        }
        database.endTransaction();
        database.close();
        return curID;
    }

    public void update(RoutesObj obj) {
        SQLiteDatabase database = this.dbOpenHelper.getWritableDatabase();
        database.execSQL("update routes set ssid=?,pass=? where id=?", new Object[]{obj.getSSID(), obj.getPassWord(), Integer.valueOf(obj.getID())});
        database.close();
    }

    public RoutesObj find(Integer id) {
        RoutesObj obj = null;
        SQLiteDatabase database = this.dbOpenHelper.getReadableDatabase();
        database.beginTransaction();
        Cursor cursor = database.rawQuery("select * from routes where id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToNext()) {
            obj = new RoutesObj();
            obj.setID(cursor.getInt(0));
            obj.setSSID(cursor.getString(1));
            obj.setPassWord(cursor.getString(2));
        }
        cursor.close();
        database.endTransaction();
        database.close();
        return obj;
    }

    public void delete(Integer... ids) {
        if (ids.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (Integer id : ids) {
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            this.dbOpenHelper.getWritableDatabase().execSQL("delete from routes where id in(" + sb.toString() + ")", ids);
        }
    }

    public RoutesObj getbySSID(String key) {
        RoutesObj obj = null;
        SQLiteDatabase database = this.dbOpenHelper.getReadableDatabase();
        database.beginTransaction();
        Cursor cursor = database.rawQuery("select * from routes where ssid=? order by id desc", new String[]{key});
        if (cursor.moveToNext()) {
            obj = new RoutesObj();
            obj.setID(cursor.getInt(0));
            obj.setSSID(cursor.getString(1));
            obj.setPassWord(cursor.getString(2));
        }
        cursor.close();
        database.endTransaction();
        database.close();
        return obj;
    }

    public List<RoutesObj> getListbyAll() {
        List<RoutesObj> ads = new ArrayList();
        SQLiteDatabase database = this.dbOpenHelper.getReadableDatabase();
        database.beginTransaction();
        Cursor cursor = database.rawQuery("select * from routes order by id desc", new String[0]);
        while (cursor.moveToNext()) {
            RoutesObj obj = new RoutesObj();
            obj.setID(cursor.getInt(0));
            obj.setSSID(cursor.getString(1));
            obj.setPassWord(cursor.getString(2));
        }
        cursor.close();
        database.endTransaction();
        database.close();
        return ads;
    }

    public List<RoutesObj> getScrollData(int startResult, int maxResult) {
        List<RoutesObj> objs = new ArrayList();
        SQLiteDatabase database = this.dbOpenHelper.getReadableDatabase();
        database.beginTransaction();
        Cursor cursor = database.rawQuery("select * from routes limit ?,?", new String[]{String.valueOf(startResult), String.valueOf(maxResult)});
        while (cursor.moveToNext()) {
            RoutesObj obj = new RoutesObj();
            obj.setID(cursor.getInt(0));
            obj.setSSID(cursor.getString(1));
            obj.setPassWord(cursor.getString(2));
            objs.add(obj);
        }
        cursor.close();
        database.endTransaction();
        database.close();
        return objs;
    }

    public void ClearData() {
        SQLiteDatabase database = this.dbOpenHelper.getReadableDatabase();
        database.execSQL("delete * from routes");
        database.close();
    }

    public Cursor getRawScrollData(int startResult, int maxResult) {
        return this.dbOpenHelper.getReadableDatabase().rawQuery("select id,ssid,pass from routes limit ?,?", new String[]{String.valueOf(startResult), String.valueOf(maxResult)});
    }

    public long getCount() {
        Cursor cursor = this.dbOpenHelper.getReadableDatabase().rawQuery("select count(*) from routes", null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }
}
