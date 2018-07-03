package net.zackzhang.app.end.view.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.widget_color_picker.view.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.util.ColorUtil

class ColorPicker : FrameLayout, SeekBar.OnSeekBarChangeListener {

    private var mColor = 0
    private var mRed = 0
    private var mGreen = 0
    private var mBlue = 0

    var mOnColorChangedListener: ((color: Int) -> Unit)? = null

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
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ColorPicker, defStyle, 0)
        mColor = ta.getColor(R.styleable.ColorPicker_defaultColor, Color.BLACK)
        ta.recycle()
    }

    private fun initViews() {
        inflate(context, R.layout.widget_color_picker, this)

        setColor(mColor)

        bar_red.setOnSeekBarChangeListener(this)
        bar_green.setOnSeekBarChangeListener(this)
        bar_blue.setOnSeekBarChangeListener(this)
    }

    fun setColor(color: Int) {
        if (mColor == color) return

        mColor = color

        mRed = Color.red(color)
        bar_red.progress = mRed
        text_red.text = ColorUtil.parseColorChannel(mRed)

        mGreen = Color.green(color)
        bar_green.progress = mGreen
        text_green.text = ColorUtil.parseColorChannel(mGreen)

        mBlue = Color.blue(color)
        bar_blue.progress = mBlue
        text_blue.text = ColorUtil.parseColorChannel(mBlue)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.bar_red -> {
                mRed = progress
                text_red.text = ColorUtil.parseColorChannel(mRed)
            }
            R.id.bar_green -> {
                mGreen = progress
                text_green.text = ColorUtil.parseColorChannel(mGreen)
            }
            R.id.bar_blue -> {
                mBlue = progress
                text_blue.text = ColorUtil.parseColorChannel(mBlue)
            }
        }
        mColor = Color.rgb(mRed, mGreen, mBlue)
        mOnColorChangedListener?.invoke(mColor)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }
}
