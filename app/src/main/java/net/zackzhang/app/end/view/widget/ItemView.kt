package net.zackzhang.app.end.view.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.widget_item_view.view.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.util.StringUtil

class ItemView : FrameLayout {

    private var mDscptTextColor = 0

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
        inflate(context, R.layout.widget_item_view, this)

        mDscptTextColor = text_dscpt.currentTextColor

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ItemView, defStyle, 0)
        image_icon.setImageDrawable(ta.getDrawable(R.styleable.ItemView_iconImageSrc))
        text_title.text = ta.getString(R.styleable.ItemView_titleText)
        setDescriptionText(ta.getString(R.styleable.ItemView_descriptionText))
        ta.recycle()
    }

    fun setDescriptionText(text: CharSequence?) {
        text_dscpt.text = colorDscptText(text)
    }

    fun setThemeColor(color: Int) {
        setImageIconTintColor(color)
        setDscptTextColor(color)
    }

    fun setImageIconTintColor(color: Int) {
        image_icon.imageTintList = ColorStateList.valueOf(color)
    }

    fun setDscptTextColor(color: Int) {
        if (mDscptTextColor == color) return
        mDscptTextColor = color
        text_dscpt.text = colorDscptText(text_dscpt.text)
    }

    private fun colorDscptText(text: CharSequence?) =
            if (text == null) null else StringUtil.addSpan(text, StringUtil.SPAN_COLOR, mDscptTextColor)
}
