package me.imzack.app.end.util

import android.annotation.TargetApi
import android.app.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.receiver.ReminderReceiver
import java.util.*

object SystemUtil {

    fun makeShortVibrate() {
        val vibrator = App.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator.vibrate(longArrayOf(0, 100), -1)
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    /** 为给定的editor显示软键盘 */
    fun showSoftInput(editor: EditText) {
        if (!editor.hasFocus()) {
            editor.requestFocus()
        }
        (App.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editor, 0)
    }

    fun showSoftInput(editor: EditText, delayTime: Long) {
        Handler().postDelayed({ showSoftInput(editor) }, delayTime)
    }

    /** 为给定的editor隐藏软键盘 */
    fun hideSoftInput(editor: EditText) {
        val manager = App.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (manager.isActive(editor)) {
            manager.hideSoftInputFromWindow(editor.windowToken, 0)
        }
    }

    /**
     * 设定reminder
     * @param planCode 用来区分各个reminder
     * @param timeInMillis Reminder触发的时间，若为0则取消定时器
     */
    fun setReminder(planCode: String, timeInMillis: Long) {
        val context = App.context
        val intent = ReminderReceiver.getPendingIntentForSend(context, planCode)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (timeInMillis != 0L) {
            manager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, intent)
        } else {
            manager.cancel(intent)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun addNotificationChannel(id: String, name: String, importance: Int, description: String) {
        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        // LED灯和振动默认false，图标小圆点默认true
        // TODO 测试声音
        channel.enableLights(true)
        channel.enableVibration(true)
        (App.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    fun showNotification(channel: String, tag: String, title: String, content: String, largeIcon: Bitmap, intent: PendingIntent, vararg actions: Notification.Action) {
        val context = App.context
        val builder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // API < 26，没有channel，单独指定声音振动等设置
            Notification.Builder(context)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(when (channel) {
                        // 每添加一个channel，需要在这里添加 API < 26 的情况，和channel的importance对应
                        Constant.NOTIFICATION_CHANNEL_ID_REMINDER -> Notification.PRIORITY_MAX
                        else -> Notification.PRIORITY_DEFAULT
                    })
        } else {
            // API < 26，声音振动等设置在channel中指定
            Notification.Builder(context, channel)
        }
                .setSmallIcon(R.drawable.img_logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(intent)
                .setColor(ResourceUtil.getColor(R.color.colorPrimary))
                .setShowWhen(true)
        // 没法直接用setActions，可能是Java和Kotlin的变长参数不兼容
        for (action in actions) {
            builder.addAction(action)
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(tag, 0, builder.build())
    }

    fun cancelNotification(tag: String) {
        (App.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(tag, 0)
    }

    fun getNotificationAction(@DrawableRes iconResId: Int, @StringRes titleResId: Int, intent: PendingIntent) =
            Notification.Action.Builder(iconResId, ResourceUtil.getString(titleResId), intent).build()!!

    fun putTextToClipboard(label: String, text: String) {
        (App.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(label, text)
    }

    /**
     * 获取首选的语言
     * @return ENGLISH、SIMPLIFIED_CHINESE、TRADITIONAL_CHINESE三者其一
     */
    val preferredLocale: Locale
        get() {
            val config = App.context.resources.configuration
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                val locale = config.locale
                if (locale == Locale.ENGLISH || locale == Locale.SIMPLIFIED_CHINESE || locale == Locale.TRADITIONAL_CHINESE) {
                    return locale
                }
            } else {
                val localeList = config.locales
                (0 until localeList.size())
                        // 为什么要用asSequence()，区间转序列？
                        .asSequence()
                        // 对每个元素进行变换
                        .map { localeList.get(it) }
                        // 保留满足条件的元素
                        .filter { it == Locale.ENGLISH || it == Locale.SIMPLIFIED_CHINESE || it == Locale.TRADITIONAL_CHINESE }
                        // 输出处理后的元素
                        .forEach { return it }
            }
            //其他语言，默认英语
            return Locale.ENGLISH
        }

    //TODO 考虑全部转移到这两个方法
    fun showToast(@StringRes msgResId: Int) {
        Toast.makeText(App.context, msgResId, Toast.LENGTH_SHORT).show()
    }

    fun showToast(msg: String) {
        Toast.makeText(App.context, msg, Toast.LENGTH_SHORT).show()
    }

    fun openLink(link: String, activity: Activity, failed: String = activity.getString(R.string.toast_no_link_app_found)) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            showToast(failed)
        }
    }

    val versionName: String
        get() {
            val context = App.context
            return try {
                val info = context.packageManager.getPackageInfo(context.packageName, 0)
                info.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                "null"
            }
        }

    val displayWidth: Int
        get() {
            val point = Point()
            (App.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
            return point.x
        }
}
