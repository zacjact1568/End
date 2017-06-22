package com.zack.enderplan.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;

public class ItemView extends FrameLayout {

    private Drawable mIconDrawable;
    private String mTitleStr;
    private CharSequence mDscptStr;

    private TextView mDscptText;

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
        initViews();
    }

    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ItemView, defStyle, 0);
        mIconDrawable = ta.getDrawable(R.styleable.ItemView_iconImageSrc);
        mTitleStr = ta.getString(R.styleable.ItemView_titleText);
        mDscptStr = ta.getString(R.styleable.ItemView_descriptionText);
        ta.recycle();
    }

    private void initViews() {
        inflate(getContext(), R.layout.widget_item_view, this);

        ((ImageView) findViewById(R.id.image_icon)).setImageDrawable(mIconDrawable);
        ((TextView) findViewById(R.id.text_title)).setText(mTitleStr);

        mDscptText = (TextView) findViewById(R.id.text_dscpt);
        setDescriptionText(mDscptStr);
    }

    public void setDescriptionText(CharSequence text) {
        mDscptText.setText(text);
    }
}
