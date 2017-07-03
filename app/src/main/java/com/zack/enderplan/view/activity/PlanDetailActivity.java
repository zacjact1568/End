package com.zack.enderplan.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerPlanDetailComponent;
import com.zack.enderplan.injector.module.PlanDetailPresenterModule;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.util.ColorUtil;
import com.zack.enderplan.view.dialog.DateTimePickerDialogFragment;
import com.zack.enderplan.view.dialog.EditorDialogFragment;
import com.zack.enderplan.presenter.PlanDetailPresenter;
import com.zack.enderplan.view.contract.PlanDetailViewContract;
import com.zack.enderplan.model.bean.FormattedPlan;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.view.dialog.MessageDialogFragment;
import com.zack.enderplan.view.dialog.TypePickerDialogFragment;
import com.zack.enderplan.view.widget.CircleColorView;
import com.zack.enderplan.view.widget.ItemView;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanDetailActivity extends BaseActivity implements PlanDetailViewContract {

    @BindView(R.id.layout_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.layout_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.bg_header)
    ImageView mHeaderBackground;
    @BindView(R.id.layout_header)
    LinearLayout mHeaderLayout;
    @BindView(R.id.text_content)
    TextView contentText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ic_type_mark)
    CircleColorView mTypeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView mTypeNameText;
    @BindView(R.id.fab_star)
    FloatingActionButton mStarFab;
    @BindView(R.id.item_type)
    ItemView mTypeItem;
    @BindView(R.id.item_deadline)
    ItemView mDeadlineItem;
    @BindView(R.id.item_reminder)
    ItemView mReminderItem;
    @BindView(R.id.btn_switch_plan_status)
    TextView switchPlanStatusButton;

    @BindColor(R.color.colorAccent)
    int mAccentColor;
    @BindColor(R.color.grey_600)
    int mGrey600Color;

    @Inject
    PlanDetailPresenter planDetailPresenter;

    public static void start(Context context, int planListPosition) {
        context.startActivity(
                new Intent(context, PlanDetailActivity.class)
                        .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
        );
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
    public void showInitialView(FormattedPlan formattedPlan, FormattedType formattedType) {

        setContentView(R.layout.activity_plan_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupActionBar();

        //注释掉这一句使AppBar可折叠
        ((AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams()).setScrollFlags(0);

        mAppBarLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mAppBarLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                planDetailPresenter.notifyPreDrawingAppBar(mAppBarLayout.getTotalScrollRange());
                return false;
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                planDetailPresenter.notifyAppBarScrolled(verticalOffset);
            }
        });

        onContentChanged(formattedPlan.getContent());
        onStarStatusChanged(formattedPlan.isStarred());
        onTypeOfPlanChanged(formattedType);
        onDeadlineChanged(formattedPlan.getDeadline());
        onReminderTimeChanged(formattedPlan.getReminderTime());
        onPlanStatusChanged(formattedPlan.isCompleted());
    }

    @Override
    public void onAppBarScrolled(float headerLayoutAlpha) {
        mHeaderLayout.setAlpha(headerLayoutAlpha);
    }

    @Override
    public void onAppBarScrolledToCriticalPoint(String toolbarTitle) {
        toolbar.setTitle(toolbarTitle);
    }

    @Override
    public void showPlanDeletionDialog(String content) {
        MessageDialogFragment fragment = MessageDialogFragment.newInstance(getString(R.string.title_dialog_delete_plan), getString(R.string.msg_dialog_delete_plan_pt1) + "\n" + content, null, getString(R.string.button_cancel), getString(R.string.delete));
        fragment.setOnPositiveButtonClickListener(new MessageDialogFragment.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick() {
                planDetailPresenter.notifyPlanDeleted();
            }
        });
        fragment.show(getSupportFragmentManager());
    }

    @Override
    public void onPlanStatusChanged(boolean isCompleted) {
        mReminderItem.setClickable(!isCompleted);
        mReminderItem.setAlpha(isCompleted ? 0.6f : 1);
        switchPlanStatusButton.setText(isCompleted ? R.string.text_make_plan_uc : R.string.text_make_plan_c);
    }

    @Override
    public void showContentEditorDialog(String content) {
        EditorDialogFragment fragment = EditorDialogFragment.newInstance(getString(R.string.title_dialog_content_editor), content, getString(R.string.hint_content_editor_edit));
        fragment.setOnOkButtonClickListener(new EditorDialogFragment.OnOkButtonClickListener() {
            @Override
            public void onOkButtonClick(String editorText) {
                planDetailPresenter.notifyContentChanged(editorText);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.CONTENT);
    }

    @Override
    public void onContentChanged(String newContent) {
        contentText.setText(newContent);
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        mStarFab.setImageResource(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        mStarFab.setImageTintList(ColorStateList.valueOf(isStarred ? mAccentColor : mGrey600Color));
    }

    @Override
    public void onTypeOfPlanChanged(FormattedType formattedType) {
        int typeMarkColorInt = formattedType.getTypeMarkColorInt();
        int headerColorInt = ColorUtil.reduceSaturation(typeMarkColorInt, 0.85f);
        getWindow().setNavigationBarColor(typeMarkColorInt);
        mCollapsingToolbarLayout.setContentScrimColor(headerColorInt);
        mCollapsingToolbarLayout.setStatusBarScrimColor(typeMarkColorInt);
        mHeaderBackground.setImageDrawable(new ColorDrawable(headerColorInt));
        mTypeMarkIcon.setFillColor(typeMarkColorInt);
        mTypeMarkIcon.setInnerIcon(formattedType.isHasTypeMarkPattern() ? getDrawable(formattedType.getTypeMarkPatternResId()) : null);
        mTypeMarkIcon.setInnerText(formattedType.getFirstChar());
        mTypeNameText.setText(formattedType.getTypeName());
        mTypeItem.setDescriptionText(formattedType.getTypeName());
        mTypeItem.setThemeColor(typeMarkColorInt);
        mDeadlineItem.setThemeColor(typeMarkColorInt);
        mReminderItem.setThemeColor(typeMarkColorInt);
    }

    @Override
    public void showTypePickerDialog(int defaultTypeListPos) {
        TypePickerDialogFragment fragment = TypePickerDialogFragment.newInstance(defaultTypeListPos);
        fragment.setOnTypePickedListener(new TypePickerDialogFragment.OnTypePickedListener() {
            @Override
            public void onTypePicked(int position) {
                planDetailPresenter.notifyTypeOfPlanChanged(position);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.TYPE);
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
    public void onDeadlineChanged(CharSequence newDeadline) {
        mDeadlineItem.setDescriptionText(newDeadline);
    }

    @Override
    public void onReminderTimeChanged(CharSequence newReminderTime) {
        mReminderItem.setDescriptionText(newReminderTime);
    }

    @Override
    public void backToTop() {
        mAppBarLayout.setExpanded(true);
    }

    @Override
    public void pressBack() {
        super.onBackPressed();
    }

    @OnClick({R.id.item_type, R.id.item_deadline, R.id.item_reminder, R.id.fab_star, R.id.btn_switch_plan_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_type:
                planDetailPresenter.notifySettingTypeOfPlan();
                break;
            case R.id.item_deadline:
                planDetailPresenter.notifySettingDeadline();
                break;
            case R.id.item_reminder:
                planDetailPresenter.notifySettingReminder();
                break;
            case R.id.fab_star:
                planDetailPresenter.notifyStarStatusChanged();
                break;
            case R.id.btn_switch_plan_status:
                planDetailPresenter.notifyPlanStatusChanged();
                break;
        }
    }
}
