package com.zack.enderplan.model.bean;

public class FormattedType {

    private int typeMarkColorInt;
    private String firstChar;
    private String typeName;
    private String ucPlanCountStr;

    public FormattedType(int typeMarkColorInt, String firstChar, String typeName, String ucPlanCountStr) {
        this.typeMarkColorInt = typeMarkColorInt;
        this.firstChar = firstChar;
        this.typeName = typeName;
        this.ucPlanCountStr = ucPlanCountStr;
    }

    public int getTypeMarkColorInt() {
        return typeMarkColorInt;
    }

    public void setTypeMarkColorInt(int typeMarkColorInt) {
        this.typeMarkColorInt = typeMarkColorInt;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUcPlanCountStr() {
        return ucPlanCountStr;
    }

    public void setUcPlanCountStr(String ucPlanCountStr) {
        this.ucPlanCountStr = ucPlanCountStr;
    }
}
