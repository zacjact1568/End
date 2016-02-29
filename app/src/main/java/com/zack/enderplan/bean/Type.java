package com.zack.enderplan.bean;

public class Type {

    public static final String TYPE_MARK_AMBER = "amber";
    public static final String TYPE_MARK_BLUE = "blue";
    public static final String TYPE_MARK_BLUE_GREY = "blue_grey";
    public static final String TYPE_MARK_BROWN = "brown";
    public static final String TYPE_MARK_CYAN = "cyan";
    public static final String TYPE_MARK_DEEP_ORANGE = "deep_orange";
    public static final String TYPE_MARK_DEEP_PURPLE = "deep_purple";
    public static final String TYPE_MARK_GREEN = "green";
    public static final String TYPE_MARK_GREY = "grey";
    public static final String TYPE_MARK_INDIGO = "indigo";
    public static final String TYPE_MARK_LIGHT_BLUE = "light_blue";
    public static final String TYPE_MARK_LIGHT_GREEN = "light_green";
    public static final String TYPE_MARK_LIME = "lime";
    public static final String TYPE_MARK_ORANGE = "orange";
    public static final String TYPE_MARK_PINK = "pink";
    public static final String TYPE_MARK_PURPLE = "purple";
    public static final String TYPE_MARK_RED = "red";
    public static final String TYPE_MARK_TEAL = "teal";
    public static final String TYPE_MARK_YELLOW = "yellow";

    private String typeCode, typeName, typeMark;

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
