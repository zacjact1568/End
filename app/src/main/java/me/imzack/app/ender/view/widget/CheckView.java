package me.imzack.app.ender.view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

import me.imzack.app.ender.R;

/**
 * A custom view that can be set as checked or unchecked.
 * @author Zack
 */
public class CheckView extends ImageView implements Checkable {

    private int mCheckedColor, mUncheckedColor;
    private boolean mChecked;

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
        setImageTintList(ColorStateList.valueOf(mChecked ? mCheckedColor : mUncheckedColor));
    }

    /** 加载自定义的属性 */
    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CheckView, defStyle, 0);
        mCheckedColor = ta.getColor(R.styleable.CheckView_checkedColor, Color.WHITE);
        mUncheckedColor = ta.getColor(R.styleable.CheckView_uncheckedColor, Color.TRANSPARENT);
        mChecked = ta.getBoolean(R.styleable.CheckView_checked, false);
        ta.recycle();
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked != mChecked) {
            mChecked = checked;
            setImageTintList(ColorStateList.valueOf(mChecked ? mCheckedColor : mUncheckedColor));
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
