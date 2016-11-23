package com.zack.enderplan.domain.activity;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.TypeMarkColorPickerDialogFragment;
import com.zack.enderplan.domain.fragment.TypeMarkPatternPickerDialogFragment;
import com.zack.enderplan.domain.view.CreateTypeView;
import com.zack.enderplan.interactor.presenter.CreateTypePresenter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.widget.CircleColorView;
import com.zack.enderplan.widget.ItemView;

import butterknife.BindColor;
import butterknife.BindString;
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
    @BindView(R.id.item_type_mark_color)
    ItemView mTypeMarkColorItem;
    @BindView(R.id.item_type_mark_pattern)
    ItemView mTypeMarkPatternItem;

    @BindColor(R.color.colorAccent)
    int mAccentColor;
    @BindColor(R.color.grey)
    int mGreyColor;

    @BindString(R.string.dscpt_click_to_set)
    String mClickToSetDscpt;

    private CreateTypePresenter mCreateTypePresenter;
    private MenuItem mCreateMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        getMenuInflater().inflate(R.menu.menu_create_type, menu);
        mCreateMenuItem = menu.findItem(R.id.action_create);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mCreateTypePresenter.notifyCancelButtonClicked();
                break;
            case R.id.action_create:
                mCreateTypePresenter.notifyCreateButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mCreateTypePresenter.notifyCancelButtonClicked();
    }

    @Override
    public void showInitialView(FormattedType formattedType) {
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_create_type);
        ButterKnife.bind(this);

        //TODO if (savedInstanceState == null)
        mCircularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mCircularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                playCircularRevealAnimation();
                return false;
            }
        });

        setSupportActionBar(mToolbar);
        setupActionBar();

        mTypeNameEditor.setText(formattedType.getTypeName());
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

        onTypeNameChanged(formattedType.getTypeName(), formattedType.getFirstChar(), true);
        onTypeMarkColorChanged(formattedType.getTypeMarkColorInt(), formattedType.getTypeMarkColorName());
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt, String colorName) {
        mTypeMarkIcon.setFillColor(colorInt);
        mTypeMarkColorItem.setDescriptionText(colorName);
    }

    @Override
    public void onTypeMarkPatternChanged(boolean hasPattern, int patternResId, String patternName) {
        mTypeMarkIcon.setInnerIcon(hasPattern ? getDrawable(patternResId) : null);
        mTypeMarkPatternItem.setDescriptionText(hasPattern ? patternName : mClickToSetDscpt);
    }

    @Override
    public void onTypeNameChanged(String typeName, String firstChar, boolean isValid) {
        mTypeNameText.setText(typeName);
        mTypeMarkIcon.setInnerText(firstChar);
        if (mCreateMenuItem != null) {
            mCreateMenuItem.setVisible(isValid);
        }
    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {
        TypeMarkColorPickerDialogFragment fragment = TypeMarkColorPickerDialogFragment.newInstance(defaultColor);
        fragment.setOnTypeMarkColorPickedListener(new TypeMarkColorPickerDialogFragment.OnTypeMarkColorPickedListener() {
            @Override
            public void onTypeMarkColorPicked(TypeMarkColor typeMarkColor) {
                mCreateTypePresenter.notifyTypeMarkColorSelected(typeMarkColor);
            }
        });
        fragment.show(getSupportFragmentManager(), "type_mark_color_picker");
    }

    @Override
    public void showTypeMarkPatternPickerDialog(String defaultPattern) {
        TypeMarkPatternPickerDialogFragment fragment = TypeMarkPatternPickerDialogFragment.newInstance(defaultPattern);
        fragment.setOnTypeMarkPatternPickedListener(new TypeMarkPatternPickerDialogFragment.OnTypeMarkPatternPickedListener() {
            @Override
            public void onTypeMarkPatternPicked(TypeMarkPattern typeMarkPattern) {
                mCreateTypePresenter.notifyTypeMarkPatternSelected(typeMarkPattern);
            }
        });
        fragment.show(getSupportFragmentManager(), "type_mark_pattern_picker");
    }

    @Override
    public void playShakeAnimation(String tag) {
        Animation shakeAnim = AnimationUtils.loadAnimation(this, R.anim.anim_shake_cta);
        switch (tag) {
            case "type_mark":
                mTypeMarkIcon.startAnimation(shakeAnim);
                break;
            case "type_name":
                mTypeNameEditor.startAnimation(shakeAnim);
                break;
            case "type_mark_color":
                mTypeMarkColorItem.startAnimation(shakeAnim);
                break;
            case "type_mark_pattern":
                mTypeMarkPatternItem.startAnimation(shakeAnim);
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

    @OnClick({R.id.item_type_mark_color, R.id.item_type_mark_pattern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_type_mark_color:
                mCreateTypePresenter.notifySettingTypeMarkColor();
                break;
            case R.id.item_type_mark_pattern:
                mCreateTypePresenter.notifySettingTypeMarkPattern();
                break;
        }
    }

    private void playCircularRevealAnimation() {
        int fabCoordinateInPx = (int) (44 * getResources().getDisplayMetrics().density + 0.5f);
        int centerX = mCircularRevealLayout.getWidth() - fabCoordinateInPx;
        int centerY = mCircularRevealLayout.getHeight() - fabCoordinateInPx;

        Animator anim = ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, centerX, centerY, 0, (float) Math.hypot(centerX, centerY));
        anim.setDuration(400);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Util.showSoftInput(mTypeNameEditor);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }
}
