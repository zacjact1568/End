package me.imzack.app.end.model.bean;

import me.imzack.app.end.common.Constant;

public class FormattedType {

    private int typeMarkColorInt;
    private String typeMarkColorName;
    private boolean hasTypeMarkPattern;
    private int typeMarkPatternResId;
    private String typeMarkPatternName;
    private String typeName;
    private String firstChar;

    public FormattedType() {

    }

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
        this(typeMarkColorInt, null, hasTypeMarkPattern, typeMarkPatternResId, null, typeName, firstChar);
    }

    /** 创建类型时使用 */
    public FormattedType(int typeMarkColorInt, String typeMarkColorName, String typeName, String firstChar) {
        this(typeMarkColorInt, typeMarkColorName, false, Constant.UNDEFINED_RES_ID, null, typeName, firstChar);
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
