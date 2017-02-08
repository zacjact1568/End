package com.zack.enderplan.model.preference;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zack.enderplan.App;
import com.zack.enderplan.R;

import java.util.Map;

public class PreferenceHelper {

    public static final String KEY_PREF_NEED_GUIDE = "need_guide";
    public static final String KEY_PREF_NIGHT_MODE = "night_mode";

    private SharedPreferences sharedPreferences;

    private static PreferenceHelper ourInstance = new PreferenceHelper();

    private PreferenceHelper() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static PreferenceHelper getInstance() {
        return ourInstance;
    }

    public boolean getNeedGuideValue() {
        return getBooleanValue(KEY_PREF_NEED_GUIDE, true);
    }

    public boolean getNightModeValue() {
        return getBooleanValue(KEY_PREF_NIGHT_MODE, false);
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public Map<String, ?> getAllValues() {
        return sharedPreferences.getAll();
    }

    public String getStringValue(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setNeedGuideValue(boolean value) {
        setValue(KEY_PREF_NEED_GUIDE, value);
    }

    public void setNightModeValue(boolean value) {
        setValue(KEY_PREF_NIGHT_MODE, value);
    }

    public void setValue(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void setValue(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void resetValues() {
        sharedPreferences.edit().clear().putBoolean(KEY_PREF_NEED_GUIDE, false).apply();
        PreferenceManager.setDefaultValues(App.getContext(), R.xml.preferences, true);
    }
}
