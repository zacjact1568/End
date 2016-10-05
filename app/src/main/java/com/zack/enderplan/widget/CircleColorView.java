package com.zack.enderplan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.zack.enderplan.R;

public class CircleColorView extends View {

    private int mFillColor = Color.BLACK;
    private float mEdgeWidth = 0f;
    private int mEdgeColor = Color.GRAY;
    private String mInnerText;
    private int mInnerTextColor = Color.WHITE;

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
        mFillColor = ta.getColor(R.styleable.CircleColorView_fill_color, mFillColor);
        mEdgeWidth = ta.getDimension(R.styleable.CircleColorView_edge_width, mEdgeWidth);
        mEdgeColor = ta.getColor(R.styleable.CircleColorView_edge_color, mEdgeColor);
        mInnerText = ta.getString(R.styleable.CircleColorView_inner_text);
        mInnerTextColor = ta.getColor(R.styleable.CircleColorView_inner_text_color, mInnerTextColor);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircle(canvas);

        drawText(canvas);
    }

    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
        invalidate();
    }

    public void setInnerText(String innerText) {
        mInnerText = innerText;
        invalidate();
    }

    private void drawCircle(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int contentWidth = getWidth() - paddingLeft - getPaddingRight();
        int contentHeight = getHeight() - paddingTop - getPaddingBottom();

        mCenterX = paddingLeft + contentWidth / 2f;
        mCenterY = paddingTop + contentHeight / 2f;

        mDiameter = Math.min(contentWidth, contentHeight);

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
        if (mInnerText == null) return;

        mTextPaint.setTextSize(mDiameter / 2f);
        mTextPaint.setColor(mInnerTextColor);

        float textOffsetY = (mTextPaint.descent() - mTextPaint.ascent()) / 2f - mTextPaint.descent();

        canvas.drawText(mInnerText, mCenterX, mCenterY + textOffsetY, mTextPaint);
    }
}
