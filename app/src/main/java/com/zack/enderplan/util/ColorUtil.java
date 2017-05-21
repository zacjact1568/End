package com.zack.enderplan.util;

import android.graphics.Color;

public class ColorUtil {

    public static String parseColor(int colorInt) {
        return parseColor(colorInt, true);
    }

    public static String parseColor(int colorInt, boolean alpha) {
        String color = Long.toHexString(0x100000000L + colorInt);
        return String.format("#%s", alpha ? color : color.substring(2)).toUpperCase();
    }

    public static String parseColorChannel(int channelInt) {
        String channel = Integer.toHexString(channelInt).toUpperCase();
        return channel.length() == 1 ? "0" + channel : channel;
    }

    public static String[] parseColorChannels(int colorInt) {
        String color = parseColor(colorInt);
        return new String[]{
                color.substring(1, 2),
                color.substring(3, 4),
                color.substring(5, 6),
                color.substring(7, 8)
        };
    }

    public static String makeColor() {
        StringBuilder color = new StringBuilder("#FF");
        for (int i = 0; i < 3; i++) {
            color.append(parseColorChannel(CommonUtil.makeRandom(0, 255)));
        }
        return color.toString();
    }

    public static int setAlpha(int alpha, int color) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int reduceSaturation(int color, float rate) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] *= rate;
        return Color.HSVToColor(hsv);
    }
}
