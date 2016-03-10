package com.zack.enderplan.bean;

public class TypeMark {

    private int resId;
    private boolean isSelected, isValid;

    public TypeMark(int resId, boolean isSelected) {
        this.resId = resId;
        this.isSelected = isSelected;
        this.isValid = true;
    }

    public TypeMark(int resId, boolean isSelected, boolean isValid) {
        this.resId = resId;
        this.isSelected = isSelected;
        this.isValid = isValid;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
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
