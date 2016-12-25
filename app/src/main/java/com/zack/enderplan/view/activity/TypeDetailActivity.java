package com.zack.enderplan.view.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerTypeDetailComponent;
import com.zack.enderplan.injector.module.TypeDetailPresenterModule;
import com.zack.enderplan.view.contract.TypeDetailViewContract;
import com.zack.enderplan.view.adapter.SingleTypePlanAdapter;
import com.zack.enderplan.view.adapter.SimpleTypeAdapter;
import com.zack.enderplan.view.callback.PlanItemTouchCallback;
import com.zack.enderplan.presenter.TypeDetailPresenter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.common.Util;
import com.zack.enderplan.view.widget.CircleColorView;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeDetailActivity extends BaseActivity implements TypeDetailViewContract {

    @BindView(R.id.layout_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_header)
    LinearLayout mHeaderLayout;
    @BindView(R.id.ic_type_mark)
    CircleColorView typeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView typeNameText;
    @BindView(R.id.text_uc_plan_count)
    TextView ucPlanCountText;
    @BindView(R.id.editor_content)
    EditText contentEditor;
    @BindView(R.id.list_plan)
    RecyclerView mPlanList;
    @BindView(R.id.layout_editor)
    FrameLayout mEditorLayout;

    @BindString(R.string.hint_editor_content_format)
    String mContentEditorHintFormat;
    @BindString(R.string.snackbar_delete_format)
    String mSnackbarDeleteFormat;
    @BindString(R.string.name_type_mark_shared_element_transition)
    String mTypeMarkSetName;

    @Inject
    TypeDetailPresenter mTypeDetailPresenter;

    public static void start(Activity activity, int position, View sharedElement, String sharedElementName) {
        Intent intent = new Intent(activity, TypeDetailActivity.class);
        intent.putExtra(Constant.POSITION, position);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, sharedElementName);
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeDetailPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerTypeDetailComponent.builder()
                .typeDetailPresenterModule(new TypeDetailPresenterModule(this, getIntent().getIntExtra(Constant.POSITION, -1)))
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
    public void showInitialView(FormattedType formattedType, String ucPlanCountStr, SingleTypePlanAdapter singleTypePlanAdapter) {
        setContentView(R.layout.activity_type_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
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

        mEditorLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mEditorLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mTypeDetailPresenter.notifyPreDrawingEditorLayout(mEditorLayout.getHeight());
                return false;
            }
        });

        singleTypePlanAdapter.setOnPlanItemClickListener(new SingleTypePlanAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(int position) {
                mTypeDetailPresenter.notifyPlanItemClicked(position);
            }
        });

        singleTypePlanAdapter.setOnStarButtonClickListener(new SingleTypePlanAdapter.OnStarButtonClickListener() {
            @Override
            public void onStarButtonClick(int position) {
                mTypeDetailPresenter.notifyPlanStarStatusChanged(position);
            }
        });

        mPlanList.setLayoutManager(new LinearLayoutManager(this));
        mPlanList.setHasFixedSize(true);
        mPlanList.setAdapter(singleTypePlanAdapter);

        PlanItemTouchCallback planItemTouchCallback = new PlanItemTouchCallback();
        planItemTouchCallback.setOnItemSwipedListener(new PlanItemTouchCallback.OnItemSwipedListener() {
            @Override
            public void onItemSwiped(int position, int direction) {
                switch (direction) {
                    case PlanItemTouchCallback.DIR_START:
                        mTypeDetailPresenter.notifyDeletingPlan(position);
                        break;
                    case PlanItemTouchCallback.DIR_END:
                        mTypeDetailPresenter.notifySwitchingPlanStatus(position);
                        break;
                }
            }
        });
        planItemTouchCallback.setOnItemMovedListener(new PlanItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                mTypeDetailPresenter.notifyPlanSequenceChanged(fromPosition, toPosition);
            }
        });
        new ItemTouchHelper(planItemTouchCallback).attachToRecyclerView(mPlanList);

        contentEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        typeMarkIcon.setInnerText(firstChar);
        typeNameText.setText(typeName);
        contentEditor.setHint(String.format(mContentEditorHintFormat, typeName));
    }

    @Override
    public void onTypeMarkColorChanged(int colorInt) {
        typeMarkIcon.setFillColor(colorInt);
    }

    @Override
    public void onTypeMarkPatternChanged(boolean hasPattern, int patternResId) {
        typeMarkIcon.setInnerIcon(hasPattern ? getDrawable(patternResId) : null);
    }

    @Override
    public void onPlanCreated() {
        mPlanList.scrollToPosition(0);
        contentEditor.setText("");
    }

    @Override
    public void onUcPlanCountChanged(String ucPlanCountStr) {
        ucPlanCountText.setText(ucPlanCountStr);
    }

    @Override
    public void onPlanDeleted(final Plan deletedPlan, final int position, final int planListPos, boolean shouldShowSnackbar) {
        if (shouldShowSnackbar) {
            Snackbar.make(mPlanList, String.format(mSnackbarDeleteFormat, deletedPlan.getContent()), Snackbar.LENGTH_LONG)
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
        PlanDetailActivity.start(this, posInPlanList, true);
    }

    @Override
    public void onAppBarScrolled(float headerLayoutAlpha) {
        mHeaderLayout.setAlpha(headerLayoutAlpha);
    }

    @Override
    public void onAppBarScrolledToCriticalPoint(String toolbarTitle, float editorLayoutTransY) {
        toolbar.setTitle(toolbarTitle);
        ObjectAnimator.ofFloat(mEditorLayout, "translationY", mEditorLayout.getTranslationY(), editorLayoutTransY)
                .setDuration(200)
                .start();
    }

    @Override
    public void backToTop() {
        mAppBarLayout.setExpanded(true);
        //TODO 滑动到顶部
        //mPlanList.scrollToPosition(0);
    }

    @Override
    public void pressBack() {
        super.onBackPressed();
    }

    @Override
    public void enterEditType(int position, boolean shouldPlaySharedElementTransition) {
        EditTypeActivity.start(this, position, shouldPlaySharedElementTransition, typeMarkIcon, mTypeMarkSetName);
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void onDetectedDeletingLastType() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_last_type)
                .setMessage(R.string.msg_dialog_last_type)
                .setPositiveButton(R.string.button_ok, null)
                .show();
    }

    @Override
    public void onDetectedTypeNotEmpty() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_type_not_empty)
                .setMessage(Util.addBoldStyle(getString(R.string.msg_dialog_type_not_empty), new String[]{getString(R.string.button_move), getString(R.string.button_delete), getString(R.string.button_cancel)}))
                .setPositiveButton(R.string.button_move, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTypeDetailPresenter.notifyMovePlanButtonClicked();
                    }
                })
                .setNeutralButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTypeDetailPresenter.notifyTypeDeletionButtonClicked(true);
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public void showMovePlanDialog(int planCount, SimpleTypeAdapter simpleTypeAdapter) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_dialog_move_plan_pt1) + " " + planCount + " " + getString(planCount > 1 ? R.string.title_dialog_move_plan_pt2_pl : R.string.title_dialog_move_plan_pt2_sg))
                .setAdapter(simpleTypeAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTypeDetailPresenter.notifyTypeItemInMovePlanDialogClicked(which);
                    }
                })
                .show();
    }

    @Override
    public void showTypeDeletionConfirmationDialog(String typeName) {
        new AlertDialog.Builder(this)
                .setTitle(typeName)
                .setMessage(R.string.msg_dialog_delete_type)
                .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTypeDetailPresenter.notifyDeletingType(false, null);
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public void showPlanMigrationConfirmationDialog(String fromTypeName, String toTypeName, final String toTypeCode) {
        new AlertDialog.Builder(this)
                .setTitle(fromTypeName)
                .setMessage(Util.addBoldStyle(getString(R.string.msg_dialog_migrate_plan_pt1) + " " + toTypeName + getString(R.string.msg_dialog_migrate_plan_pt2), new String[]{toTypeName}))
                .setPositiveButton(R.string.btn_dialog_move_and_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTypeDetailPresenter.notifyDeletingType(true, toTypeCode);
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @OnClick({R.id.ic_clear_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_clear_text:
                contentEditor.setText("");
                break;
        }
    }
}
