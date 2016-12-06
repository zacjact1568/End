package com.zack.enderplan.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.zack.enderplan.App;
import com.zack.enderplan.receiver.ReminderReceiver;

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

    public static int makeRandom(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static String parseColorChannel(int channelInt) {
        String channel = Integer.toHexString(channelInt).toUpperCase();
        return channel.length() == 1 ? "0" + channel : channel;
    }

    public static String[] parseColorChannels(int colorInt) {
        String color = parseColor(colorInt);
        return new String[]{
                color.substring(1, 2),
                color.substring(3, 4),
                color.substring(5, 6),
                color.substring(7, 8)
        };
    }

    public static String makeColor() {
        StringBuilder color = new StringBuilder("#");
        //限制透明度
        color.append(parseColorChannel(makeRandom(156, 255)));
        //RGB颜色
        for (int i = 0; i < 3; i++) {
            color.append(parseColorChannel(makeRandom(0, 255)));
        }
        return color.toString();
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

    /** 为给定的editor显示软键盘 */
    public static void showSoftInput(EditText editor) {
        if (!editor.hasFocus()) return;
        ((InputMethodManager) App.getGlobalContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editor, 0);
    }

    /** 为给定的editor隐藏软键盘 */
    public static void hideSoftInput(EditText editor) {
        InputMethodManager manager = (InputMethodManager) App.getGlobalContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive(editor)) {
            manager.hideSoftInputFromWindow(editor.getWindowToken(), 0);
        }
    }

    /**
     * 设定reminder
     * @param planCode 用来区分各个reminder
     * @param timeInMillis Reminder触发的时间，若为0则取消定时器
     */
    public static void setReminder(String planCode, long timeInMillis) {
        Context context = App.getGlobalContext();
        Intent intent = new Intent(context, ReminderReceiver.class);
        //相当于只有下面这条语句才能区分不同的PendingIntent，也就是code才能区分，如果code相同，那么extra也会被无视
        intent.setAction("com.zack.enderplan.ACTION_REMINDER_PLAN_" + planCode);
        intent.setPackage(context.getPackageName());
        intent.putExtra("plan_code", planCode);
        //此处FLAG_UPDATE_CURRENT真的很有用，修改过plan后再set，通知内容也会被替换成新的plan.content了，也就不需要cancel后再set了
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (timeInMillis != 0) {
            manager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
        } else {
            manager.cancel(pi);
        }
    }
}
