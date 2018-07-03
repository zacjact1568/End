package net.zackzhang.app.end.util

import android.text.format.DateFormat
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import java.util.*

object TimeUtil {

    fun isValidTime(timeInMillis: Long) = timeInMillis == 0L || isFutureTime(timeInMillis)

    fun getDateTimePickerDefaultTime(timeInMillis: Long): Long {
        if (timeInMillis == 0L) {
            val calendar = Calendar.getInstance()
            //TODO 延后的时间可以自定义
            calendar.add(Calendar.MINUTE, 1)
            return calendar.timeInMillis
        }
        return timeInMillis
    }

    fun isFutureTime(timeInMillis: Long) = timeInMillis > System.currentTimeMillis()

    val is24HourFormat
        get() = DateFormat.is24HourFormat(App.context)

    fun formatDate(timeInMillis: Long): String? {
        if (timeInMillis == 0L) {
            return null
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val standardCal = Calendar.getInstance()
        //标准时间设为昨天零点
        standardCal.add(Calendar.DAY_OF_MONTH, -1)
        standardCal.set(Calendar.HOUR_OF_DAY, 0)
        standardCal.set(Calendar.MINUTE, 0)
        standardCal.set(Calendar.SECOND, 0)
        standardCal.set(Calendar.MILLISECOND, 0)
        //比较
        if (calendar.after(standardCal)) {
            //标准时间设为今天零点
            standardCal.add(Calendar.DAY_OF_MONTH, 1)
            //比较
            if (calendar.before(standardCal)) {
                //在昨天
                return ResourceUtil.getString(R.string.text_yesterday)
            }
            //标准时间设为明天零点
            standardCal.add(Calendar.DAY_OF_MONTH, 1)
            //比较
            if (calendar.before(standardCal)) {
                //在今天
                return ResourceUtil.getString(R.string.text_today)
            }
            //标准时间设为后天零点
            standardCal.add(Calendar.DAY_OF_MONTH, 1)
            //比较
            if (calendar.before(standardCal)) {
                //在明天
                return ResourceUtil.getString(R.string.text_tomorrow)
            }
            //在后天以及以后，不需要时间描述
        }
        //在前天以及以前，不需要时间描述
        return DateFormat.getMediumDateFormat(App.context).format(Date(timeInMillis))
    }

    fun formatTime(timeInMillis: Long): String? {
        if (timeInMillis == 0L) {
            return null
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val standardCal = Calendar.getInstance()
        if (calendar.after(standardCal)) {
            //标准时间设为1小时后
            standardCal.add(Calendar.HOUR_OF_DAY, 1)
            if (calendar.before(standardCal)) {
                //1小时以内
                val minuteCount = (calendar.timeInMillis - System.currentTimeMillis()).toInt() / (1000 * 60)
                return when (minuteCount) {
                    0 -> ResourceUtil.getString(R.string.dscpt_time_within_1_minute)
                    1 -> ResourceUtil.getString(R.string.dscpt_time_1_minute)
                    else -> String.format(ResourceUtil.getString(R.string.dscpt_time_multi_minutes), minuteCount)
                }
            }
            //在1小时以后，不需要时间描述
        }
        //在当前时间之前，不需要时间描述
        return DateFormat.getTimeFormat(App.context).format(Date(timeInMillis))
    }

    fun formatDateTime(timeInMillis: Long): String? {
        if (timeInMillis == 0L) {
            return null
        }
        val date = Date(timeInMillis)
        val dateStr = DateFormat.getMediumDateFormat(App.context).format(date)
        val timeStr = DateFormat.getTimeFormat(App.context).format(date)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        val standardCal = Calendar.getInstance()
        if (calendar.after(standardCal)) {
            //标准时间设为1小时后
            standardCal.add(Calendar.HOUR_OF_DAY, 1)
            if (calendar.before(standardCal)) {
                //1小时以内
                val minuteCount = (calendar.timeInMillis - System.currentTimeMillis()).toInt() / (1000 * 60)
                return when (minuteCount) {
                    0 -> ResourceUtil.getString(R.string.dscpt_time_within_1_minute)
                    1 -> ResourceUtil.getString(R.string.dscpt_time_1_minute)
                    else -> String.format(ResourceUtil.getString(R.string.dscpt_time_multi_minutes), minuteCount)
                }
            }
            //不在1小时之内，还原时间
            standardCal.add(Calendar.HOUR_OF_DAY, -1)
        }
        //标准时间设为昨天零点
        standardCal.add(Calendar.DAY_OF_MONTH, -1)
        standardCal.set(Calendar.HOUR_OF_DAY, 0)
        standardCal.set(Calendar.MINUTE, 0)
        standardCal.set(Calendar.SECOND, 0)
        standardCal.set(Calendar.MILLISECOND, 0)
        //比较
        if (calendar.after(standardCal)) {
            //标准时间设为今天零点
            standardCal.add(Calendar.DAY_OF_MONTH, 1)
            //比较
            if (calendar.before(standardCal)) {
                //在昨天
                return String.format(ResourceUtil.getString(R.string.dscpt_time_yesterday), timeStr)
            }
            //标准时间设为明天零点
            standardCal.add(Calendar.DAY_OF_MONTH, 1)
            //比较
            if (calendar.before(standardCal)) {
                //在今天
                return String.format(ResourceUtil.getString(R.string.dscpt_time_today), timeStr)
            }
            //标准时间设为后天零点
            standardCal.add(Calendar.DAY_OF_MONTH, 1)
            //比较
            if (calendar.before(standardCal)) {
                //在明天
                return String.format(ResourceUtil.getString(R.string.dscpt_time_tomorrow), timeStr)
            }
            //在后天以及以后，不需要时间描述
        }
        //在前天以及以前，不需要时间描述
        //TODO 添加星期显示
        return String.format(ResourceUtil.getString(R.string.dscpt_time_general), dateStr, timeStr)
    }

    fun isToday(timeInMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        //获取当前时间
        val standardCal = Calendar.getInstance()
        //标准时间设为今天零点
        standardCal.set(Calendar.HOUR_OF_DAY, 0)
        standardCal.set(Calendar.MINUTE, 0)
        standardCal.set(Calendar.SECOND, 0)
        standardCal.set(Calendar.MILLISECOND, 0)
        if (calendar.after(standardCal)) {
            //不在昨天及以前，标准时间设为明天零点
            standardCal.add(Calendar.DAY_OF_MONTH, 1)
            if (calendar.before(standardCal)) {
                //在今天
                return true
            }
        }
        return false
    }
}
