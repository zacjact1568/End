package me.imzack.app.end.view.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import me.imzack.app.end.R;
import me.imzack.app.end.common.Constant;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleGuidePageFragment extends BaseFragment {

    @BindView(R.id.image_subject)
    ImageView mSubjectImage;
    @BindView(R.id.text_title)
    TextView mTitleText;
    @BindView(R.id.text_dscpt)
    TextView mDscptText;
    @BindView(R.id.btn_action)
    Button mActionButton;

    private static final String ARG_IMAGE_RES_ID = "0";
    private static final String ARG_TITLE_RES_ID = "1";
    private static final String ARG_DSCPT_RES_ID = "2";
    private static final String ARG_BTN_TEXT_RES_ID = "3";
    private static final String ARG_BTN_CLK_LSNR = "4";

    private int mImageResId, mTitleResId, mDscptResId, mBtnTextResId;
    private OnButtonClickListener mBtnClkLsnr;

    public static SimpleGuidePageFragment newInstance(@DrawableRes int imageResId, @StringRes int titleResId,
                                                      @StringRes int descriptionResId, @StringRes int buttonTextResId,
                                                      OnButtonClickListener onButtonClickListener) {
        SimpleGuidePageFragment fragment = new SimpleGuidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        args.putInt(ARG_TITLE_RES_ID, titleResId);
        args.putInt(ARG_DSCPT_RES_ID, descriptionResId);
        args.putInt(ARG_BTN_TEXT_RES_ID, buttonTextResId);
        args.putSerializable(ARG_BTN_CLK_LSNR, onButtonClickListener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mImageResId = args.getInt(ARG_IMAGE_RES_ID);
            mTitleResId = args.getInt(ARG_TITLE_RES_ID);
            mDscptResId = args.getInt(ARG_DSCPT_RES_ID);
            mBtnTextResId = args.getInt(ARG_BTN_TEXT_RES_ID);
            mBtnClkLsnr = (OnButtonClickListener) args.getSerializable(ARG_BTN_CLK_LSNR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_guide_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (mImageResId == Constant.UNDEFINED_RES_ID) {
            mSubjectImage.setVisibility(View.INVISIBLE);
        } else {
            mSubjectImage.setImageResource(mImageResId);
        }
        if (mTitleResId == Constant.UNDEFINED_RES_ID) {
            mTitleText.setVisibility(View.INVISIBLE);
        } else {
            mTitleText.setText(mTitleResId);
        }
        if (mDscptResId == Constant.UNDEFINED_RES_ID) {
            mDscptText.setVisibility(View.INVISIBLE);
        } else {
            mDscptText.setText(mDscptResId);
        }
        if (mBtnTextResId == Constant.UNDEFINED_RES_ID) {
            mActionButton.setVisibility(View.INVISIBLE);
        } else {
            mActionButton.setText(mBtnTextResId);
        }
        mActionButton.setOnClickListener(mBtnClkLsnr);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static class Builder {

        private int mImageResId, mTitleResId, mDscptResId, mBtnTextResId;
        private OnButtonClickListener mBtnClkLsnr;
        
        public Builder() {
            mImageResId = Constant.UNDEFINED_RES_ID;
            mTitleResId = Constant.UNDEFINED_RES_ID;
            mDscptResId = Constant.UNDEFINED_RES_ID;
            mBtnTextResId = Constant.UNDEFINED_RES_ID;
        }

        public Builder setImage(@DrawableRes int resId) {
            mImageResId = resId;
            return this;
        }

        public Builder setTitle(@StringRes int resId) {
            mTitleResId = resId;
            return this;
        }

        public Builder setDescription(@StringRes int resId) {
            mDscptResId = resId;
            return this;
        }

        public Builder setButton(@StringRes int resId, OnButtonClickListener listener) {
            mBtnTextResId = resId;
            mBtnClkLsnr = listener;
            return this;
        }

        public SimpleGuidePageFragment create() {
            return newInstance(mImageResId, mTitleResId, mDscptResId, mBtnTextResId, mBtnClkLsnr);
        }
    }

    public interface OnButtonClickListener extends View.OnClickListener, Serializable {

    }
}
