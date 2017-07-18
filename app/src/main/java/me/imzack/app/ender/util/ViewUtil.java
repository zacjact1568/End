package me.imzack.app.ender.util;

import android.graphics.Bitmap;
import android.view.View;

import java.lang.reflect.Method;

public class ViewUtil {

    /**
     * 通过反射获取未绘制控件的高<br>
     * Reference: https://my.oschina.net/u/269663/blog/388980
     * @param view 要获取高的控件
     * @return 控件的高
     */
    public static int getViewHeight(View view) {
        try {
            Method method = view.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            method.setAccessible(true);
            method.invoke(
                    view,
                    View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view.getMeasuredHeight();
    }

    public static Bitmap convertViewToBitmap(View view, int size) {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY);
        view.measure(measureSpec, measureSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    public static int getScreenCoordinateY(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[1];
    }
}
