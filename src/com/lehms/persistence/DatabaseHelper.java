package com.lehms.persistence;

import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_TABLE_ROSTER = "roster";
    public static final String DATABASE_TABLE_EVENT = "event";
    
    private static final String DATABASE_NAME = "LEHMS";
    private static final int DATABASE_VERSION = 10;
	
    private static final String DATABASE_CREATE_ROSTER_TABLE =
        "create table roster (_id integer not null, "
        + "data text not null, created integer not null, user_id text not null);";

    private static final String DATABASE_CREATE_EVENT_TABLE =
        "create table event (_id integer primary key autoincrement, "
        + " event_type text not null, created_date integer not null,"
        + " data text not null, data_type text not null," 
        + " status text not null, attempts integer, error_message);";

	DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(DATABASE_CREATE_ROSTER_TABLE);
    	db.execSQL(DATABASE_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ROSTER); 
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_EVENT);
        onCreate(db);
    }
}