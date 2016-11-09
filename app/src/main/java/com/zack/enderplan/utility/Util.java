package com.zack.enderplan.utility;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.LocaleList;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.View;

import com.zack.enderplan.App;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;

public class Util {

    public static String makeCode() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8);
    }

    /**
     * 为给定字符串添加删除线
     * @param str 要添加删除线的字符串
     * @return 已添加删除线的字符串
     */
    public static SpannableString addStrikethroughSpan(String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * 为给定字符串中的子字符串加粗
     * @param str 给定字符串
     * @param bolds 要加粗的子字符串
     * @return 已部分加粗的字符串
     */
    public static SpannableString addBoldStyle(String str, String[] bolds) {
        SpannableString ss = new SpannableString(str);
        for (String bold : bolds) {
            int start = str.indexOf(bold);
            int end = start + bold.length();
            ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return ss;
    }

    public static String parseColor(int colorInt) {
        return String.format("#%s", Long.toHexString(0x100000000L + colorInt)).toUpperCase();
    }

    public static void makeShortVibrate() {
        Vibrator vibrator = (Vibrator) App.getGlobalContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100};
        vibrator.vibrate(pattern, -1);
    }

    /** 检测Android版本是否低于6.0 */
    public static boolean isVersionBelowMarshmallow() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    /** 检测Android版本是否低于7.0 */
    public static boolean isVersionBelowNougat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }

    /**
     * 获取首选的语言
     * @return ENGLISH、SIMPLIFIED_CHINESE、TRADITIONAL_CHINESE三者其一
     */
    public static Locale getPreferredLocale() {
        Configuration config = App.getGlobalContext().getResources().getConfiguration();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Locale locale = config.locale;
            if (locale.equals(Locale.ENGLISH) || locale.equals(Locale.SIMPLIFIED_CHINESE) || locale.equals(Locale.TRADITIONAL_CHINESE)) {
                return locale;
            }
        } else {
            LocaleList localeList = config.getLocales();
            for (int i = 0; i < localeList.size(); i++) {
                Locale locale = localeList.get(i);
                if (locale.equals(Locale.ENGLISH) || locale.equals(Locale.SIMPLIFIED_CHINESE) || locale.equals(Locale.TRADITIONAL_CHINESE)) {
                    return locale;
                }
            }
        }
        //其他语言，默认英语
        return Locale.ENGLISH;
    }

    public static String makeColorChannel() {
        String channel = Integer.toHexString((int) Math.floor(Math.random() * 256)).toUpperCase();
        return channel.length() == 1 ? "0" + channel : channel;
    }

    public static String makeColor() {
        return String.format("#FF%s%s%s", makeColorChannel(), makeColorChannel(), makeColorChannel());
    }

    /**
     * 通过反射获取未绘制控件的高<br>
     * Reference: https://my.oschina.net/u/269663/blog/388980
     * @param view 要获取高的控件
     * @return 控件的高
     */
    public static int getViewHeight(View view) {
        try {
            Method method = view.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            method.setAccessible(true);
            method.invoke(
                    view,
                    View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view.getMeasuredHeight();
    }

    /**
     * 通过资源文件名获取drawable资源id，如果name为null，返回0（0是非法资源id）
     * @param name 资源文件名
     * @return 资源id
     */
    public static int getDrawableResourceId(String name) {
        //0 is an invalid resource id
        if (name == null) return 0;
        Context context = App.getGlobalContext();
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    /** 比较两个对象是否相等（两个都可为null）*/
    public static boolean isObjectEqual(Object obj0, Object obj1) {
        return (obj0 == null && obj1 == null) || (obj0 != null && obj0.equals(obj1));
    }
}
