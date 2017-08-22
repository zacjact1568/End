package me.imzack.app.end.view.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.util.ColorUtil

class ColorPicker : FrameLayout, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.bar_red)
    lateinit var mRedBar: SeekBar
    @BindView(R.id.text_red)
    lateinit var mRedText: TextView
    @BindView(R.id.bar_green)
    lateinit var mGreenBar: SeekBar
    @BindView(R.id.text_green)
    lateinit var mGreenText: TextView
    @BindView(R.id.bar_blue)
    lateinit var mBlueBar: SeekBar
    @BindView(R.id.text_blue)
    lateinit var mBlueText: TextView

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

        ButterKnife.bind(this)

        setColor(mColor)

        mRedBar.setOnSeekBarChangeListener(this)
        mGreenBar.setOnSeekBarChangeListener(this)
        mBlueBar.setOnSeekBarChangeListener(this)
    }

    fun setColor(color: Int) {
        if (mColor == color) return

        mColor = color

        mRed = Color.red(color)
        mRedBar.progress = mRed
        mRedText.text = ColorUtil.parseColorChannel(mRed)

        mGreen = Color.green(color)
        mGreenBar.progress = mGreen
        mGreenText.text = ColorUtil.parseColorChannel(mGreen)

        mBlue = Color.blue(color)
        mBlueBar.progress = mBlue
        mBlueText.text = ColorUtil.parseColorChannel(mBlue)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.bar_red -> {
                mRed = progress
                mRedText.text = ColorUtil.parseColorChannel(mRed)
            }
            R.id.bar_green -> {
                mGreen = progress
                mGreenText.text = ColorUtil.parseColorChannel(mGreen)
            }
            R.id.bar_blue -> {
                mBlue = progress
                mBlueText.text = ColorUtil.parseColorChannel(mBlue)
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
