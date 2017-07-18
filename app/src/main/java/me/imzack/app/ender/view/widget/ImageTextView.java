package me.imzack.app.ender.view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.imzack.app.ender.R;

public class ImageTextView extends LinearLayout {

    private Drawable mImage;
    private String mText;
    private float mSize;
    private int mColor;

    private TextView mTextView;

    public ImageTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        loadAttrs(attrs, defStyle);
        initViews();
    }

    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ImageTextView, defStyle, 0);
        mImage = ta.getDrawable(R.styleable.ImageTextView_image);
        mText = ta.getString(R.styleable.ImageTextView_text);
        mSize = ta.getDimension(R.styleable.ImageTextView_size, 0f);
        mColor = ta.getColor(R.styleable.ImageTextView_color, Color.BLACK);
        ta.recycle();
    }

    private void initViews() {
        setGravity(Gravity.CENTER);

        ImageView image = new ImageView(getContext());
        image.setImageDrawable(mImage);
        image.setImageTintList(ColorStateList.valueOf(mColor));

        addView(image, new LayoutParams((int) mSize, (int) mSize));

        mTextView = new TextView(getContext());
        mTextView.setTextSize(mSize / 3);
        mTextView.setTextColor(mColor);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart((int) (mSize / 4));

        addView(mTextView, params);

        setText(mText);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
}
