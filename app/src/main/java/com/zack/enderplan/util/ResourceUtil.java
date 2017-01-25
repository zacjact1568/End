package com.zack.enderplan.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.zack.enderplan.App;

public class ResourceUtil {

    /**
     * 通过资源文件名获取drawable资源id，如果name为null，返回0（0是非法资源id）
     * @param name 资源文件名
     * @return 资源id
     */
    public static int getDrawableResourceId(String name) {
        //0 is an invalid resource id
        if (name == null) return 0;
        Context context = App.getContext();
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(App.getContext(), resId);
    }

    public static String getString(@StringRes int resId) {
        return App.getContext().getString(resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return App.getContext().getDrawable(resId);
    }

    public static Drawable getDrawable(String name) {
        int resId = getDrawableResourceId(name);
        if (resId == 0) return null;
        return getDrawable(resId);
    }
}