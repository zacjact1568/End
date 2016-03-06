package com.zack.enderplan.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import java.util.UUID;

public class Util {

    public static String makeCode() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8);
    }

    /**
     * Add a strikethrough over the given string.
     * @param str The string that need to handle
     * @return The string with a strikethrough
     */
    public static SpannableString addStrikethroughSpan(String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /*public static int[] getPriorityLevelMarkResIds(Context context) {
        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.priority_level_marks);
        int length = typedArray.length();
        int[] resIds = new int[length];
        for (int i = 0; i < length; i++) {
            resIds[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        return resIds;
    }*/
}
