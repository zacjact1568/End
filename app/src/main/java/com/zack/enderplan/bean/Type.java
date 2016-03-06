package com.zack.enderplan.bean;

public class Type {

    private String typeCode, typeName, typeMark;

    public Type(String typeCode) {
        this.typeCode = typeCode;
        this.typeName = null;
        this.typeMark = null;
    }

    public Type(String typeCode, String typeName, String typeMark) {
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.typeMark = typeMark;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeMark() {
        return typeMark;
    }

    public void setTypeMark(String typeMark) {
        this.typeMark = typeMark;
    }
}
