package com.zack.enderplan.model.preference;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;

import java.util.Map;

public class PreferenceHelper {

    private SharedPreferences mSharedPreferences;

    private static PreferenceHelper ourInstance = new PreferenceHelper();

    private PreferenceHelper() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static PreferenceHelper getInstance() {
        return ourInstance;
    }

    public boolean getNeedGuideValue() {
        return mSharedPreferences.getBoolean(Constant.PREF_KEY_NEED_GUIDE, true);
    }

    public boolean getNightModeValue() {
        return mSharedPreferences.getBoolean(Constant.PREF_KEY_NIGHT_MODE, false);
    }

    public String getDrawerHeaderDisplayValue() {
        return mSharedPreferences.getString(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY, Constant.PREF_VALUE_DHD_UPC);
    }

    public String getTypeListItemEndDisplayValue() {
        return mSharedPreferences.getString(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY, Constant.PREF_VALUE_TLIED_STUPC);
    }

    public void setNeedGuideValue(boolean value) {
        mSharedPreferences.edit().putBoolean(Constant.PREF_KEY_NEED_GUIDE, value).apply();
    }

    public void setNightModeValue(boolean value) {
        mSharedPreferences.edit().putBoolean(Constant.PREF_KEY_NIGHT_MODE, value).apply();
    }

    public void setDrawerHeaderDisplayValue(String value) {
        mSharedPreferences.edit().putString(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY, value).apply();
    }

    public Map<String, ?> getAllValues() {
        return mSharedPreferences.getAll();
    }

    public void resetAllValues() {
        mSharedPreferences.edit().clear().putBoolean(Constant.PREF_KEY_NEED_GUIDE, false).apply();
        PreferenceManager.setDefaultValues(App.getContext(), R.xml.preferences, true);
    }

    public void registerOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
