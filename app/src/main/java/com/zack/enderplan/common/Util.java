package com.zack.enderplan.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.LocaleList;
import android.os.Vibrator;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.receiver.ReminderReceiver;

import java.lang.reflect.Method;
import java.util.Calendar;
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
        Vibrator vibrator = (Vibrator) App.getContext().getSystemService(Context.VIBRATOR_SERVICE);
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
        Configuration config = App.getContext().getResources().getConfiguration();
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
        Context context = App.getContext();
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    /** 比较两个对象是否相等（两个都可为null）*/
    public static boolean isObjectEqual(Object obj0, Object obj1) {
        return (obj0 == null && obj1 == null) || (obj0 != null && obj0.equals(obj1));
    }

    /** 为给定的editor显示软键盘 */
    public static void showSoftInput(EditText editor) {
        if (!editor.hasFocus()) return;
        ((InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editor, 0);
    }

    /** 为给定的editor隐藏软键盘 */
    public static void hideSoftInput(EditText editor) {
        InputMethodManager manager = (InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive(editor)) {
            manager.hideSoftInputFromWindow(editor.getWindowToken(), 0);
        }
    }

    /**
     * 设定reminder
     * @param planCode 用来区分各个reminder
     * @param timeInMillis Reminder触发的时间，若为Constant.TIME_UNDEFINED则取消定时器
     */
    public static void setReminder(String planCode, long timeInMillis) {
        Context context = App.getContext();
        PendingIntent intent = ReminderReceiver.getPendingIntentForSend(context, planCode);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (timeInMillis != Constant.UNDEFINED_TIME) {
            manager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, intent);
        } else {
            manager.cancel(intent);
        }
    }

    public static int convertDpToPx(int dp) {
        return (int) (dp * App.getContext().getResources().getDisplayMetrics().density + 0.5f);
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

    public static boolean isFutureTime(long timeInMillis) {
        return timeInMillis == Constant.UNDEFINED_TIME || timeInMillis > System.currentTimeMillis();
    }

    public static void showNotification(String tag, String title, String content, Bitmap largeIcon, PendingIntent intent, NotificationCompat.Action... actions) {
        Context context = App.getContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_check_box_black_24dp)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(intent)
                .setColor(getColor(R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setShowWhen(true);
        for (NotificationCompat.Action action : actions) {
            builder.addAction(action);
        }
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(tag, 0, builder.build());
    }

    public static void cancelNotification(String tag) {
        ((NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(tag, 0);
    }

    public static Bitmap convertViewToBitmap(View view, int size) {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY);
        view.measure(measureSpec, measureSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    public static String getFirstChar(String str) {
        return str.substring(0, 1);
    }

    public static NotificationCompat.Action getNotificationAction(@DrawableRes int iconResId, @StringRes int titleResId, PendingIntent intent) {
        return new NotificationCompat.Action.Builder(iconResId, getString(titleResId), intent).build();
    }

    //TODO 考虑全部转移到这两个方法
    public static void showToast(@StringRes int msgResId) {
        Toast.makeText(App.getContext(), msgResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static long getDateTimePickerDefaultTime(long timeInMillis) {
        if (timeInMillis == Constant.UNDEFINED_TIME) {
            Calendar calendar = Calendar.getInstance();
            //TODO 延后的时间可以自定义
            calendar.add(Calendar.MINUTE, 1);
            timeInMillis = calendar.getTimeInMillis();
        }
        return timeInMillis;
    }

    public static int getScreenCoordinateY(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[1];
    }

    public static void putTextToClipboard(String label, String text) {
        ((ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(label, text));
    }
}
