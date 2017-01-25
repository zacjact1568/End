package com.zack.enderplan.util;

public class ColorUtil {

    public static String parseColor(int colorInt) {
        return String.format("#%s", Long.toHexString(0x100000000L + colorInt)).toUpperCase();
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
        StringBuilder color = new StringBuilder("#");
        //限制透明度
        color.append(parseColorChannel(CommonUtil.makeRandom(156, 255)));
        //RGB颜色
        for (int i = 0; i < 3; i++) {
            color.append(parseColorChannel(CommonUtil.makeRandom(0, 255)));
        }
        return color.toString();
    }
}
