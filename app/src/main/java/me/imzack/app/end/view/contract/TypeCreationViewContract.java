package me.imzack.app.end.view.contract;

import android.support.annotation.DrawableRes;

import me.imzack.app.end.model.bean.FormattedType;

public interface TypeCreationViewContract extends BaseViewContract {

    void showInitialView(FormattedType formattedType);

    void onTypeNameChanged(String typeName, String firstChar);

    void onTypeMarkColorChanged(int colorInt, String colorName);

    void onTypeMarkPatternChanged(boolean hasPattern, @DrawableRes int patternResId, String patternName);

    void showTypeNameEditorDialog(String defaultName);

    void showTypeMarkColorPickerDialog(String defaultColor);

    void showTypeMarkPatternPickerDialog(String defaultPattern);
}
