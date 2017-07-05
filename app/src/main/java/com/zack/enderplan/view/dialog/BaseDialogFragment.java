package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.SystemUtil;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class BaseDialogFragment extends DialogFragment {

    @BindView(R.id.text_title)
    TextView mTitleText;
    @BindView(R.id.btn_neutral)
    Button mNeutralButton;
    @BindView(R.id.btn_negative)
    Button mNegativeButton;
    @BindView(R.id.btn_positive)
    Button mPositiveButton;

    public static final String ARG_TITLE_STR = "0";
    public static final String ARG_NEU_BTN_STR = "1";
    public static final String ARG_NEU_BTN_CLK_LSNR = "2";
    public static final String ARG_NEG_BTN_STR = "3";
    public static final String ARG_NEG_BTN_CLK_LSNR = "4";
    public static final String ARG_POS_BTN_STR = "5";
    public static final String ARG_POS_BTN_CLK_LSNR = "6";

    private String mTitleString, mNeutralButtonString, mNegativeButtonString, mPositiveButtonString;
    private OnButtonClickListener mNeutralButtonClickListener, mNegativeButtonClickListener, mPositiveButtonClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTitleString = args.getString(ARG_TITLE_STR);
            mNeutralButtonString = args.getString(ARG_NEU_BTN_STR);
            mNeutralButtonClickListener = (OnButtonClickListener) args.getSerializable(ARG_NEU_BTN_CLK_LSNR);
            mNegativeButtonString = args.getString(ARG_NEG_BTN_STR);
            mNegativeButtonClickListener = (OnButtonClickListener) args.getSerializable(ARG_NEG_BTN_CLK_LSNR);
            mPositiveButtonString = args.getString(ARG_POS_BTN_STR);
            mPositiveButtonClickListener = (OnButtonClickListener) args.getSerializable(ARG_POS_BTN_CLK_LSNR);
        }
    }

    /** 重写这个方法提供内容区域的view */
    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup root);

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dialog_fragment_base, container, false);
        root.addView(onCreateContentView(inflater, root), 1);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //通过动态设置内容区域的view宽度来设置dialog宽度（不能直接设置根view的宽度，因为它的LayoutParams为null）
        ((ViewGroup) view).getChildAt(1).getLayoutParams().width = (int) (SystemUtil.getDisplayWidth() * 0.8f);

        setTitle(mTitleString);
        setNeutralButtonString(mNeutralButtonString);
        setNeutralButtonClickListener(mNeutralButtonClickListener);
        setNegativeButtonString(mNegativeButtonString);
        setNegativeButtonClickListener(mNegativeButtonClickListener);
        setPositiveButtonString(mPositiveButtonString);
        setPositiveButtonClickListener(mPositiveButtonClickListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNeutralButtonClickListener = null;
        mNegativeButtonClickListener = null;
        mPositiveButtonClickListener = null;
    }

    @OnClick({R.id.btn_neutral, R.id.btn_negative, R.id.btn_positive})
    public void onClick(View view) {
        boolean dismiss = true;
        switch (view.getId()) {
            case R.id.btn_neutral:
                if (mNeutralButtonClickListener != null) {
                    dismiss = mNeutralButtonClickListener.onClick();
                }
                break;
            case R.id.btn_negative:
                if (mNegativeButtonClickListener != null) {
                    dismiss = mNegativeButtonClickListener.onClick();
                }
                break;
            case R.id.btn_positive:
                if (mPositiveButtonClickListener != null) {
                    dismiss = mPositiveButtonClickListener.onClick();
                }
                break;
        }
        if (dismiss) {
            getDialog().dismiss();
        }
    }

    /** 传入null表示不显示标题 */
    //TODO 这里应该将传入的字段放进Arguments，fragment重建后才能恢复新的？
    public void setTitle(String title) {
        mTitleString = title;
        mTitleText.setVisibility(mTitleString == null ? View.GONE : View.VISIBLE);
        mTitleText.setText(mTitleString);
    }

    public void setNeutralButtonString(String text) {
        mNeutralButtonString = text;
        mNeutralButton.setVisibility(mNeutralButtonString == null ? View.GONE : View.VISIBLE);
        mNeutralButton.setText(mNeutralButtonString);
    }

    public void setNeutralButtonClickListener(OnButtonClickListener listener) {
        mNeutralButtonClickListener = listener;
    }

    public void setNegativeButtonString(String text) {
        mNegativeButtonString = text;
        mNegativeButton.setVisibility(mNegativeButtonString == null ? View.GONE : View.VISIBLE);
        mNegativeButton.setText(mNegativeButtonString);
    }

    public void setNegativeButtonClickListener(OnButtonClickListener listener) {
        mNegativeButtonClickListener = listener;
    }

    public void setPositiveButtonString(String text) {
        mPositiveButtonString = text;
        mPositiveButton.setVisibility(mPositiveButtonString == null ? View.GONE : View.VISIBLE);
        mPositiveButton.setText(mPositiveButtonString);
    }

    public void setPositiveButtonClickListener(OnButtonClickListener listener) {
        mPositiveButtonClickListener = listener;
    }

    public void show(FragmentManager manager) {
        super.show(manager, null);
    }

    public static abstract class Builder<DF extends BaseDialogFragment> {

        private String mTitleStr, mNeuBtnStr, mNegBtnStr, mPosBtnStr;
        private OnButtonClickListener mNeuBtnClkLsnr, mNegBtnClkLsnr, mPosBtnClkLsnr;

        public Builder() {

        }

        public Builder setTitle(String title) {
            mTitleStr = title;
            return this;
        }

        public Builder setTitle(@StringRes int resId) {
            return setTitle(ResourceUtil.getString(resId));
        }

        /** 若listener为null，点击此按钮直接关闭dialog */
        public Builder setNeutralButton(String text, OnButtonClickListener listener) {
            mNeuBtnStr = text;
            mNeuBtnClkLsnr = listener;
            return this;
        }

        public Builder setNeutralButton(@StringRes int resId, OnButtonClickListener listener) {
            return setNeutralButton(ResourceUtil.getString(resId), listener);
        }

        public Builder setNegativeButton(String text, OnButtonClickListener listener) {
            mNegBtnStr = text;
            mNegBtnClkLsnr = listener;
            return this;
        }

        public Builder setNegativeButton(@StringRes int resId, OnButtonClickListener listener) {
            return setNegativeButton(ResourceUtil.getString(resId), listener);
        }

        public Builder setPositiveButton(String text, OnButtonClickListener listener) {
            mPosBtnStr = text;
            mPosBtnClkLsnr = listener;
            return this;
        }

        public Builder setPositiveButton(@StringRes int resId, OnButtonClickListener listener) {
            return setPositiveButton(ResourceUtil.getString(resId), listener);
        }

        /** 重写此方法提供子类DialogFragment */
        protected abstract DF onBuildContent();

        /** 仅创建DialogFragment，不附到activity上 */
        public final DF build() {
            DF dialogFragment = onBuildContent();
            Bundle args = dialogFragment.getArguments();
            args.putString(ARG_TITLE_STR, mTitleStr);
            args.putString(ARG_NEU_BTN_STR, mNeuBtnStr);
            args.putSerializable(ARG_NEU_BTN_CLK_LSNR, mNeuBtnClkLsnr);
            args.putString(ARG_NEG_BTN_STR, mNegBtnStr);
            args.putSerializable(ARG_NEG_BTN_CLK_LSNR, mNegBtnClkLsnr);
            args.putString(ARG_POS_BTN_STR, mPosBtnStr);
            args.putSerializable(ARG_POS_BTN_CLK_LSNR, mPosBtnClkLsnr);
            return dialogFragment;
        }

        /** 创建DialogFragment并将其附到activity上 */
        public final void show(FragmentManager manager) {
            build().show(manager);
        }
    }

    public interface OnButtonClickListener extends Serializable {
        /** 按钮按下时调用，返回值表示是否关闭dialog */
        boolean onClick();
    }
}
