package com.zack.enderplan.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.receiver.ReminderReceiver;

import java.util.Locale;

public class SystemUtil {

    /** 检测Android版本是否低于6.0 */
    public static boolean isVersionBelowMarshmallow() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    /** 检测Android版本是否低于7.0 */
    public static boolean isVersionBelowNougat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.N;
    }

    public static void makeShortVibrate() {
        Vibrator vibrator = (Vibrator) App.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100};
        vibrator.vibrate(pattern, -1);
    }

    /** 为给定的editor显示软键盘 */
    public static void showSoftInput(EditText editor) {
        if (!editor.hasFocus()) {
            editor.requestFocus();
        }
        ((InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editor, 0);
    }

    public static void showSoftInput(final EditText editor, long delayTime) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftInput(editor);
            }
        }, delayTime);
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

    public static void showNotification(String tag, String title, String content, Bitmap largeIcon, PendingIntent intent, NotificationCompat.Action... actions) {
        Context context = App.getContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_check_circle_black_24dp)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(intent)
                .setColor(ResourceUtil.getColor(R.color.colorPrimary))
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

    public static NotificationCompat.Action getNotificationAction(@DrawableRes int iconResId, @StringRes int titleResId, PendingIntent intent) {
        return new NotificationCompat.Action.Builder(iconResId, ResourceUtil.getString(titleResId), intent).build();
    }

    public static void putTextToClipboard(String label, String text) {
        ((ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(label, text));
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

    //TODO 考虑全部转移到这两个方法
    public static void showToast(@StringRes int msgResId) {
        Toast.makeText(App.getContext(), msgResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
