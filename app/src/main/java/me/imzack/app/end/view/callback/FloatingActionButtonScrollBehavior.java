package me.imzack.app.end.view.callback;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings("unused")
public class FloatingActionButtonScrollBehavior extends FloatingActionButton.Behavior {

    public FloatingActionButtonScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //consumed表示target此时可以跟随用户手势滑动，unconsumed表示target已经滑动到边界但是用户还在尝试滑动
        int translationY = child.getHeight() + ((ViewGroup.MarginLayoutParams) child.getLayoutParams()).bottomMargin;
        if (dyConsumed > 0 && child.getTranslationY() == 0) {
            //向下滑动的手势，并且fab已移动到顶，隐藏fab
            moveFab(child, 0, translationY);
        } else if (dyConsumed < 0 && child.getTranslationY() == translationY) {
            //向上滑动的手势，并且fab已移动到底，显示fab
            moveFab(child, translationY, 0);
        }
    }

    private void moveFab(FloatingActionButton fab, float fromTranslationY, float toTranslationY) {
        ObjectAnimator.ofFloat(fab, "translationY", fromTranslationY, toTranslationY)
                .setDuration(100)
                .start();
    }
}
