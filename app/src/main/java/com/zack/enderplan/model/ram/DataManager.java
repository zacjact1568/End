package com.zack.enderplan.model.ram;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;

import com.zack.enderplan.R;
import com.zack.enderplan.application.App;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.bean.TypeMark;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private static final String LOG_TAG = "DataManager";

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

    private DatabaseDispatcher databaseDispatcher;
    private List<Plan> planList;
    private List<Type> typeList;
    private int uncompletedPlanCount;
    private List<TypeMark> typeMarkList;
    private Map<String, Integer> typeCodeAndColorResMap;
    private Map<String, Integer> typeMarkAndColorResMap;
    private Map<String, Integer> ucPlanCountOfEachTypeMap;
    //private boolean isDataLoaded = false;
    private DataStatus dataStatus;

    private static DataManager ourInstance = new DataManager();

    private DataManager() {
        databaseDispatcher = DatabaseDispatcher.getInstance();
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
            //isDataLoaded = true;
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
            //isDataLoaded = false;
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

    //添加计划到list TODO if 添加到最后？
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

    //交换list中两个类型的位置
    public void swapTypesInTypeList(int fromLocation, int toLocation) {
        Collections.swap(typeList, fromLocation, toLocation);
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

    /*//更新储存的提醒时间（位置未知时）
    public void updateReminderTime(String planCode, long newReminderTime) {
        for (int i = 0; i < planList.size(); i++) {
            Plan plan = planList.get(i);
            if (plan.getPlanCode().equals(planCode)) {
                //TODO 改成只遍历有reminder的plan
                plan.setReminderTime(newReminderTime);
                //反映到presenter层
                EventBus.getDefault().post(new ReminderTimeChangedEvent(i));
                break;
            }
        }
    }*/

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
        /*for (int i = 0; i < typeMarkResArray.length(); i++) {
            if (typeMarkResArray.getColor(i, 0) == Color.parseColor(typeMark)) {
                return typeMarkResArray.getResourceId(i, 0);
            }
        }*/
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

        /*for (int i = 0; i < getTypeMarkCount(); i++) {
            if (getTypeMark(i).getColorInt() == Color.parseColor(fromTypeMark) && i != toLocation) {
                //说明类型颜色有改变
                //以前的改为可用
                getTypeMark(i).setIsValid(true);
                //现在的改为不可用
                getTypeMark(toLocation).setIsValid(false);
                break;
            }
        }*/
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

    //添加类型码和类型颜色同其颜色资源的映射 TODO 改成maps
    public void putMappingInFindingColorResMap(String typeCode, String typeMark) {
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
            clearUcPlanCountOfOneType(typeCode);
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

    //删除某类型具有的未完成计划数量 TODO 方法名改成上面那样
    public void clearUcPlanCountOfOneType(String typeCode) {
        ucPlanCountOfEachTypeMap.remove(typeCode);
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
            planList.addAll(databaseDispatcher.loadPlan());
            typeList.addAll(databaseDispatcher.loadType());
            initOtherDataUsingLists();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //进入下一个状态
            dataStatus = DataStatus.STATUS_DATA_LOADED;

            LogUtil.d(LOG_TAG, "数据加载完毕");

            EventBus.getDefault().post(new DataLoadedEvent());
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
    }
}
