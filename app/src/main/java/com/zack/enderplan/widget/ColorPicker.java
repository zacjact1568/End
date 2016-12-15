package com.zack.enderplan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColorPicker extends FrameLayout implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.bar_alpha)
    SeekBar mAlphaBar;
    @BindView(R.id.text_alpha)
    TextView mAlphaText;
    @BindView(R.id.bar_red)
    SeekBar mRedBar;
    @BindView(R.id.text_red)
    TextView mRedText;
    @BindView(R.id.bar_green)
    SeekBar mGreenBar;
    @BindView(R.id.text_green)
    TextView mGreenText;
    @BindView(R.id.bar_blue)
    SeekBar mBlueBar;
    @BindView(R.id.text_blue)
    TextView mBlueText;

    private int mColor;
    private int mAlpha, mRed, mGreen, mBlue;
    private OnColorChangedListener mOnColorChangedListener;

    public ColorPicker(Context context) {
        super(context);
        init(null, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        loadAttrs(attrs, defStyle);
        initViews();
    }

    private void loadAttrs(AttributeSet attrs, int defStyle) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker, defStyle, 0);
        mColor = ta.getColor(R.styleable.ColorPicker_defaultColor, Color.BLACK);
        ta.recycle();
    }

    private void initViews() {
        inflate(getContext(), R.layout.widget_color_picker, this);

        ButterKnife.bind(this);

        setColor(mColor);

        mAlphaBar.setOnSeekBarChangeListener(this);
        mRedBar.setOnSeekBarChangeListener(this);
        mGreenBar.setOnSeekBarChangeListener(this);
        mBlueBar.setOnSeekBarChangeListener(this);
    }

    public void setColor(int color) {
        if (mColor == color) return;

        mColor = color;

        mAlpha = Color.alpha(color);
        mAlphaBar.setProgress(mAlpha - 156);
        mAlphaText.setText(Util.parseColorChannel(mAlpha));

        mRed = Color.red(color);
        mRedBar.setProgress(mRed);
        mRedText.setText(Util.parseColorChannel(mRed));

        mGreen = Color.green(color);
        mGreenBar.setProgress(mGreen);
        mGreenText.setText(Util.parseColorChannel(mGreen));

        mBlue = Color.blue(color);
        mBlueBar.setProgress(mBlue);
        mBlueText.setText(Util.parseColorChannel(mBlue));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.bar_alpha:
                mAlpha = progress + 156;
                mAlphaText.setText(Util.parseColorChannel(mAlpha));
                break;
            case R.id.bar_red:
                mRed = progress;
                mRedText.setText(Util.parseColorChannel(mRed));
                break;
            case R.id.bar_green:
                mGreen = progress;
                mGreenText.setText(Util.parseColorChannel(mGreen));
                break;
            case R.id.bar_blue:
                mBlue = progress;
                mBlueText.setText(Util.parseColorChannel(mBlue));
                break;
        }
        mColor = Color.argb(mAlpha, mRed, mGreen, mBlue);
        if (mOnColorChangedListener != null) {
            mOnColorChangedListener.onColorChanged(mColor);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        mOnColorChangedListener = listener;
    }
}
