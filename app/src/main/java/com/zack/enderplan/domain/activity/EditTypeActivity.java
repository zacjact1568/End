package com.zack.enderplan.domain.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.TypeMarkColorPickerDialogFragment;
import com.zack.enderplan.domain.fragment.EditorDialogFragment;
import com.zack.enderplan.domain.view.EditTypeView;
import com.zack.enderplan.interactor.presenter.EditTypePresenter;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.widget.CircleColorView;
import com.zack.enderplan.widget.ItemView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditTypeActivity extends BaseActivity implements EditTypeView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ic_type_mark)
    CircleColorView mTypeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView mTypeNameText;
    @BindView(R.id.item_type_name)
    ItemView mTypeNameItem;
    @BindView(R.id.item_type_mark_color)
    ItemView mTypeMarkColorItem;

    private EditTypePresenter mEditTypePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditTypePresenter = new EditTypePresenter(this, getIntent().getIntExtra("position", -1));
        mEditTypePresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditTypePresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void showInitialView(int typeMarkColorInt, String firstChar, String typeName, String typeMarkColorName) {
        setContentView(R.layout.activity_edit_type);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        mTypeMarkIcon.setFillColor(typeMarkColorInt);
        mTypeMarkIcon.setInnerText(firstChar);

        mTypeNameText.setText(typeName);

        mTypeNameItem.setDescriptionText(typeName);

        mTypeMarkColorItem.setDescriptionText(typeMarkColorName);
    }

    @Override
    public void showTypeNameEditorDialog(String originalEditorText) {
        EditorDialogFragment fragment = EditorDialogFragment.newInstance(App.getGlobalContext().getString(R.string.title_dialog_type_name_editor), originalEditorText);
        fragment.setOnPositiveButtonClickListener(new EditorDialogFragment.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(String editorText) {
                mEditTypePresenter.notifyUpdatingTypeName(editorText);
            }
        });
        fragment.show(getSupportFragmentManager(), "type_name_editor");
    }

    @Override
    public void onTypeNameChanged(String typeName, String firstChar) {
        mTypeMarkIcon.setInnerText(firstChar);
        mTypeNameText.setText(typeName);
        mTypeNameItem.setDescriptionText(typeName);
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt, String colorName) {
        mTypeMarkIcon.setFillColor(colorInt);
        mTypeMarkColorItem.setDescriptionText(colorName);
    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {
        TypeMarkColorPickerDialogFragment fragment = TypeMarkColorPickerDialogFragment.newInstance(defaultColor);
        fragment.setOnTypeMarkColorPickedListener(new TypeMarkColorPickerDialogFragment.OnTypeMarkColorPickedListener() {
            @Override
            public void onTypeMarkColorPicked(TypeMarkColor typeMarkColor) {
                mEditTypePresenter.notifyTypeMarkColorSelected(typeMarkColor);
            }
        });
        fragment.show(getSupportFragmentManager(), "type_mark_color_picker");
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.item_type_name, R.id.item_type_mark_color, R.id.item_type_mark_pattern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_type_name:
                mEditTypePresenter.notifySettingTypeName();
                break;
            case R.id.item_type_mark_color:
                mEditTypePresenter.notifySettingTypeMarkColor();
                break;
            case R.id.item_type_mark_pattern:
                //TODO pattern
                break;
        }
    }
}
