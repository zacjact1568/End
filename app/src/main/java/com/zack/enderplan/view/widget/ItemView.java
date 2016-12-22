package com.zack.enderplan.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;

public class ItemView extends FrameLayout {

    private Drawable mIconDrawable;
    private String mTitleStr;
    private String mDscptStr;
    private boolean mIsDscptActive;

    private TextView mDscptText;

    private int mPrimaryColor, mTextLightGreyColor;

    public ItemView(Context context) {
        super(context);
        init(null, 0);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        loadAttrs(attrs, defStyle);
        loadResources();
        initViews();
    }

    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ItemView, defStyle, 0);
        mIconDrawable = ta.getDrawable(R.styleable.ItemView_iconImageSrc);
        mTitleStr = ta.getString(R.styleable.ItemView_titleText);
        mDscptStr = ta.getString(R.styleable.ItemView_descriptionText);
        mIsDscptActive = ta.getBoolean(R.styleable.ItemView_descriptionTextActive, false);
        ta.recycle();
    }

    private void loadResources() {
        mPrimaryColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mTextLightGreyColor = ContextCompat.getColor(getContext(), R.color.grey_text_light);
    }

    private void initViews() {
        inflate(getContext(), R.layout.widget_item_view, this);

        ((ImageView) findViewById(R.id.image_icon)).setImageDrawable(mIconDrawable);
        ((TextView) findViewById(R.id.text_title)).setText(mTitleStr);

        mDscptText = (TextView) findViewById(R.id.text_dscpt);
        setDescriptionText(mDscptStr, mIsDscptActive);
    }

    public void setDescriptionText(String text) {
        mDscptText.setText(text);
    }

    public void setDescriptionActive(boolean isActive) {
        mDscptText.setTextColor(isActive ? mPrimaryColor : mTextLightGreyColor);
    }

    public void setDescriptionText(String text, boolean isActive) {
        setDescriptionText(text);
        setDescriptionActive(isActive);
    }
}
