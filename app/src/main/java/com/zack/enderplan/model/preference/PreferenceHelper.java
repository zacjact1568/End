package com.zack.enderplan.model.preference;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zack.enderplan.App;
import com.zack.enderplan.R;

import java.util.Map;

public class PreferenceHelper {

    public static final String KEY_PREF_NEED_GUIDE = "need_guide";
    public static final String KEY_PREF_NIGHT_MODE = "night_mode";
    public static final String KEY_PREF_DRAWER_HEADER_DISPLAY = "drawer_header_display";

    public static final String VALUE_PREF_DHD_UPC = "uc_plan_count";
    public static final String VALUE_PREF_DHD_PC = "plan_count";
    public static final String VALUE_PREF_DHD_TUPC = "today_uc_plan_count";

    private SharedPreferences mSharedPreferences;

    private static PreferenceHelper ourInstance = new PreferenceHelper();

    private PreferenceHelper() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static PreferenceHelper getInstance() {
        return ourInstance;
    }

    public boolean getNeedGuideValue() {
        return getValue(KEY_PREF_NEED_GUIDE, true);
    }

    public boolean getNightModeValue() {
        return getValue(KEY_PREF_NIGHT_MODE, false);
    }

    public String getDrawerHeaderDisplayValue() {
        return getValue(KEY_PREF_DRAWER_HEADER_DISPLAY, VALUE_PREF_DHD_UPC);
    }

    private boolean getValue(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    private String getValue(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public Map<String, ?> getAllValues() {
        return mSharedPreferences.getAll();
    }

    public void setNeedGuideValue(boolean value) {
        setValue(KEY_PREF_NEED_GUIDE, value);
    }

    public void setNightModeValue(boolean value) {
        setValue(KEY_PREF_NIGHT_MODE, value);
    }

    public void setDrawerHeaderDisplayValue(String value) {
        setValue(KEY_PREF_DRAWER_HEADER_DISPLAY, value);
    }

    private void setValue(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    private void setValue(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void resetValues() {
        mSharedPreferences.edit().clear().putBoolean(KEY_PREF_NEED_GUIDE, false).apply();
        PreferenceManager.setDefaultValues(App.getContext(), R.xml.preferences, true);
    }

    public void registerOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
