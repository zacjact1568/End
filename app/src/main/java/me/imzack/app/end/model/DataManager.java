package me.imzack.app.end.model;

import android.text.TextUtils;

import me.imzack.app.end.App;
import me.imzack.app.end.model.bean.PlanCount;
import me.imzack.app.end.model.preference.PreferenceHelper;
import me.imzack.app.end.util.SystemUtil;
import me.imzack.app.end.model.bean.TypeMarkColor;
import me.imzack.app.end.model.bean.TypeMarkPattern;
import me.imzack.app.end.model.bean.Plan;
import me.imzack.app.end.model.bean.Type;
import me.imzack.app.end.model.database.DatabaseHelper;
import me.imzack.app.end.eventbus.event.DataLoadedEvent;
import me.imzack.app.end.common.Constant;
import me.imzack.app.end.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private DatabaseHelper mDatabaseHelper;
    private PreferenceHelper mPreferenceHelper;
    private List<Plan> mPlanList;
    private List<Type> mTypeList;
    private Map<String, PlanCount> mTypeCodePlanCountMap;
    private boolean isDataLoaded = false;

    private static DataManager ourInstance = new DataManager();

    private DataManager() {
        mDatabaseHelper = DatabaseHelper.getInstance();
        mPreferenceHelper = PreferenceHelper.getInstance();
        mPlanList = new ArrayList<>();
        mTypeList = new ArrayList<>();
        mTypeCodePlanCountMap = new HashMap<>();

        loadFromDatabase();
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    /** 从数据库中加载 */
    private void loadFromDatabase() {
        mDatabaseHelper.loadDataAsync(new DatabaseHelper.DataLoadedCallback() {
            @Override
            public void onDataLoaded(List<Plan> planList, List<Type> typeList) {
                //以下代码都是在主线程中执行的，所以如果在主线程访问mPlanList或mTypeList，只有两种情况：空或满数据
                mPlanList.addAll(planList);
                mTypeList.addAll(typeList);
                for (Type type : mTypeList) {
                    mTypeCodePlanCountMap.put(type.getTypeCode(), new PlanCount());
                }
                for (int i = 0; i < getPlanCount(); i++) {
                    Plan plan = getPlan(i);
                    //初始化每个类型具有的计划数量map
                    mTypeCodePlanCountMap.get(plan.getTypeCode()).increase(1, plan.isCompleted());
                    //将过期的reminder移除（在某些rom中，如果未开启后台运行权限，杀死进程后无法被系统唤醒）
                    if (plan.hasReminder() && !TimeUtil.isFutureTime(plan.getReminderTime())) {
                        //有reminder且已过时
                        notifyReminderTimeChanged(i, Constant.UNDEFINED_TIME);
                    }
                }
                //已加载数据
                isDataLoaded = true;
                //发送事件
                App.getEventBus().post(new DataLoadedEvent());
            }
        });
    }

    /** 获取当前数据加载状态 */
    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    //**************** PlanList ****************

    /** 获取某个计划 */
    public Plan getPlan(int location) {
        return mPlanList.get(location);
    }

    /** 交换计划列表中的两个元素（只能在两个相同完成状态的计划之间交换）*/
    public void swapPlansInPlanList(int oneLocation, int anotherLocation) {
        Plan onePlan = getPlan(oneLocation);
        Plan anotherPlan = getPlan(anotherLocation);
        if (onePlan.isCompleted() && anotherPlan.isCompleted()) {
            //完成->完成，交换completion time
            long oneCompletionTime = onePlan.getCompletionTime();
            onePlan.setCompletionTime(anotherPlan.getCompletionTime());
            anotherPlan.setCompletionTime(oneCompletionTime);
            mDatabaseHelper.updateCompletionTime(onePlan.getPlanCode(), onePlan.getCompletionTime());
            mDatabaseHelper.updateCompletionTime(anotherPlan.getPlanCode(), anotherPlan.getCompletionTime());
        } else if (!onePlan.isCompleted() && !anotherPlan.isCompleted()) {
            //未完成->未完成，交换creation time
            long oneCreationTime = onePlan.getCreationTime();
            onePlan.setCreationTime(anotherPlan.getCreationTime());
            anotherPlan.setCreationTime(oneCreationTime);
            mDatabaseHelper.updateCreationTime(onePlan.getPlanCode(), onePlan.getCreationTime());
            mDatabaseHelper.updateCreationTime(anotherPlan.getPlanCode(), anotherPlan.getCreationTime());
        } else {
            return;
        }
        Collections.swap(mPlanList, oneLocation, anotherLocation);
    }

    /** 获取最近创建的计划位置 */
    public int getRecentlyCreatedPlanLocation() {
        return 0;
    }

    /** 获取当前计划的数量 */
    public int getPlanCount() {
        return mPlanList.size();
    }

    /** 获取计划在PlanList中的序号 */
    public int getPlanLocationInPlanList(String planCode) {
        for (int i = 0; i < getPlanCount(); i++) {
            if (getPlan(i).getPlanCode().equals(planCode)) {
                return i;
            }
        }
        return -1;
    }

    /** 获取指定类型的所有计划 */
    public List<Plan> getSingleTypePlanList(String typeCode) {
        List<Plan> singleTypePlanList = new ArrayList<>();
        for (Plan plan : mPlanList) {
            if (plan.getTypeCode().equals(typeCode)) {
                singleTypePlanList.add(plan);
            }
        }
        return singleTypePlanList;
    }

    /** 获取指定类型全部计划的位置 */
    public List<Integer> getPlanLocationListOfOneType(String typeCode) {
        List<Integer> planLocationList = new ArrayList<>();
        for (int i = 0; i < getPlanCount(); i++) {
            if (getPlan(i).getTypeCode().equals(typeCode)) {
                planLocationList.add(i);
            }
        }
        return planLocationList;
    }

    /** 将fromLocationList提供的位置上的计划迁移到另一个类型 */
    public void migratePlan(List<Integer> fromLocationList, String toTypeCode) {
        for (int fromLocation : fromLocationList) {
            notifyTypeOfPlanChanged(fromLocation, getPlan(fromLocation).getTypeCode(), toTypeCode);
        }
    }

    /** 创建计划 (Inserted at the beginning of mPlanList) */
    public void notifyPlanCreated(Plan newPlan) {
        notifyPlanCreated(0, newPlan);
    }

    /** 创建计划 (Inserted at a specified location of mPlanList) */
    public void notifyPlanCreated(int location, Plan newPlan) {
        mPlanList.add(location, newPlan);
        mTypeCodePlanCountMap.get(newPlan.getTypeCode()).increase(1, newPlan.isCompleted());
        //设置提醒
        if (newPlan.hasReminder()) {
            //有提醒，需要设置
            SystemUtil.setReminder(newPlan.getPlanCode(), newPlan.getReminderTime());
        }
        //存储至数据库
        mDatabaseHelper.savePlan(newPlan);
    }

    /** 删除计划 */
    public void notifyPlanDeleted(int location) {
        Plan plan = getPlan(location);
        mTypeCodePlanCountMap.get(plan.getTypeCode()).increase(-1, plan.isCompleted());
        if (plan.hasReminder()) {
            //This plan has registered a reminder that need to be canceled
            SystemUtil.setReminder(plan.getPlanCode(), Constant.UNDEFINED_TIME);
        }
        mPlanList.remove(location);
        //更新数据库
        mDatabaseHelper.deletePlan(plan.getPlanCode());
    }

    /** 编辑计划内容 */
    public void notifyPlanContentChanged(int location, String newContent) {
        Plan plan = getPlan(location);
        plan.setContent(newContent);
        mDatabaseHelper.updateContent(plan.getPlanCode(), newContent);
    }

    /** 编辑计划类型 */
    public void notifyTypeOfPlanChanged(int location, String oldTypeCode, String newTypeCode) {
        if (oldTypeCode.equals(newTypeCode)) return;
        Plan plan = getPlan(location);
        boolean completed = plan.isCompleted();
        mTypeCodePlanCountMap.get(oldTypeCode).increase(-1, completed);
        mTypeCodePlanCountMap.get(newTypeCode).increase(1, completed);
        plan.setTypeCode(newTypeCode);
        mDatabaseHelper.updateTypeOfPlan(plan.getPlanCode(), newTypeCode);
    }

    /** 编辑计划星标状态 */
    public void notifyStarStatusChanged(int location) {
        Plan plan = getPlan(location);
        plan.invertStarStatus();
        mDatabaseHelper.updateStarStatus(plan.getPlanCode(), plan.getStarStatus());
    }

    /** 编辑计划截止时间 */
    public void notifyDeadlineChanged(int location, long newDeadline) {
        Plan plan = getPlan(location);
        plan.setDeadline(newDeadline);
        mDatabaseHelper.updateDeadline(plan.getPlanCode(), newDeadline);
    }

    /** 编辑计划提醒时间 */
    public void notifyReminderTimeChanged(int location, long newReminderTime) {
        Plan plan = getPlan(location);
        SystemUtil.setReminder(plan.getPlanCode(), newReminderTime);
        plan.setReminderTime(newReminderTime);
        mDatabaseHelper.updateReminderTime(plan.getPlanCode(), newReminderTime);
    }

    /** 编辑计划完成状态 */
    public void notifyPlanStatusChanged(int location) {
        Plan plan = getPlan(location);

        //旧的完成状态
        boolean isCompletedPast = plan.isCompleted();
        //更新Map
        mTypeCodePlanCountMap.get(plan.getTypeCode()).exchange(1, !isCompletedPast);

        //操作list
        mPlanList.remove(location);

        long currentTimeMillis = System.currentTimeMillis();
        long newCreationTime = isCompletedPast ? currentTimeMillis : Constant.UNDEFINED_TIME;
        long newCompletionTime = isCompletedPast ? Constant.UNDEFINED_TIME : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompletedPast ? 0 : getUcPlanCount();
        mPlanList.add(newPosition, plan);

        mDatabaseHelper.updatePlanStatus(plan.getPlanCode(), newCreationTime, newCompletionTime);
    }

    /** 获取今天截止的未完成计划的数量 */
    public int getTodayUcPlanCount() {
        int count = 0;
        for (Plan plan : mPlanList) {
            if (TimeUtil.isToday(plan.getDeadline())) {
                count++;
            }
        }
        return count;
    }

    /** 获取搜索到的所有计划 */
    public void searchPlan(List<Plan> planSearchList, String searchText) {
        planSearchList.clear();
        if (TextUtils.isEmpty(searchText)) return;
        for (Plan plan : mPlanList) {
            if (plan.getContent().toLowerCase().contains(searchText.toLowerCase())) {
                planSearchList.add(plan);
            }
        }
    }

    //**************** TypeList ****************

    /** 获取某个类型 */
    public Type getType(int location) {
        return mTypeList.get(location);
    }

    /** 交换类型列表中的两个元素 */
    public void swapTypesInTypeList(int oneLocation, int anotherLocation) {
        Type oneType = getType(oneLocation);
        Type anotherType = getType(anotherLocation);
        int oneTypeSequence = oneType.getTypeSequence();
        oneType.setTypeSequence(anotherType.getTypeSequence());
        anotherType.setTypeSequence(oneTypeSequence);
        mDatabaseHelper.updateTypeSequence(oneType.getTypeCode(), oneType.getTypeSequence());
        mDatabaseHelper.updateTypeSequence(anotherType.getTypeCode(), anotherType.getTypeSequence());
        Collections.swap(mTypeList, oneLocation, anotherLocation);
    }

    /** 获取最近创建的类型位置 */
    public int getRecentlyCreatedTypeLocation() {
        return getTypeCount() - 1;
    }

    /** 获取当前类型的数量 */
    public int getTypeCount() {
        return mTypeList.size();
    }

    /** 获取类型在TypeList中的序号 */
    public int getTypeLocationInTypeList(String typeCode) {
        for (int i = 0; i < getTypeCount(); i++) {
            if (getType(i).getTypeCode().equals(typeCode)) {
                return i;
            }
        }
        return -1;
    }

    /** 创建类型（插入到末尾）*/
    public void notifyTypeCreated(Type newType) {
        mTypeList.add(newType);
        mTypeCodePlanCountMap.put(newType.getTypeCode(), new PlanCount());
        mDatabaseHelper.saveType(newType);
    }

    /** 删除类型 */
    public void notifyTypeDeleted(int location) {
        Type type = getType(location);
        //删除所有对应的计划
        for (int i = 0; i < getPlanCount(); i++) {
            if (getPlan(i).getTypeCode().equals(type.getTypeCode())) {
                notifyPlanDeleted(i);
                i--;
            }
        }
        mTypeCodePlanCountMap.remove(type.getTypeCode());
        mTypeList.remove(location);
        mDatabaseHelper.deleteType(type.getTypeCode());
    }

    /** 更新类型名称 */
    public void notifyUpdatingTypeName(int location, String newTypeName) {
        Type type = getType(location);
        type.setTypeName(newTypeName);
        mDatabaseHelper.updateTypeName(type.getTypeCode(), newTypeName);
    }

    /** 更新类型标记颜色 */
    public void notifyUpdatingTypeMarkColor(int location, String newTypeMarkColor) {
        Type type = getType(location);
        type.setTypeMarkColor(newTypeMarkColor);
        mDatabaseHelper.updateTypeMarkColor(type.getTypeCode(), newTypeMarkColor);
    }

    /** 更新类型标记图案 */
    public void notifyUpdatingTypeMarkPattern(int location, String newTypeMarkPattern) {
        Type type = getType(location);
        type.setTypeMarkPattern(newTypeMarkPattern);
        mDatabaseHelper.updateTypeMarkPattern(type.getTypeCode(), newTypeMarkPattern);
    }

    /** 重排类型（集中重排）*/
    public void notifyTypeSequenceRearranged() {
        for (int i = 0; i < getTypeCount(); i++) {
            Type type = getType(i);
            if (type.getTypeSequence() != i) {
                //更新typeList
                //在移动typeList的item的时候只是交换了items在list中的位置，并没有改变item中的type_sequence
                type.setTypeSequence(i);
                //更新数据库
                mDatabaseHelper.updateTypeSequence(type.getTypeCode(), i);
            }
        }
    }

    /** 获取TypeMarkColor */
    public String getTypeMarkColor(String typeCode) {
        for (Type type : mTypeList) {
            if (type.getTypeCode().equals(typeCode)) {
                return type.getTypeMarkColor();
            }
        }
        return null;
    }

    /** 判断给定类型名称是否已使用过 */
    public boolean isTypeNameUsed(String typeName) {
        for (Type type : mTypeList) {
            if (type.getTypeName().equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    /** 判断给定类型颜色是否已使用过 */
    public boolean isTypeMarkColorUsed(String typeMarkColor) {
        for (Type type : mTypeList) {
            if (type.getTypeMarkColor().equals(typeMarkColor)) {
                return true;
            }
        }
        return false;
    }

    /** 获取不包含指定type的list */
    public List<Type> getExcludedTypeList(String excludedTypeCode) {
        List<Type> excludedTypeList = new ArrayList<>();
        for (Type type : mTypeList) {
            if (!type.getTypeCode().equals(excludedTypeCode)) {
                excludedTypeList.add(type);
            }
        }
        return excludedTypeList;
    }

    /** 获取搜索到的所有类型 */
    public void searchType(List<Type> typeSearchList, String searchText) {
        typeSearchList.clear();
        if (TextUtils.isEmpty(searchText)) return;
        for (Type type : mTypeList) {
            if (type.getTypeName().toLowerCase().contains(searchText.toLowerCase())) {
                typeSearchList.add(type);
            }
        }
    }

    //**************** Database (TypeName & TypeMark) ****************

    /** 获取全部TypeMark颜色 */
    public List<TypeMarkColor> getTypeMarkColorList() {
        return mDatabaseHelper.loadTypeMarkColor();
    }

    /** 获取全部TypeMark图案 */
    public List<TypeMarkPattern> getTypeMarkPatternList() {
        return mDatabaseHelper.loadTypeMarkPattern();
    }

    /** 数据库获取颜色名称 */
    public String getTypeMarkColorName(String colorHex) {
        String colorName = mDatabaseHelper.queryTypeMarkColorNameByTypeMarkColorHex(colorHex);
        return colorName == null ? colorHex : colorName;
    }

    /** 数据库获取图案名称 */
    public String getTypeMarkPatternName(String patternFn) {
        return patternFn == null ? null : mDatabaseHelper.queryTypeMarkPatternNameByTypeMarkPatternFn(patternFn);
    }

    //**************** TypeCodePlanCountMap ****************

    /** 获取未完成计划的数量 */
    public int getUcPlanCount() {
        int count = 0;
        for (Map.Entry<String, PlanCount> entry : mTypeCodePlanCountMap.entrySet()) {
            count += entry.getValue().getUncompleted();
        }
        return count;
    }

    /** 获取指定类型中未完成计划的数量 */
    public int getUcPlanCountOfOneType(String typeCode) {
        return mTypeCodePlanCountMap.get(typeCode).getUncompleted();
    }

    /** 获取指定类型中所有计划的数量 */
    public int getPlanCountOfOneType(String typeCode) {
        return mTypeCodePlanCountMap.get(typeCode).getAll();
    }

    /** 判断某类型是否为空 */
    public boolean isTypeEmpty(String typeCode) {
        return mTypeCodePlanCountMap.get(typeCode).getAll() == 0;
    }

    //**************** PreferenceHelper ****************

    public PreferenceHelper getPreferenceHelper() {
        return mPreferenceHelper;
    }
}
