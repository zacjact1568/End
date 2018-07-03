package net.zackzhang.app.end.util

import net.zackzhang.app.end.App
import java.util.*

object CommonUtil {

    fun makeCode() = UUID.randomUUID().toString().substring(0, 8)

    fun makeRandom(min: Int, max: Int) = Math.floor(Math.random() * (max - min + 1) + min).toInt()

    /** 比较两个对象是否相等（两个都可为null） */
    fun isObjectEqual(obj0: Any?, obj1: Any?) = obj0 == null && obj1 == null || obj0 != null && obj0 == obj1

    fun convertDpToPx(dp: Int) = (dp * App.context.resources.displayMetrics.density + 0.5f).toInt()

    fun convertSpToPx(sp: Int) = (sp * App.context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
}
