package com.citizen.fmc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.citizen.fmc.model.ImageTextModel;
import com.citizen.fmc.model.UserNotificationModel;

import java.util.ArrayList;

/**
 * ======> Created by dheeraj-gangwar on 2018-03-16 <======
 */

public class DBHelper extends SQLiteOpenHelper {
    private final Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "faridabad_municipal_council_citizen_app_database_v" + DATABASE_VERSION + ".db";


    /* ============================== CREATE TABLE STATEMENTS ============================== */

    private static final String SQL_CREATE_NOTIFICATION_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DatabaseTable.NotificationTable.TABLE_NAME + " (" +
                    DatabaseTable.NotificationTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_ID + " TEXT," +
                    DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_FROM + " TEXT," +
                    DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_DATE_TIME_STAMP + " TEXT," +
                    DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_TITLE + " TEXT," +
                    DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_BODY + " TEXT," +
                    DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_IMAGE + " TEXT" +
                    " )";


    private static final String SQL_CREATE_GENDER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DatabaseTable.GenderTable.TABLE_NAME + " (" +
                    DatabaseTable.GenderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseTable.GenderTable.COLUMN_NAME_GENDER_ID + " TEXT UNIQUE," +
                    DatabaseTable.GenderTable.COLUMN_NAME_GENDER_TITLE + " TEXT" +
                    " )";


    //AQI Table Structure:-
    private static final String SQL_CREATE_AQI_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DatabaseTable.aqiTable.TABLE_NAME + " (" +
                    DatabaseTable.aqiTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DatabaseTable.aqiTable.COLUMN_AQI_CITY + " TEXT, " +
                    DatabaseTable.aqiTable.COLUMN_AQI_COUNTRY + " TEXT, " +
                    DatabaseTable.aqiTable.COLUMN_AQI_LOCATIONS + " TEXT, " +
                    DatabaseTable.aqiTable.COLUMN_AQI_COUNT + " TEXT " +
                    " )";

    /* ========================================================================================== */



    /* ============================== DELETE TABLE STATEMENTS ============================== */

    private static final String SQL_DELETE_NOTIFICATION_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseTable.NotificationTable.TABLE_NAME;


    private static final String SQL_DELETE_GENDER_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseTable.GenderTable.TABLE_NAME;

    /* ========================================================================================== */


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NOTIFICATION_TABLE);
        db.execSQL(SQL_CREATE_GENDER_TABLE);
        db.execSQL(SQL_CREATE_AQI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_NOTIFICATION_TABLE);
        db.execSQL(SQL_DELETE_GENDER_TABLE);
        db.execSQL(SQL_CREATE_AQI_TABLE);
    }


    /* ============================== INSERT DATA METHODS ============================== */

    public int insertNotificationData(String messageId, String messageFrom,
                                      String dateTimeStamp, String messageTitle,
                                      String messageBody, String messageImagePath) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_ID, messageId);
        values.put(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_FROM, messageFrom);
        values.put(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_DATE_TIME_STAMP, dateTimeStamp);
        values.put(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_TITLE, messageTitle);
        values.put(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_BODY, messageBody);
        values.put(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_IMAGE, messageImagePath);

        // Inserting Row
        long insertCheck = writableDatabase.insert(DatabaseTable.NotificationTable.TABLE_NAME, null, values);
        writableDatabase.close(); // Closing database connection
        return (int) insertCheck;
    }

    public int insertGenderData(int genderId, String genderTitle) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.GenderTable.COLUMN_NAME_GENDER_ID, genderId);
        values.put(DatabaseTable.GenderTable.COLUMN_NAME_GENDER_TITLE, genderTitle);

        // Inserting Row
        long insertCheck = writableDatabase.insert(DatabaseTable.GenderTable.TABLE_NAME, null, values);
        writableDatabase.close(); // Closing database connection
        return (int) insertCheck;
    }

    /* ========================================================================================== */


    /* ============================== GET DATA METHODS ============================== */

    public int getUserNotificationCount() {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        long _count = DatabaseUtils.queryNumEntries(readableDatabase, DatabaseTable.NotificationTable.TABLE_NAME);
        readableDatabase.close();
        return (int) _count;
    }

    public ArrayList<UserNotificationModel> getAllUserNotification() {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor mCursor = readableDatabase.query(DatabaseTable.NotificationTable.TABLE_NAME,
                new String[]{DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_ID,
                        DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_FROM,
                        DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_DATE_TIME_STAMP,
                        DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_TITLE,
                        DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_BODY,
                        DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_IMAGE},
                null, null, null, null, null, null);
        ArrayList<UserNotificationModel> list = new ArrayList<>();
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            do {
                list.add(new UserNotificationModel(
                        mCursor.getString(mCursor.getColumnIndex(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_ID)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_MESSAGE_FROM)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_DATE_TIME_STAMP)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_TITLE)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_BODY)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseTable.NotificationTable.COLUMN_NAME_NOTIFICATION_IMAGE))
                ));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        readableDatabase.close();
        return list;
    }

    public ArrayList<ImageTextModel> getAllGender() {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor mCursor = readableDatabase.query(DatabaseTable.GenderTable.TABLE_NAME,
                new String[]{DatabaseTable.GenderTable.COLUMN_NAME_GENDER_ID,
                        DatabaseTable.GenderTable.COLUMN_NAME_GENDER_TITLE},
                null, null, null, null, null, null);
        ArrayList<ImageTextModel> list = new ArrayList<>();
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            do {
                list.add(new ImageTextModel(
                        mCursor.getInt(mCursor.getColumnIndex(DatabaseTable.GenderTable.COLUMN_NAME_GENDER_ID)),
                        mCursor.getString(mCursor.getColumnIndex(DatabaseTable.GenderTable.COLUMN_NAME_GENDER_TITLE))));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        readableDatabase.close();
        return list;
    }

    //Air Quality Index Saving Data:-
    public int saveAQIData(String cityAQI , String countryAQI , String locationsAQI , String countAQI){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("city" , cityAQI);
        values.put("country" , countryAQI);
        values.put("locations" , locationsAQI);
        values.put("number" , countAQI);

        long insertCheck = writableDatabase.insert(DatabaseTable.aqiTable.TABLE_NAME , null , values);
        writableDatabase.close();
        return (int) insertCheck;
    }


    //Fetching Delhi AQI Data From Table:-
    public Cursor getDelhiAQIData(String cityName){
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        return readableDatabase.rawQuery("select number FROM aqi_table where city='" + cityName + "'"
                , null);
    }

    /* ========================================================================================== */
}
