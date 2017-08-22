package me.imzack.app.end.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View

import me.imzack.app.end.R
import me.imzack.app.end.util.CommonUtil
import me.imzack.app.end.util.StringUtil

class CircleColorView : View {

    private var mFillColor: Int = 0
    private var mEdgeWidth: Float = 0f
    private var mEdgeColor: Int = 0
    private var mInnerText: String? = null
    private var mInnerTextColor: Int = 0
    private var mInnerIcon: Drawable? = null
    private var mInnerIconTintColor: Int = 0

    private var mPaint = Paint()
    private var mTextPaint = TextPaint()

    private var mDiameter: Int = 0
    private var mCenterX: Float = 0f
    private var mCenterY: Float = 0f

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

        mPaint.style = Paint.Style.FILL
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true

        mTextPaint.style = Paint.Style.FILL
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    private fun loadAttrs(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleColorView, defStyle, 0)
        mFillColor = ta.getColor(R.styleable.CircleColorView_fillColor, Color.BLACK)
        mEdgeWidth = ta.getDimension(R.styleable.CircleColorView_edgeWidth, 0f)
        mEdgeColor = ta.getColor(R.styleable.CircleColorView_edgeColor, Color.WHITE)
        mInnerText = ta.getString(R.styleable.CircleColorView_innerText)
        mInnerTextColor = ta.getColor(R.styleable.CircleColorView_innerTextColor, Color.WHITE)
        mInnerIcon = ta.getDrawable(R.styleable.CircleColorView_innerIcon)
        mInnerIconTintColor = ta.getColor(R.styleable.CircleColorView_innerIconTintColor, Color.WHITE)
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        initSize()

        drawCircle(canvas)

        drawText(canvas)

        drawIcon(canvas)
    }

    private fun initSize() {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        mCenterX = paddingLeft + contentWidth / 2f
        mCenterY = paddingTop + contentHeight / 2f

        mDiameter = Math.min(contentWidth, contentHeight)
    }

    private fun drawCircle(canvas: Canvas) {

        val edgeRadius = mDiameter / 2f

        if (mEdgeWidth > 0f) {
            mPaint.color = mEdgeColor
            canvas.drawCircle(mCenterX, mCenterY, edgeRadius, mPaint)
        }

        val fillRadius = edgeRadius - mEdgeWidth

        mPaint.color = mFillColor
        canvas.drawCircle(mCenterX, mCenterY, fillRadius, mPaint)
    }

    private fun drawText(canvas: Canvas) {
        // 若没有文本字符串或者有图标，不绘制文本
        if (TextUtils.isEmpty(mInnerText) || mInnerIcon != null) return

        // 经过上句的判断，这里mInnerText肯定不为null了，不知道为什么没有自动转换成非空类型
        if (mInnerText!!.length > 1) {
            mInnerText = StringUtil.getFirstChar(mInnerText!!)
        }

        mTextPaint.textSize = mDiameter / 2f
        mTextPaint.color = mInnerTextColor

        val textOffsetY = (mTextPaint.descent() - mTextPaint.ascent()) / 2f - mTextPaint.descent()

        canvas.drawText(mInnerText!!, mCenterX, mCenterY + textOffsetY, mTextPaint)
    }

    private fun drawIcon(canvas: Canvas) {
        if (mInnerIcon == null) return

        val radius = mDiameter / 4f
        // 这里mInnerIcon也一定不为空
        mInnerIcon!!.setBounds(
                (mCenterX - radius).toInt(),
                (mCenterY - radius).toInt(),
                (mCenterX + radius).toInt(),
                (mCenterY + radius).toInt()
        )
        mInnerIcon!!.setTint(mInnerIconTintColor)
        mInnerIcon!!.draw(canvas)
    }

    fun setFillColor(fillColor: Int) {
        if (fillColor == mFillColor) return
        mFillColor = fillColor
        invalidate()
    }

    fun setEdgeWidth(edgeWidth: Float) {
        if (edgeWidth == mEdgeWidth) return
        mEdgeWidth = edgeWidth
        invalidate()
    }

    fun setEdgeColor(edgeColor: Int) {
        if (edgeColor == mEdgeColor) return
        mEdgeColor = edgeColor
        invalidate()
    }

    fun setInnerText(innerText: String?) {
        if (CommonUtil.isObjectEqual(innerText, mInnerText)) return
        mInnerText = innerText
        invalidate()
    }

    fun setInnerIcon(innerIcon: Drawable?) {
        if (CommonUtil.isObjectEqual(innerIcon, mInnerIcon)) return
        mInnerIcon = innerIcon
        mInnerIcon?.setTint(mInnerIconTintColor)
        invalidate()
    }

    fun setInnerIconTintColor(innerIconTintColor: Int) {
        if (innerIconTintColor == mInnerIconTintColor) return
        mInnerIconTintColor = innerIconTintColor
        invalidate()
    }
}
