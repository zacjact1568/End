package me.imzack.app.end.model.preference

import android.content.SharedPreferences
import android.preference.PreferenceManager

import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant

class PreferenceHelper {

    private val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)

    var needGuideValue
        get() = mSharedPreferences.getBoolean(Constant.PREF_KEY_NEED_GUIDE, true)
        set(value) = mSharedPreferences.edit().putBoolean(Constant.PREF_KEY_NEED_GUIDE, value).apply()

    var nightModeValue
        get() = mSharedPreferences.getBoolean(Constant.PREF_KEY_NIGHT_MODE, false)
        set(value) = mSharedPreferences.edit().putBoolean(Constant.PREF_KEY_NIGHT_MODE, value).apply()

    var drawerHeaderDisplayValue
        get() = mSharedPreferences.getString(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY, Constant.PREF_VALUE_DHD_UPC)!!
        set(value) = mSharedPreferences.edit().putString(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY, value).apply()

    var typeListItemEndDisplayValue
        get() = mSharedPreferences.getString(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY, Constant.PREF_VALUE_TLIED_STUPC)!!
        set(value) = mSharedPreferences.edit().putString(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY, value).apply()

    val allValues
        get() = mSharedPreferences.all

    fun resetAllValues() {
        mSharedPreferences.edit().clear().putBoolean(Constant.PREF_KEY_NEED_GUIDE, false).apply()
        PreferenceManager.setDefaultValues(App.context, R.xml.preferences, true)
    }

    fun registerOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
