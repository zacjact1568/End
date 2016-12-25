package com.zack.enderplan.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zack.enderplan.App;
import com.zack.enderplan.model.bean.Data;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DatabaseManager {

    private static final String DB_NAME = "com.zack.enderplan.db";

    private static final int DB_VERSION = 1;

    private SQLiteDatabase database;

    private static DatabaseManager ourInstance = new DatabaseManager();

    private DatabaseManager() {
        Context context = App.getContext();
        DatabaseOpenHelper dbHelper = new DatabaseOpenHelper(context, DB_NAME, null, DB_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    //*****************Data********************

    public void loadDataAsync(final DataLoadedCallback callback) {
        Observable<Data> observable = Observable.create(new ObservableOnSubscribe<Data>() {
            @Override
            public void subscribe(ObservableEmitter<Data> e) throws Exception {
                e.onNext(new Data(loadPlan(), loadType()));
            }
        });
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Consumer<Data>() {
            @Override
            public void accept(Data data) throws Exception {
                callback.onDataLoaded(data.getPlanList(), data.getTypeList());
            }
        });
    }

    //*****************Type********************

    public void saveType(Type type) {
        if (type != null) {
            ContentValues values = new ContentValues();
            values.put(Constant.TYPE_CODE, type.getTypeCode());
            values.put(Constant.TYPE_NAME, type.getTypeName());
            values.put(Constant.TYPE_MARK_COLOR, type.getTypeMarkColor());
            values.put(Constant.TYPE_MARK_PATTERN, type.getTypeMarkPattern());
            values.put(Constant.TYPE_SEQUENCE, type.getTypeSequence());
            database.insert(Constant.TYPE, null, values);
        }
    }

    public List<Type> loadType() {
        List<Type> typeList = new ArrayList<>();
        Cursor cursor = database.query(Constant.TYPE, null, null, null, null, null, Constant.TYPE_SEQUENCE);
        if (cursor.moveToFirst()) {
            do {
                typeList.add(new Type(
                        cursor.getString(cursor.getColumnIndex(Constant.TYPE_CODE)),
                        cursor.getString(cursor.getColumnIndex(Constant.TYPE_NAME)),
                        cursor.getString(cursor.getColumnIndex(Constant.TYPE_MARK_COLOR)),
                        cursor.getString(cursor.getColumnIndex(Constant.TYPE_MARK_PATTERN)),
                        cursor.getInt(cursor.getColumnIndex(Constant.TYPE_SEQUENCE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return typeList;
    }

    public TypeMark queryTypeMarkByTypeCode(String typeCode) {
        TypeMark typeMark = null;
        Cursor cursor = database.query(Constant.TYPE, new String[]{Constant.TYPE_MARK_COLOR, Constant.TYPE_MARK_PATTERN},
                Constant.TYPE_CODE + " = ?", new String[]{typeCode}, null, null, null);
        if (cursor.moveToFirst()) {
            typeMark = new TypeMark(
                    cursor.getString(cursor.getColumnIndex(Constant.TYPE_MARK_COLOR)),
                    cursor.getString(cursor.getColumnIndex(Constant.TYPE_MARK_PATTERN))
            );
        }
        cursor.close();
        return typeMark;
    }

    public void updateType(String typeCode, ContentValues values) {
        database.update(Constant.TYPE, values, Constant.TYPE_CODE + " = ?", new String[]{typeCode});
    }

    public void updateTypeBase(String typeCode, String typeName, String typeMarkColor, String typeMarkPattern) {
        ContentValues values = new ContentValues();
        values.put(Constant.TYPE_NAME, typeName);
        values.put(Constant.TYPE_MARK_COLOR, typeMarkColor);
        values.put(Constant.TYPE_MARK_PATTERN, typeMarkPattern);
        updateType(typeCode, values);
    }

    public void updateTypeName(String typeCode, String typeName) {
        ContentValues values = new ContentValues();
        values.put(Constant.TYPE_NAME, typeName);
        updateType(typeCode, values);
    }

    public void updateTypeMark(String typeCode, String typeMarkColor, String typeMarkPattern) {
        ContentValues values = new ContentValues();
        values.put(Constant.TYPE_MARK_COLOR, typeMarkColor);
        values.put(Constant.TYPE_MARK_PATTERN, typeMarkPattern);
        updateType(typeCode, values);
    }

    public void updateTypeMarkColor(String typeCode, String typeMarkColor) {
        ContentValues values = new ContentValues();
        values.put(Constant.TYPE_MARK_COLOR, typeMarkColor);
        updateType(typeCode, values);
    }

    public void updateTypeMarkPattern(String typeCode, String typeMarkPattern) {
        ContentValues values = new ContentValues();
        values.put(Constant.TYPE_MARK_PATTERN, typeMarkPattern);
        updateType(typeCode, values);
    }

    public void updateTypeSequence(String typeCode, int typeSequence) {
        ContentValues values = new ContentValues();
        values.put(Constant.TYPE_SEQUENCE, typeSequence);
        updateType(typeCode, values);
    }

    public void deleteType(String typeCode) {
        database.delete(Constant.TYPE, Constant.TYPE_CODE + " = ?", new String[]{typeCode});
    }

    //*****************Plan********************

    public void savePlan(Plan plan) {
        if (plan != null) {
            ContentValues values = new ContentValues();
            values.put(Constant.PLAN_CODE, plan.getPlanCode());
            values.put(Constant.CONTENT, plan.getContent());
            values.put(Constant.TYPE_CODE, plan.getTypeCode());
            values.put(Constant.CREATION_TIME, plan.getCreationTime());
            values.put(Constant.DEADLINE, plan.getDeadline());
            values.put(Constant.COMPLETION_TIME, plan.getCompletionTime());
            values.put(Constant.STAR_STATUS, plan.getStarStatus());
            values.put(Constant.REMINDER_TIME, plan.getReminderTime());
            database.insert(Constant.PLAN, null, values);
        }
    }

    public List<Plan> loadPlan() {
        String planCode, content, typeCode;
        long creationTime, deadline, completionTime, reminderTime;
        int starStatus;
        List<Plan> planList = new ArrayList<>();
        String orderBy = Constant.CREATION_TIME + " desc, " + Constant.COMPLETION_TIME + " desc";
        Cursor cursor = database.query(Constant.PLAN, null, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                planCode = cursor.getString(cursor.getColumnIndex(Constant.PLAN_CODE));
                content = cursor.getString(cursor.getColumnIndex(Constant.CONTENT));
                typeCode = cursor.getString(cursor.getColumnIndex(Constant.TYPE_CODE));
                creationTime = cursor.getLong(cursor.getColumnIndex(Constant.CREATION_TIME));
                deadline = cursor.getLong(cursor.getColumnIndex(Constant.DEADLINE));
                completionTime = cursor.getLong(cursor.getColumnIndex(Constant.COMPLETION_TIME));
                starStatus = cursor.getInt(cursor.getColumnIndex(Constant.STAR_STATUS));
                reminderTime = cursor.getLong(cursor.getColumnIndex(Constant.REMINDER_TIME));
                planList.add(new Plan(planCode, content, typeCode, creationTime, deadline,
                        completionTime, starStatus, reminderTime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return planList;
    }

    public void updatePlan(String planCode, ContentValues values) {
        database.update(Constant.PLAN, values, Constant.PLAN_CODE + " = ?", new String[]{planCode});
    }

    public void updateContent(String planCode, String content) {
        ContentValues values = new ContentValues();
        values.put(Constant.CONTENT, content);
        updatePlan(planCode, values);
    }

    public void updateTypeOfPlan(String planCode, String typeCode) {
        ContentValues values = new ContentValues();
        values.put(Constant.TYPE_CODE, typeCode);
        updatePlan(planCode, values);
    }

    public void updateDeadline(String planCode, long deadline) {
        ContentValues values = new ContentValues();
        values.put(Constant.DEADLINE, deadline);
        updatePlan(planCode, values);
    }

    public void updateStarStatus(String planCode, int starStatus) {
        ContentValues values = new ContentValues();
        values.put(Constant.STAR_STATUS, starStatus);
        updatePlan(planCode, values);
    }

    public void updateReminderTime(String planCode, long reminderTime) {
        ContentValues values = new ContentValues();
        values.put(Constant.REMINDER_TIME, reminderTime);
        updatePlan(planCode, values);
    }

    public void updatePlanStatus(String planCode, long creationTime, long completionTime) {
        ContentValues values = new ContentValues();
        values.put(Constant.CREATION_TIME, creationTime);
        values.put(Constant.COMPLETION_TIME, completionTime);
        updatePlan(planCode, values);
    }

    public void updateCreationTime(String planCode, long creationTime) {
        ContentValues values = new ContentValues();
        values.put(Constant.CREATION_TIME, creationTime);
        updatePlan(planCode, values);
    }

    public void updateCompletionTime(String planCode, long completionTime) {
        ContentValues values = new ContentValues();
        values.put(Constant.COMPLETION_TIME, completionTime);
        updatePlan(planCode, values);
    }

    public void deletePlan(String planCode) {
        database.delete(Constant.PLAN, Constant.PLAN_CODE + " = ?", new String[]{planCode});
    }

    public Plan queryPlan(String planCode) {
        Plan plan = null;
        Cursor cursor = database.query(Constant.PLAN, null, Constant.PLAN_CODE + " = ?", new String[]{planCode},
                null, null, null);
        if (cursor.moveToFirst()) {
            plan = new Plan(
                    planCode,
                    cursor.getString(cursor.getColumnIndex(Constant.CONTENT)),
                    cursor.getString(cursor.getColumnIndex(Constant.TYPE_CODE)),
                    cursor.getLong(cursor.getColumnIndex(Constant.CREATION_TIME)),
                    cursor.getLong(cursor.getColumnIndex(Constant.DEADLINE)),
                    cursor.getLong(cursor.getColumnIndex(Constant.COMPLETION_TIME)),
                    cursor.getInt(cursor.getColumnIndex(Constant.STAR_STATUS)),
                    cursor.getLong(cursor.getColumnIndex(Constant.REMINDER_TIME))
            );
        }
        cursor.close();
        return plan;
    }

    public String queryContentByPlanCode(String planCode) {
        String content = "";
        Cursor cursor = database.query(Constant.PLAN, new String[]{Constant.CONTENT}, Constant.PLAN_CODE + " = ?",
                new String[]{planCode}, null, null, null);
        if (cursor.moveToFirst()) {
            content = cursor.getString(cursor.getColumnIndex(Constant.CONTENT));
        }
        cursor.close();
        return content;
    }

    public Map<String, Long> queryReminderTimeWithEnabledReminder() {
        String planCode;
        Long reminderTime;
        Map<String, Long> reminderTimeMap = new HashMap<>();
        Cursor cursor = database.query(Constant.PLAN, new String[]{Constant.PLAN_CODE, Constant.REMINDER_TIME},
                Constant.REMINDER_TIME + " > ?", new String[]{"0"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                planCode = cursor.getString(cursor.getColumnIndex(Constant.PLAN_CODE));
                reminderTime = cursor.getLong(cursor.getColumnIndex(Constant.REMINDER_TIME));
                reminderTimeMap.put(planCode, reminderTime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminderTimeMap;
    }

    //*****************TypeMarkColor*****************

    public List<TypeMarkColor> loadTypeMarkColor() {
        SQLiteDatabase typeMarkDB = SQLiteDatabase.openDatabase(App.getContext().getDatabasePath(Constant.DB_TYPE_MARK).getPath(), null, SQLiteDatabase.OPEN_READONLY);
        List<TypeMarkColor> typeMarkColorList = new ArrayList<>();
        String colorNameColumnName = String.format(Constant.COLOR_NAME, getColumnNameSuffixByLocale());
        Cursor cursor = typeMarkDB.query(Constant.COLOR, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                typeMarkColorList.add(new TypeMarkColor(
                        cursor.getString(cursor.getColumnIndex(Constant.COLOR_HEX)),
                        cursor.getString(cursor.getColumnIndex(colorNameColumnName))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        typeMarkDB.close();
        return typeMarkColorList;
    }

    public String queryTypeMarkColorNameByTypeMarkColorHex(String typeMarkColorHex) {
        SQLiteDatabase typeMarkDB = SQLiteDatabase.openDatabase(App.getContext().getDatabasePath(Constant.DB_TYPE_MARK).getPath(), null, SQLiteDatabase.OPEN_READONLY);
        String colorNameColumnName = String.format(Constant.COLOR_NAME, getColumnNameSuffixByLocale());
        Cursor cursor = typeMarkDB.query(Constant.COLOR, new String[]{colorNameColumnName}, Constant.COLOR_HEX + " = ?", new String[]{typeMarkColorHex}, null, null, null);
        String typeMarkColorName = null;
        if (cursor.moveToFirst()) {
            typeMarkColorName = cursor.getString(cursor.getColumnIndex(colorNameColumnName));
        }
        cursor.close();
        typeMarkDB.close();
        return typeMarkColorName;
    }

    //*****************TypeMarkPattern*****************

    public List<TypeMarkPattern> loadTypeMarkPattern() {
        SQLiteDatabase typeMarkDB = SQLiteDatabase.openDatabase(App.getContext().getDatabasePath(Constant.DB_TYPE_MARK).getPath(), null, SQLiteDatabase.OPEN_READONLY);
        List<TypeMarkPattern> typeMarkPatternList = new ArrayList<>();
        String patternNameColumnName = String.format(Constant.PATTERN_NAME, getColumnNameSuffixByLocale());
        Cursor cursor = typeMarkDB.query(Constant.PATTERN, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                typeMarkPatternList.add(new TypeMarkPattern(
                        cursor.getString(cursor.getColumnIndex(Constant.PATTERN_FN)),
                        cursor.getString(cursor.getColumnIndex(patternNameColumnName))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        typeMarkDB.close();
        return typeMarkPatternList;
    }

    public String queryTypeMarkPatternNameByTypeMarkPatternFn(String typeMarkPatternFn) {
        SQLiteDatabase typeMarkDB = SQLiteDatabase.openDatabase(App.getContext().getDatabasePath(Constant.DB_TYPE_MARK).getPath(), null, SQLiteDatabase.OPEN_READONLY);
        String patternNameColumnName = String.format(Constant.PATTERN_NAME, getColumnNameSuffixByLocale());
        Cursor cursor = typeMarkDB.query(Constant.PATTERN, new String[]{patternNameColumnName}, Constant.PATTERN_FN + " = ?", new String[]{typeMarkPatternFn}, null, null, null);
        String typeMarkPatternName = null;
        if (cursor.moveToFirst()) {
            typeMarkPatternName = cursor.getString(cursor.getColumnIndex(patternNameColumnName));
        }
        cursor.close();
        typeMarkDB.close();
        return typeMarkPatternName;
    }

    //*****************Others*****************

    private String getColumnNameSuffixByLocale() {
        Locale preferredLocale = Util.getPreferredLocale();
        if (preferredLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
            return Constant.ZH_CN;
        } else if (preferredLocale.equals(Locale.TRADITIONAL_CHINESE)) {
            return Constant.ZH_TW;
        } else {
            return Constant.EN;
        }
    }

    public interface DataLoadedCallback {
        void onDataLoaded(List<Plan> planList, List<Type> typeList);
    }
}
