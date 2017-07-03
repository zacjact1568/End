package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.util.SystemUtil;

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

    public static final String ARG_TITLE = "title";
    public static final String ARG_NEU_BTN = "neu_btn";
    public static final String ARG_NEG_BTN = "neg_btn";
    public static final String ARG_POS_BTN = "pos_btn";

    public static final int BTN_NEU = 0;
    public static final int BTN_NEG = 1;
    public static final int BTN_POS = 2;

    String mTitleString, mNeutralButtonString, mNegativeButtonString, mPositiveButtonString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTitleString = args.getString(ARG_TITLE);
            mNeutralButtonString = args.getString(ARG_NEU_BTN);
            mNegativeButtonString = args.getString(ARG_NEG_BTN);
            mPositiveButtonString = args.getString(ARG_POS_BTN);
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

        setTitleString(mTitleString);
        setNeutralButtonString(mNeutralButtonString);
        setNegativeButtonString(mNegativeButtonString);
        setPositiveButtonString(mPositiveButtonString);
    }

    /** 按钮按下时调用，返回值表示是否关闭dialog */
    public abstract boolean onButtonClicked(int which);

    @OnClick({R.id.btn_neutral, R.id.btn_negative, R.id.btn_positive})
    public void onClick(View view) {
        boolean dismiss = false;
        switch (view.getId()) {
            case R.id.btn_neutral:
                dismiss = onButtonClicked(BTN_NEU);
                break;
            case R.id.btn_negative:
                dismiss = onButtonClicked(BTN_NEG);
                break;
            case R.id.btn_positive:
                dismiss = onButtonClicked(BTN_POS);
                break;
        }
        if (dismiss) {
            getDialog().dismiss();
        }
    }

    /** 传入null表示不显示标题 */
    public void setTitleString(String titleString) {
        mTitleString = titleString;
        mTitleText.setVisibility(mTitleString == null ? View.GONE : View.VISIBLE);
        mTitleText.setText(mTitleString);
    }

    public void setNeutralButtonString(String neutralButtonString) {
        mNeutralButtonString = neutralButtonString;
        mNeutralButton.setVisibility(mNeutralButtonString == null ? View.GONE : View.VISIBLE);
        mNeutralButton.setText(mNeutralButtonString);
    }

    public void setNegativeButtonString(String negativeButtonString) {
        mNegativeButtonString = negativeButtonString;
        mNegativeButton.setVisibility(mNegativeButtonString == null ? View.GONE : View.VISIBLE);
        mNegativeButton.setText(mNegativeButtonString);
    }

    public void setPositiveButtonString(String positiveButtonString) {
        mPositiveButtonString = positiveButtonString;
        mPositiveButton.setVisibility(mPositiveButtonString == null ? View.GONE : View.VISIBLE);
        mPositiveButton.setText(mPositiveButtonString);
    }

    public void show(FragmentManager manager) {
        super.show(manager, null);
    }
}
