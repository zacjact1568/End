package com.zack.enderplan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.zack.enderplan.R;

/**
 * A custom view that can be set as checked or unchecked.
 * @author Zack
 */
public class CheckView extends View implements Checkable {

    private Drawable mCheckIcon;
    private boolean mChecked = false;

    public CheckView(Context context) {
        super(context);
        init(null, 0);
    }

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CheckView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        loadAttrs(attrs, defStyle);
        setVisibility(mChecked ? VISIBLE : INVISIBLE);
    }

    /** 加载自定义的属性 */
    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CheckView, defStyle, 0);
        mCheckIcon = ta.getDrawable(R.styleable.CheckView_check_icon);
        mChecked = ta.getBoolean(R.styleable.CheckView_checked, mChecked);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int contentWidth = getWidth() - paddingLeft - getPaddingRight();
        int contentHeight = getHeight() - paddingTop - getPaddingBottom();

        if (mCheckIcon != null) {
            mCheckIcon.setBounds(paddingLeft, paddingTop, paddingLeft + contentWidth, paddingTop + contentHeight);
            mCheckIcon.draw(canvas);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked != mChecked) {
            mChecked = checked;
            setVisibility(mChecked ? VISIBLE : INVISIBLE);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
