package com.zack.enderplan.manager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import com.zack.enderplan.R;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Type;

import java.util.List;

public class TypeManager {

    private List<Type> typeList;
    private TypedArray typeMarks;

    public TypeManager(Context context) {
        typeList = EnderPlanDB.getInstance(context).loadType();
        typeMarks = context.getResources().obtainTypedArray(R.array.type_marks);
    }

    public List<Type> getTypeList() {
        return typeList;
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
}
