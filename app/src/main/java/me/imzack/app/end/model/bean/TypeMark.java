package me.imzack.app.end.model.bean;

public class TypeMark {

    private String colorHex, patternFn;

    public TypeMark(String colorHex, String patternFn) {
        this.colorHex = colorHex;
        this.patternFn = patternFn;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getPatternFn() {
        return patternFn;
    }

    public void setPatternFn(String patternFn) {
        this.patternFn = patternFn;
    }
}
