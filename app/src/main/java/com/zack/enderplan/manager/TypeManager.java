package com.zack.enderplan.manager;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.Log;

import com.zack.enderplan.R;
import com.zack.enderplan.application.EnderPlanApp;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.TypeMark;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeManager {

    private List<Type> typeList;
    private TypedArray typeMarks;
    private Map<String, Integer> planCountOfEachTypeMap;
    private boolean isAlive = false;

    private static TypeManager typeManager;

    private TypeManager() {
        typeList = EnderPlanDB.getInstance().loadType();
        typeMarks = EnderPlanApp.getGlobalContext().getResources().obtainTypedArray(R.array.type_marks);
        planCountOfEachTypeMap = new HashMap<>();
    }

    public synchronized static TypeManager getInstance() {
        if (typeManager == null) {
            typeManager = new TypeManager();
        }
        return typeManager;
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public int getTypeCount() {
        return typeList.size();
    }

    public int findColorResByTypeMark(String typeMark) {
        for (int i = 0; i < typeMarks.length(); i++) {
            if (typeMarks.getColor(i, 0) == Color.parseColor(typeMark)) {
                return typeMarks.getResourceId(i, 0);
            }
        }
        typeMarks.recycle();
        return 0;
    }

    public String findTypeNameByTypeCode(String typeCode) {
        for (Type type : typeList) {
            if (type.getTypeCode().equals(typeCode)) {
                return type.getTypeName();
            }
        }
        return "";
    }

    public String findTypeMarkByTypeCode(String typeCode) {
        for (Type type : typeList) {
            if (type.getTypeCode().equals(typeCode)) {
                return type.getTypeMark();
            }
        }
        return "";
    }

    public List<TypeMark> getTypeMarkList() {
        List<TypeMark> typeMarkList = new ArrayList<>();
        for (int i = 0; i < typeMarks.length(); i++) {
            TypeMark typeMark = new TypeMark(typeMarks.getResourceId(i, 0), false);
            for (Type type : typeList) {
                if (Color.parseColor(type.getTypeMark()) == typeMarks.getColor(i, 0)) {
                    //说明此种颜色已经被使用
                    typeMark.setIsValid(false);
                    break;
                }
            }
            typeMarkList.add(typeMark);
        }
        return typeMarkList;
    }

    public Map<String, Integer> getPlanCountOfEachTypeMap() {
        return planCountOfEachTypeMap;
    }

    public void initPlanCountOfEachTypeMap(List<Plan> planList) {
        if (!isAlive) {
            for (Plan plan : planList) {
                updatePlanCountOfEachType(plan.getTypeCode(), 1);
            }
            isAlive = true;
        }
    }

    public void updatePlanCountOfEachType(String typeCode, int variation) {
        Integer count = planCountOfEachTypeMap.get(typeCode);
        if (count == null) {
            count = 0;
        }
        planCountOfEachTypeMap.put(typeCode, count + variation);
    }

    public void updatePlanCountOfEachType(String fromTypeCode, String toTypeCode) {
        if (!fromTypeCode.equals(toTypeCode)) {
            updatePlanCountOfEachType(fromTypeCode, -1);
            updatePlanCountOfEachType(toTypeCode, 1);
        }
    }

    public void clearPlanCountOfEachType() {
        planCountOfEachTypeMap.clear();
    }
}
