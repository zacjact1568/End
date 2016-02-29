package com.zack.enderplan.manager;

import android.content.Context;

import com.zack.enderplan.R;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Type;

import java.util.List;

public class TypeManager {

    private List<Type> typeList;

    public static final int TYPE_NAME_CODE = 0;
    public static final int TYPE_MARK_CODE = 1;

    public TypeManager(Context context) {
        typeList = EnderPlanDB.getInstance(context).loadType();
    }

    /**
     * Find the color resource id by type mark.
     * @param typeMark The type mark from database.
     * @return The color resource id.
     */
    public int findColorIdByTypeMark(String typeMark) {
        switch (typeMark) {
            case Type.TYPE_MARK_INDIGO:
                return R.color.indigo;
            case Type.TYPE_MARK_RED:
                return R.color.red;
            case Type.TYPE_MARK_BLUE:
                return R.color.blue;
            case Type.TYPE_MARK_GREEN:
                return R.color.green;
            case Type.TYPE_MARK_YELLOW:
                return R.color.yellow;
            case Type.TYPE_MARK_GREY:
                return R.color.grey;
            default:
                return 0;
        }
    }

    /**
     * Find other values of a type object in type list by given type code.
     * @param typeCode The code of the type object.
     * @param targetCode The name id of the value that will be found.
     * @return The result value.
     */
    public String convertTypeCode(String typeCode, int targetCode) {
        String result = "";
        for (Type type : typeList) {
            if (type.getTypeCode().equals(typeCode)) {
                switch (targetCode) {
                    case TYPE_NAME_CODE:
                        result = type.getTypeName();
                        break;
                    case TYPE_MARK_CODE:
                        result = type.getTypeMark();
                        break;
                    default:
                        break;
                }
                break;
            }
        }
        return result;
    }
}
