package com.zack.enderplan.utility;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.LocaleList;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import com.zack.enderplan.App;

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

    public static Locale getPreferredLocale() {
        Configuration config = App.getGlobalContext().getResources().getConfiguration();
        if (isVersionBelowNougat()) {
            return config.locale.equals(Locale.SIMPLIFIED_CHINESE) ? Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH;
        }
        //For Android 7.0
        LocaleList localeList = config.getLocales();
        for (int i = 0; i < localeList.size(); i++) {
            Locale locale = localeList.get(i);
            if (!locale.equals(Locale.SIMPLIFIED_CHINESE) && !locale.equals(Locale.ENGLISH)) continue;
            return locale;
        }
        return Locale.ENGLISH;
    }

    public static String makeColorChannel() {
        String channel = Integer.toHexString((int) Math.floor(Math.random() * 256)).toUpperCase();
        return channel.length() == 1 ? "0" + channel : channel;
    }

    public static String makeColor() {
        return String.format("#FF%s%s%s", makeColorChannel(), makeColorChannel(), makeColorChannel());
    }
}
