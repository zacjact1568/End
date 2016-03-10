package com.zack.enderplan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zack.enderplan.application.EnderPlanApp;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnderPlanDB {

    public static final String DB_NAME = "com.zack.enderplan_database";

    public static final int DB_VERSION = 1;

    public static final String DB_STR_PLAN = "plan";
    public static final String DB_STR_TYPE = "type";
    public static final String DB_STR_TYPE_CODE = "type_code";
    public static final String DB_STR_TYPE_NAME = "type_name";
    public static final String DB_STR_TYPE_MARK = "type_mark";
    public static final String DB_STR_PLAN_CODE = "plan_code";
    public static final String DB_STR_CONTENT = "content";
    public static final String DB_STR_CREATION_TIME = "creation_time";
    public static final String DB_STR_DEADLINE = "deadline";
    public static final String DB_STR_COMPLETION_TIME = "completion_time";
    public static final String DB_STR_STAR_STATUS = "star_status";
    public static final String DB_STR_REMINDER_TIME = "reminder_time";

    private static EnderPlanDB enderplanDB;

    private SQLiteDatabase database;

    private EnderPlanDB() {
        Context context = EnderPlanApp.getGlobalContext();
        EnderPlanOpenHelper dbHelper = new EnderPlanOpenHelper(context, DB_NAME, null, DB_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    public synchronized static EnderPlanDB getInstance() {
        if (enderplanDB == null) {
            enderplanDB = new EnderPlanDB();
        }
        return enderplanDB;
    }

    //*****************Type********************

    public void saveType(Type type) {
        if (type != null) {
            ContentValues values = new ContentValues();
            values.put(DB_STR_TYPE_CODE, type.getTypeCode());
            values.put(DB_STR_TYPE_NAME, type.getTypeName());
            values.put(DB_STR_TYPE_MARK, type.getTypeMark());
            database.insert(DB_STR_TYPE, null, values);
        }
    }

    public List<Type> loadType() {
        String typeCode, typeName, typeMark;
        List<Type> typeList = new ArrayList<>();
        Cursor cursor = database.query(DB_STR_TYPE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                typeCode = cursor.getString(cursor.getColumnIndex(DB_STR_TYPE_CODE));
                typeName = cursor.getString(cursor.getColumnIndex(DB_STR_TYPE_NAME));
                typeMark = cursor.getString(cursor.getColumnIndex(DB_STR_TYPE_MARK));
                typeList.add(new Type(typeCode, typeName, typeMark));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return typeList;
    }

    //*****************Plan********************

    public void savePlan(Plan plan) {
        if (plan != null) {
            ContentValues values = new ContentValues();
            values.put(DB_STR_PLAN_CODE, plan.getPlanCode());
            values.put(DB_STR_CONTENT, plan.getContent());
            values.put(DB_STR_TYPE_CODE, plan.getTypeCode());
            values.put(DB_STR_CREATION_TIME, plan.getCreationTime());
            values.put(DB_STR_DEADLINE, plan.getDeadline());
            values.put(DB_STR_COMPLETION_TIME, plan.getCompletionTime());
            values.put(DB_STR_STAR_STATUS, plan.getStarStatus());
            values.put(DB_STR_REMINDER_TIME, plan.getReminderTime());
            database.insert(DB_STR_PLAN, null, values);
        }
    }

    public List<Plan> loadPlan() {
        String planCode, content, typeCode;
        long creationTime, deadline, completionTime, reminderTime;
        int starStatus;
        List<Plan> planList = new ArrayList<>();
        String orderBy = DB_STR_CREATION_TIME + " desc, " + DB_STR_COMPLETION_TIME + " desc";
        Cursor cursor = database.query(DB_STR_PLAN, null, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                planCode = cursor.getString(cursor.getColumnIndex(DB_STR_PLAN_CODE));
                content = cursor.getString(cursor.getColumnIndex(DB_STR_CONTENT));
                typeCode = cursor.getString(cursor.getColumnIndex(DB_STR_TYPE_CODE));
                creationTime = cursor.getLong(cursor.getColumnIndex(DB_STR_CREATION_TIME));
                deadline = cursor.getLong(cursor.getColumnIndex(DB_STR_DEADLINE));
                completionTime = cursor.getLong(cursor.getColumnIndex(DB_STR_COMPLETION_TIME));
                starStatus = cursor.getInt(cursor.getColumnIndex(DB_STR_STAR_STATUS));
                reminderTime = cursor.getLong(cursor.getColumnIndex(DB_STR_REMINDER_TIME));
                planList.add(new Plan(planCode, content, typeCode, creationTime, deadline,
                        completionTime, starStatus, reminderTime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return planList;
    }

    public void editPlan(String planCode, ContentValues values) {
        database.update(DB_STR_PLAN, values, DB_STR_PLAN_CODE + " = ?", new String[]{planCode});
    }

    public void deletePlan(String planCode) {
        database.delete(DB_STR_PLAN, DB_STR_PLAN_CODE + " = ?", new String[]{planCode});
    }

    public String queryContentByPlanCode(String planCode) {
        String content = "";
        Cursor cursor = database.query(DB_STR_PLAN, new String[]{DB_STR_CONTENT}, DB_STR_PLAN_CODE + " = ?",
                new String[]{planCode}, null, null, null);
        if (cursor.moveToFirst()) {
            content = cursor.getString(cursor.getColumnIndex(DB_STR_CONTENT));
        }
        cursor.close();
        return content;
    }

    public Map<String, Long> queryReminderTimeWithEnabledReminder() {
        String planCode;
        Long reminderTime;
        Map<String, Long> reminderTimeMap = new HashMap<>();
        Cursor cursor = database.query(DB_STR_PLAN, new String[]{DB_STR_PLAN_CODE, DB_STR_REMINDER_TIME},
                DB_STR_REMINDER_TIME + " > ?", new String[]{"0"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                planCode = cursor.getString(cursor.getColumnIndex(DB_STR_PLAN_CODE));
                reminderTime = cursor.getLong(cursor.getColumnIndex(DB_STR_REMINDER_TIME));
                reminderTimeMap.put(planCode, reminderTime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminderTimeMap;
    }
}
