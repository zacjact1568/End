package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.R;

import butterknife.BindView;

public class MessageDialogFragment extends BaseDialogFragment {

    @BindView(R.id.text_message)
    TextView mMessageText;

    private static final String ARG_MESSAGE = "message";

    private CharSequence mMessage;
    private OnNeutralButtonClickListener mOnNeutralButtonClickListener;
    private OnPositiveButtonClickListener mOnPositiveButtonClickListener;

    public MessageDialogFragment() {

    }

    public static MessageDialogFragment newInstance(String title, CharSequence message, String neuBtnText, String negBtnText, String posBtnText) {
        MessageDialogFragment fragment = new MessageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_NEU_BTN, neuBtnText);
        args.putString(ARG_NEG_BTN, negBtnText);
        args.putString(ARG_POS_BTN, posBtnText);
        args.putCharSequence(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mMessage = args.getCharSequence(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_message, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageText.setText(mMessage);
    }

    @Override
    public boolean onButtonClicked(int which) {
        switch (which) {
            case BTN_NEU:
                if (mOnNeutralButtonClickListener != null) {
                    mOnNeutralButtonClickListener.onNeutralButtonClick();
                }
                break;
            case BTN_NEG:
                break;
            case BTN_POS:
                if (mOnPositiveButtonClickListener != null) {
                    mOnPositiveButtonClickListener.onPositiveButtonClick();
                }
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNeutralButtonClickListener = null;
        mOnPositiveButtonClickListener = null;
    }

    public interface OnNeutralButtonClickListener {
        void onNeutralButtonClick();
    }

    public void setOnNeutralButtonClickListener(OnNeutralButtonClickListener listener) {
        mOnNeutralButtonClickListener = listener;
    }

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick();
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener listener) {
        mOnPositiveButtonClickListener = listener;
    }
}
