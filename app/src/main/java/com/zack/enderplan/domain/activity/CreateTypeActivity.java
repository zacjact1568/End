package com.zack.enderplan.domain.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.TypeMarkColorPickerDialogFragment;
import com.zack.enderplan.domain.view.CreateTypeView;
import com.zack.enderplan.interactor.presenter.CreateTypePresenter;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.widget.CircleColorView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTypeActivity extends BaseActivity implements CreateTypeView {

    @BindView(R.id.layout_circular_reveal)
    LinearLayout mCircularRevealLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ic_type_mark)
    CircleColorView mTypeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView mTypeNameText;
    @BindView(R.id.editor_type_name)
    EditText mTypeNameEditor;
    @BindView(R.id.layout_type_mark_color)
    RelativeLayout mTypeMarkColorLayout;
    @BindView(R.id.text_type_mark_color_name)
    TextView mTypeMarkColorNameText;
    @BindView(R.id.ic_type_mark_color)
    CircleColorView mTypeMarkColorIcon;
    @BindView(R.id.btn_create)
    TextView mCreateButton;

    @BindColor(R.color.colorAccent)
    int mAccentColor;
    @BindColor(R.color.grey)
    int mGreyColor;

    private CreateTypePresenter mCreateTypePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO CR动画

        mCreateTypePresenter = new CreateTypePresenter(this);
        mCreateTypePresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCreateTypePresenter.detachView();
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
        setContentView(R.layout.activity_create_type);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        mTypeMarkIcon.setFillColor(typeMarkColorInt);
        mTypeMarkIcon.setInnerText(firstChar);

        mTypeNameText.setText(typeName);

        mTypeNameEditor.setText(typeName);
        mTypeNameEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCreateTypePresenter.notifyTypeNameTextChanged(s.toString());
            }
        });

        mTypeMarkColorNameText.setText(typeMarkColorName);

        mTypeMarkColorIcon.setFillColor(typeMarkColorInt);
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt, String colorName) {
        mTypeMarkIcon.setFillColor(colorInt);
        mTypeMarkColorNameText.setText(colorName);
        mTypeMarkColorIcon.setFillColor(colorInt);
    }

    @Override
    public void onTypeNameChanged(String typeName, String firstChar, boolean isValid) {
        mTypeNameText.setText(typeName);
        mTypeMarkIcon.setInnerText(firstChar);
        mCreateButton.setClickable(isValid);
        mCreateButton.setBackgroundTintList(ColorStateList.valueOf(isValid ? mAccentColor : mGreyColor));
    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {
        TypeMarkColorPickerDialogFragment fragment = TypeMarkColorPickerDialogFragment.newInstance(defaultColor);
        fragment.setOnColorPickedListener(new TypeMarkColorPickerDialogFragment.OnColorPickedListener() {
            @Override
            public void onColorPicked(TypeMarkColor typeMarkColor) {
                mCreateTypePresenter.notifyTypeMarkColorSelected(typeMarkColor);
            }
        });
        fragment.show(getSupportFragmentManager(), "type_mark_color_picker");
    }

    @Override
    public void playShakeAnimation(String tag) {
        Animation shakeAnim = AnimationUtils.loadAnimation(this, R.anim.anim_shake_cta);
        switch (tag) {
            case "type_name":
                mTypeNameEditor.startAnimation(shakeAnim);
                break;
            case "type_mark_color":
                mTypeMarkColorLayout.startAnimation(shakeAnim);
                break;
            case "type_mark_pattern":
                //TODO pattern
                break;
        }
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exitCreateType() {
        finish();
    }

    @OnClick({R.id.layout_type_mark_color, R.id.layout_type_mark_pattern, R.id.btn_create, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_type_mark_color:
                mCreateTypePresenter.notifyTypeMarkColorLayoutClicked();
                break;
            case R.id.layout_type_mark_pattern:
                //TODO pattern
                break;
            case R.id.btn_create:
                mCreateTypePresenter.notifyCreateButtonClicked();
                break;
            case R.id.btn_cancel:
                mCreateTypePresenter.notifyCancelButtonClicked();
                break;
        }
    }
}
