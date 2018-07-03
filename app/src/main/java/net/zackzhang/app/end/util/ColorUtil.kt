package net.zackzhang.app.end.util

import android.graphics.Color

object ColorUtil {

    fun parseColor(colorInt: Int, alpha: Boolean = true): String {
        val color = java.lang.Long.toHexString(0x100000000L + colorInt)
        return String.format("#%s", if (alpha) color else color.substring(2)).toUpperCase()
    }

    fun parseColorChannel(channelInt: Int): String {
        val channel = Integer.toHexString(channelInt).toUpperCase()
        return if (channel.length == 1) "0" + channel else channel
    }

    fun parseColorChannels(colorInt: Int): Array<String> {
        val color = parseColor(colorInt)
        return arrayOf(color.substring(1, 2), color.substring(3, 4), color.substring(5, 6), color.substring(7, 8))
    }

    fun makeColor(): String {
        val color = StringBuilder("#FF")
        for (i in 0..2) color.append(parseColorChannel(CommonUtil.makeRandom(0, 255)))
        return color.toString()
    }

    fun setAlpha(alpha: Int, color: Int) = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

    fun reduceSaturation(color: Int, rate: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] *= rate
        return Color.HSVToColor(hsv)
    }
}
