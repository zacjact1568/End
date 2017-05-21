package com.zack.enderplan.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static final int SPAN_STRIKETHROUGH = 0;
    public static final int SPAN_BOLD_STYLE = 1;
    public static final int SPAN_UNDERLINE = 2;

    /** 整个字符串都添加span */
    public static SpannableString addSpan(String str, int span) {
        SpannableString ss = new SpannableString(str);
        addSpan(ss, span, 0, str.length());
        return ss;
    }

    /** 一个字符串不同段上添加span，参数segs只表示段的类型（每一种段可以在字符串中重复），无视大小写，允许重叠 */
    public static SpannableString addSpan(String str, String[] segs, int span) {
        SpannableString ss = new SpannableString(str);
        for (String seg : segs) {
            List<Integer> segLocationList = getSubstringLocationList(str, seg);
            for (int segLocation : segLocationList) {
                addSpan(ss, span, segLocation, seg.length());
            }
        }
        return ss;
    }

    /** 一个字符串不同段上添加不同的span，段必须和span一一对应，即不允许重复 */
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

    private static void addSpan(SpannableString ss, int span, int start, int length) {
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
    }

    public static String getFirstChar(String str) {
        return str.substring(0, 1);
    }

    /** 将主串中所有子串转为大写，无视大小写，允许重叠 */
    public static String toUpperCase(String str, String[] segs) {
        StringBuilder sb = new StringBuilder(str);
        for (String seg : segs) {
            List<Integer> segLocationList = getSubstringLocationList(str, seg);
            for (int segLocation : segLocationList) {
                sb.replace(segLocation, segLocation + seg.length(), seg.toUpperCase());
            }
        }
        return sb.toString();
    }

    /** 获取所有子串在主串中的起始位置（暴力算法），无视大小写，允许重叠 */
    //TODO String.replaceAll
    private static List<Integer> getSubstringLocationList(String str, String sub) {
        List<Integer> locationList = new ArrayList<>();
        char[] strChs = str.toCharArray();
        char[] subChs = sub.toCharArray();
        for (int i = 0; i < strChs.length - subChs.length; i++) {
            for (int j = 0; j < subChs.length; j++) {
                if (Character.toLowerCase(strChs[i + j]) != Character.toLowerCase(subChs[j])) break;
                if (j == subChs.length - 1) {
                    locationList.add(i);
                }
            }
        }
        return locationList;
    }
}
