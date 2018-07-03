package net.zackzhang.app.end.view.contract

import android.support.annotation.DrawableRes

import net.zackzhang.app.end.model.bean.FormattedType

interface TypeEditViewContract : BaseViewContract {

    fun showInitialView(formattedType: FormattedType)

    fun showTypeNameEditorDialog(originalEditorText: String)

    fun onTypeNameChanged(typeName: String, firstChar: String)

    fun onTypeMarkColorChanged(colorInt: Int, colorName: String)

    fun onTypeMarkPatternChanged(hasPattern: Boolean, @DrawableRes patternResId: Int, patternName: String?)

    fun showTypeMarkColorPickerDialog(defaultColor: Int)

    fun showTypeMarkPatternPickerDialog(defaultPattern: String?)
}
