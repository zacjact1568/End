package me.imzack.app.end.model.bean

data class FormattedType(
        var typeMarkColorInt: Int = 0,
        var typeMarkColorName: String? = null,
        var hasTypeMarkPattern: Boolean = false,
        var typeMarkPatternResId: Int = 0,
        var typeMarkPatternName: String? = null,
        var typeName: String = "",
        var firstChar: String = ""
)

/**
 * 编辑类型：所有字段
 * 显示类型详情：typeMarkColorName和typeMarkPatternName为null
 * 创建类型：hasTypeMarkPattern = false，typeMarkPatternResId = Constant.UNDEFINED_RES_ID，typeMarkPatternName = null
 */
