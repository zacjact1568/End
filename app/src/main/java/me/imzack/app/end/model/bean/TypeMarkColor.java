package me.imzack.app.end.model.bean;

public class TypeMarkColor {

    private String colorHex, colorName;

    public TypeMarkColor(String colorHex, String colorName) {
        this.colorHex = colorHex;
        this.colorName = colorName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
}
