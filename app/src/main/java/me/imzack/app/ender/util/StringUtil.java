package me.imzack.app.ender.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static final int SPAN_STRIKETHROUGH = 0;
    public static final int SPAN_BOLD_STYLE = 1;
    public static final int SPAN_UNDERLINE = 2;
    public static final int SPAN_COLOR = 3;
    public static final int SPAN_URL = 4;
    public static final int SPAN_CLICKABLE = 5;

    public static SpannableString addSpan(CharSequence cs, int span) {
        return addSpan(cs, span, null);
    }

    /** 整个字符串都添加span */
    public static SpannableString addSpan(CharSequence cs, int span, Object extra) {
        SpannableString ss = new SpannableString(cs);
        addSpan(ss, span, extra, 0, cs.length());
        return ss;
    }

    public static SpannableString addSpan(CharSequence cs, String[] segs, int span) {
        return addSpan(cs, segs, span, null);
    }

    /** 一个字符串不同段上添加span，参数segs只表示段的类型（每一种段可以在字符串中重复），无视大小写，允许重叠 */
    public static SpannableString addSpan(CharSequence cs, String[] segs, int span, Object extra) {
        SpannableString ss = new SpannableString(cs);
        for (String seg : segs) {
            List<Integer> segLocationList = getSubstringLocationList(cs.toString(), seg);
            for (int segLocation : segLocationList) {
                addSpan(ss, span, extra, segLocation, seg.length());
            }
        }
        return ss;
    }

    /** 一个字符串不同段上添加不同的span，段必须和span一一对应，即不允许重复 */
    public static SpannableString addSpan(CharSequence cs, String[] segs, int[] spans, Object[] extras) {
        if (segs.length != spans.length) {
            throw new RuntimeException("The length of string segment array and span type array should be equal");
        }
        if (spans.length != extras.length) {
            throw new RuntimeException("The length of span type array and extra array should be equal");
        }
        SpannableString ss = new SpannableString(cs);
        for (int i = 0; i < segs.length; i++) {
            String seg = segs[i];
            addSpan(ss, spans[i], extras[i], cs.toString().indexOf(seg), seg.length());
        }
        return ss;
    }

    private static void addSpan(SpannableString ss, int span, Object extra, int start, int length) {
        Object what;
        switch (span) {
            case SPAN_STRIKETHROUGH:
                what = new StrikethroughSpan();
                break;
            case SPAN_BOLD_STYLE:
                //TODO 将int extra提取出来
                what = new StyleSpan(Typeface.BOLD);
                break;
            case SPAN_UNDERLINE:
                what = new UnderlineSpan();
                break;
            case SPAN_COLOR:
                what = new ForegroundColorSpan((int) extra);
                break;
            case SPAN_URL:
                what = new URLSpan((String) extra);
                break;
            case SPAN_CLICKABLE:
                what = extra;
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

    /** 字符串长度，英文算1位，中文算2位 */
    public static int getLength(String str) {
        int n = 0;
        char chs[] = str.toCharArray();
        for (char ch : chs) {
            if (ch <= 0x00FF) {
                //英文字符
                n = n + 1;
            } else if ((ch >= 0x0391 && ch <= 0xFFE5)) {
                //中文字符
                n = n + 2;
            }
        }
        return n;
    }
}
