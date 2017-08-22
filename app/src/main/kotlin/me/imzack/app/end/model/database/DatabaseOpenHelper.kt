package me.imzack.app.end.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import me.imzack.app.end.common.Constant

class DatabaseOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    private val CREATE_TABLE_PLAN = "create table " + Constant.PLAN + " (" +
            Constant.CODE + " text primary key, " +
            Constant.CONTENT + " text, " +
            Constant.TYPE_CODE + " text, " +
            Constant.CREATION_TIME + " integer, " +
            Constant.DEADLINE + " integer, " +
            Constant.COMPLETION_TIME + " integer, " +
            Constant.STAR_STATUS + " integer, " +
            Constant.REMINDER_TIME + " integer)"

    private val CREATE_TABLE_TYPE = "create table " + Constant.TYPE + " (" +
            Constant.CODE + " text primary key, " +
            Constant.NAME + " text, " +
            Constant.MARK_COLOR + " text, " +
            Constant.MARK_PATTERN + " text, " +
            Constant.SEQUENCE + " integer)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_PLAN)
        db.execSQL(CREATE_TABLE_TYPE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
