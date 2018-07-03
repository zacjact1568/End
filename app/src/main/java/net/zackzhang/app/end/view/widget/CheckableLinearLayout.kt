package net.zackzhang.app.end.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.LinearLayout

/**
 * A checkable [LinearLayout] that can be used as the item of a [android.widget.ListView].
 * 
 * NOTE: This layout has to include one and only child view that implements [Checkable].
 * 
 * @author Zack
 */
class CheckableLinearLayout : LinearLayout, Checkable {

    private var mChecked = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setChecked(checked: Boolean) {
        if (checked != mChecked) {
            mChecked = checked
            refreshViewState()
            refreshDrawableState()
        }
    }

    override fun isChecked() = mChecked

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (mChecked) {
            View.mergeDrawableStates(drawableState, intArrayOf(android.R.attr.state_checked))
        }
        return drawableState
    }

    private fun refreshViewState() {
        getCheckableView(this).isChecked = mChecked
    }

    /** 寻找实现了Checkable接口的控件（先序遍历） */
    private fun getCheckableView(parent: ViewGroup): Checkable {
        (0 until parent.childCount)
                .asSequence()
                .map { parent.getChildAt(it) }
                .forEach {
                    when (it) {
                        is Checkable -> return it
                        //如果子视图为嵌套布局，沿子视图搜索
                        is ViewGroup -> return getCheckableView(it)
                    }
                }
        //未找到实现了Checkable接口的控件
        throw RuntimeException("No checkable child view found")
    }
}
