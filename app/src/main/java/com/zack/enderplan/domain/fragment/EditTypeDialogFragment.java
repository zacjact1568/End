package com.zack.enderplan.domain.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.interactor.adapter.TypeMarkColorAdapter;
import com.zack.enderplan.interactor.adapter.TypeMarkColourAdapter;
import com.zack.enderplan.interactor.presenter.EditTypePresenter;
import com.zack.enderplan.domain.view.EditTypeView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** Deprecated class */
public class EditTypeDialogFragment extends DialogFragment implements EditTypeView {

    @BindView(R.id.wrapper_type_name_editor)
    TextInputLayout typeNameEditorWrapper;
    @BindView(R.id.grid_view)
    GridView gridView;
    @BindView(R.id.button_save)
    TextView saveButton;

    @BindColor(android.R.color.secondary_text_light_nodisable)
    int negativeColor;
    @BindColor(R.color.colorPrimary)
    int positiveColor;

    private static final String ARG_POSITION = "position";

    private int mPosition;
    private EditTypePresenter editTypePresenter;

    public EditTypeDialogFragment() {
        // Required empty public constructor
    }

    public static EditTypeDialogFragment newInstance(int position) {
        EditTypeDialogFragment fragment = new EditTypeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
        }

        editTypePresenter = new EditTypePresenter(this, mPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_edit_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        editTypePresenter.setInitialView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        editTypePresenter.detachView();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        //editTypePresenter.notifyTypeEditCanceled();
    }

    public void showInitialView(String typeName, TypeMarkColorAdapter typeMarkColorAdapter) {

        TextInputEditText typeNameEditor;

        if (typeNameEditorWrapper.getEditText() != null) {
            typeNameEditor = (TextInputEditText) typeNameEditorWrapper.getEditText();
        } else {
            throw new RuntimeException("TextInputLayout has to warp an EditText or its descendant");
        }

        typeNameEditor.setText(typeName);
        typeNameEditor.setSelection(typeNameEditor.length());
        typeNameEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //editTypePresenter.notifyTypeNameChanged(s.toString());
            }
        });

        gridView.setAdapter(typeMarkColorAdapter);
        //gridView.setOnItemClickListener((parent, view, position, id) -> editTypePresenter.notifyTypeMarkClicked(position));
    }

    public void updateSaveButton(boolean isEnabled) {
        saveButton.setClickable(isEnabled);
        saveButton.setTextColor(isEnabled ? positiveColor : negativeColor);
    }

    public void closeDialog(boolean isCanceled) {
        if (isCanceled) {
            getDialog().cancel();
        } else {
            getDialog().dismiss();
        }
    }

    @OnClick({R.id.button_cancel, R.id.button_save})
    public void onClick(View view) {
        //editTypePresenter.notifyViewClicked(view.getId());
    }

    @Override
    public void showInitialView(int typeMarkColorInt, String firstChar, String typeName, String typeMarkColorName) {

    }

    @Override
    public void showTypeNameEditorDialog(String originalEditorText) {

    }

    @Override
    public void onTypeNameChanged(String typeName, String firstChar) {

    }

    @Override
    public void onTypeMarkColorChanged(int colorInt, String colorName) {

    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {

    }

    @Override
    public void showToast(@StringRes int msgResId) {

    }
}
