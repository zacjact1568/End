package com.zack.enderplan.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Util;

public class CircleColorView extends View {

    private int mFillColor;
    private float mEdgeWidth;
    private int mEdgeColor;
    private String mInnerText;
    private int mInnerTextColor;
    private Drawable mInnerIcon;
    private int mInnerIconTintColor;

    private Paint mPaint;
    private TextPaint mTextPaint;

    private int mDiameter;
    private float mCenterX;
    private float mCenterY;

    public CircleColorView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleColorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        loadAttrs(attrs, defStyle);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    /** 加载自定义的属性 */
    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircleColorView, defStyle, 0);
        mFillColor = ta.getColor(R.styleable.CircleColorView_fillColor, Color.BLACK);
        mEdgeWidth = ta.getDimension(R.styleable.CircleColorView_edgeWidth, 0f);
        mEdgeColor = ta.getColor(R.styleable.CircleColorView_edgeColor, Color.WHITE);
        mInnerText = ta.getString(R.styleable.CircleColorView_innerText);
        mInnerTextColor = ta.getColor(R.styleable.CircleColorView_innerTextColor, Color.WHITE);
        mInnerIcon = ta.getDrawable(R.styleable.CircleColorView_innerIcon);
        mInnerIconTintColor = ta.getColor(R.styleable.CircleColorView_innerIconTintColor, Color.WHITE);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initSize();

        drawCircle(canvas);

        drawText(canvas);

        drawIcon(canvas);
    }

    private void initSize() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int contentWidth = getWidth() - paddingLeft - getPaddingRight();
        int contentHeight = getHeight() - paddingTop - getPaddingBottom();

        mCenterX = paddingLeft + contentWidth / 2f;
        mCenterY = paddingTop + contentHeight / 2f;

        mDiameter = Math.min(contentWidth, contentHeight);
    }

    private void drawCircle(Canvas canvas) {

        float edgeRadius = mDiameter / 2f;

        if (mEdgeWidth > 0f) {
            mPaint.setColor(mEdgeColor);
            canvas.drawCircle(mCenterX, mCenterY, edgeRadius, mPaint);
        }

        float fillRadius = edgeRadius - mEdgeWidth;

        mPaint.setColor(mFillColor);
        canvas.drawCircle(mCenterX, mCenterY, fillRadius, mPaint);
    }

    private void drawText(Canvas canvas) {
        if (mInnerText == null || mInnerText.isEmpty() || mInnerIcon != null) return;

        if (mInnerText.length() > 1) {
            mInnerText = Util.getFirstChar(mInnerText);
        }

        mTextPaint.setTextSize(mDiameter / 2f);
        mTextPaint.setColor(mInnerTextColor);

        float textOffsetY = (mTextPaint.descent() - mTextPaint.ascent()) / 2f - mTextPaint.descent();

        canvas.drawText(mInnerText, mCenterX, mCenterY + textOffsetY, mTextPaint);
    }

    private void drawIcon(Canvas canvas) {
        if (mInnerIcon == null) return;

        float radius = mDiameter / 4f;
        mInnerIcon.setBounds(
                (int) (mCenterX - radius),
                (int) (mCenterY - radius),
                (int) (mCenterX + radius),
                (int) (mCenterY + radius)
        );
        mInnerIcon.setTint(mInnerIconTintColor);
        mInnerIcon.draw(canvas);
    }

    public void setFillColor(int fillColor) {
        if (fillColor == mFillColor) return;
        mFillColor = fillColor;
        invalidate();
    }

    public void setInnerText(String innerText) {
        if (Util.isObjectEqual(innerText, mInnerText)) return;
        mInnerText = innerText;
        invalidate();
    }

    public void setInnerIcon(Drawable innerIcon) {
        if (Util.isObjectEqual(innerIcon, mInnerIcon)) return;
        mInnerIcon = innerIcon;
        if (mInnerIcon != null) {
            mInnerIcon.setTint(mInnerIconTintColor);
        }
        invalidate();
    }
}
