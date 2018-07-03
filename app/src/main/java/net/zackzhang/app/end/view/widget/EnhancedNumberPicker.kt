package net.zackzhang.app.end.view.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker

import net.zackzhang.app.end.R

class EnhancedNumberPicker : NumberPicker {

    private var mDividerColor = 0

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
        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        val ta = context.obtainStyledAttributes(attrs, R.styleable.EnhancedNumberPicker, defStyle, 0)
        setDividerColor(ta.getColor(R.styleable.EnhancedNumberPicker_dividerColor, -1))
        ta.recycle()
    }

    override fun addView(child: View) {
        super.addView(child)
        updateChild(child)
    }

    override fun addView(child: View, index: Int) {
        super.addView(child, index)
        updateChild(child)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        updateChild(child)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        super.addView(child, params)
        updateChild(child)
    }

    override fun addView(child: View, width: Int, height: Int) {
        super.addView(child, width, height)
        updateChild(child)
    }

    fun setDividerColor(color: Int) {
        //TODO 注意其他的view，由于下面这条语句，可能无法初始化
        if (mDividerColor == color || color == -1) return
        mDividerColor = color
        try {
            val field = NumberPicker::class.java.getDeclaredField("mSelectionDivider")
            field.isAccessible = true
            field.set(this, ColorDrawable(mDividerColor))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun updateChild(child: View) {
        if (child is EditText) {
            child.textSize = 16f
        }
    }

    private val editText: EditText?
        get() {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is EditText) {
                    return child
                }
            }
            return null
        }
}
