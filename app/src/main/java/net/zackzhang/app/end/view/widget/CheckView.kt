package net.zackzhang.app.end.view.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.ImageView

import net.zackzhang.app.end.R

/**
 * A custom view that can be set as checked or unchecked.
 *
 * @author Zack
 */
class CheckView : ImageView, Checkable {

    private var mCheckedColor = 0
    private var mUncheckedColor = 0
    private var mChecked = false

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        loadAttrs(attrs, defStyle)
        imageTintList = ColorStateList.valueOf(if (mChecked) mCheckedColor else mUncheckedColor)
    }

    private fun loadAttrs(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CheckView, defStyle, 0)
        mCheckedColor = ta.getColor(R.styleable.CheckView_checkedColor, Color.WHITE)
        mUncheckedColor = ta.getColor(R.styleable.CheckView_uncheckedColor, Color.TRANSPARENT)
        mChecked = ta.getBoolean(R.styleable.CheckView_checked, false)
        ta.recycle()
    }

    override fun setChecked(checked: Boolean) {
        if (checked != mChecked) {
            mChecked = checked
            imageTintList = ColorStateList.valueOf(if (mChecked) mCheckedColor else mUncheckedColor)
        }
    }

    override fun isChecked() = mChecked

    override fun toggle() {
        isChecked = !mChecked
    }
}
