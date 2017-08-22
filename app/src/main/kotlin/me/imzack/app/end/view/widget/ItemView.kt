package me.imzack.app.end.view.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.util.StringUtil

class ItemView : FrameLayout {

    @BindView(R.id.image_icon)
    lateinit var mImageIcon: ImageView
    @BindView(R.id.text_title)
    lateinit var mTitleText: TextView
    @BindView(R.id.text_dscpt)
    lateinit var mDscptText: TextView

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
        ButterKnife.bind(this)

        mDscptTextColor = mDscptText.currentTextColor

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ItemView, defStyle, 0)
        mImageIcon.setImageDrawable(ta.getDrawable(R.styleable.ItemView_iconImageSrc))
        mTitleText.text = ta.getString(R.styleable.ItemView_titleText)
        setDescriptionText(ta.getString(R.styleable.ItemView_descriptionText))
        ta.recycle()
    }

    fun setDescriptionText(text: CharSequence?) {
        mDscptText.text = colorDscptText(text)
    }

    fun setThemeColor(color: Int) {
        setImageIconTintColor(color)
        setDscptTextColor(color)
    }

    fun setImageIconTintColor(color: Int) {
        mImageIcon.imageTintList = ColorStateList.valueOf(color)
    }

    fun setDscptTextColor(color: Int) {
        if (mDscptTextColor == color) return
        mDscptTextColor = color
        mDscptText.text = colorDscptText(mDscptText.text)
    }

    private fun colorDscptText(text: CharSequence?) =
            if (text == null) null else StringUtil.addSpan(text, StringUtil.SPAN_COLOR, mDscptTextColor)
}
