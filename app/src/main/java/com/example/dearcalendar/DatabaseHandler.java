package com.example.dearcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import java.util.ArrayList;

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

        sql =  "Create Table users" +
                "(userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "userName TEXT NOT NULL," +
                "userPassword TEXT NOT NULL," +
                "passwordHint TEXT NOT NULL);";

        db.execSQL(sql);
        setUser(db);

        sql =  "Create Table eventImages" +
                "(imageId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "recTarget INTEGER NOT NULL," +
                "content BLOB NOT NULL);";

        db.execSQL(sql);
    }

    private void setUser(SQLiteDatabase db)
    {
        ContentValues contentValues =  new ContentValues();
        contentValues.put("userName","user");
        contentValues.put("userPassword","");
        contentValues.put("passwordHint","");
        db.insert("users",null,contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS events";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS users";
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
        return result != -1;
    }

    public boolean editEntry(String title, String state, String color, String start,
                            String end, String details, int target)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
        contentValues.put("eventTitle",title);
        contentValues.put("eventState",state);
        contentValues.put("eventColor",color);
        contentValues.put("startHour",start);
        contentValues.put("endHour",end);
        contentValues.put("eventDetails",details);

        long result = db.update("events",contentValues,"eventId like "+ target,null);
        return result != -1;
    }

    public Cursor getData(String sql)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public void deleteAllEntries()
    {
        String sql = "DELETE FROM events WHERE 1";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    public void run(String sql)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(sql);
    }

    private boolean updateUser(SQLiteDatabase db,ContentValues contentValues)
    {
        long result = db.update("users",contentValues,"userName LIKE 'user'",null);
        return result != -1;
    }

    public boolean editPassword(String newPassword)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
        contentValues.put("userPassword",newPassword);

        return updateUser(db,contentValues);
    }

    public boolean createPassword(String password, String hint)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
        contentValues.put("userPassword",password);
        contentValues.put("passwordHint",hint);

        return updateUser(db,contentValues);
    }

    public String getSetPassword()
    {
        String sql = "SELECT userPassword FROM users WHERE userName LIKE 'user';";
        Cursor cursor = this.getData(sql);
        cursor.moveToNext();
        return cursor.getString(0);
    }

    public String getPasswordHint()
    {
        String sql = "SELECT passwordHint FROM users WHERE userName LIKE 'user';";
        Cursor cursor = this.getData(sql);
        cursor.moveToNext();
        return cursor.getString(0);
    }

    private boolean encryptEntry(SQLiteDatabase db, EncryptionHandler e, int eventId, String rawTitle, String rawDetails, String password)
    {
        ContentValues contentValues =  new ContentValues();

        String title = e.encrypt(rawTitle,password);
        String details = e.encrypt(rawDetails,password);

        contentValues.put("eventTitle",title);
        contentValues.put("eventDetails",details);

        long result = db.update("events",contentValues,"eventId LIKE "+ eventId,null);
        return result != -1;
    }

    public void encryptEntries(String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        EncryptionHandler e = new EncryptionHandler();
        String sql = "SELECT eventId,eventTitle,eventDetails FROM events WHERE 1;";
        Cursor cursor = getData(sql);
        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String details = cursor.getString(2);
            encryptEntry(db,e,id,title,details,password);
        }
    }

    public void reEncryptEntries(String oldPassword, String newPassword)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        EncryptionHandler e = new EncryptionHandler();
        String sql = "SELECT eventId,eventTitle,eventDetails FROM events WHERE 1;";
        Cursor cursor = getData(sql);
        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String details = cursor.getString(2);
            title = e.decrypt(title,oldPassword);
            details = e.decrypt(details,oldPassword);

            encryptEntry(db,e,id,title,details,newPassword);
        }
    }

    public int getLastIndex()
    {
        String sql = "SELECT eventId FROM events WHERE 1 ORDER BY eventId DESC;";
        Cursor cursor = getData(sql);
        try {
            cursor.moveToNext();
            return cursor.getInt(0);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public boolean addImage(int recEventTarget, byte[] imageByte)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("recTarget",recEventTarget);
        contentValues.put("content",imageByte);

        long result = db.insert("eventImages",null,contentValues);
        return result != -1;
    }

    private boolean existsImage(int recId)
    {
        String sql = "SELECT recTarget FROM eventImages WHERE recTarget == "+ recId + ";";
        Cursor cursor = getData(sql);
        return cursor.getCount()>0;
    }

    public boolean editImage(int recEventTarget, byte[] imageByte)
    {
        if(existsImage(recEventTarget)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("content", imageByte);

            long result = db.update("eventImages", contentValues, "recTarget LIKE " + recEventTarget, null);
            return result != -1;
        }
        else
            addImage(recEventTarget, imageByte);
        return true;

    }

    public ArrayList<EventHolder> getEvents(String date)
    {
        String sql = "SELECT * FROM events WHERE startDate LIKE '" + date +
                "' ORDER BY startHour;";
        Cursor cursor = this.getData(sql);

        ArrayList<EventHolder> result = new ArrayList<>();
        while(cursor.moveToNext())
        {
            EventHolder element = new EventHolder();
            element.setId(cursor.getInt(0));
            element.setRecId(cursor.getInt(1));
            element.setTitle(cursor.getString(2));
            element.setColor(cursor.getString(4));
            element.setStart(cursor.getString(5));
            element.setEnd(cursor.getString(6));

            result.add(element);
        }
        return result;
    }
    public void attachImages(ArrayList<EventHolder> events)
    {
        String sql = "SELECT content FROM eventImages WHERE recTarget == ";
        for(EventHolder e : events)
        {
            String run = sql + e.getRecId() + ";";
            Cursor cursor = this.getData(run);

            cursor.moveToNext();
            if(cursor.getCount()==1)
                e.setImage(cursor.getBlob(0));
        }
    }

    public void removeUnusedImage(int recTarget)
    {
        String sql = "SELECT recID FROm events WHERE recId ==" + recTarget + ";";
        if(getData(sql).getCount()>0)
            return;
        sql = "DELETE FROM eventImages WHERE recTarget = " + recTarget + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    public void removeImages()
    {
        String sql = "DELETE FROM eventImages WHERE 1;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    public Bitmap getImage(int recId)
    {
        String sql = "SELECT content FROM eventImages WHERE recTarget == " + recId + ";";
        Cursor cursor = this.getData(sql);
        if(cursor.getCount()==0)
            return null;
        cursor.moveToNext();
        byte[] imageBytes = cursor.getBlob(0);
        return getBitmap(imageBytes);

    }
}
