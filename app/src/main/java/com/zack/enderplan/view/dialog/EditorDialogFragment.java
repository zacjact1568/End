package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.SystemUtil;

import java.io.Serializable;

import butterknife.BindView;

public class EditorDialogFragment extends BaseDialogFragment {

    @BindView(R.id.editor)
    EditText mEditor;

    private static final String ARG_EDITOR_TEXT = "editor_text";
    private static final String ARG_EDITOR_HINT = "editor_hint";
    private static final String ARG_TEXT_EDITED_LSNR = "text_edited_lsnr";

    private String mEditorTextStr, mEditorHintStr;
    private OnTextEditedListener mOnTextEditedListener;

    public EditorDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mEditorTextStr = args.getString(ARG_EDITOR_TEXT);
            mEditorHintStr = args.getString(ARG_EDITOR_HINT);
            mOnTextEditedListener = (OnTextEditedListener) args.getSerializable(ARG_TEXT_EDITED_LSNR);
        }

        setPositiveButtonClickListener(new OnButtonClickListener() {
            @Override
            public boolean onClick() {
                if (mOnTextEditedListener != null) {
                    mOnTextEditedListener.onTextEdited(mEditor.getText().toString());
                }
                return true;
            }
        });
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup root) {
        return inflater.inflate(R.layout.dialog_fragment_editor, root, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditor.setText(mEditorTextStr);
        mEditor.setHint(mEditorHintStr);
        mEditor.setSelection(mEditor.length());
        SystemUtil.showSoftInput(mEditor, 100);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnTextEditedListener = null;
    }

    public static class Builder extends BaseDialogFragment.Builder<EditorDialogFragment> {

        private String mEditorText, mEditorHint;
        private OnTextEditedListener mOnTextEditedListener;

        public Builder setEditorText(String editorText) {
            mEditorText = editorText;
            return this;
        }

        public Builder setEditorHint(String editorHint) {
            mEditorHint = editorHint;
            return this;
        }

        public Builder setEditorHint(@StringRes int resId) {
            return setEditorHint(ResourceUtil.getString(resId));
        }

        public Builder setPositiveButton(String text, OnTextEditedListener listener) {
            setPositiveButton(text, (OnButtonClickListener) null);
            mOnTextEditedListener = listener;
            return this;
        }

        public Builder setPositiveButton(@StringRes int resId, OnTextEditedListener listener) {
            return setPositiveButton(ResourceUtil.getString(resId), listener);
        }

        @Override
        protected EditorDialogFragment onBuildContent() {
            EditorDialogFragment fragment = new EditorDialogFragment();
            Bundle args = new Bundle();
            args.putString(ARG_EDITOR_TEXT, mEditorText);
            args.putString(ARG_EDITOR_HINT, mEditorHint);
            args.putSerializable(ARG_TEXT_EDITED_LSNR, mOnTextEditedListener);
            fragment.setArguments(args);
            return fragment;
        }
    }

    public interface OnTextEditedListener extends Serializable {
        void onTextEdited(String text);
    }
}
