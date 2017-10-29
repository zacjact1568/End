package me.imzack.app.end.model.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.imzack.app.end.App
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.bean.*
import me.imzack.app.end.util.SystemUtil
import java.util.*

class DatabaseHelper {

    private val DB_NAME = "${App.context.packageName}.db"

    private val DB_VERSION = 1

    private val mDatabase = DatabaseOpenHelper(App.context, DB_NAME, null, DB_VERSION).writableDatabase

    //*****************Data********************

    fun loadDataAsync(callback: (planList: List<Plan>, typeList: List<Type>) -> Unit) {
        Observable
                .create(ObservableOnSubscribe<Data> { it.onNext(Data(loadPlan(), loadType())) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { (planList, typeList) -> callback(planList, typeList) }
    }

    //*****************Plan********************

    private fun loadPlan(): List<Plan> {
        val planList = mutableListOf<Plan>()
        val orderBy = "${Constant.CREATION_TIME} desc, ${Constant.COMPLETION_TIME} desc"
        val cursor = mDatabase.query(Constant.PLAN, null, null, null, null, null, orderBy)
        if (cursor.moveToFirst()) {
            do {
                planList.add(Plan(
                        cursor.getString(cursor.getColumnIndex(Constant.CODE)),
                        cursor.getString(cursor.getColumnIndex(Constant.CONTENT)),
                        cursor.getString(cursor.getColumnIndex(Constant.TYPE_CODE)),
                        cursor.getLong(cursor.getColumnIndex(Constant.CREATION_TIME)),
                        cursor.getLong(cursor.getColumnIndex(Constant.DEADLINE)),
                        cursor.getLong(cursor.getColumnIndex(Constant.COMPLETION_TIME)),
                        cursor.getInt(cursor.getColumnIndex(Constant.STAR_STATUS)),
                        cursor.getLong(cursor.getColumnIndex(Constant.REMINDER_TIME))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return planList
    }

    fun savePlan(plan: Plan) {
        val values = ContentValues()
        values.put(Constant.CODE, plan.code)
        values.put(Constant.CONTENT, plan.content)
        values.put(Constant.TYPE_CODE, plan.typeCode)
        values.put(Constant.CREATION_TIME, plan.creationTime)
        values.put(Constant.DEADLINE, plan.deadline)
        values.put(Constant.COMPLETION_TIME, plan.completionTime)
        values.put(Constant.STAR_STATUS, plan.starStatus)
        values.put(Constant.REMINDER_TIME, plan.reminderTime)
        mDatabase.insert(Constant.PLAN, null, values)
    }

    fun updateContent(planCode: String, content: String) {
        val values = ContentValues()
        values.put(Constant.CONTENT, content)
        updatePlan(planCode, values)
    }

    fun updateTypeOfPlan(planCode: String, typeCode: String) {
        val values = ContentValues()
        values.put(Constant.TYPE_CODE, typeCode)
        updatePlan(planCode, values)
    }

    fun updateDeadline(planCode: String, deadline: Long) {
        val values = ContentValues()
        values.put(Constant.DEADLINE, deadline)
        updatePlan(planCode, values)
    }

    fun updateStarStatus(planCode: String, starStatus: Int) {
        val values = ContentValues()
        values.put(Constant.STAR_STATUS, starStatus)
        updatePlan(planCode, values)
    }

    fun updateReminderTime(planCode: String, reminderTime: Long) {
        val values = ContentValues()
        values.put(Constant.REMINDER_TIME, reminderTime)
        updatePlan(planCode, values)
    }

    fun updatePlanStatus(planCode: String, creationTime: Long, completionTime: Long) {
        val values = ContentValues()
        values.put(Constant.CREATION_TIME, creationTime)
        values.put(Constant.COMPLETION_TIME, completionTime)
        updatePlan(planCode, values)
    }

    fun updateCreationTime(planCode: String, creationTime: Long) {
        val values = ContentValues()
        values.put(Constant.CREATION_TIME, creationTime)
        updatePlan(planCode, values)
    }

    fun updateCompletionTime(planCode: String, completionTime: Long) {
        val values = ContentValues()
        values.put(Constant.COMPLETION_TIME, completionTime)
        updatePlan(planCode, values)
    }

    private fun updatePlan(planCode: String, values: ContentValues) {
        mDatabase.update(Constant.PLAN, values, "${Constant.CODE} = ?", arrayOf(planCode))
    }

    fun deletePlan(planCode: String) {
        mDatabase.delete(Constant.PLAN, "${Constant.CODE} = ?", arrayOf(planCode))
    }

    fun queryPlan(planCode: String): Plan? {
        var plan: Plan? = null
        val cursor = mDatabase.query(Constant.PLAN, null, "${Constant.CODE} = ?", arrayOf(planCode), null, null, null)
        if (cursor.moveToFirst()) {
            plan = Plan(
                    planCode,
                    cursor.getString(cursor.getColumnIndex(Constant.CONTENT)),
                    cursor.getString(cursor.getColumnIndex(Constant.TYPE_CODE)),
                    cursor.getLong(cursor.getColumnIndex(Constant.CREATION_TIME)),
                    cursor.getLong(cursor.getColumnIndex(Constant.DEADLINE)),
                    cursor.getLong(cursor.getColumnIndex(Constant.COMPLETION_TIME)),
                    cursor.getInt(cursor.getColumnIndex(Constant.STAR_STATUS)),
                    cursor.getLong(cursor.getColumnIndex(Constant.REMINDER_TIME))
            )
        }
        cursor.close()
        return plan
    }

    fun queryContentByPlanCode(planCode: String): String? {
        var content: String? = null
        val cursor = mDatabase.query(Constant.PLAN, arrayOf(Constant.CONTENT), "${Constant.CODE} = ?", arrayOf(planCode), null, null, null)
        if (cursor.moveToFirst()) {
            content = cursor.getString(cursor.getColumnIndex(Constant.CONTENT))
        }
        cursor.close()
        return content
    }

    fun queryReminderTimeWithEnabledReminder(): Map<String, Long> {
        val reminderTimeMap = hashMapOf<String, Long>()
        val cursor = mDatabase.query(Constant.PLAN, arrayOf(Constant.CODE, Constant.REMINDER_TIME), "${Constant.REMINDER_TIME} > ?", arrayOf("0"), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                reminderTimeMap.put(
                        cursor.getString(cursor.getColumnIndex(Constant.CODE)),
                        cursor.getLong(cursor.getColumnIndex(Constant.REMINDER_TIME))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return reminderTimeMap
    }

    //*****************Type********************

    private fun loadType(): List<Type> {
        val typeList = mutableListOf<Type>()
        val cursor = mDatabase.query(Constant.TYPE, null, null, null, null, null, Constant.SEQUENCE)
        if (cursor.moveToFirst()) {
            do {
                typeList.add(Type(
                        cursor.getString(cursor.getColumnIndex(Constant.CODE)),
                        cursor.getString(cursor.getColumnIndex(Constant.NAME)),
                        cursor.getString(cursor.getColumnIndex(Constant.MARK_COLOR)),
                        cursor.getString(cursor.getColumnIndex(Constant.MARK_PATTERN)),
                        cursor.getInt(cursor.getColumnIndex(Constant.SEQUENCE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return typeList
    }

    fun saveType(type: Type) {
        val values = ContentValues()
        values.put(Constant.CODE, type.code)
        values.put(Constant.NAME, type.name)
        values.put(Constant.MARK_COLOR, type.markColor)
        values.put(Constant.MARK_PATTERN, type.markPattern)
        values.put(Constant.SEQUENCE, type.sequence)
        mDatabase.insert(Constant.TYPE, null, values)
    }

    fun queryTypeMarkByTypeCode(typeCode: String): TypeMark? {
        var typeMark: TypeMark? = null
        val cursor = mDatabase.query(Constant.TYPE, arrayOf(Constant.MARK_COLOR, Constant.MARK_PATTERN), "${Constant.CODE} = ?", arrayOf(typeCode), null, null, null)
        if (cursor.moveToFirst()) {
            typeMark = TypeMark(
                    cursor.getString(cursor.getColumnIndex(Constant.MARK_COLOR)),
                    cursor.getString(cursor.getColumnIndex(Constant.MARK_PATTERN))
            )
        }
        cursor.close()
        return typeMark
    }

    fun updateTypeBase(typeCode: String, typeName: String, typeMarkColor: String, typeMarkPattern: String) {
        val values = ContentValues()
        values.put(Constant.NAME, typeName)
        values.put(Constant.MARK_COLOR, typeMarkColor)
        values.put(Constant.MARK_PATTERN, typeMarkPattern)
        updateType(typeCode, values)
    }

    fun updateTypeName(typeCode: String, typeName: String) {
        val values = ContentValues()
        values.put(Constant.NAME, typeName)
        updateType(typeCode, values)
    }

    fun updateTypeMark(typeCode: String, typeMarkColor: String, typeMarkPattern: String) {
        val values = ContentValues()
        values.put(Constant.MARK_COLOR, typeMarkColor)
        values.put(Constant.MARK_PATTERN, typeMarkPattern)
        updateType(typeCode, values)
    }

    fun updateTypeMarkColor(typeCode: String, typeMarkColor: String) {
        val values = ContentValues()
        values.put(Constant.MARK_COLOR, typeMarkColor)
        updateType(typeCode, values)
    }

    fun updateTypeMarkPattern(typeCode: String, typeMarkPattern: String?) {
        val values = ContentValues()
        values.put(Constant.MARK_PATTERN, typeMarkPattern)
        updateType(typeCode, values)
    }

    fun updateTypeSequence(typeCode: String, typeSequence: Int) {
        val values = ContentValues()
        values.put(Constant.SEQUENCE, typeSequence)
        updateType(typeCode, values)
    }

    private fun updateType(typeCode: String, values: ContentValues) {
        mDatabase.update(Constant.TYPE, values, "${Constant.CODE} = ?", arrayOf(typeCode))
    }

    fun deleteType(typeCode: String) {
        mDatabase.delete(Constant.TYPE, "${Constant.CODE} = ?", arrayOf(typeCode))
    }

    fun queryType(typeCode: String): Type? {
        var type: Type? = null
        val cursor = mDatabase.query(Constant.TYPE, null, "${Constant.CODE} = ?", arrayOf(typeCode), null, null, null)
        if (cursor.moveToFirst()) {
            type = Type(
                    typeCode,
                    cursor.getString(cursor.getColumnIndex(Constant.NAME)),
                    cursor.getString(cursor.getColumnIndex(Constant.MARK_COLOR)),
                    cursor.getString(cursor.getColumnIndex(Constant.MARK_PATTERN)),
                    cursor.getInt(cursor.getColumnIndex(Constant.SEQUENCE))
            )
        }
        cursor.close()
        return type
    }

    //*****************TypeMarkColor*****************

    fun loadTypeMarkColor(): List<TypeMarkColor> {
        val typeMarkDB = SQLiteDatabase.openDatabase(App.context.getDatabasePath(Constant.DB_TYPE_MARK).path, null, SQLiteDatabase.OPEN_READONLY)
        val typeMarkColorList = mutableListOf<TypeMarkColor>()
        val colorNameColumnName = String.format(Constant.COLOR_NAME, columnNameSuffixByLocale)
        val cursor = typeMarkDB.query(Constant.COLOR, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                typeMarkColorList.add(TypeMarkColor(
                        cursor.getString(cursor.getColumnIndex(Constant.COLOR_HEX)),
                        cursor.getString(cursor.getColumnIndex(colorNameColumnName))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        typeMarkDB.close()
        return typeMarkColorList
    }

    fun queryTypeMarkColorNameByTypeMarkColorHex(typeMarkColorHex: String): String? {
        val typeMarkDB = SQLiteDatabase.openDatabase(App.context.getDatabasePath(Constant.DB_TYPE_MARK).path, null, SQLiteDatabase.OPEN_READONLY)
        val colorNameColumnName = String.format(Constant.COLOR_NAME, columnNameSuffixByLocale)
        val cursor = typeMarkDB.query(Constant.COLOR, arrayOf(colorNameColumnName), "${Constant.COLOR_HEX} = ?", arrayOf(typeMarkColorHex), null, null, null)
        var typeMarkColorName: String? = null
        if (cursor.moveToFirst()) {
            typeMarkColorName = cursor.getString(cursor.getColumnIndex(colorNameColumnName))
        }
        cursor.close()
        typeMarkDB.close()
        return typeMarkColorName
    }

    //*****************TypeMarkPattern*****************

    fun loadTypeMarkPattern(): List<TypeMarkPattern> {
        val typeMarkDB = SQLiteDatabase.openDatabase(App.context.getDatabasePath(Constant.DB_TYPE_MARK).path, null, SQLiteDatabase.OPEN_READONLY)
        val typeMarkPatternList = mutableListOf<TypeMarkPattern>()
        val patternNameColumnName = String.format(Constant.PATTERN_NAME, columnNameSuffixByLocale)
        val cursor = typeMarkDB.query(Constant.PATTERN, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                typeMarkPatternList.add(TypeMarkPattern(
                        cursor.getString(cursor.getColumnIndex(Constant.PATTERN_FN)),
                        cursor.getString(cursor.getColumnIndex(patternNameColumnName))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        typeMarkDB.close()
        return typeMarkPatternList
    }

    // typeMarkPatternFn不能为可空类型，最后拼接的sql语句会为pattern_fn = ?，就无法查询
    fun queryTypeMarkPatternNameByTypeMarkPatternFn(typeMarkPatternFn: String): String? {
        val typeMarkDB = SQLiteDatabase.openDatabase(App.context.getDatabasePath(Constant.DB_TYPE_MARK).path, null, SQLiteDatabase.OPEN_READONLY)
        val patternNameColumnName = String.format(Constant.PATTERN_NAME, columnNameSuffixByLocale)
        val cursor = typeMarkDB.query(Constant.PATTERN, arrayOf(patternNameColumnName), "${Constant.PATTERN_FN} = ?", arrayOf(typeMarkPatternFn), null, null, null)
        var typeMarkPatternName: String? = null
        if (cursor.moveToFirst()) {
            typeMarkPatternName = cursor.getString(cursor.getColumnIndex(patternNameColumnName))
        }
        cursor.close()
        typeMarkDB.close()
        return typeMarkPatternName
    }

    //*****************Others*****************

    private val columnNameSuffixByLocale
        get() = when (SystemUtil.preferredLocale) {
            Locale.SIMPLIFIED_CHINESE -> Constant.ZH_CN
            Locale.TRADITIONAL_CHINESE -> Constant.ZH_TW
            else -> Constant.EN
        }
}
