package com.zack.enderplan.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zack.enderplan.util.Constant;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_PLAN = "create table " + Constant.PLAN + " (" +
            Constant.PLAN_CODE + " text primary key, " +
            Constant.CONTENT + " text, " +
            Constant.TYPE_CODE + " text, " +
            Constant.CREATION_TIME + " integer, " +
            Constant.DEADLINE + " integer, " +
            Constant.COMPLETION_TIME + " integer, " +
            Constant.STAR_STATUS + " integer, " +
            Constant.REMINDER_TIME + " integer)";

    private static final String CREATE_TABLE_TYPE = "create table " + Constant.TYPE + " (" +
            Constant.TYPE_CODE + " text primary key, " +
            Constant.TYPE_NAME + " text, " +
            Constant.TYPE_MARK_COLOR + " text, " +
            Constant.TYPE_MARK_PATTERN + " text, " +
            Constant.TYPE_SEQUENCE + " integer)";

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
