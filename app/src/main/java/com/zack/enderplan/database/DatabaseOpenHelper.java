package com.zack.enderplan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_PLAN = "create table plan (" +
            "plan_code text primary key, " +
            "content text, " +
            "type_code text, " +
            "creation_time integer, " +
            "deadline integer, " +
            "completion_time integer, " +
            "star_status integer, " +
            "reminder_time integer)";

    private static final String CREATE_TABLE_TYPE = "create table type (" +
            "type_code text primary key, " +
            "type_name text, " +
            "type_mark text, " +
            "type_sequence integer)";

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAN);
        db.execSQL(CREATE_TABLE_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
