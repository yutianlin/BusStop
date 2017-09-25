package com.example.android.busstop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luyun_2 on 2017-09-05.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "bus_stops";
    private static final String COL0 = "id";
    private static final String COL1 = "stop_number";
    private static final String COL2 = "start";
    private static final String COL3 = "destination";
    private static final String COL4 = "bus_number";

    public DatabaseHelper (Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        // Create initial database with ID and 4 columns

        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL1 + " INTEGER, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        // // TODO: 2017-09-24 Check what this does

        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(int stopNum, String start, String destin, int busNum){

        // When a new route is added

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, stopNum);
        contentValues.put(COL2, start);
        contentValues.put(COL3, destin);
        contentValues.put(COL4, busNum);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getData(){

        // When all the information in database needs to be retrieved for MainActivity

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getBusDetails(String start, String destin, int busNum) {

        // When NewBus needs to load previous data entered (ID and Stop Number)

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + ", " + COL1 + " FROM " +
                TABLE_NAME + " WHERE " + COL2 +  "='" + start + "' AND " + COL3 + " = '" +
                destin + "' AND " + COL4 + " = '" + busNum + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getStopNum(String start, String destin, int busNum){

        // When SMS needs to be sent, stop number is retrieved

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " +  COL1 + " FROM " +
                TABLE_NAME + " WHERE " + COL2 +  "='" + start + "' AND " + COL3 + " = '" +
                destin + "' AND " + COL4 + " = '" + busNum + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateDetails(int stopNum, String start, String destin, int busNum, Long id){

        // When information in NewBus needs to be updated

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL1 + " = '" + stopNum + "', " + COL2 +
                " = '" + start + "', " + COL3 + " = '" + destin + "', " + COL4 + " = '" +
                busNum + "' WHERE " + COL0 + " = '" + id + "'";
        db.execSQL(query);
    }

    public void deleteRoute(Long id){

        // When information in NewBus needs to be deleted

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL0 + " = '" + id + "'";
        db.execSQL(query);
    }

}
