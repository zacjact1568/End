package com.zack.enderplan.view.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerPlanDetailComponent;
import com.zack.enderplan.injector.module.PlanDetailPresenterModule;
import com.zack.enderplan.view.dialog.DateTimePickerDialogFragment;
import com.zack.enderplan.view.dialog.EditorDialogFragment;
import com.zack.enderplan.view.adapter.SimpleTypeAdapter;
import com.zack.enderplan.presenter.PlanDetailPresenter;
import com.zack.enderplan.view.contract.PlanDetailViewContract;
import com.zack.enderplan.model.bean.FormattedPlan;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.view.widget.ItemView;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanDetailActivity extends BaseActivity implements PlanDetailViewContract {

    @BindView(R.id.layout_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.bg_toolbar)
    ImageView mToolbarBackground;
    @BindView(R.id.layout_header)
    RelativeLayout mHeaderLayout;
    @BindView(R.id.text_content)
    TextView contentText;
    @BindView(R.id.ic_star)
    ImageView starIcon;
    @BindView(R.id.ic_deadline)
    ImageView deadlineIcon;
    @BindView(R.id.ic_reminder)
    ImageView reminderIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_content)
    LinearLayout mContentLayout;
    @BindView(R.id.text_content_collapsed)
    TextView mContentCollapsedText;
    @BindView(R.id.spinner_type)
    Spinner mTypeSpinner;
    @BindView(R.id.item_deadline)
    ItemView mDeadlineItem;
    @BindView(R.id.item_reminder)
    ItemView mReminderItem;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.btn_switch_plan_status)
    TextView switchPlanStatusButton;

    @BindColor(R.color.colorAccent)
    int mAccentColor;
    @BindColor(R.color.grey_600)
    int mGrey600Color;
    @BindColor(R.color.colorPrimaryLight)
    int mPrimaryLightColor;

    @Inject
    PlanDetailPresenter planDetailPresenter;

    private MenuItem mStarMenuItem;

    public static void start(Activity activity, int planListPosition, boolean transition) {
        Intent intent = new Intent(activity, PlanDetailActivity.class);
        intent.putExtra(Constant.PLAN_LIST_POSITION, planListPosition);
        activity.startActivity(intent, transition ? ActivityOptions.makeSceneTransitionAnimation(activity).toBundle() : null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        planDetailPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerPlanDetailComponent.builder()
                .planDetailPresenterModule(new PlanDetailPresenterModule(this, getIntent().getIntExtra(Constant.PLAN_LIST_POSITION, -1)))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        planDetailPresenter.detach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_detail, menu);
        mStarMenuItem = menu.findItem(R.id.action_star);
        //在这里改变图标的tint，因为没法在xml文件中改
        (menu.findItem(R.id.action_edit)).getIcon().setTint(Color.WHITE);
        (menu.findItem(R.id.action_delete)).getIcon().setTint(Color.WHITE);
        planDetailPresenter.notifyMenuCreated();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_star:
                planDetailPresenter.notifyStarStatusChanged();
                break;
            case R.id.action_edit:
                planDetailPresenter.notifyContentEditingButtonClicked();
                break;
            case R.id.action_delete:
                planDetailPresenter.notifyPlanDeletionButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        planDetailPresenter.notifyBackPressed();
    }

    @Override
    public void showInitialView(FormattedPlan formattedPlan, SimpleTypeAdapter simpleTypeAdapter) {

        setContentView(R.layout.activity_plan_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupActionBar();

        mAppBarLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                planDetailPresenter.notifyPreDrawingAppBar(mAppBarLayout.getTotalScrollRange());
                mAppBarLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                planDetailPresenter.notifyAppBarScrolled(verticalOffset);
            }
        });

        mContentCollapsedText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                planDetailPresenter.notifyPreDrawingContentCollapsedText(mContentCollapsedText.getHeight());
                mContentCollapsedText.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        mTypeSpinner.setAdapter(simpleTypeAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                planDetailPresenter.notifyTypeCodeChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        onContentChanged(formattedPlan.getContent());
        onStarStatusChanged(formattedPlan.isStarred());
        onTypeOfPlanChanged(formattedPlan.getSpinnerPos());
        onDeadlineChanged(formattedPlan.isHasDeadline(), formattedPlan.getDeadline());
        onReminderTimeChanged(formattedPlan.isHasReminder(), formattedPlan.getReminderTime());
        onPlanStatusChanged(formattedPlan.isCompleted());
    }

    @Override
    public void updateStarMenuItem(boolean isStarred) {
        if (mStarMenuItem == null) return;
        mStarMenuItem.setIcon(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        mStarMenuItem.getIcon().setTint(Color.WHITE);
    }

    @Override
    public void onAppBarScrolled(float headerLayoutAlpha, float contentLayoutTransY) {
        mHeaderLayout.setAlpha(headerLayoutAlpha);
        mContentLayout.setTranslationY(contentLayoutTransY);
    }

    @Override
    public void onAppBarScrolledToCriticalPoint(String toolbarTitle, boolean isStarMenuItemVisible) {
        toolbar.setTitle(toolbarTitle);
        mStarMenuItem.setVisible(isStarMenuItemVisible);
    }

    @Override
    public void showPlanDeletionDialog(String content) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_delete_plan)
                .setMessage(getString(R.string.msg_dialog_delete_plan_pt1) + "\n" + content)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        planDetailPresenter.notifyPlanDeleted();
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public void onPlanStatusChanged(boolean isCompleted) {
        mToolbarBackground.setImageResource(isCompleted ? R.drawable.bg_c_plan_detail : R.drawable.bg_uc_plan_detail);
        mContentCollapsedText.setBackgroundColor(isCompleted ? Color.LTGRAY : mPrimaryLightColor);
        switchPlanStatusButton.setText(isCompleted ? R.string.text_make_plan_uc : R.string.text_make_plan_c);
    }

    @Override
    public void showContentEditorDialog(String content) {
        EditorDialogFragment fragment = EditorDialogFragment.newInstance(App.getContext().getString(R.string.title_dialog_content_editor), content);
        fragment.setOnPositiveButtonClickListener(new EditorDialogFragment.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(String editorText) {
                planDetailPresenter.notifyContentChanged(editorText);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.CONTENT);
    }

    @Override
    public void onContentChanged(String newContent) {
        contentText.setText(newContent);
        mContentCollapsedText.setText(newContent);
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        updateStarMenuItem(isStarred);
        starIcon.setVisibility(isStarred ? View.VISIBLE : View.GONE);
        fab.setImageTintList(ColorStateList.valueOf(isStarred ? mAccentColor : mGrey600Color));
    }

    @Override
    public void onTypeOfPlanChanged(int posInTypeList) {
        mTypeSpinner.setSelection(posInTypeList);
    }

    @Override
    public void showDeadlinePickerDialog(long defaultDeadline) {
        DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(defaultDeadline);
        fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
            @Override
            public void onDateTimePicked(long timeInMillis) {
                planDetailPresenter.notifyDeadlineChanged(timeInMillis);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.DEADLINE);
    }

    @Override
    public void showReminderTimePickerDialog(long defaultReminderTime) {
        DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(defaultReminderTime);
        fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
            @Override
            public void onDateTimePicked(long timeInMillis) {
                planDetailPresenter.notifyReminderTimeChanged(timeInMillis);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.REMINDER_TIME);
    }

    @Override
    public void onDeadlineChanged(boolean hasDeadline, String newDeadline) {
        deadlineIcon.setVisibility(hasDeadline ? View.VISIBLE : View.GONE);
        mDeadlineItem.setDescriptionText(newDeadline, hasDeadline);
    }

    @Override
    public void onReminderTimeChanged(boolean hasReminder, String newReminderTime) {
        reminderIcon.setVisibility(hasReminder ? View.VISIBLE : View.GONE);
        mReminderItem.setDescriptionText(newReminderTime, hasReminder);
    }

    @Override
    public void backToTop() {
        mAppBarLayout.setExpanded(true);
    }

    @Override
    public void pressBack() {
        super.onBackPressed();
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exit() {
        finish();
    }

    @OnClick({R.id.item_deadline, R.id.item_reminder, R.id.fab, R.id.btn_switch_plan_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_deadline:
                planDetailPresenter.notifySettingDeadline();
                break;
            case R.id.item_reminder:
                planDetailPresenter.notifySettingReminder();
                break;
            case R.id.fab:
                planDetailPresenter.notifyStarStatusChanged();
                break;
            case R.id.btn_switch_plan_status:
                planDetailPresenter.notifyPlanStatusChanged();
                break;
        }
    }
}
