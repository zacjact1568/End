package me.imzack.app.end.util

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.*

object StringUtil {

    const val SPAN_STRIKETHROUGH = 0
    const val SPAN_BOLD_STYLE = 1
    const val SPAN_UNDERLINE = 2
    const val SPAN_COLOR = 3
    const val SPAN_URL = 4
    const val SPAN_CLICKABLE = 5

    /** 整个字符串都添加span */
    fun addSpan(cs: CharSequence, span: Int, extra: Any? = null): SpannableString {
        val ss = SpannableString(cs)
        addSpan(ss, span, extra, 0, cs.length)
        return ss
    }

    /** 一个字符串不同段上添加span，参数segs只表示段的类型（每一种段可以在字符串中重复），无视大小写，允许重叠 */
    fun addSpan(cs: CharSequence, segs: Array<String>, span: Int, extra: Any? = null): SpannableString {
        val ss = SpannableString(cs)
        for (seg in segs) {
            val segLocationList = getSubstringLocationList(cs.toString(), seg)
            for (segLocation in segLocationList) {
                addSpan(ss, span, extra, segLocation, seg.length)
            }
        }
        return ss
    }

    /** 一个字符串不同段上添加不同的span，段必须和span一一对应，即不允许重复 */
    fun addSpan(cs: CharSequence, segs: Array<String>, spans: IntArray, extras: Array<Any>): SpannableString {
        if (segs.size != spans.size) {
            throw RuntimeException("The length of string segment array and span type array should be equal")
        }
        if (spans.size != extras.size) {
            throw RuntimeException("The length of span type array and extra array should be equal")
        }
        val ss = SpannableString(cs)
        for (i in segs.indices) {
            val seg = segs[i]
            addSpan(ss, spans[i], extras[i], cs.toString().indexOf(seg), seg.length)
        }
        return ss
    }

    private fun addSpan(ss: SpannableString, span: Int, extra: Any?, start: Int, length: Int) {
        ss.setSpan(when (span) {
            SPAN_STRIKETHROUGH -> StrikethroughSpan()
            // TODO bold style -> int extra
            SPAN_BOLD_STYLE -> StyleSpan(Typeface.BOLD)
            SPAN_UNDERLINE -> UnderlineSpan()
            SPAN_COLOR -> ForegroundColorSpan(extra as Int)
            SPAN_URL -> URLSpan(extra as String)
            SPAN_CLICKABLE -> extra
            else -> throw IllegalArgumentException("The argument span cannot be $span")
        }, start, start + length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
    }

    /** 添加白色字体 span */
    fun addWhiteColorSpan(cs: CharSequence) = addSpan(cs, SPAN_COLOR, Color.WHITE)

    fun getFirstChar(str: String) = str.substring(0, 1)

    /** 将主串中所有子串转为大写，忽略大小写，允许重叠 */
    fun toUpperCase(str: String, segs: Array<String>): String {
        val sb = StringBuilder(str)
        for (seg in segs) {
            val segLocationList = getSubstringLocationList(str, seg)
            for (segLocation in segLocationList) {
                sb.replace(segLocation, segLocation + seg.length, seg.toUpperCase())
            }
        }
        return sb.toString()
    }

    /** 获取所有子串在主串中的起始位置，忽略大小写，允许重叠 */
    private fun getSubstringLocationList(str: String, sub: String): List<Int> {
        val locationList = mutableListOf<Int>()
        var index = 0
        var location: Int
        while (index < str.length - sub.length) {
            location = str.indexOf(sub, index, true)
            if (location == -1) break
            locationList.add(location)
            // 从下一个字符开始查找
            index = location + 1
        }
        return locationList
    }

    /** 字符串长度，英文算1位，中文算2位 */
    fun getLength(str: String): Int {
        var n = 0
        val chs = str.toCharArray()
        for (ch in chs) {
            if (ch.toInt() <= 0x00FF) {
                //英文字符
                n += 1
            } else if (ch.toInt() in 0x0391..0xFFE5) {
                //中文字符
                n += 2
            }
        }
        return n
    }
}
