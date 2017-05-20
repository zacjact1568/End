package com.zack.enderplan.model.bean;

public class Type {

    private String typeCode;
    private String typeName;
    private String typeMarkColor;
    private String typeMarkPattern;
    private int typeSequence;

    public Type(String typeCode, int typeSequence) {
        this(typeCode, null, null, typeSequence);
    }

    public Type(String typeCode, String typeName, String typeMarkColor, int typeSequence) {
        this(typeCode, typeName, typeMarkColor, null, typeSequence);
    }

    public Type(String typeCode, String typeName, String typeMarkColor, String typeMarkPattern, int typeSequence) {
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.typeMarkColor = typeMarkColor;
        this.typeMarkPattern = typeMarkPattern;
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

    public String getTypeMarkColor() {
        return typeMarkColor;
    }

    public void setTypeMarkColor(String typeMarkColor) {
        this.typeMarkColor = typeMarkColor;
    }

    public String getTypeMarkPattern() {
        return typeMarkPattern;
    }

    public void setTypeMarkPattern(String typeMarkPattern) {
        this.typeMarkPattern = typeMarkPattern;
    }

    public int getTypeSequence() {
        return typeSequence;
    }

    public void setTypeSequence(int typeSequence) {
        this.typeSequence = typeSequence;
    }

    public boolean hasTypeMarkPattern() {
        return typeMarkPattern != null;
    }
}
