package com.zack.enderplan.model;

import android.os.AsyncTask;

import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.utility.ReminderManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.utility.LogUtil;
import com.zack.enderplan.utility.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private static final String LOG_TAG = "DataManager";

    /** 状态标记：还未初始化存储的数据结构 */
    public static final int STATUS_STRUCT_NOT_INIT = 0;
    /** 状态标记：已初始化数据结构，但还未加载数据（数据为空）*/
    public static final int STATUS_DATA_NOT_LOAD = 1;
    /** 状态标记：正在子线程中从数据库加载数据，此时应该避免再开加载数据的线程 */
    public static final int STATUS_DATA_ON_LOAD = 2;
    /** 状态标记：数据加载完成 */
    public static final int STATUS_DATA_LOADED = 3;

    private DatabaseManager mDatabaseManager;
    private ReminderManager mReminderManager;
    private List<Plan> planList;
    private List<Type> typeList;
    private int uncompletedPlanCount;
    private Map<String, TypeMark> mTypeCodeAndTypeMarkMap;
    private Map<String, Integer> ucPlanCountOfEachTypeMap;
    private int dataStatus;

    private static DataManager ourInstance = new DataManager();

    private DataManager() {
        mDatabaseManager = DatabaseManager.getInstance();
        mReminderManager = ReminderManager.getInstance();

        //初始化状态
        dataStatus = STATUS_STRUCT_NOT_INIT;

        LogUtil.d(LOG_TAG, "DataManager实例化完成");
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    /** 初始化数据存储结构<br>NOTE：必须经过HomeActivity的构造才能执行此方法 */
    public void initDataStruct() {
        if (dataStatus == STATUS_STRUCT_NOT_INIT) {
            planList = new ArrayList<>();
            typeList = new ArrayList<>();
            uncompletedPlanCount = 0;
            mTypeCodeAndTypeMarkMap = new HashMap<>();
            ucPlanCountOfEachTypeMap = new HashMap<>();

            //进入下一个状态
            dataStatus = STATUS_DATA_NOT_LOAD;

            LogUtil.d(LOG_TAG, "数据结构初始化完成");
        }
    }

    /** 从数据库加载<br>NOTE：必须经过AllPlansFragment的构造才能执行此方法 */
    public void loadFromDatabase() {
        if (dataStatus == STATUS_DATA_NOT_LOAD) {
            //进入下一个状态
            dataStatus = STATUS_DATA_ON_LOAD;
            new LoadDataTask().execute();
        }
    }

    /** 清除数据存储结构中的数据 */
    public void clearData() {
        if (dataStatus == STATUS_DATA_LOADED) {
            planList.clear();
            typeList.clear();
            uncompletedPlanCount = 0;
            mTypeCodeAndTypeMarkMap.clear();
            ucPlanCountOfEachTypeMap.clear();

            //将状态置为未加载
            dataStatus = STATUS_DATA_NOT_LOAD;
        }
    }

    /** 获取当前状态 */
    public int getDataStatus() {
        return dataStatus;
    }

    //****************PlanList****************

    //获取planList
    public List<Plan> getPlanList() {
        return planList;
    }

    //获取某个计划
    public Plan getPlan(int location) {
        return planList.get(location);
    }

    //添加计划到list
    public void addToPlanList(int location, Plan newPlan) {
        planList.add(location, newPlan);
    }

    //从list删除计划
    public void removeFromPlanList(int location) {
        planList.remove(location);
    }

    /** 获取最近创建的计划位置 */
    public int getRecentlyCreatedPlanLocation() {
        return 0;
    }

    /** 获取最近创建的计划 */
    public Plan getRecentlyCreatedPlan() {
        return getPlan(getRecentlyCreatedPlanLocation());
    }

    /** 获取当前计划的数量 */
    public int getPlanCount() {
        return planList.size();
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

    /** 创建计划 (Inserted at the beginning of planList) */
    public void notifyPlanCreated(Plan newPlan) {
        notifyPlanCreated(0, newPlan);
    }

    /** 创建计划 (Inserted at a specified location of planList) */
    public void notifyPlanCreated(int location, Plan newPlan) {
        addToPlanList(location, newPlan);
        if (newPlan.getCompletionTime() == 0) {
            //说明该计划还未完成
            //更新未完成计划的数量
            updateUcPlanCount(1);
            //更新每个类型具有的计划数量map
            updateUcPlanCountOfEachTypeMap(newPlan.getTypeCode(), 1);
        }
        //设置提醒
        if (newPlan.getReminderTime() != 0) {
            //有提醒，需要设置
            mReminderManager.setAlarm(newPlan.getPlanCode(), newPlan.getReminderTime());
        }
        //存储至数据库
        mDatabaseManager.savePlan(newPlan);
    }

    /** 删除计划 */
    public void notifyPlanDeleted(int location) {
        Plan plan = getPlan(location);
        if (plan.getCompletionTime() == 0) {
            //That means this plan is uncompleted
            updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), -1);
            updateUcPlanCount(-1);
        }
        if (plan.getReminderTime() != 0) {
            //This plan has registered a reminder that need to be canceled
            mReminderManager.cancelAlarm(plan.getPlanCode());
        }
        removeFromPlanList(location);
        //更新数据库
        mDatabaseManager.deletePlan(plan.getPlanCode());
    }

    /** 编辑计划内容 */
    public void notifyPlanContentChanged(int location, String newContent) {
        Plan plan = getPlan(location);
        plan.setContent(newContent);
        mDatabaseManager.updateContent(plan.getPlanCode(), newContent);
    }

    /** 编辑计划类型 */
    public void notifyTypeOfPlanChanged(int location, String oldTypeCode, String newTypeCode) {
        Plan plan = getPlan(location);
        if (plan.getCompletionTime() == 0) {
            //说明此计划还未完成，把此Uc计划的类型改变反映到UcMap
            updateUcPlanCountOfEachTypeMap(oldTypeCode, newTypeCode);
        }
        //再来改变typeCode
        plan.setTypeCode(newTypeCode);
        mDatabaseManager.updateTypeOfPlan(plan.getPlanCode(), newTypeCode);
    }

    /** 编辑计划星标状态 */
    public void notifyStarStatusChanged(int location) {
        Plan plan = getPlan(location);
        int newStarStatus = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED ?
                Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
        plan.setStarStatus(newStarStatus);
        mDatabaseManager.updateStarStatus(plan.getPlanCode(), newStarStatus);
    }

    /** 编辑计划截止时间 */
    public void notifyDeadlineChanged(int location, long newDeadline) {
        Plan plan = getPlan(location);
        plan.setDeadline(newDeadline);
        mDatabaseManager.updateDeadline(plan.getPlanCode(), newDeadline);
    }

    /** 编辑计划提醒时间 */
    public void notifyReminderTimeChanged(int location, long newReminderTime) {
        Plan plan = getPlan(location);
        if (newReminderTime != 0) {
            mReminderManager.setAlarm(plan.getPlanCode(), newReminderTime);
        } else {
            mReminderManager.cancelAlarm(plan.getPlanCode());
        }
        plan.setReminderTime(newReminderTime);
        mDatabaseManager.updateReminderTime(plan.getPlanCode(), newReminderTime);
    }

    /** 编辑计划完成状态 */
    public void notifyPlanStatusChanged(int location) {
        Plan plan = getPlan(location);

        //旧的完成状态
        boolean isCompletedPast = plan.getCompletionTime() != 0;
        //更新Maps
        updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), isCompletedPast ? 1 : -1);
        updateUcPlanCount(isCompletedPast ? 1 : -1);

        //操作list
        removeFromPlanList(location);

        long currentTimeMillis = System.currentTimeMillis();
        long newCreationTime = isCompletedPast ? currentTimeMillis : 0;
        long newCompletionTime = isCompletedPast ? 0 : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompletedPast ? 0 : getUcPlanCount();
        addToPlanList(newPosition, plan);

        mDatabaseManager.updatePlanStatus(plan.getPlanCode(), newCreationTime, newCompletionTime);
    }

    //****************TypeList****************

    //获取typeList
    public List<Type> getTypeList() {
        return typeList;
    }

    //获取某个类型
    public Type getType(int location) {
        return typeList.get(location);
    }

    //添加类型到list
    public void addToTypeList(int location, Type newType) {
        typeList.add(location, newType);
    }

    //添加类型到list的最后
    public void addToTypeList(Type newType) {
        typeList.add(newType);
    }

    //从list删除类型
    public void removeFromTypeList(int location) {
        typeList.remove(location);
    }

    /** 移动类型列表中的元素 */
    public void moveTypeInTypeList(int fromLocation, int toLocation) {
        if (fromLocation < toLocation) {
            for (int i = fromLocation; i < toLocation; i++) {
                Collections.swap(typeList, i, i + 1);
            }
        } else {
            for (int i = fromLocation; i > toLocation; i--) {
                Collections.swap(typeList, i, i - 1);
            }
        }
    }

    /** 交换类型列表中的两个元素 */
    public void swapTypesInTypeList(int oneLocation, int anotherLocation) {
        Collections.swap(typeList, oneLocation, anotherLocation);
    }

    /** 获取最近创建的类型位置 */
    public int getRecentlyCreatedTypeLocation() {
        return getTypeCount() - 1;
    }

    /** 获取最近创建的类型 */
    public Type getRecentlyCreatedType() {
        return getType(getRecentlyCreatedTypeLocation());
    }

    /** 获取当前类型的数量 */
    public int getTypeCount() {
        return typeList.size();
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

    /** 创建类型 (Inserted at the end of typeList) */
    public void notifyTypeCreated(Type newType) {
        notifyTypeCreated(getTypeCount(), newType);
    }

    /** 创建类型 (Inserted at a specified location of typeList) */
    public void notifyTypeCreated(int location, Type newType) {
        addToTypeList(location, newType);
        mTypeCodeAndTypeMarkMap.put(
                newType.getTypeCode(),
                new TypeMark(newType.getTypeMarkColor(), newType.getTypeMarkPattern())
        );
        //储存至数据库
        mDatabaseManager.saveType(newType);
    }

    /** 删除类型 */
    public void notifyTypeDeleted(int location) {
        Type type = getType(location);
        removeFromTypeList(location);
        mTypeCodeAndTypeMarkMap.remove(type.getTypeCode());
        mDatabaseManager.deleteType(type.getTypeCode());
    }

    /** 更新类型名称 */
    public void notifyUpdatingTypeName(int location, String newTypeName) {
        Type type = getType(location);
        type.setTypeName(newTypeName);
        mDatabaseManager.updateTypeName(type.getTypeCode(), newTypeName);
    }

    /** 更新类型标记颜色 */
    public void notifyUpdatingTypeMarkColor(int location, String newTypeMarkColor) {
        Type type = getType(location);
        type.setTypeMarkColor(newTypeMarkColor);
        mTypeCodeAndTypeMarkMap.get(type.getTypeCode()).setColorHex(newTypeMarkColor);
        mDatabaseManager.updateTypeMarkColor(type.getTypeCode(), newTypeMarkColor);
    }

    /** 更新类型标记图案 */
    public void notifyUpdatingTypeMarkPattern(int location, String newTypeMarkPattern) {
        Type type = getType(location);
        type.setTypeMarkPattern(newTypeMarkPattern);
        mTypeCodeAndTypeMarkMap.get(type.getTypeCode()).setPatternId(newTypeMarkPattern);
        mDatabaseManager.updateTypeMarkPattern(type.getTypeCode(), newTypeMarkPattern);
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
                mDatabaseManager.updateTypeSequence(type.getTypeCode(), i);
            }
        }
    }

    //****************PlanList & TypeList****************

    /** 获取单个类型的所有未完成的计划 */
    public List<Plan> getSingleTypeUcPlanList(String typeCode) {
        List<Plan> singleTypeUcPlanList = new ArrayList<>();
        for (Plan plan : planList) {
            if (plan.isCompleted()) {
                break;
            }
            if (plan.getTypeCode().equals(typeCode)) {
                singleTypeUcPlanList.add(plan);
            }
        }
        return singleTypeUcPlanList;
    }

    /** 获取某类型所有未完成计划在PlanList中的序号（更新所有计划列表时使用）*/
    public List<Integer> getSingleTypeUcPlanLocations(String typeCode) {
        List<Integer> singleTypeUcPlanLocations = new ArrayList<>();
        for (int i = 0; i < getPlanCount(); i++) {
            Plan plan = getPlan(i);
            if (plan.getCompletionTime() != 0) {
                break;
            }
            if (plan.getTypeCode().equals(typeCode)) {
                singleTypeUcPlanLocations.add(i);
            }
        }
        return singleTypeUcPlanLocations;
    }

    //****************TypeName & TypeMark****************

    /** 判断给定类型名称是否已使用过 */
    public boolean isTypeNameUsed(String typeName) {
        for (int i = 0; i < getTypeCount(); i++) {
            if (getType(i).getTypeName().equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    /** 判断给定类型颜色是否已使用过 */
    public boolean isTypeMarkColorUsed(String typeMarkColor) {
        for (int i = 0; i < getTypeCount(); i++) {
            if (getType(i).getTypeMarkColor().equals(typeMarkColor)) {
                return true;
            }
        }
        return false;
    }

    /** 判断给定类型图案是否已使用过 */
    public boolean isTypeMarkPatternUsed(String typeMarkPattern) {
        for (int i = 0; i < getTypeCount(); i++) {
            if (getType(i).getTypeMarkPattern().equals(typeMarkPattern)) {
                return true;
            }
        }
        return false;
    }

    /** 获取全部TypeMark颜色 */
    public List<TypeMarkColor> getTypeMarkColorList() {
        return mDatabaseManager.loadTypeMarkColor();
    }

    /** 获取可用的TypeMark颜色（添加类型时使用）*/
    public List<TypeMarkColor> getValidTypeMarkColorList() {
        List<TypeMarkColor> typeMarkColorList = mDatabaseManager.loadTypeMarkColor();
        for (int i = 0; i < typeMarkColorList.size(); i++) {
            if (isTypeMarkColorUsed(typeMarkColorList.get(i).getColorHex())) {
                //此颜色已被使用
                typeMarkColorList.remove(i);
                i--;
            }
        }
        return typeMarkColorList;
    }

    /** 获取可用的TypeMark颜色（编辑类型时使用）*/
    public List<TypeMarkColor> getValidTypeMarkColorList(String includedColorHex) {
        List<TypeMarkColor> typeMarkColorList = mDatabaseManager.loadTypeMarkColor();
        for (int i = 0; i < typeMarkColorList.size(); i++) {
            String colorHex = typeMarkColorList.get(i).getColorHex();
            if (isTypeMarkColorUsed(colorHex) && !colorHex.equals(includedColorHex)) {
                //此颜色已被使用，且不等于给定的颜色
                typeMarkColorList.remove(i);
                i--;
            }
        }
        return typeMarkColorList;
    }

    /** 数据库获取颜色名称 */
    public String getTypeMarkColorName(String colorHex) {
        String colorName = mDatabaseManager.queryTypeMarkColorNameByTypeMarkColorHex(colorHex);
        return colorName == null ? colorHex : colorName;
    }

    /** 获取一个随机的TypeMark颜色 */
    public String getRandomTypeMarkColor() {
        while (true) {
            String color = Util.makeColor();
            if (!isTypeMarkColorUsed(color)) {
                return color;
            }
        }
    }

    //****************UncompletedPlanCount****************

    /** 获取未完成计划的数量 */
    public int getUcPlanCount() {
        return uncompletedPlanCount;
    }

    /** 更新未完成计划的数量 */
    public void updateUcPlanCount(int variation) {
        uncompletedPlanCount += variation;
    }

    //****************TypeCodeAndTypeMarkMap****************

    /** 获取TypeCode和TypeMark的对应表 */
    public Map<String, TypeMark> getTypeCodeAndTypeMarkMap() {
        return mTypeCodeAndTypeMarkMap;
    }

    //****************UcPlanCountOfEachTypeMap****************

    /** 获取每个类型未完成计划的数量 */
    public Map<String, Integer> getUcPlanCountOfEachTypeMap() {
        return ucPlanCountOfEachTypeMap;
    }

    /** 更新每个类型未完成计划的数量（添加或删除计划时）*/
    public void updateUcPlanCountOfEachTypeMap(String typeCode, int variation) {
        Integer count = ucPlanCountOfEachTypeMap.get(typeCode);
        if (count == null) {
            count = 0;
        } else if (count == 1 && variation == -1) {
            //结果为0，需要删除该键
            ucPlanCountOfEachTypeMap.remove(typeCode);
            return;
        }
        ucPlanCountOfEachTypeMap.put(typeCode, count + variation);
    }

    /** 更新每个类型未完成计划的数量（修改计划时）*/
    public void updateUcPlanCountOfEachTypeMap(String fromTypeCode, String toTypeCode) {
        if (!fromTypeCode.equals(toTypeCode)) {
            updateUcPlanCountOfEachTypeMap(fromTypeCode, -1);
            updateUcPlanCountOfEachTypeMap(toTypeCode, 1);
        }
    }

    /** 查询某个类型是否有未完成的计划 */
    public boolean isUcPlanOfOneTypeExists(String typeCode) {
        return ucPlanCountOfEachTypeMap.containsKey(typeCode);
    }

    //****************初始化操作****************

    /** 在数据从数据库中加载完成后，初始化其他的数据对象 */
    private void initOtherDataUsingLists() {
        //Using planList
        for (Plan plan : planList) {
            if (plan.getCompletionTime() != 0) {
                //说明已经遍历到已完成的计划的部分了，可以不再遍历下去了
                break;
            }
            //初始化（计算）未完成计划的数量（仅一次）
            uncompletedPlanCount++;
            //初始化（计算）每个类型具有的未完成计划数量map（仅一次）
            updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), 1);
        }
        //Using typeList
        for (Type type : typeList) {
            //初始化TypeCode和TypeMark的对应表（仅一次）
            mTypeCodeAndTypeMarkMap.put(
                    type.getTypeCode(),
                    new TypeMark(type.getTypeMarkColor(), type.getTypeMarkPattern())
            );
        }
    }

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            planList.addAll(mDatabaseManager.loadPlan());
            typeList.addAll(mDatabaseManager.loadType());
            initOtherDataUsingLists();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //进入下一个状态
            dataStatus = STATUS_DATA_LOADED;

            LogUtil.d(LOG_TAG, "数据加载完毕");

            EventBus.getDefault().post(new DataLoadedEvent());
        }
    }
}
