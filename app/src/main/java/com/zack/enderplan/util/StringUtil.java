package com.zack.enderplan.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

public class StringUtil {

    /** 为给定字符串添加删除线 */
    public static SpannableString addStrikethroughSpan(String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /** 为给定字符串中的子字符串加粗 */
    public static SpannableString addBoldStyle(String str, String[] bolds) {
        SpannableString ss = new SpannableString(str);
        for (String bold : bolds) {
            int start = str.indexOf(bold);
            int end = start + bold.length();
            ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return ss;
    }

    public static String getFirstChar(String str) {
        return str.substring(0, 1);
    }
}
