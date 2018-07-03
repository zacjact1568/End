package net.zackzhang.app.end.model

import android.text.TextUtils
import net.zackzhang.app.end.App
import net.zackzhang.app.end.event.DataLoadedEvent
import net.zackzhang.app.end.model.bean.Plan
import net.zackzhang.app.end.model.bean.PlanCount
import net.zackzhang.app.end.model.bean.Type
import net.zackzhang.app.end.model.database.DatabaseHelper
import net.zackzhang.app.end.model.preference.PreferenceHelper
import net.zackzhang.app.end.util.SystemUtil
import net.zackzhang.app.end.util.TimeUtil
import java.util.*

object DataManager {

    // 持有两个helper对象的引用，实为单例
    val databaseHelper = DatabaseHelper()
    val preferenceHelper = PreferenceHelper()

    // 用于存储全局数据的结构
    private val mPlanList = mutableListOf<Plan>()
    private val mTypeList = mutableListOf<Type>()
    private val mTypeCodePlanCountMap = mutableMapOf<String, PlanCount>()

    // 当前数据加载状态
    var isDataLoaded = false
        private set

    /** 从数据库中加载数据 */
    fun loadData() {
        if (isDataLoaded) return
        databaseHelper.loadDataAsync { planList, typeList ->
            //以下代码都是在主线程中执行的，所以如果在主线程访问mPlanList或mTypeList，只有两种情况：空或满数据
            mPlanList.addAll(planList)
            mTypeList.addAll(typeList)
            for ((code) in mTypeList) {
                mTypeCodePlanCountMap.put(code, PlanCount())
            }
            for (i in 0 until planCount) {
                val plan = getPlan(i)
                //初始化每个类型具有的计划数量map
                mTypeCodePlanCountMap[plan.typeCode]?.increase(1, plan.isCompleted)
                //将过期的reminder移除（在某些rom中，如果未开启后台运行权限，杀死进程后无法被系统唤醒）
                if (plan.hasReminder && !TimeUtil.isFutureTime(plan.reminderTime)) {
                    //有reminder且已过时
                    clearPastReminderTime(i)
                }
            }
            //已加载数据
            isDataLoaded = true
            //发送事件
            App.eventBus.post(DataLoadedEvent())
        }
    }

    /** 卸载数据（暂时不用） */
    fun unloadData() {
        if (!isDataLoaded) return
        isDataLoaded = false
        mPlanList.clear()
        mTypeList.clear()
        mTypeCodePlanCountMap.clear()
    }

    //**************** PlanList ****************

    /** 获取某个计划 */
    fun getPlan(location: Int) = mPlanList[location]

    /** 从数据库获取某个计划 */
    fun getPlanFromDatabase(code: String) = databaseHelper.queryPlan(code) ?: throw IllegalArgumentException("No plans in database have code \"$code\"")

    /** 交换计划列表中的两个元素（只能在两个相同完成状态的计划之间交换） */
    fun swapPlansInPlanList(oneLocation: Int, anotherLocation: Int) {
        val onePlan = getPlan(oneLocation)
        val anotherPlan = getPlan(anotherLocation)
        if (onePlan.isCompleted && anotherPlan.isCompleted) {
            //完成->完成，交换completion time
            val oneCompletionTime = onePlan.completionTime
            onePlan.completionTime = anotherPlan.completionTime
            anotherPlan.completionTime = oneCompletionTime
            databaseHelper.updateCompletionTime(onePlan.code, onePlan.completionTime)
            databaseHelper.updateCompletionTime(anotherPlan.code, anotherPlan.completionTime)
        } else if (!onePlan.isCompleted && !anotherPlan.isCompleted) {
            //未完成->未完成，交换creation time
            val oneCreationTime = onePlan.creationTime
            onePlan.creationTime = anotherPlan.creationTime
            anotherPlan.creationTime = oneCreationTime
            databaseHelper.updateCreationTime(onePlan.code, onePlan.creationTime)
            databaseHelper.updateCreationTime(anotherPlan.code, anotherPlan.creationTime)
        } else {
            return
        }
        Collections.swap(mPlanList, oneLocation, anotherLocation)
    }

    /** 获取当前计划的数量 */
    val planCount
        get() = mPlanList.size

    /** 获取最近创建的计划位置 */
    val recentlyCreatedPlanLocation
        get() = 0

    /** 获取计划在PlanList中的序号 */
    fun getPlanLocationInPlanList(code: String) = (0 until planCount).firstOrNull { getPlan(it).code == code } ?: -1

    /** 获取指定类型的所有计划 */
    fun getSingleTypePlanList(typeCode: String) = mPlanList.filter { it.typeCode == typeCode }.toMutableList()

    /** 获取指定类型全部计划的位置 */
    fun getPlanLocationListOfOneType(typeCode: String) = (0 until planCount).filter { getPlan(it).typeCode == typeCode }

    /** 将fromLocationList提供的位置上的计划迁移到另一个类型 */
    fun migratePlan(fromLocationList: List<Int>, toTypeCode: String) {
        for (fromLocation in fromLocationList) {
            notifyTypeOfPlanChanged(fromLocation, getPlan(fromLocation).typeCode, toTypeCode)
        }
    }

    /** 创建计划，若不指定location，则插入到表头 */
    fun notifyPlanCreated(newPlan: Plan, location: Int = 0) {
        mPlanList.add(location, newPlan)
        mTypeCodePlanCountMap[newPlan.typeCode]?.increase(1, newPlan.isCompleted)
        //设置提醒
        if (newPlan.hasReminder) {
            //有提醒，需要设置
            SystemUtil.setReminder(newPlan.code, newPlan.reminderTime)
        }
        //存储至数据库
        databaseHelper.savePlan(newPlan)
    }

    /** 删除计划 */
    fun notifyPlanDeleted(location: Int) {
        val plan = getPlan(location)
        mTypeCodePlanCountMap[plan.typeCode]?.increase(-1, plan.isCompleted)
        if (plan.hasReminder) {
            //This plan has registered a reminder that need to be canceled
            SystemUtil.setReminder(plan.code, 0L)
        }
        mPlanList.removeAt(location)
        //更新数据库
        databaseHelper.deletePlan(plan.code)
    }

    /** 编辑计划内容 */
    fun notifyPlanContentChanged(location: Int, newContent: String) {
        val plan = getPlan(location)
        plan.content = newContent
        databaseHelper.updateContent(plan.code, newContent)
    }

    /** 编辑计划类型 */
    fun notifyTypeOfPlanChanged(location: Int, oldTypeCode: String, newTypeCode: String) {
        if (oldTypeCode == newTypeCode) return
        val plan = getPlan(location)
        val completed = plan.isCompleted
        mTypeCodePlanCountMap[oldTypeCode]?.increase(-1, completed)
        mTypeCodePlanCountMap[newTypeCode]?.increase(1, completed)
        plan.typeCode = newTypeCode
        databaseHelper.updateTypeOfPlan(plan.code, newTypeCode)
    }

    /** 编辑计划星标状态 */
    fun notifyStarStatusChanged(location: Int) {
        val plan = getPlan(location)
        plan.invertStarStatus()
        databaseHelper.updateStarStatus(plan.code, plan.starStatus)
    }

    /** 编辑计划截止时间 */
    fun notifyDeadlineChanged(location: Int, newDeadline: Long) {
        val plan = getPlan(location)
        plan.deadline = newDeadline
        databaseHelper.updateDeadline(plan.code, newDeadline)
    }

    /** 编辑计划提醒时间 */
    fun notifyReminderTimeChanged(location: Int, newReminderTime: Long) {
        val plan = getPlan(location)
        SystemUtil.setReminder(plan.code, newReminderTime)
        plan.reminderTime = newReminderTime
        databaseHelper.updateReminderTime(plan.code, newReminderTime)
    }

    /** 清空计划提醒时间 */
    fun clearPastReminderTime(location: Int) {
        val plan = getPlan(location)
        plan.reminderTime = 0L
        clearPastReminderTimeInDatabase(plan.code)
    }

    /** 清空数据库中的计划提醒时间 */
    fun clearPastReminderTimeInDatabase(code: String) {
        databaseHelper.updateReminderTime(code, 0L)
    }

    /** 编辑计划完成状态 */
    fun notifyPlanStatusChanged(location: Int) {
        val plan = getPlan(location)

        //旧的完成状态
        val isCompletedPast = plan.isCompleted
        //更新Map
        mTypeCodePlanCountMap[plan.typeCode]?.exchange(1, !isCompletedPast)

        //操作list
        mPlanList.removeAt(location)

        val currentTimeMillis = System.currentTimeMillis()
        val newCreationTime = if (isCompletedPast) currentTimeMillis else 0L
        val newCompletionTime = if (isCompletedPast) 0L else currentTimeMillis

        plan.creationTime = newCreationTime
        plan.completionTime = newCompletionTime

        val newPosition = if (isCompletedPast) 0 else ucPlanCount
        mPlanList.add(newPosition, plan)

        databaseHelper.updatePlanStatus(plan.code, newCreationTime, newCompletionTime)
    }

    /** 获取今天截止的未完成计划的数量 */
    val todayUcPlanCount: Int
        get() {
            var count = 0
            for ((_, _, _, _, deadline) in mPlanList) {
                if (TimeUtil.isToday(deadline)) {
                    count++
                }
            }
            return count
        }

    /** 获取搜索到的所有计划 */
    fun searchPlan(planSearchList: MutableList<Plan>, searchText: String) {
        planSearchList.clear()
        if (TextUtils.isEmpty(searchText)) return
        mPlanList.filterTo(planSearchList) { it.content.toLowerCase().contains(searchText.toLowerCase()) }
    }

    //**************** TypeList ****************

    /** 获取某个类型 */
    fun getType(location: Int) = mTypeList[location]

    /** 从数据库获取某个类型 */
    fun getTypeFromDatabase(code: String) = databaseHelper.queryType(code) ?: throw IllegalArgumentException("No types in database have code \"$code\"")

    /** 交换类型列表中的两个元素 */
    fun swapTypesInTypeList(oneLocation: Int, anotherLocation: Int) {
        val oneType = getType(oneLocation)
        val anotherType = getType(anotherLocation)
        val oneTypeSequence = oneType.sequence
        oneType.sequence = anotherType.sequence
        anotherType.sequence = oneTypeSequence
        databaseHelper.updateTypeSequence(oneType.code, oneType.sequence)
        databaseHelper.updateTypeSequence(anotherType.code, anotherType.sequence)
        Collections.swap(mTypeList, oneLocation, anotherLocation)
    }

    /** 获取当前类型的数量 */
    val typeCount
        get() = mTypeList.size

    /** 获取最近创建的类型位置 */
    val recentlyCreatedTypeLocation
        get() = typeCount - 1

    /** 获取类型在TypeList中的序号 */
    fun getTypeLocationInTypeList(code: String) = (0 until typeCount).firstOrNull { getType(it).code == code } ?: -1

    /** 创建类型（插入到末尾） */
    fun notifyTypeCreated(newType: Type) {
        mTypeList.add(newType)
        mTypeCodePlanCountMap.put(newType.code, PlanCount())
        databaseHelper.saveType(newType)
    }

    /** 删除类型 */
    fun notifyTypeDeleted(location: Int) {
        val (code) = getType(location)
        //删除所有对应的计划
        //TODO 类似逻辑是否可以换成stream形式
        var i = 0
        while (i < planCount) {
            if (getPlan(i).typeCode == code) {
                notifyPlanDeleted(i)
                break
            } else {
                i++
            }
        }
        mTypeCodePlanCountMap.remove(code)
        mTypeList.removeAt(location)
        databaseHelper.deleteType(code)
    }

    /** 更新类型名称 */
    fun notifyUpdatingTypeName(location: Int, newTypeName: String) {
        val type = getType(location)
        type.name = newTypeName
        databaseHelper.updateTypeName(type.code, newTypeName)
    }

    /** 更新类型标记颜色 */
    fun notifyUpdatingTypeMarkColor(location: Int, newTypeMarkColor: String) {
        val type = getType(location)
        type.markColor = newTypeMarkColor
        databaseHelper.updateTypeMarkColor(type.code, newTypeMarkColor)
    }

    /** 更新类型标记图案 */
    fun notifyUpdatingTypeMarkPattern(location: Int, newTypeMarkPattern: String?) {
        val type = getType(location)
        type.markPattern = newTypeMarkPattern
        databaseHelper.updateTypeMarkPattern(type.code, newTypeMarkPattern)
    }

    /** 重排类型（集中重排） */
    fun notifyTypeSequenceRearranged() {
        for (i in 0 until typeCount) {
            val type = getType(i)
            if (type.sequence != i) {
                //更新typeList
                //在移动typeList的item的时候只是交换了items在list中的位置，并没有改变item中的type_sequence
                type.sequence = i
                //更新数据库
                databaseHelper.updateTypeSequence(type.code, i)
            }
        }
    }

    /** 获取TypeMarkColor */
    fun getTypeMarkColor(code: String): String? {
        for ((codeInTypeList, _, typeMarkColor) in mTypeList) {
            if (codeInTypeList == code) {
                return typeMarkColor
            }
        }
        return null
    }

    /** 判断给定类型名称是否已使用过 */
    fun isTypeNameUsed(typeName: String): Boolean {
        for ((_, typeNameInTypeList) in mTypeList) {
            if (typeNameInTypeList == typeName) {
                return true
            }
        }
        return false
    }

    /** 判断给定类型颜色是否已使用过 */
    fun isTypeMarkColorUsed(typeMarkColor: String): Boolean {
        for ((_, _, typeMarkColorInTypeList) in mTypeList) {
            if (typeMarkColorInTypeList == typeMarkColor) {
                return true
            }
        }
        return false
    }

    /** 获取不包含指定type的list */
    fun getExcludedTypeList(excludedCode: String) = mTypeList.filterTo(mutableListOf()) { it.code != excludedCode }.toList()

    /** 获取搜索到的所有类型 */
    fun searchType(typeSearchList: MutableList<Type>, searchText: String) {
        typeSearchList.clear()
        if (TextUtils.isEmpty(searchText)) return
        mTypeList.filterTo(typeSearchList) { it.name.contains(searchText, true) }
    }

    //**************** Database (TypeName & TypeMark) ****************

    /** 获取全部TypeMark颜色 */
    val typeMarkColorList
        get() = databaseHelper.loadTypeMarkColor()

    /** 获取全部TypeMark图案 */
    val typeMarkPatternList
        get() = databaseHelper.loadTypeMarkPattern()

    /** 数据库获取颜色名称 */
    fun getTypeMarkColorName(colorHex: String) = databaseHelper.queryTypeMarkColorNameByTypeMarkColorHex(colorHex) ?: colorHex

    /** 数据库获取图案名称 */
    fun getTypeMarkPatternName(patternFn: String?) = patternFn?.let { databaseHelper.queryTypeMarkPatternNameByTypeMarkPatternFn(it) }

    //**************** TypeCodePlanCountMap ****************

    /** 获取未完成计划的数量 */
    val ucPlanCount: Int
        get() {
            var count = 0
            for ((_, value) in mTypeCodePlanCountMap) {
                count += value.uncompleted
            }
            return count
        }

    /** 获取指定类型中未完成计划的数量 */
    fun getUcPlanCountOfOneType(code: String) = mTypeCodePlanCountMap[code]?.uncompleted ?: throw IllegalArgumentException("\"$code\" is not the code of any type")

    /** 获取指定类型中所有计划的数量 */
    fun getPlanCountOfOneType(code: String) = mTypeCodePlanCountMap[code]?.all ?: throw IllegalArgumentException("\"$code\" is not the code of any type")

    /** 判断某类型是否为空 */
    fun isTypeEmpty(code: String) = mTypeCodePlanCountMap[code]?.all == 0
}
