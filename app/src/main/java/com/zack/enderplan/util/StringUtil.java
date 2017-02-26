package com.zack.enderplan.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import java.util.Arrays;

public class StringUtil {

    public static final int SPAN_STRIKETHROUGH = 0;
    public static final int SPAN_BOLD_STYLE = 1;
    public static final int SPAN_UNDERLINE = 2;

    /** 整个字符串都添加span */
    public static SpannableString addSpan(String str, int span) {
        return addSpan(new SpannableString(str), span, 0, str.length());
    }

    /** 一个字符串不同段上添加相同的span */
    public static SpannableString addSpan(String str, String[] segs, int span) {
        int[] spans = new int[segs.length];
        Arrays.fill(spans, span);
        return addSpan(str, segs, spans);
    }

    /** 一个字符串不同段上添加不同的span */
    public static SpannableString addSpan(String str, String[] segs, int[] spans) {
        if (segs.length != spans.length) {
            throw new RuntimeException("The length of string segment array and span type array should be equal");
        }
        SpannableString ss = new SpannableString(str);
        for (int i = 0; i < segs.length; i++) {
            String seg = segs[i];
            addSpan(ss, spans[i], str.indexOf(seg), seg.length());
        }
        return ss;
    }

    private static SpannableString addSpan(SpannableString ss, int span, int start, int length) {
        Object what;
        switch (span) {
            case SPAN_STRIKETHROUGH:
                what = new StrikethroughSpan();
                break;
            case SPAN_BOLD_STYLE:
                what = new StyleSpan(Typeface.BOLD);
                break;
            case SPAN_UNDERLINE:
                what = new UnderlineSpan();
                break;
            default:
                throw new IllegalArgumentException("The argument span cannot be " + span);
        }
        ss.setSpan(what, start, start + length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return ss;
    }

    public static String getFirstChar(String str) {
        return str.substring(0, 1);
    }
}
