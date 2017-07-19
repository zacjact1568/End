package me.imzack.app.end.view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import me.imzack.app.end.R;
import me.imzack.app.end.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemView extends FrameLayout {

    @BindView(R.id.image_icon)
    ImageView mImageIcon;
    @BindView(R.id.text_title)
    TextView mTitleText;
    @BindView(R.id.text_dscpt)
    TextView mDscptText;

    private int mDscptTextColor;

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
        inflate(getContext(), R.layout.widget_item_view, this);
        ButterKnife.bind(this);

        mDscptTextColor = mDscptText.getCurrentTextColor();

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ItemView, defStyle, 0);
        mImageIcon.setImageDrawable(ta.getDrawable(R.styleable.ItemView_iconImageSrc));
        mTitleText.setText(ta.getString(R.styleable.ItemView_titleText));
        setDescriptionText(ta.getString(R.styleable.ItemView_descriptionText));
        ta.recycle();
    }

    public void setDescriptionText(CharSequence text) {
        mDscptText.setText(colorDscptText(text));
    }

    public void setThemeColor(int color) {
        setImageIconTintColor(color);
        setDscptTextColor(color);
    }

    public void setImageIconTintColor(int color) {
        mImageIcon.setImageTintList(ColorStateList.valueOf(color));
    }

    public void setDscptTextColor(int color) {
        if (mDscptTextColor == color) return;
        mDscptTextColor = color;
        mDscptText.setText(colorDscptText(mDscptText.getText()));
    }

    private CharSequence colorDscptText(CharSequence text) {
        return text == null ? null : StringUtil.addSpan(text, StringUtil.SPAN_COLOR, mDscptTextColor);
    }
}
