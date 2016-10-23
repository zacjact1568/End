package com.zack.enderplan.model.bean;

public class TypeMarkColor {

    private String colorHex, colorName;

    public TypeMarkColor(String colorHex, String colorName) {
        this.colorHex = colorHex;
        this.colorName = colorName;
    }

    public TypeMarkColor() {
        this.colorHex = null;
        this.colorName = null;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColor(String colorHex, String colorName) {
        this.colorHex = colorHex;
        this.colorName = colorName;
    }

    public void setColor(String colorHex) {
        this.colorHex = colorHex;
        this.colorName = colorHex;
    }
}
