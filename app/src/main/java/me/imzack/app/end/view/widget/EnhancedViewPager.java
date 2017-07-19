package me.imzack.app.end.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class EnhancedViewPager extends ViewPager {

    private boolean mScrollingEnabled = true;

    public EnhancedViewPager(Context context) {
        super(context);
    }

    public EnhancedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mScrollingEnabled && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mScrollingEnabled && super.onInterceptTouchEvent(ev);
    }

    /**
     * 开启或关闭ViewPager的左右滑动
     * @param scrollingEnabled ViewPager是否可以左右滑动
     */
    public void setScrollingEnabled(boolean scrollingEnabled) {
        mScrollingEnabled = scrollingEnabled;
    }
}
