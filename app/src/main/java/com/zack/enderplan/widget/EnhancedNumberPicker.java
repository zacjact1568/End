package com.zack.enderplan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.zack.enderplan.R;

import java.lang.reflect.Field;

public class EnhancedNumberPicker extends NumberPicker {

    private int mDividerColor;

    public EnhancedNumberPicker(Context context) {
        super(context);
        init(null, 0);
    }

    public EnhancedNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EnhancedNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.EnhancedNumberPicker, defStyle, 0);
        setDividerColor(ta.getColor(R.styleable.EnhancedNumberPicker_dividerColor, -1));
        ta.recycle();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateChild(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        updateChild(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateChild(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateChild(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        updateChild(child);
    }

    public void setDividerColor(int color) {
        //TODO 注意其他的view，由于下面这条语句，可能无法初始化
        if (mDividerColor == color || color == -1) return;
        mDividerColor = color;
        try {
            Field field = NumberPicker.class.getDeclaredField("mSelectionDivider");
            field.setAccessible(true);
            field.set(this, new ColorDrawable(mDividerColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateChild(View child) {
        if (child instanceof EditText) {
            ((EditText) child).setTextSize(16);
        }
    }

    private EditText getEditText() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                return (EditText) child;
            }
        }
        return null;
    }
}
