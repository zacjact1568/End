package com.zack.enderplan.domain.activity;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditTypeActivity extends BaseActivity implements EditTypeView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ic_type_mark_preview)
    CircleColorView mTypeMarkPreviewIcon;
    @BindView(R.id.text_type_name_preview)
    TextView mTypeNamePreviewText;
    @BindView(R.id.text_type_name)
    TextView mTypeNameText;
    @BindView(R.id.text_type_mark_color_name)
    TextView mTypeMarkColorNameText;
    @BindView(R.id.ic_type_mark_color)
    CircleColorView mTypeMarkColorIcon;
    @BindView(R.id.text_type_mark_pattern_name)
    TextView mTypeMarkPatternNameText;
    @BindView(R.id.ic_type_mark_pattern)
    ImageView mTypeMarkPatternIcon;

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

        mTypeMarkPreviewIcon.setFillColor(typeMarkColorInt);
        mTypeMarkPreviewIcon.setInnerText(firstChar);

        mTypeNamePreviewText.setText(typeName);

        mTypeNameText.setText(typeName);

        mTypeMarkColorNameText.setText(typeMarkColorName);

        mTypeMarkColorIcon.setFillColor(typeMarkColorInt);
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
        mTypeMarkPreviewIcon.setInnerText(firstChar);
        mTypeNamePreviewText.setText(typeName);
        mTypeNameText.setText(typeName);
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt, String colorName) {
        mTypeMarkPreviewIcon.setFillColor(colorInt);
        mTypeMarkColorNameText.setText(colorName);
        mTypeMarkColorIcon.setFillColor(colorInt);
    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {
        TypeMarkColorPickerDialogFragment fragment = TypeMarkColorPickerDialogFragment.newInstance(defaultColor);
        fragment.setOnColorPickedListener(new TypeMarkColorPickerDialogFragment.OnColorPickedListener() {
            @Override
            public void onColorPicked(TypeMarkColor typeMarkColor) {
                mEditTypePresenter.notifyTypeMarkColorSelected(typeMarkColor);
            }
        });
        fragment.show(getSupportFragmentManager(), "type_mark_color_picker");
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.layout_type_name, R.id.layout_type_mark_color, R.id.layout_type_mark_pattern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_type_name:
                mEditTypePresenter.notifyTypeNameItemClicked();
                break;
            case R.id.layout_type_mark_color:
                mEditTypePresenter.notifyTypeMarkColorItemClicked();
                break;
            case R.id.layout_type_mark_pattern:
                //TODO pattern
                break;
        }
    }
}
