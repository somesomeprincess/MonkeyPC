package hippo.com.xuhangtest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import hippo.com.xuhangtest.module.PowerPercent;

public class DBManager {
    private DatabaseHelper mHelper;
    String dbname = "power.db";
    SQLiteDatabase db;
    public DBManager(Context context){
        mHelper = new DatabaseHelper(context,dbname);
        db =mHelper.getWritableDatabase();

    }
    public int cleartable(String table){
        db.execSQL("delete from "+table);
        int row = db.delete(table,"1", new String[]{""});
        return row;
    }

    public void droptable(String table){
        db.execSQL("drop table "+table);
        db.execSQL("create table "+table+" (_id integer primary key,time text,power text,addform text)");
    }

    public void insert(PowerPercent percent){
        db.execSQL("insert into power values(null,?,?,?)",new Object[]{percent.curtime,percent.powerpct,percent.addform});
    }

    public void insert(String table, ContentValues values){
        db.insert(table,null,values);
    }

    public void closeDB(){
        db.close();
    }
    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs){
        return db.query(table,projection,selection,selectionArgs,null,null,null,null);
    };

}
