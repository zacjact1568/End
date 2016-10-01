package com.zack.enderplan.model;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;

import com.zack.enderplan.R;
import com.zack.enderplan.App;
import com.zack.enderplan.utility.ReminderManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.utility.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private static final String LOG_TAG = "DataManager";

    //TODO 将enum改成常量
    /** 数据加载状态标记 */
    public enum DataStatus {
        /** 状态标记：还未初始化存储的数据结构 */
        STATUS_STRUCT_NOT_INIT,
        /** 状态标记：已初始化数据结构，但还未加载数据（数据为空） */
        STATUS_DATA_NOT_LOAD,
        /** 状态标记：正在子线程中从数据库加载数据，此时应该避免再开加载数据的线程 */
        STATUS_DATA_ON_LOAD,
        /** 状态标记：数据加载完成 */
        STATUS_DATA_LOADED
    }

    private DatabaseManager mDatabaseManager;
    private ReminderManager mReminderManager;
    private List<Plan> planList;
    private List<Type> typeList;
    private int uncompletedPlanCount;
    private List<TypeMark> typeMarkList;
    private Map<String, Integer> typeCodeAndColorResMap;
    private Map<String, Integer> typeMarkAndColorResMap;
    private Map<String, Integer> ucPlanCountOfEachTypeMap;
    private DataStatus dataStatus;

    private static DataManager ourInstance = new DataManager();

    private DataManager() {
        mDatabaseManager = DatabaseManager.getInstance();
        mReminderManager = ReminderManager.getInstance();

        //初始化状态
        dataStatus = DataStatus.STATUS_STRUCT_NOT_INIT;

        LogUtil.d(LOG_TAG, "DataManager实例化完成");
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    /** 初始化数据存储结构<br>NOTE：必须经过HomeActivity的构造才能执行此方法 */
    public void initDataStruct() {
        if (dataStatus == DataStatus.STATUS_STRUCT_NOT_INIT) {
            planList = new ArrayList<>();
            typeList = new ArrayList<>();
            uncompletedPlanCount = 0;
            typeMarkList = new ArrayList<>();
            typeCodeAndColorResMap = new HashMap<>();
            typeMarkAndColorResMap = new HashMap<>();
            ucPlanCountOfEachTypeMap = new HashMap<>();

            //进入下一个状态
            dataStatus = DataStatus.STATUS_DATA_NOT_LOAD;

            LogUtil.d(LOG_TAG, "数据结构初始化完成");
        }
    }

    /** 从数据库加载<br>NOTE：必须经过AllPlansFragment的构造才能执行此方法 */
    public void loadFromDatabase() {
        if (dataStatus == DataStatus.STATUS_DATA_NOT_LOAD) {
            //进入下一个状态
            dataStatus = DataStatus.STATUS_DATA_ON_LOAD;
            new LoadDataTask().execute();
        }
    }

    /** 清除数据存储结构中的数据 */
    public void clearData() {
        if (dataStatus == DataStatus.STATUS_DATA_LOADED) {
            planList.clear();
            typeList.clear();
            uncompletedPlanCount = 0;
            typeMarkList.clear();
            typeCodeAndColorResMap.clear();
            typeMarkAndColorResMap.clear();
            ucPlanCountOfEachTypeMap.clear();

            //将状态置为未加载
            dataStatus = DataStatus.STATUS_DATA_NOT_LOAD;
        }
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
        mDatabaseManager.editContent(plan.getPlanCode(), newContent);
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
        mDatabaseManager.editTypeOfPlan(plan.getPlanCode(), newTypeCode);
    }

    /** 编辑计划星标状态 */
    public void notifyStarStatusChanged(int location) {
        Plan plan = getPlan(location);
        int newStarStatus = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED ?
                Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
        plan.setStarStatus(newStarStatus);
        mDatabaseManager.editStarStatus(plan.getPlanCode(), newStarStatus);
    }

    /** 编辑计划截止时间 */
    public void notifyDeadlineChanged(int location, long newDeadline) {
        Plan plan = getPlan(location);
        plan.setDeadline(newDeadline);
        mDatabaseManager.editDeadline(plan.getPlanCode(), newDeadline);
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
        mDatabaseManager.editReminderTime(plan.getPlanCode(), newReminderTime);
    }

    /** 编辑计划完成状态 */
    public void notifyPlanStatusChanged(int location) {
        Plan plan = getPlan(location);

        //旧的完成状态
        boolean isCompletedPast = plan.getCompletionTime() != 0;
        //更新Maps
        updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), isCompletedPast ? 1 : -1);
        updateUcPlanCount(isCompletedPast ? 1 : -1);

        if (plan.getReminderTime() != 0) {
            //有设置提醒，需要移除
            mReminderManager.cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            mDatabaseManager.editReminderTime(plan.getPlanCode(), 0);
        }

        //操作list
        removeFromPlanList(location);

        long currentTimeMillis = System.currentTimeMillis();
        long newCreationTime = isCompletedPast ? currentTimeMillis : 0;
        long newCompletionTime = isCompletedPast ? 0 : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompletedPast ? 0 : getUcPlanCount();
        addToPlanList(newPosition, plan);

        mDatabaseManager.editPlanStatus(plan.getPlanCode(), newCreationTime, newCompletionTime);
    }

    /** 创建类型 (Inserted at the end of typeList) */
    public void notifyTypeCreated(Type newType) {
        notifyTypeCreated(getTypeCount(), newType);
    }

    /** 创建类型 (Inserted at a specified location of typeList) */
    public void notifyTypeCreated(int location, Type newType) {
        addToTypeList(location, newType);
        updateTypeMarkList(newType.getTypeMark());
        putMappingInFindingColorResMaps(newType.getTypeCode(), newType.getTypeMark());
        //储存至数据库
        mDatabaseManager.saveType(newType);
    }

    /** 删除类型 */
    public void notifyTypeDeleted(int location) {
        Type type = getType(location);
        removeFromTypeList(location);
        //删除了一个类型，需要更新可用的类型颜色
        updateTypeMarkList(type.getTypeMark());
        removeMappingInFindingColorResMap(type.getTypeCode(), type.getTypeMark());
        mDatabaseManager.deleteType(type.getTypeCode());
    }

    /** 编辑类型 */
    public void notifyTypeEdited(int location, String newTypeName, String oldTypeMark, String newTypeMark) {
        Type type = getType(location);
        updateTypeMarkList(oldTypeMark, newTypeMark);
        updateFindingColorResMap(type.getTypeCode(), type.getTypeMark(), newTypeMark);
        //更新type（list中的type实际上也更新了）
        type.setTypeName(newTypeName);
        type.setTypeMark(newTypeMark);
        //更新数据库
        mDatabaseManager.editTypeBase(type.getTypeCode(), type.getTypeName(), type.getTypeMark());
    }

    /** 重排类型 (集中重排) */
    public void notifyTypeSequenceRearranged() {
        for (int i = 0; i < getTypeCount(); i++) {
            Type type = getType(i);
            if (type.getTypeSequence() != i) {
                //更新typeList
                //在移动typeList的item的时候只是交换了items在list中的位置，并没有改变item中的type_sequence
                type.setTypeSequence(i);
                //更新数据库
                mDatabaseManager.editTypeSequence(type.getTypeCode(), i);
            }
        }
    }

    /** 获取当前状态 */
    public DataStatus getDataStatus() {
        return dataStatus;
    }

    //获取planList
    public List<Plan> getPlanList() {
        return planList;
    }

    //获取typeList
    public List<Type> getTypeList() {
        return typeList;
    }

    //获取某个计划
    public Plan getPlan(int location) {
        return planList.get(location);
    }

    //获取某个类型
    public Type getType(int location) {
        return typeList.get(location);
    }

    //添加计划到list
    public void addToPlanList(int location, Plan newPlan) {
        planList.add(location, newPlan);
    }

    //添加类型到list
    public void addToTypeList(int location, Type newType) {
        typeList.add(location, newType);
    }

    //添加类型到list的最后
    public void addToTypeList(Type newType) {
        typeList.add(newType);
    }

    //从list删除计划
    public void removeFromPlanList(int location) {
        planList.remove(location);
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

    /** 获取最近创建的计划位置 */
    public int getRecentlyCreatedPlanLocation() {
        return 0;
    }

    /** 获取最近创建的计划 */
    public Plan getRecentlyCreatedPlan() {
        return getPlan(getRecentlyCreatedPlanLocation());
    }

    /** 获取最近创建的类型位置 */
    public int getRecentlyCreatedTypeLocation() {
        return getTypeCount() - 1;
    }

    /** 获取最近创建的类型 */
    public Type getRecentlyCreatedType() {
        return getType(getRecentlyCreatedTypeLocation());
    }

    //获取单个类型的所有未完成的计划
    public List<Plan> getSingleTypeUcPlanList(String typeCode) {
        List<Plan> singleTypeUcPlanList = new ArrayList<>();
        for (Plan plan : planList) {
            if (plan.getCompletionTime() != 0) {
                break;
            }
            if (plan.getTypeCode().equals(typeCode)) {
                singleTypeUcPlanList.add(plan);
            }
        }
        return singleTypeUcPlanList;
    }

    //获取未完成计划的数量
    public int getUcPlanCount() {
        return uncompletedPlanCount;
    }

    //更新未完成计划的数量
    public void updateUcPlanCount(int variation) {
        uncompletedPlanCount += variation;
    }

    //获取当前计划的数量
    public int getPlanCount() {
        return planList.size();
    }

    //获取当前类型的数量
    public int getTypeCount() {
        return typeList.size();
    }

    //获取类型颜色的数量
    public int getTypeMarkCount() {
        return typeMarkList.size();
    }

    //获取计划在list中的序号
    public int getPlanLocationInPlanList(String planCode) {
        for (int i = 0; i < getPlanCount(); i++) {
            if (getPlan(i).getPlanCode().equals(planCode)) {
                return i;
            }
        }
        return 0;
    }

    //获取类型在list中的序号
    public int getTypeLocationInTypeList(String typeCode) {
        for (int i = 0; i < getTypeCount(); i++) {
            if (getType(i).getTypeCode().equals(typeCode)) {
                return i;
            }
        }
        return 0;
    }

    //获取类型颜色在list中的序号
    public int getTypeMarkLocationInTypeMarkList(String typeMark) {
        for (int i = 0; i < getTypeMarkCount(); i++) {
            if (getTypeMark(i).getColorInt() == Color.parseColor(typeMark)) {
                return i;
            }
        }
        return 0;
    }

    //获取所有具有给定类型的未完成计划的序号
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

    //根据类型颜色寻找颜色资源
    public int findColorResByTypeMark(String typeMarkStr) {
        for (TypeMark typeMark : typeMarkList) {
            if (typeMark.getColorInt() == Color.parseColor(typeMarkStr)) {
                return typeMark.getResId();
            }
        }
        return 0;
    }

    //根据类型码寻找类型名
    public String findTypeNameByTypeCode(String typeCode) {
        for (Type type : typeList) {
            if (type.getTypeCode().equals(typeCode)) {
                return type.getTypeName();
            }
        }
        return "";
    }

    //根据类型码寻找类型颜色
    public String findTypeMarkByTypeCode(String typeCode) {
        for (Type type : typeList) {
            if (type.getTypeCode().equals(typeCode)) {
                return type.getTypeMark();
            }
        }
        return "";
    }

    //获取类型颜色
    public TypeMark getTypeMark(int location) {
        return typeMarkList.get(location);
    }

    //获取所有可选的类型颜色list
    public List<TypeMark> getTypeMarkList() {
        return typeMarkList;
    }

    //更新类型颜色list（添加或删除）
    public void updateTypeMarkList(int location) {
        TypeMark typeMark = getTypeMark(location);
        typeMark.setIsValid(!typeMark.isValid());
    }

    //更新类型颜色list（添加或删除）
    public void updateTypeMarkList(String typeMarkStr) {
        for (TypeMark typeMark : typeMarkList) {
            if (typeMark.getColorInt() == Color.parseColor(typeMarkStr)) {
                typeMark.setIsValid(!typeMark.isValid());
                break;
            }
        }
    }

    //更新类型颜色list（修改）
    public void updateTypeMarkList(int fromLocation, int toLocation) {
        if (fromLocation != toLocation) {
            //若类型颜色有改变，将以前的改为可用
            getTypeMark(fromLocation).setIsValid(true);
        }
        //现在（也可以是以前）的改为不可用
        getTypeMark(toLocation).setIsValid(false);
    }

    /** 更新类型颜色list (via type marks) */
    public void updateTypeMarkList(String fromTypeMark, String toTypeMark) {
        updateTypeMarkList(
                getTypeMarkLocationInTypeMarkList(fromTypeMark),
                getTypeMarkLocationInTypeMarkList(toTypeMark)
        );
    }

    //获取类型码与其颜色资源的映射表
    public Map<String, Integer> getTypeCodeAndColorResMap() {
        return typeCodeAndColorResMap;
    }

    //获取类型颜色与其颜色资源的映射表
    public Map<String, Integer> getTypeMarkAndColorResMap() {
        return typeMarkAndColorResMap;
    }

    //更新类型码和类型颜色同其颜色资源的映射
    public void updateFindingColorResMap(String typeCode, String fromTypeMark, String toTypeMark) {
        if (!fromTypeMark.equals(toTypeMark)) {
            int colorRes = findColorResByTypeMark(toTypeMark);
            typeCodeAndColorResMap.put(typeCode, colorRes);
            typeMarkAndColorResMap.remove(fromTypeMark);
            typeMarkAndColorResMap.put(toTypeMark, colorRes);
        }
    }

    //添加类型码和类型颜色同其颜色资源的映射
    public void putMappingInFindingColorResMaps(String typeCode, String typeMark) {
        int colorRes = findColorResByTypeMark(typeMark);
        typeCodeAndColorResMap.put(typeCode, colorRes);
        typeMarkAndColorResMap.put(typeMark, colorRes);
    }

    //删除类型码和类型颜色同其颜色资源的映射
    public void removeMappingInFindingColorResMap(String typeCode, String typeMark) {
        typeCodeAndColorResMap.remove(typeCode);
        typeMarkAndColorResMap.remove(typeMark);
    }

    //获取每个类型具有的未完成计划数量map
    public Map<String, Integer> getUcPlanCountOfEachTypeMap() {
        return ucPlanCountOfEachTypeMap;
    }

    //更新每个类型具有的未完成计划数量map（添加或删除计划时）
    public void updateUcPlanCountOfEachTypeMap(String typeCode, int variation) {
        Integer count = ucPlanCountOfEachTypeMap.get(typeCode);
        if (count == null) {
            count = 0;
        } else if (count == 1 && variation == -1) {
            //结果为0，需要删除该键
            clearUcPlanCountOfOneTypeMap(typeCode);
            return;
        }
        ucPlanCountOfEachTypeMap.put(typeCode, count + variation);
    }

    //更新每个类型具有的未完成计划数量map（修改计划时）
    public void updateUcPlanCountOfEachTypeMap(String fromTypeCode, String toTypeCode) {
        if (!fromTypeCode.equals(toTypeCode)) {
            updateUcPlanCountOfEachTypeMap(fromTypeCode, -1);
            updateUcPlanCountOfEachTypeMap(toTypeCode, 1);
        }
    }

    //每个类型具有的未完成计划数量map里是否有该类型
    public boolean isUcPlanCountOfOneTypeExists(String typeCode) {
        return ucPlanCountOfEachTypeMap.containsKey(typeCode);
    }

    //删除某类型具有的未完成计划数量
    public void clearUcPlanCountOfOneTypeMap(String typeCode) {
        ucPlanCountOfEachTypeMap.remove(typeCode);
    }

    /** 获取指定位置计划的完成状态 TODO delete this method */
    public boolean isPlanCompleted(int location) {
        return getPlan(location).getCompletionTime() != 0;
    }

    //用lists初始化一些数据对象
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

        //TODO 这一段放到需要时（新建type时）才用
        //Using typeMarkResArray & typeList
        TypedArray typeMarkResArray = App.getGlobalContext().getResources().obtainTypedArray(R.array.type_marks);
        for (int i = 0; i < typeMarkResArray.length(); i++) {
            //组装所有可选的类型颜色list
            int colorInt = typeMarkResArray.getColor(i, 0);
            TypeMark typeMark = new TypeMark(typeMarkResArray.getResourceId(i, 0), colorInt, false);
            for (Type type : typeList) {
                if (Color.parseColor(type.getTypeMark()) == colorInt) {
                    //说明此种颜色已经被使用
                    typeMark.setIsValid(false);
                    break;
                }
            }
            typeMarkList.add(typeMark);
        }
        typeMarkResArray.recycle();

        //Using typeList NOTE: 不能和上面那一步交换位置
        for (Type type : typeList) {
            //初始化（计算）类型码和类型颜色同其颜色资源的映射表（仅一次）
            int colorRes = findColorResByTypeMark(type.getTypeMark());
            typeCodeAndColorResMap.put(type.getTypeCode(), colorRes);
            typeMarkAndColorResMap.put(type.getTypeMark(), colorRes);
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
            dataStatus = DataStatus.STATUS_DATA_LOADED;

            LogUtil.d(LOG_TAG, "数据加载完毕");

            EventBus.getDefault().post(new DataLoadedEvent());
        }
    }
}
