package com.zack.enderplan.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zack.enderplan.R;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.SystemUtil;

import butterknife.BindView;

public class EditorDialogFragment extends BaseDialogFragment {

    @BindView(R.id.editor)
    EditText mEditor;

    private static final String ARG_EDITOR_TEXT = "editor_text";
    private static final String ARG_EDITOR_HINT = "editor_hint";

    private String mEditorTextStr, mEditorHintStr;
    private OnOkButtonClickListener mOnOkButtonClickListener;

    public EditorDialogFragment() {

    }

    public static EditorDialogFragment newInstance(String title, String editorText, String editorHint) {
        EditorDialogFragment fragment = new EditorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_NEG_BTN, ResourceUtil.getString(R.string.button_cancel));
        args.putString(ARG_POS_BTN, ResourceUtil.getString(R.string.button_ok));
        args.putString(ARG_EDITOR_TEXT, editorText);
        args.putString(ARG_EDITOR_HINT, editorHint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mEditorTextStr = args.getString(ARG_EDITOR_TEXT);
            mEditorHintStr = args.getString(ARG_EDITOR_HINT);
        }
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
    public boolean onButtonClicked(int which) {
        switch (which) {
            case BTN_NEG:
                break;
            case BTN_POS:
                if (mOnOkButtonClickListener != null) {
                    mOnOkButtonClickListener.onOkButtonClick(mEditor.getText().toString());
                }
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnOkButtonClickListener = null;
    }

    public interface OnOkButtonClickListener {
        void onOkButtonClick(String editorText);
    }

    public void setOnOkButtonClickListener(OnOkButtonClickListener listener) {
        mOnOkButtonClickListener = listener;
    }
}
