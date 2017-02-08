package com.zack.enderplan.util;

import com.zack.enderplan.model.preference.PreferenceHelper;

import java.util.Map;

public class DevelopUtil {

    public static void logAllSharedPreferences(String tag) {
        Map<String, ?> preferenceMap = PreferenceHelper.getInstance().getAllValues();
        if (preferenceMap.isEmpty()) {
            LogUtil.d("No shared preferences");
        } else {
            for (Map.Entry<String, ?> entry : preferenceMap.entrySet()) {
                LogUtil.d(tag, entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
