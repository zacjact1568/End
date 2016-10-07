package com.zack.enderplan.domain.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.view.CreateTypeView;
import com.zack.enderplan.interactor.adapter.TypeMarkAdapter;
import com.zack.enderplan.interactor.presenter.CreateTypePresenter;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTypeDialogFragment extends DialogFragment implements CreateTypeView {

    @BindView(R.id.wrapper_type_name_editor)
    TextInputLayout mTypeNameEditorWrapper;
    @BindView(R.id.grid_type_marks)
    GridView mTypeMarksGrid;
    @BindView(R.id.btn_save)
    TextView mSaveButton;

    @BindColor(android.R.color.secondary_text_light_nodisable)
    int mNegativeColor;
    @BindColor(R.color.colorPrimary)
    int mPositiveColor;

    private CreateTypePresenter mCreateTypePresenter;

    public CreateTypeDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreateTypePresenter = new CreateTypePresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_create_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mCreateTypePresenter.setInitialView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCreateTypePresenter.detachView();
    }

    @Override
    public void showInitialView(TypeMarkAdapter typeMarkAdapter) {

        TextInputEditText typeNameEditor;

        if (mTypeNameEditorWrapper.getEditText() != null) {
            typeNameEditor = (TextInputEditText) mTypeNameEditorWrapper.getEditText();
        } else {
            throw new RuntimeException("TextInputLayout has to warp an EditText or its descendant");
        }

        typeNameEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCreateTypePresenter.notifyTypeNameChanged(s.toString());
            }
        });

        mTypeMarksGrid.setAdapter(typeMarkAdapter);
        mTypeMarksGrid.setOnItemClickListener((parent, view, position, id) -> mCreateTypePresenter.notifyTypeMarkClicked(position));

        //必须放在这里，如果放在布局文件中的话，setOnClickListener会将clickable设成true，就没用了
        mSaveButton.setClickable(false);
    }

    @Override
    public void updateSaveButton(boolean isEnabled) {
        mSaveButton.setClickable(isEnabled);
        mSaveButton.setTextColor(isEnabled ? mPositiveColor : mNegativeColor);
    }

    @Override
    public void closeDialog() {
        getDialog().dismiss();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_save})
    public void onClick(View view) {
        mCreateTypePresenter.notifyViewClicked(view.getId());
    }
}
