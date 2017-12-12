package me.imzack.app.end.view.callback

import android.animation.ObjectAnimator
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

@Suppress("unused")
class FloatingActionButtonScrollBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior(context, attrs) {

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton,
                                     directTargetChild: View, target: View, axes: Int, type: Int) =
            axes == ViewCompat.SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View,
                                dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        //consumed表示target此时可以跟随用户手势滑动，unconsumed表示target已经滑动到边界但是用户还在尝试滑动
        val translationY = child.height + (child.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        if (dyConsumed > 0 && child.translationY == 0f) {
            //向下滑动的手势，并且fab已移动到顶，隐藏fab
            moveFab(child, 0f, translationY.toFloat())
        } else if (dyConsumed < 0 && child.translationY == translationY.toFloat()) {
            //向上滑动的手势，并且fab已移动到底，显示fab
            moveFab(child, translationY.toFloat(), 0f)
        }
    }

    private fun moveFab(fab: FloatingActionButton, fromTranslationY: Float, toTranslationY: Float) {
        ObjectAnimator.ofFloat(fab, "translationY", fromTranslationY, toTranslationY)
                .setDuration(100)
                .start()
    }
}
