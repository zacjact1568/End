package com.zack.enderplan.model;

import com.zack.enderplan.util.ColorUtil;
import com.zack.enderplan.util.CommonUtil;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private DatabaseManager mDatabaseManager;
    private List<Plan> mPlanList;
    private List<Type> mTypeList;
    private int mUcPlanCount;
    private Map<String, TypeMark> mTypeCodeAndTypeMarkMap;
    private Map<String, Integer> mUcPlanCountOfEachTypeMap;
    private boolean isDataLoaded = false;

    private static DataManager ourInstance = new DataManager();

    private DataManager() {
        mDatabaseManager = DatabaseManager.getInstance();
        mPlanList = new ArrayList<>();
        mTypeList = new ArrayList<>();
        mUcPlanCount = 0;
        mTypeCodeAndTypeMarkMap = new HashMap<>();
        mUcPlanCountOfEachTypeMap = new HashMap<>();

        loadFromDatabase();
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    /** 从数据库中加载 */
    private void loadFromDatabase() {
        mDatabaseManager.loadDataAsync(new DatabaseManager.DataLoadedCallback() {
            @Override
            public void onDataLoaded(List<Plan> planList, List<Type> typeList) {
                //以下代码都是在主线程中执行的，所以如果在主线程访问mPlanList或mTypeList，只有两种情况：空或满数据
                mPlanList.addAll(planList);
                mTypeList.addAll(typeList);
                for (Plan plan : mPlanList) {
                    //说明已经遍历到已完成的计划的部分了，可以不再遍历下去了
                    if (plan.isCompleted()) break;
                    //初始化（计算）未完成计划的数量（仅一次）
                    mUcPlanCount++;
                    //初始化（计算）每个类型具有的未完成计划数量map（仅一次）
                    updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), 1);
                }
                for (Type type : mTypeList) {
                    //初始化TypeCode和TypeMark的对应表（仅一次）
                    mTypeCodeAndTypeMarkMap.put(
                            type.getTypeCode(),
                            new TypeMark(type.getTypeMarkColor(), type.getTypeMarkPattern())
                    );
                }
                //已加载数据
                isDataLoaded = true;
                //发送事件
                EventBus.getDefault().post(new DataLoadedEvent());
            }
        });
    }

    /** 获取当前数据加载状态 */
    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    //****************PlanList****************

    //获取planList
    public List<Plan> getPlanList() {
        return mPlanList;
    }

    //获取某个计划
    public Plan getPlan(int location) {
        return mPlanList.get(location);
    }

    //添加计划到list
    public void addToPlanList(int location, Plan newPlan) {
        mPlanList.add(location, newPlan);
    }

    //从list删除计划
    public void removeFromPlanList(int location) {
        mPlanList.remove(location);
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
            mDatabaseManager.updateCompletionTime(onePlan.getPlanCode(), onePlan.getCompletionTime());
            mDatabaseManager.updateCompletionTime(anotherPlan.getPlanCode(), anotherPlan.getCompletionTime());
        } else if (!onePlan.isCompleted() && !anotherPlan.isCompleted()) {
            //未完成->未完成，交换creation time
            long oneCreationTime = onePlan.getCreationTime();
            onePlan.setCreationTime(anotherPlan.getCreationTime());
            anotherPlan.setCreationTime(oneCreationTime);
            mDatabaseManager.updateCreationTime(onePlan.getPlanCode(), onePlan.getCreationTime());
            mDatabaseManager.updateCreationTime(anotherPlan.getPlanCode(), anotherPlan.getCreationTime());
        } else {
            return;
        }
        Collections.swap(mPlanList, oneLocation, anotherLocation);
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

    /** 判断某类型是否有对应的计划 */
    public boolean isPlanOfOneTypeExists(String typeCode) {
        for (Plan plan : mPlanList) {
            if (plan.getTypeCode().equals(typeCode)) {
                return true;
            }
        }
        return false;
    }

    /** 获取某类型的计划数量 */
    public int getPlanCountOfOneType(String typeCode) {
        int count = 0;
        for (Plan plan : mPlanList) {
            if (plan.getTypeCode().equals(typeCode)) {
                count++;
            }
        }
        return count;
    }

    /** 将一个类型中的所有计划迁移到另一个类型 */
    public void migratePlan(String fromTypeCode, String toTypeCode) {
        if (fromTypeCode.equals(toTypeCode)) return;
        for (int i = 0; i < getPlanCount(); i++) {
            if (getPlan(i).getTypeCode().equals(fromTypeCode)) {
                notifyTypeOfPlanChanged(i, fromTypeCode, toTypeCode);
            }
        }
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
        addToPlanList(location, newPlan);
        if (!newPlan.isCompleted()) {
            //说明该计划还未完成
            //更新未完成计划的数量
            updateUcPlanCount(1);
            //更新每个类型具有的计划数量map
            updateUcPlanCountOfEachTypeMap(newPlan.getTypeCode(), 1);
        }
        //设置提醒
        if (newPlan.hasReminder()) {
            //有提醒，需要设置
            SystemUtil.setReminder(newPlan.getPlanCode(), newPlan.getReminderTime());
        }
        //存储至数据库
        mDatabaseManager.savePlan(newPlan);
    }

    /** 删除计划 */
    public void notifyPlanDeleted(int location) {
        Plan plan = getPlan(location);
        if (!plan.isCompleted()) {
            //That means this plan is uncompleted
            updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), -1);
            updateUcPlanCount(-1);
        }
        if (plan.hasReminder()) {
            //This plan has registered a reminder that need to be canceled
            SystemUtil.setReminder(plan.getPlanCode(), Constant.UNDEFINED_TIME);
        }
        removeFromPlanList(location);
        //更新数据库
        mDatabaseManager.deletePlan(plan.getPlanCode());
    }

    //TODO notify***全部改为动宾形式
    /** 删除某类型的全部计划 */
    public void deletePlanOfOneType(String typeCode) {
        for (int i = 0; i < getPlanCount(); i++) {
            if (getPlan(i).getTypeCode().equals(typeCode)) {
                notifyPlanDeleted(i);
                i--;
            }
        }
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
        if (!plan.isCompleted()) {
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
        plan.invertStarStatus();
        mDatabaseManager.updateStarStatus(plan.getPlanCode(), plan.getStarStatus());
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
        SystemUtil.setReminder(plan.getPlanCode(), newReminderTime);
        plan.setReminderTime(newReminderTime);
        mDatabaseManager.updateReminderTime(plan.getPlanCode(), newReminderTime);
    }

    /** 编辑计划完成状态 */
    public void notifyPlanStatusChanged(int location) {
        Plan plan = getPlan(location);

        //旧的完成状态
        boolean isCompletedPast = plan.isCompleted();
        //更新Maps
        updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), isCompletedPast ? 1 : -1);
        updateUcPlanCount(isCompletedPast ? 1 : -1);

        //操作list
        removeFromPlanList(location);

        long currentTimeMillis = System.currentTimeMillis();
        long newCreationTime = isCompletedPast ? currentTimeMillis : Constant.UNDEFINED_TIME;
        long newCompletionTime = isCompletedPast ? Constant.UNDEFINED_TIME : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompletedPast ? 0 : getUcPlanCount();
        addToPlanList(newPosition, plan);

        mDatabaseManager.updatePlanStatus(plan.getPlanCode(), newCreationTime, newCompletionTime);
    }

    /**
     * 将已过时的提醒移除<br>
     * 在某些rom中，reminder不能在指定时间触发，调用此方法移除这些过时的reminder
     */
    public void removeExpiredReminders() {
        for (int i = 0; i < getPlanCount(); i++) {
            Plan plan = getPlan(i);
            //说明已经遍历到已完成的计划的部分了，可以不再遍历下去了
            if (plan.isCompleted()) break;
            if (plan.hasReminder() && !TimeUtil.isFutureTime(plan.getReminderTime())) {
                //有reminder且已过时
                notifyReminderTimeChanged(i, Constant.UNDEFINED_TIME);
            }
        }
    }

    //****************TypeList****************

    //获取typeList
    public List<Type> getTypeList() {
        return mTypeList;
    }

    /** 获取一个新的typeList，不包含指定的type */
    public List<Type> getTypeList(String exclude) {
        List<Type> typeList = new ArrayList<>();
        for (Type type : this.mTypeList) {//TODO 去掉this
            if (!type.getTypeCode().equals(exclude)) {
                typeList.add(type);
            }
        }
        return typeList;
    }

    //获取某个类型
    public Type getType(int location) {
        return mTypeList.get(location);
    }

    //添加类型到list
    public void addToTypeList(int location, Type newType) {
        mTypeList.add(location, newType);
    }

    //添加类型到list的最后
    public void addToTypeList(Type newType) {
        mTypeList.add(newType);
    }

    //从list删除类型
    public void removeFromTypeList(int location) {
        mTypeList.remove(location);
    }

    /** 移动类型列表中的元素 */
    public void moveTypeInTypeList(int fromLocation, int toLocation) {
        if (fromLocation < toLocation) {
            for (int i = fromLocation; i < toLocation; i++) {
                Collections.swap(mTypeList, i, i + 1);
            }
        } else {
            for (int i = fromLocation; i > toLocation; i--) {
                Collections.swap(mTypeList, i, i - 1);
            }
        }
    }

    /** 交换类型列表中的两个元素 */
    public void swapTypesInTypeList(int oneLocation, int anotherLocation) {
        Type oneType = getType(oneLocation);
        Type anotherType = getType(anotherLocation);
        int oneTypeSequence = oneType.getTypeSequence();
        oneType.setTypeSequence(anotherType.getTypeSequence());
        anotherType.setTypeSequence(oneTypeSequence);
        mDatabaseManager.updateTypeSequence(oneType.getTypeCode(), oneType.getTypeSequence());
        mDatabaseManager.updateTypeSequence(anotherType.getTypeCode(), anotherType.getTypeSequence());
        Collections.swap(mTypeList, oneLocation, anotherLocation);
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

    /** 创建类型 (Inserted at the end of mTypeList) */
    public void notifyTypeCreated(Type newType) {
        notifyTypeCreated(getTypeCount(), newType);
    }

    /** 创建类型 (Inserted at a specified location of mTypeList) */
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
        //删除对应的计划
        deletePlanOfOneType(type.getTypeCode());
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
        mTypeCodeAndTypeMarkMap.get(type.getTypeCode()).setPatternFn(newTypeMarkPattern);
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

    /** 判断给定类型是否已使用过 */
    public boolean isTypeMarkUsed(String typeMarkColor, String typeMarkPattern) {
        for (Type type : mTypeList) {
            if (CommonUtil.isObjectEqual(type.getTypeMarkPattern(), typeMarkPattern) && type.getTypeMarkColor().equals(typeMarkColor)) {
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

    /** 判断给定类型图案是否已使用过 */
    public boolean isTypeMarkPatternUsed(String typeMarkPattern) {
        for (Type type : mTypeList) {
            if (CommonUtil.isObjectEqual(type.getTypeMarkPattern(), typeMarkPattern)) {
                return true;
            }
        }
        return false;
    }

    /** 获取全部TypeMark颜色 */
    public List<TypeMarkColor> getTypeMarkColorList() {
        return mDatabaseManager.loadTypeMarkColor();
    }

    /** 获取全部TypeMark图案 */
    public List<TypeMarkPattern> getTypeMarkPatternList() {
        return mDatabaseManager.loadTypeMarkPattern();
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

    /** 数据库获取图案名称 */
    public String getTypeMarkPatternName(String patternFn) {
        return patternFn == null ? null : mDatabaseManager.queryTypeMarkPatternNameByTypeMarkPatternFn(patternFn);
    }

    /** 获取一个随机的TypeMark颜色 */
    public String getRandomTypeMarkColor() {
        while (true) {
            String color = ColorUtil.makeColor();
            if (!isTypeMarkColorUsed(color)) {
                return color;
            }
        }
    }

    //****************UncompletedPlanCount****************

    /** 获取未完成计划的数量 */
    public int getUcPlanCount() {
        return mUcPlanCount;
    }

    /** 更新未完成计划的数量 */
    public void updateUcPlanCount(int variation) {
        mUcPlanCount += variation;
    }

    //****************TypeCodeAndTypeMarkMap****************

    /** 获取TypeCode和TypeMark的对应表 */
    public Map<String, TypeMark> getTypeCodeAndTypeMarkMap() {
        return mTypeCodeAndTypeMarkMap;
    }

    //****************UcPlanCountOfEachTypeMap****************

    /** 获取每个类型未完成计划的数量 */
    public Map<String, Integer> getUcPlanCountOfEachTypeMap() {
        return mUcPlanCountOfEachTypeMap;
    }

    /** 获取指定类型未完成计划的数量 */
    public int getUcPlanCountOfOneType(String typeCode) {
        return mUcPlanCountOfEachTypeMap.get(typeCode);
    }

    /** 更新每个类型未完成计划的数量（添加或删除计划时）*/
    public void updateUcPlanCountOfEachTypeMap(String typeCode, int variation) {
        Integer count = mUcPlanCountOfEachTypeMap.get(typeCode);
        if (count == null) {
            count = 0;
        } else if (count == 1 && variation == -1) {
            //结果为0，需要删除该键
            mUcPlanCountOfEachTypeMap.remove(typeCode);
            return;
        }
        mUcPlanCountOfEachTypeMap.put(typeCode, count + variation);
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
        return mUcPlanCountOfEachTypeMap.containsKey(typeCode);
    }
}
