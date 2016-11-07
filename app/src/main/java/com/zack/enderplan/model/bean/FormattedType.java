package com.zack.enderplan.model.bean;

public class FormattedType {

    private int typeMarkColorInt;
    private String typeMarkColorName;
    private boolean hasTypeMarkPattern;
    private int typeMarkPatternResId;
    private String typeMarkPatternName;
    private String typeName;
    private String firstChar;

    /** 编辑类型时使用 */
    public FormattedType(int typeMarkColorInt, String typeMarkColorName, boolean hasTypeMarkPattern,
                         int typeMarkPatternResId, String typeMarkPatternName, String typeName, String firstChar) {
        this.typeMarkColorInt = typeMarkColorInt;
        this.typeMarkColorName = typeMarkColorName;
        this.hasTypeMarkPattern = hasTypeMarkPattern;
        this.typeMarkPatternResId = typeMarkPatternResId;
        this.typeMarkPatternName = typeMarkPatternName;
        this.typeName = typeName;
        this.firstChar = firstChar;
    }

    /** 显示类型详情时使用 */
    public FormattedType(int typeMarkColorInt, boolean hasTypeMarkPattern, int typeMarkPatternResId,
                         String typeName, String firstChar) {
        this.typeMarkColorInt = typeMarkColorInt;
        this.typeMarkColorName = null;
        this.hasTypeMarkPattern = hasTypeMarkPattern;
        this.typeMarkPatternResId = typeMarkPatternResId;
        this.typeMarkPatternName = null;
        this.typeName = typeName;
        this.firstChar = firstChar;
    }

    /** 创建类型时使用 */
    public FormattedType(int typeMarkColorInt, String typeMarkColorName, String typeName, String firstChar) {
        this.typeMarkColorInt = typeMarkColorInt;
        this.typeMarkColorName = typeMarkColorName;
        this.hasTypeMarkPattern = false;
        this.typeMarkPatternResId = -1;
        this.typeMarkPatternName = null;
        this.typeName = typeName;
        this.firstChar = firstChar;
    }

    public int getTypeMarkColorInt() {
        return typeMarkColorInt;
    }

    public void setTypeMarkColorInt(int typeMarkColorInt) {
        this.typeMarkColorInt = typeMarkColorInt;
    }

    public String getTypeMarkColorName() {
        return typeMarkColorName;
    }

    public void setTypeMarkColorName(String typeMarkColorName) {
        this.typeMarkColorName = typeMarkColorName;
    }

    public boolean isHasTypeMarkPattern() {
        return hasTypeMarkPattern;
    }

    public void setHasTypeMarkPattern(boolean hasTypeMarkPattern) {
        this.hasTypeMarkPattern = hasTypeMarkPattern;
    }

    public int getTypeMarkPatternResId() {
        return typeMarkPatternResId;
    }

    public void setTypeMarkPatternResId(int typeMarkPatternResId) {
        this.typeMarkPatternResId = typeMarkPatternResId;
    }

    public String getTypeMarkPatternName() {
        return typeMarkPatternName;
    }

    public void setTypeMarkPatternName(String typeMarkPatternName) {
        this.typeMarkPatternName = typeMarkPatternName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }
}
