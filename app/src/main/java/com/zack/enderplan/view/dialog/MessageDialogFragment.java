package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;

import butterknife.BindView;

public class MessageDialogFragment extends BaseDialogFragment {

    @BindView(R.id.text_message)
    TextView mMessageText;

    private static final String ARG_MESSAGE = "message";

    private CharSequence mMessage;

    public MessageDialogFragment() {

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

    public static class Builder extends BaseDialogFragment.Builder<MessageDialogFragment> {

        private CharSequence mMessage;

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(@StringRes int resId) {
            return setMessage(ResourceUtil.getString(resId));
        }

        @Override
        protected MessageDialogFragment onBuildContent() {
            MessageDialogFragment fragment = new MessageDialogFragment();
            Bundle args = new Bundle();
            args.putCharSequence(ARG_MESSAGE, mMessage);
            fragment.setArguments(args);
            return fragment;
        }
    }
}
