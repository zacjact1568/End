package com.zack.enderplan.manager;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;

import com.zack.enderplan.R;
import com.zack.enderplan.application.EnderPlanApp;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.bean.TypeMark;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.ReminderTimeChangedEvent;
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

    private EnderPlanDB enderplanDB;
    private List<Plan> planList;
    private List<Type> typeList;
    private int uncompletedPlanCount = 0;
    private List<TypeMark> typeMarkList;
    private Map<String, Integer> typeCodeAndColorResMap;
    private Map<String, Integer> typeMarkAndColorResMap;
    private Map<String, Integer> planCountOfEachTypeMap;
    private boolean isAlive = false;

    private static DataManager ourInstance = new DataManager();

    private DataManager() {
        enderplanDB = EnderPlanDB.getInstance();
        planList = new ArrayList<>();
        typeList = new ArrayList<>();
        typeMarkList = new ArrayList<>();
        typeCodeAndColorResMap = new HashMap<>();
        typeMarkAndColorResMap = new HashMap<>();
        planCountOfEachTypeMap = new HashMap<>();
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    //从数据库加载
    public void loadFromDatabase() {
        if (!isAlive) {
            //第一次或进程被杀后再次打开app，需要从数据库加载
            isAlive = true;
            new LoadDataTask().execute();
        }
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
            if (plan.getTypeCode().equals(typeCode) && plan.getCompletionTime() == 0) {
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

    //更新储存的提醒时间（位置未知时）
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
    }

    //获取当前类型的数量
    public int getTypeCount() {
        return typeList.size();
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
                return;
            }
        }
    }

    //更新类型颜色list（修改）
    public void updateTypeMarkList(String fromTypeMark, String toTypeMark) {
        //
    }

    //初始化类型颜色list中的选中指示变量
    public void clearTypeMarkSelectionStatus(int location) {
        getTypeMark(location).setIsSelected(false);
    }

    //获取类型码与其颜色资源的映射表
    public Map<String, Integer> getTypeCodeAndColorResMap() {
        return typeCodeAndColorResMap;
    }

    //获取类型颜色与其颜色资源的映射表
    public Map<String, Integer> getTypeMarkAndColorResMap() {
        return typeMarkAndColorResMap;
    }

    //更新类型码和类型颜色同其颜色资源的映射表
    public void updateFindingColorResMap(String typeCode, String typeMark) {
        //TODO ...
    }

    //添加类型码和类型颜色同其颜色资源的映射
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

    //获取每个类型具有的计划数量map
    public Map<String, Integer> getPlanCountOfEachTypeMap() {
        return planCountOfEachTypeMap;
    }

    //更新每个类型具有的计划数量map（添加或删除计划时）TODO 在方法名后面添加Map
    public void updatePlanCountOfEachType(String typeCode, int variation) {
        Integer count = planCountOfEachTypeMap.get(typeCode);
        if (count == null) {
            count = 0;
        } else if (count == 1 && variation == -1) {
            //结果为0，需要删除该键
            clearPlanCountOfOneType(typeCode);
            return;
        }
        planCountOfEachTypeMap.put(typeCode, count + variation);
    }

    //更新每个类型具有的计划数量map（修改计划时）
    public void updatePlanCountOfEachType(String fromTypeCode, String toTypeCode) {
        if (!fromTypeCode.equals(toTypeCode)) {
            updatePlanCountOfEachType(fromTypeCode, -1);
            updatePlanCountOfEachType(toTypeCode, 1);
        }
    }

    //每个类型具有的计划数量map里是否有该类型
    public boolean isPlanCountOfOneTypeExists(String typeCode) {
        return planCountOfEachTypeMap.containsKey(typeCode);
    }

    //删除某类型具有的计划数量 TODO 方法名改成上面那样
    public void clearPlanCountOfOneType(String typeCode) {
        planCountOfEachTypeMap.remove(typeCode);
    }

    //用lists初始化一些数据对象
    private void initOtherDataUsingLists() {
        //Using planList
        for (Plan plan : planList) {
            //初始化（计算）未完成计划的数量（仅一次）
            if (plan.getCreationTime() != 0) {
                uncompletedPlanCount++;
            }
            //初始化（计算）每个类型具有的计划数量map（仅一次）
            updatePlanCountOfEachType(plan.getTypeCode(), 1);
        }

        //Using typeMarkResArray & typeList
        TypedArray typeMarkResArray = EnderPlanApp.getGlobalContext().getResources().obtainTypedArray(R.array.type_marks);
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
            planList.addAll(enderplanDB.loadPlan());
            typeList.addAll(enderplanDB.loadType());
            initOtherDataUsingLists();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EventBus.getDefault().post(new DataLoadedEvent());
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
    }
}
