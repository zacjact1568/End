package me.imzack.app.ender.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * A checkable {@link LinearLayout} that can be used as the item of a {@link android.widget.ListView}.<br>
 * NOTE: This layout has to include one and only child view that implements {@link Checkable}.
 * @author Zack
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private boolean mChecked;

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked != mChecked) {
            mChecked = checked;
            refreshViewState();
            refreshDrawableState();
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

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mChecked) {
            mergeDrawableStates(drawableState, new int[]{android.R.attr.state_checked});
        }
        return drawableState;
    }

    private void refreshViewState() {
        Checkable checkableView = getCheckableView(this);
        if (checkableView != null) {
            checkableView.setChecked(mChecked);
        } else {
            //未找到实现了Checkable接口的控件
            throw new RuntimeException("No checkable child view found");
        }
    }

    /** 寻找实现了Checkable接口的控件（先序遍历）*/
    private Checkable getCheckableView(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof Checkable) {
                return (Checkable) childView;
            } else if (childView instanceof ViewGroup) {
                //如果子视图为嵌套布局，沿子视图搜索
                return getCheckableView((ViewGroup) childView);
            }
        }
        return null;
    }
}
