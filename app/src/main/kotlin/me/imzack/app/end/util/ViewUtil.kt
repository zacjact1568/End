package me.imzack.app.end.util

import android.graphics.Bitmap
import android.view.View

object ViewUtil {

    /**
     * 通过反射获取未绘制控件的高<br>
     * Reference: https://my.oschina.net/u/269663/blog/388980
     * @param view 要获取高的控件
     * @return 控件的高
     */
    fun getViewHeight(view: View): Int {
        try {
            val method = view.javaClass.getDeclaredMethod("onMeasure", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(
                    view,
                    View.MeasureSpec.makeMeasureSpec((view.parent as View).measuredWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view.measuredHeight
    }

    fun convertViewToBitmap(view: View, size: Int): Bitmap {
        val measureSpec = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
        view.measure(measureSpec, measureSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        return view.drawingCache
    }

    fun getScreenCoordinateY(view: View): Int {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return location[1]
    }
}
