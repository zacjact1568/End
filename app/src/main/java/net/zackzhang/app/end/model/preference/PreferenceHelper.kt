package net.zackzhang.app.end.model.preference

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager

import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant

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

    var needNotificationChannelsInitializationValue
        @TargetApi(Build.VERSION_CODES.O)
        get() = mSharedPreferences.getBoolean(Constant.PREF_KEY_NEED_NOTIFICATION_CHANNELS_INITIALIZATION, true)
        @TargetApi(Build.VERSION_CODES.O)
        set(value) = mSharedPreferences.edit().putBoolean(Constant.PREF_KEY_NEED_NOTIFICATION_CHANNELS_INITIALIZATION, value).apply()

    val allValues: Map<String, *>
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
