package me.imzack.app.end.util;

import me.imzack.app.end.App;

import java.util.UUID;

public class CommonUtil {

    public static String makeCode() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8);
    }

    public static int makeRandom(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    /** 比较两个对象是否相等（两个都可为null）*/
    public static boolean isObjectEqual(Object obj0, Object obj1) {
        return (obj0 == null && obj1 == null) || (obj0 != null && obj0.equals(obj1));
    }

    public static int convertDpToPx(int dp) {
        return (int) (dp * App.getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int convertSpToPx(int sp) {
        return (int) (sp * App.getContext().getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }
}
