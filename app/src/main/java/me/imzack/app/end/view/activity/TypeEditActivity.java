package me.imzack.app.end.view.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import me.imzack.app.end.App;

import me.imzack.app.end.R;
import me.imzack.app.end.injector.component.DaggerTypeEditComponent;
import me.imzack.app.end.injector.module.TypeEditPresenterModule;
import me.imzack.app.end.presenter.TypeEditPresenter;
import me.imzack.app.end.util.ColorUtil;
import me.imzack.app.end.view.dialog.TypeMarkColorPickerDialogFragment;
import me.imzack.app.end.view.dialog.EditorDialogFragment;
import me.imzack.app.end.view.dialog.TypeMarkPatternPickerDialogFragment;
import me.imzack.app.end.view.contract.TypeEditViewContract;
import me.imzack.app.end.model.bean.FormattedType;
import me.imzack.app.end.model.bean.TypeMarkColor;
import me.imzack.app.end.model.bean.TypeMarkPattern;
import me.imzack.app.end.common.Constant;
import me.imzack.app.end.view.widget.CircleColorView;
import me.imzack.app.end.view.widget.ItemView;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeEditActivity extends BaseActivity implements TypeEditViewContract {

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

    @BindString(R.string.dscpt_unsettled)
    String mUnsettledDscpt;

    @Inject
    TypeEditPresenter mTypeEditPresenter;

    public static void start(Activity activity, int typeListPosition, boolean enableTransition, View sharedElement, String transitionName) {
        Intent intent = new Intent(activity, TypeEditActivity.class);
        intent.putExtra(Constant.TYPE_LIST_POSITION, typeListPosition);
        intent.putExtra(Constant.ENABLE_TRANSITION, enableTransition);
        if (enableTransition) {
            activity.startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, transitionName).toBundle()
            );
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeEditPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerTypeEditComponent.builder()
                .typeEditPresenterModule(new TypeEditPresenterModule(
                        this,
                        getIntent().getIntExtra(Constant.TYPE_LIST_POSITION, -1)
                ))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTypeEditPresenter.detach();
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
    public void showInitialView(FormattedType formattedType) {
        if (getIntent().getBooleanExtra(Constant.ENABLE_TRANSITION, false)) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        setContentView(R.layout.activity_type_edit);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        onTypeNameChanged(formattedType.getTypeName(), formattedType.getFirstChar());
        onTypeMarkColorChanged(formattedType.getTypeMarkColorInt(), formattedType.getTypeMarkColorName());
        onTypeMarkPatternChanged(formattedType.isHasTypeMarkPattern(), formattedType.getTypeMarkPatternResId(), formattedType.getTypeMarkPatternName());
    }

    @Override
    public void showTypeNameEditorDialog(String originalEditorText) {
        new EditorDialogFragment.Builder()
                .setEditorText(originalEditorText)
                .setEditorHint(R.string.hint_type_name_editor_edit)
                .setPositiveButton(R.string.button_ok, new EditorDialogFragment.OnTextEditedListener() {
                    @Override
                    public void onTextEdited(String text) {
                        mTypeEditPresenter.notifyUpdatingTypeName(text);
                    }
                })
                .setTitle(R.string.title_dialog_type_name_editor)
                .setNegativeButton(R.string.button_cancel, null)
                .show(getSupportFragmentManager());
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
        mTypeNameItem.setThemeColor(colorInt);
        mTypeMarkColorItem.setDescriptionText(colorName);
        mTypeMarkColorItem.setThemeColor(colorInt);
        mTypeMarkPatternItem.setThemeColor(colorInt);
    }

    @Override
    public void onTypeMarkPatternChanged(boolean hasPattern, int patternResId, String patternName) {
        mTypeMarkIcon.setInnerIcon(hasPattern ? getDrawable(patternResId) : null);
        mTypeMarkPatternItem.setDescriptionText(hasPattern ? patternName : mUnsettledDscpt);
    }

    @Override
    public void showTypeMarkColorPickerDialog(String defaultColor) {
        TypeMarkColorPickerDialogFragment.newInstance(
                defaultColor,
                new TypeMarkColorPickerDialogFragment.OnTypeMarkColorPickedListener() {
                    @Override
                    public void onTypeMarkColorPicked(TypeMarkColor typeMarkColor) {
                        mTypeEditPresenter.notifyTypeMarkColorSelected(typeMarkColor);
                    }
                }
        ).show(getSupportFragmentManager());
    }

    @Override
    public void showTypeMarkPatternPickerDialog(String defaultPattern) {
        TypeMarkPatternPickerDialogFragment.newInstance(
                defaultPattern,
                new TypeMarkPatternPickerDialogFragment.OnTypeMarkPatternPickedListener() {
                    @Override
                    public void onTypeMarkPatternPicked(TypeMarkPattern typeMarkPattern) {
                        mTypeEditPresenter.notifyTypeMarkPatternSelected(typeMarkPattern);
                    }
                }
        ).show(getSupportFragmentManager());
    }

    @OnClick({R.id.item_type_name, R.id.item_type_mark_color, R.id.item_type_mark_pattern})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_type_name:
                mTypeEditPresenter.notifySettingTypeName();
                break;
            case R.id.item_type_mark_color:
                mTypeEditPresenter.notifySettingTypeMarkColor();
                break;
            case R.id.item_type_mark_pattern:
                mTypeEditPresenter.notifySettingTypeMarkPattern();
                break;
        }
    }
}
