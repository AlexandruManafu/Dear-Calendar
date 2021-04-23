package com.example.dearcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(@Nullable Context context) {
        super(context, "data", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create Table events" +
                "(eventId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "recId INTEGER NOT NULL," +
                "eventTitle TEXT NOT NULL," +
                "eventState TEXT NOT NULL," +
                "eventColor TEXT NOT NULL," +
                "startHour TEXT NOT NULL," +
                "endHour TEXT NOT NULL," +
                "startDate TEXT NOT NULL," +
                "eventRec TEXT NOT NULL," +
                "recDuration INTEGER," +
                "eventDetails TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS events";
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean addEntry(int recId, String title, String state, String color, String start,
                            String end, String date, String rec, int expire, String details)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
        contentValues.put("recId",recId);
        contentValues.put("eventTitle",title);
        contentValues.put("eventState",state);
        contentValues.put("eventColor",color);
        contentValues.put("startHour",start);
        contentValues.put("endHour",end);
        contentValues.put("startDate",date);
        contentValues.put("eventRec",rec);
        contentValues.put("recDuration",expire);
        contentValues.put("eventDetails",details);

        long result = db.insert("events",null,contentValues);
        if(result==-1)
            return false;
        return true;
    }

    public Cursor getData(String sql)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(sql, null);
        return data;
    }

}
