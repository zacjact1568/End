package com.zack.enderplan.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.zack.enderplan.R;

public class EditorDialogFragment extends DialogFragment {

    private static final String ARG_TITLE_TEXT = "title_text";
    private static final String ARG_CONTENT_EDITOR = "content_editor";

    private EditText mEditor;
    private String mTitleTextStr, mContentEditorStr;
    private OnPositiveButtonClickListener mOnPositiveButtonClickListener;

    public EditorDialogFragment() {
        // Required empty public constructor
    }

    public static EditorDialogFragment newInstance(String titleText, String editorText) {
        EditorDialogFragment fragment = new EditorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_TEXT, titleText);
        args.putString(ARG_CONTENT_EDITOR, editorText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTitleTextStr = args.getString(ARG_TITLE_TEXT);
            mContentEditorStr = args.getString(ARG_CONTENT_EDITOR);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mEditor = new EditText(getContext());
        if (!TextUtils.isEmpty(mContentEditorStr)) {
            mEditor.setText(mContentEditorStr);
            mEditor.setSelection(mEditor.length());
        }

        FrameLayout root = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 40, 60, 0);
        root.addView(mEditor, params);

        return new AlertDialog.Builder(getContext())
                .setTitle(mTitleTextStr)
                .setView(root)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnPositiveButtonClickListener != null) {
                            mOnPositiveButtonClickListener.onPositiveButtonClick(mEditor.getText().toString());
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnPositiveButtonClickListener = null;
    }

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick(String editorText);
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener listener) {
        mOnPositiveButtonClickListener = listener;
    }
}
