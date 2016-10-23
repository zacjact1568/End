package com.zack.enderplan.model.bean;

public class TypeMark {

    private String colorHex, patternId;

    public TypeMark(String colorHex, String patternId) {
        this.colorHex = colorHex;
        this.patternId = patternId;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getPatternId() {
        return patternId;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }
}
