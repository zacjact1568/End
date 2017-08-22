package me.imzack.app.end.view.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import me.imzack.app.end.R

class ImageTextView : LinearLayout {

    // 下述变量可以直接赋值，但之后需要调用对应的update*方法
    var mImage: Drawable? = null
    var mText: String? = null
    var mSize: Float = 0f
    var mColor: Int = 0

    private val mImageView = ImageView(context)
    private val mTextView = TextView(context)

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
        initViews()
    }

    private fun loadAttrs(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView, defStyle, 0)
        mImage = ta.getDrawable(R.styleable.ImageTextView_image)
        mText = ta.getString(R.styleable.ImageTextView_text)
        mSize = ta.getDimension(R.styleable.ImageTextView_size, 0f)
        mColor = ta.getColor(R.styleable.ImageTextView_color, Color.BLACK)
        ta.recycle()
    }

    private fun initViews() {
        gravity = Gravity.CENTER

        mImageView.setImageDrawable(mImage)
        mImageView.imageTintList = ColorStateList.valueOf(mColor)

        addView(mImageView, LinearLayout.LayoutParams(mSize.toInt(), mSize.toInt()))

        mTextView.textSize = mSize / 3
        mTextView.setTextColor(mColor)

        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.marginStart = (mSize / 4).toInt()

        addView(mTextView, params)

        updateText()
    }

    fun updateText() {
        mTextView.text = mText
    }
}
