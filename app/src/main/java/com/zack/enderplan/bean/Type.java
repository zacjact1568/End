package com.zack.enderplan.bean;

public class Type {

    private String typeCode, typeName, typeMark;
    private int typeSequence;

    public Type(String typeCode, int typeSequence) {
        this.typeCode = typeCode;
        this.typeName = null;
        this.typeMark = null;
        this.typeSequence = typeSequence;
    }

    public Type(String typeCode, String typeName, String typeMark, int typeSequence) {
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.typeMark = typeMark;
        this.typeSequence = typeSequence;
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

    public int getTypeSequence() {
        return typeSequence;
    }

    public void setTypeSequence(int typeSequence) {
        this.typeSequence = typeSequence;
    }
}
