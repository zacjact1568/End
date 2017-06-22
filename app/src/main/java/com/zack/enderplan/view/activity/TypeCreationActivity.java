package com.zack.enderplan.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.util.ColorUtil;
import com.zack.enderplan.injector.component.DaggerTypeCreationComponent;
import com.zack.enderplan.injector.module.TypeCreationPresenterModule;
import com.zack.enderplan.view.contract.TypeCreationViewContract;
import com.zack.enderplan.view.dialog.EditorDialogFragment;
import com.zack.enderplan.view.dialog.TypeMarkColorPickerDialogFragment;
import com.zack.enderplan.view.dialog.TypeMarkPatternPickerDialogFragment;
import com.zack.enderplan.presenter.TypeCreationPresenter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.TypeMarkColor;
import com.zack.enderplan.model.bean.TypeMarkPattern;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.view.widget.CircleColorView;
import com.zack.enderplan.view.widget.ItemView;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeCreationActivity extends BaseActivity implements TypeCreationViewContract {

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
    @BindView(R.id.item_type_mark_pattern)
    ItemView mTypeMarkPatternItem;

    @BindString(R.string.dscpt_touch_to_set)
    String mClickToSetDscpt;

    @Inject
    TypeCreationPresenter mTypeCreationPresenter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TypeCreationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeCreationPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerTypeCreationComponent.builder()
                .typeCreationPresenterModule(new TypeCreationPresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTypeCreationPresenter.detach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_type_creation, menu);
        //在这里改变图标的tint，因为没法在xml文件中改
        (menu.findItem(R.id.action_create)).getIcon().setTint(Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mTypeCreationPresenter.notifyCancelButtonClicked();
                break;
            case R.id.action_create:
                mTypeCreationPresenter.notifyCreateButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mTypeCreationPresenter.notifyCancelButtonClicked();
    }

    @Override
    public void showInitialView(FormattedType formattedType) {
//        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_type_creation);
        ButterKnife.bind(this);

//        //TODO if (savedInstanceState == null)
//        mCircularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                mCircularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
//                playCircularRevealAnimation();
//                return false;
//            }
//        });

        setSupportActionBar(mToolbar);
        setupActionBar();

        onTypeNameChanged(formattedType.getTypeName(), formattedType.getFirstChar());
        onTypeMarkColorChanged(formattedType.getTypeMarkColorInt(), formattedType.getTypeMarkColorName());
    }

    @Override
    public void onTypeNameChanged(String typeName, String firstChar) {
        mTypeMarkIcon.setInnerText(firstChar);
        mTypeNameText.setText(typeName);
        mTypeNameItem.setDescriptionText(typeName);
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt, String colorName) {
        getWindow().setNavigationBarColor(colorInt);
        getWindow().setStatusBarColor(colorInt);
        mToolbar.setBackgroundColor(ColorUtil.reduceSaturation(colorInt, 0.85f));
        mTypeMarkIcon.setFillColor(colorInt);
        mTypeMarkColorItem.setDescriptionText(colorName);
    }

    @Override
    public void onTypeMarkPatternChanged(boolean hasPattern, int patternResId, String patternName) {
        mTypeMarkIcon.setInnerIcon(hasPattern ? getDrawable(patternResId) : null);
        mTypeMarkPatternItem.setDescriptionText(hasPattern ? patternName : mClickToSetDscpt);
    }

    @Override
    public void showTypeNameEditorDialog(String defaultName) {
        EditorDialogFragment fragment = EditorDialogFragment.newInstance(getString(R.string.title_dialog_type_name_editor), defaultName);
        fragment.setOnPositiveButtonClickListener(new EditorDialogFragment.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(String editorText) {
                mTypeCreationPresenter.notifyTypeNameEdited(editorText);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.TYPE_NAME);
    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {
        TypeMarkColorPickerDialogFragment fragment = TypeMarkColorPickerDialogFragment.newInstance(defaultColor);
        fragment.setOnTypeMarkColorPickedListener(new TypeMarkColorPickerDialogFragment.OnTypeMarkColorPickedListener() {
            @Override
            public void onTypeMarkColorPicked(TypeMarkColor typeMarkColor) {
                mTypeCreationPresenter.notifyTypeMarkColorSelected(typeMarkColor);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.TYPE_MARK_COLOR);
    }

    @Override
    public void showTypeMarkPatternPickerDialog(String defaultPattern) {
        TypeMarkPatternPickerDialogFragment fragment = TypeMarkPatternPickerDialogFragment.newInstance(defaultPattern);
        fragment.setOnTypeMarkPatternPickedListener(new TypeMarkPatternPickerDialogFragment.OnTypeMarkPatternPickedListener() {
            @Override
            public void onTypeMarkPatternPicked(TypeMarkPattern typeMarkPattern) {
                mTypeCreationPresenter.notifyTypeMarkPatternSelected(typeMarkPattern);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.TYPE_MARK_PATTERN);
    }

    @OnClick({R.id.item_type_name, R.id.item_type_mark_color, R.id.item_type_mark_pattern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_type_name:
                mTypeCreationPresenter.notifySettingTypeName();
                break;
            case R.id.item_type_mark_color:
                mTypeCreationPresenter.notifySettingTypeMarkColor();
                break;
            case R.id.item_type_mark_pattern:
                mTypeCreationPresenter.notifySettingTypeMarkPattern();
                break;
        }
    }

//    private void playCircularRevealAnimation() {
//        int fabCoordinateInPx = (int) (44 * getResources().getDisplayMetrics().density + 0.5f);
//        int centerX = mCircularRevealLayout.getWidth() - fabCoordinateInPx;
//        int centerY = mCircularRevealLayout.getHeight() - fabCoordinateInPx;
//
//        Animator anim = ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, centerX, centerY, 0, (float) Math.hypot(centerX, centerY));
//        anim.setDuration(400);
//        anim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                CommonUtil.showSoftInput(mTypeNameEditor);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        anim.start();
//    }
}
