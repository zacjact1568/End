package com.zack.enderplan.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnimRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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

    public static String getQuantityString(@PluralsRes int resId, int quantity) {
        return App.getContext().getResources().getQuantityString(resId, quantity, quantity);
    }

    public static String getQuantityString(@StringRes int formatResId, @PluralsRes int argResId, int quantity) {
        return String.format(getString(formatResId), getQuantityString(argResId, quantity));
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return App.getContext().getDrawable(resId);
    }

    public static Drawable getDrawable(String name) {
        int resId = getDrawableResourceId(name);
        if (resId == 0) return null;
        return getDrawable(resId);
    }

    public static String[] getStringArray(@ArrayRes int resId) {
        return App.getContext().getResources().getStringArray(resId);
    }

    public static Animation getAnimation(@AnimRes int resId) {
        return AnimationUtils.loadAnimation(App.getContext(), resId);
    }
}
