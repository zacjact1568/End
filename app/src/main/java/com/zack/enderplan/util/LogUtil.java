package com.zack.enderplan.util;

import android.util.Log;

import com.zack.enderplan.model.DataManager;

import java.util.Map;

public class LogUtil {

    private static final String DEF_TAG = "Default Tag";

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    //level的值是多少，就打印对应常量级别以上的日志（级别就是常量值）
    //e.g.为WARN就打印WARN级别以上的日志（WARN、ERROR）
    public static int level = VERBOSE;

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, int msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg + "");
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void d(String msg) {
        d(DEF_TAG, msg);
    }

    public static void d(int msg) {
        d(DEF_TAG, msg);
    }

    public static void here() {
        d("****HERE****");
    }

    public static void logAllSharedPreferences(String tag) {
        Map<String, ?> pm = DataManager.getInstance().getPreferenceHelper().getAllValues();
        if (pm.isEmpty()) {
            d("No shared preferences");
        } else {
            for (Map.Entry<String, ?> entry : pm.entrySet()) {
                d(tag, entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
