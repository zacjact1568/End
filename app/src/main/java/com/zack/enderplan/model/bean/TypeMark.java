package com.zack.enderplan.model.bean;

public class TypeMark {

    private int resId, colorInt;
    private boolean isSelected, isValid;

    public TypeMark(int resId, int colorInt, boolean isSelected) {
        this.resId = resId;
        this.colorInt = colorInt;
        this.isSelected = isSelected;
        this.isValid = true;
    }

    public TypeMark(int resId, int colorInt, boolean isSelected, boolean isValid) {
        this.resId = resId;
        this.colorInt = colorInt;
        this.isSelected = isSelected;
        this.isValid = isValid;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getColorInt() {
        return colorInt;
    }

    public void setColorInt(int colorInt) {
        this.colorInt = colorInt;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }
}
