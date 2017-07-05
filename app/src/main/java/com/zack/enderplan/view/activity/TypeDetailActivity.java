package com.zack.enderplan.view.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.util.ColorUtil;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.util.StringUtil;
import com.zack.enderplan.injector.component.DaggerTypeDetailComponent;
import com.zack.enderplan.injector.module.TypeDetailPresenterModule;
import com.zack.enderplan.view.adapter.SingleTypePlanListAdapter;
import com.zack.enderplan.view.contract.TypeDetailViewContract;
import com.zack.enderplan.presenter.TypeDetailPresenter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.view.dialog.BaseDialogFragment;
import com.zack.enderplan.view.dialog.MessageDialogFragment;
import com.zack.enderplan.view.dialog.TypePickerForPlanMigrationDialogFragment;
import com.zack.enderplan.view.widget.CircleColorView;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeDetailActivity extends BaseActivity implements TypeDetailViewContract {

    @BindView(R.id.layout_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.layout_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.bg_header)
    ImageView mHeaderBackground;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.layout_header)
    LinearLayout mHeaderLayout;
    @BindView(R.id.ic_type_mark)
    CircleColorView mTypeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView mTypeNameText;
    @BindView(R.id.text_uc_plan_count)
    TextView mUcPlanCountText;
    @BindView(R.id.editor_content)
    EditText mContentEditor;
    @BindView(R.id.ic_clear_text)
    ImageView mClearTextIcon;
    @BindView(R.id.list_single_type_plan)
    RecyclerView mSingleTypePlanList;
    @BindView(R.id.layout_editor)
    FrameLayout mEditorLayout;

    @BindString(R.string.hint_editor_content_format)
    String mContentEditorHintFormat;
    @BindString(R.string.snackbar_delete_format)
    String mSnackbarDeleteFormat;
    @BindString(R.string.transition_type_mark_icon)
    String mTypeMarkIconSetName;

    @Inject
    TypeDetailPresenter mTypeDetailPresenter;

    public static void start(Activity activity, int typeListPosition, boolean enableTransition, View sharedElement, String transitionName) {
        Intent intent = new Intent(activity, TypeDetailActivity.class);
        intent.putExtra(Constant.TYPE_LIST_POSITION, typeListPosition);
        intent.putExtra(Constant.ENABLE_TRANSITION, enableTransition);
        intent.putExtra(Constant.TRANSITION_NAME, transitionName);
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
        mTypeDetailPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerTypeDetailComponent.builder()
                .typeDetailPresenterModule(new TypeDetailPresenterModule(this, getIntent().getIntExtra(Constant.TYPE_LIST_POSITION, -1)))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTypeDetailPresenter.notifySwitchingViewVisibility(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTypeDetailPresenter.notifySwitchingViewVisibility(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTypeDetailPresenter.detach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_type_detail, menu);
        //在这里改变图标的tint，因为没法在xml文件中改
        (menu.findItem(R.id.action_edit)).getIcon().setTint(Color.WHITE);
        (menu.findItem(R.id.action_delete)).getIcon().setTint(Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_edit:
                mTypeDetailPresenter.notifyTypeEditingButtonClicked();
                break;
            case R.id.action_delete:
                //TODO 可能需要收起软键盘
                mTypeDetailPresenter.notifyTypeDeletionButtonClicked(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mTypeDetailPresenter.notifyBackPressed();
    }

    @Override
    public void showInitialView(FormattedType formattedType, String ucPlanCountStr, SingleTypePlanListAdapter singleTypePlanListAdapter, ItemTouchHelper itemTouchHelper) {
        boolean enableTransition = getIntent().getBooleanExtra(Constant.ENABLE_TRANSITION, false);

        if (enableTransition) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        setContentView(R.layout.activity_type_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        mAppBarLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mAppBarLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mTypeDetailPresenter.notifyPreDrawingAppBar(mAppBarLayout.getTotalScrollRange());
                return false;
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mTypeDetailPresenter.notifyAppBarScrolled(verticalOffset);
            }
        });

        if (enableTransition) {
            mTypeMarkIcon.setTransitionName(getIntent().getStringExtra(Constant.TRANSITION_NAME));
        }

        mEditorLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mEditorLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mTypeDetailPresenter.notifyPreDrawingEditorLayout(mEditorLayout.getHeight());
                return false;
            }
        });

        mSingleTypePlanList.setAdapter(singleTypePlanListAdapter);
        mSingleTypePlanList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mTypeDetailPresenter.notifyPlanListScrolled(
                        !mSingleTypePlanList.canScrollVertically(-1),
                        !mSingleTypePlanList.canScrollVertically(1)
                );
            }
        });
        itemTouchHelper.attachToRecyclerView(mSingleTypePlanList);

        mContentEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTypeDetailPresenter.notifyContentEditorTextChanged(s.toString());
            }
        });
        mContentEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mTypeDetailPresenter.notifyCreatingPlan(v.getText().toString());
                }
                return false;
            }
        });

        onTypeNameChanged(formattedType.getTypeName(), formattedType.getFirstChar());
        onTypeMarkColorChanged(formattedType.getTypeMarkColorInt());
        onTypeMarkPatternChanged(formattedType.isHasTypeMarkPattern(), formattedType.getTypeMarkPatternResId());
        onUcPlanCountChanged(ucPlanCountStr);
    }

    @Override
    public void onTypeNameChanged(String typeName, String firstChar) {
        mTypeMarkIcon.setInnerText(firstChar);
        mTypeNameText.setText(typeName);
        mContentEditor.setHint(String.format(mContentEditorHintFormat, typeName));
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt) {
        getWindow().setNavigationBarColor(colorInt);
        int headerColorInt = ColorUtil.reduceSaturation(colorInt, 0.85f);
        mCollapsingToolbarLayout.setContentScrimColor(headerColorInt);
        mCollapsingToolbarLayout.setStatusBarScrimColor(colorInt);
        mHeaderBackground.setImageDrawable(new ColorDrawable(headerColorInt));
        mTypeMarkIcon.setFillColor(colorInt);
    }

    @Override
    public void onTypeMarkPatternChanged(boolean hasPattern, int patternResId) {
        mTypeMarkIcon.setInnerIcon(hasPattern ? getDrawable(patternResId) : null);
    }

    @Override
    public void onPlanCreated() {
        mSingleTypePlanList.scrollToPosition(0);
        mContentEditor.setText(null);
    }

    @Override
    public void onUcPlanCountChanged(String ucPlanCountStr) {
        mUcPlanCountText.setText(ucPlanCountStr);
    }

    @Override
    public void onPlanDeleted(final Plan deletedPlan, final int position, final int planListPos, boolean shouldShowSnackbar) {
        if (shouldShowSnackbar) {
            Snackbar.make(mSingleTypePlanList, String.format(mSnackbarDeleteFormat, deletedPlan.getContent()), Snackbar.LENGTH_LONG)
                    .setAction(R.string.button_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTypeDetailPresenter.notifyCreatingPlan(deletedPlan, position, planListPos);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onPlanItemClicked(int posInPlanList) {
        PlanDetailActivity.start(this, posInPlanList);
    }

    @Override
    public void onAppBarScrolled(float headerLayoutAlpha) {
        mHeaderLayout.setAlpha(headerLayoutAlpha);
    }

    @Override
    public void onAppBarScrolledToCriticalPoint(String toolbarTitle, float editorLayoutTransY) {
        mToolbar.setTitle(toolbarTitle);
        ObjectAnimator.ofFloat(mEditorLayout, "translationY", mEditorLayout.getTranslationY(), editorLayoutTransY)
                .setDuration(200)
                .start();
    }

    @Override
    public void changeContentEditorClearTextIconVisibility(boolean isVisible) {
        mClearTextIcon.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void backToTop() {
        mAppBarLayout.setExpanded(true);
        //TODO 滑动到顶部
        //mSingleTypePlanList.scrollToPosition(0);
    }

    @Override
    public void pressBack() {
        super.onBackPressed();
    }

    @Override
    public void enterEditType(int position, boolean enableTransition) {
        TypeEditActivity.start(
                this,
                position,
                enableTransition,
                mTypeMarkIcon,
                mTypeMarkIconSetName
        );
    }

    @Override
    public void onDetectedDeletingLastType() {
        new MessageDialogFragment.Builder()
                .setMessage(R.string.msg_dialog_last_type)
                .setTitle(R.string.title_dialog_last_type)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_ok, null)
                .show(getSupportFragmentManager());
    }

    @Override
    public void onDetectedTypeNotEmpty(int planCount) {
        String[] buttons = {getString(R.string.button_move), getString(R.string.button_delete), getString(R.string.button_cancel)};
        new MessageDialogFragment.Builder()
                .setMessage(StringUtil.addSpan(
                        StringUtil.toUpperCase(ResourceUtil.getQuantityString(R.string.msg_dialog_type_not_empty, R.plurals.text_plan_count, planCount), buttons),
                        buttons,
                        StringUtil.SPAN_BOLD_STYLE
                ))
                .setTitle(R.string.title_dialog_type_not_empty)
                .setNegativeButton(buttons[1], new BaseDialogFragment.OnButtonClickListener() {
                    @Override
                    public boolean onClick() {
                        mTypeDetailPresenter.notifyTypeDeletionButtonClicked(true);
                        return true;
                    }
                })
                .setNegativeButton(buttons[2], null)
                .setPositiveButton(buttons[0], new BaseDialogFragment.OnButtonClickListener() {
                    @Override
                    public boolean onClick() {
                        mTypeDetailPresenter.notifyMovePlanButtonClicked();
                        return true;
                    }
                })
                .show(getSupportFragmentManager());
    }

    @Override
    public void showMovePlanDialog(String typeCode) {
        TypePickerForPlanMigrationDialogFragment.newInstance(
                typeCode,
                new TypePickerForPlanMigrationDialogFragment.OnTypePickedListener() {
                    @Override
                    public void onTypePicked(String typeCode, String typeName) {
                        mTypeDetailPresenter.notifyTypeItemInMovePlanDialogClicked(typeCode, typeName);
                    }
                }
        ).show(getSupportFragmentManager());
    }

    @Override
    public void showTypeDeletionConfirmationDialog(String typeName) {
        new MessageDialogFragment.Builder()
                .setMessage(R.string.msg_dialog_delete_type)
                .setTitle(typeName)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete, new BaseDialogFragment.OnButtonClickListener() {
                    @Override
                    public boolean onClick() {
                        mTypeDetailPresenter.notifyDeletingType(false, null);
                        return true;
                    }
                })
                .show(getSupportFragmentManager());
    }

    @Override
    public void showPlanMigrationConfirmationDialog(String fromTypeName, int planCount, String toTypeName, final String toTypeCode) {
        new MessageDialogFragment.Builder()
                .setMessage(StringUtil.addSpan(
                        String.format(ResourceUtil.getString(R.string.msg_dialog_migrate_plan), ResourceUtil.getQuantityString(R.plurals.text_plan_count, planCount), toTypeName, fromTypeName),
                        new String[]{toTypeName, fromTypeName},
                        StringUtil.SPAN_BOLD_STYLE
                ))
                .setTitle(fromTypeName)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.btn_dialog_move_and_delete, new BaseDialogFragment.OnButtonClickListener() {
                    @Override
                    public boolean onClick() {
                        mTypeDetailPresenter.notifyDeletingType(true, toTypeCode);
                        return true;
                    }
                })
                .show(getSupportFragmentManager());
    }

    @OnClick({R.id.ic_clear_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_clear_text:
                mContentEditor.setText("");
                break;
        }
    }
}
