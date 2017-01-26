package com.zack.enderplan.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.util.SystemUtil;
import com.zack.enderplan.injector.component.DaggerPlanCreationComponent;
import com.zack.enderplan.injector.module.PlanCreationPresenterModule;
import com.zack.enderplan.view.contract.PlanCreationViewContract;
import com.zack.enderplan.view.dialog.DateTimePickerDialogFragment;
import com.zack.enderplan.view.adapter.SimpleTypeAdapter;
import com.zack.enderplan.presenter.PlanCreationPresenter;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.view.widget.ItemView;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanCreationActivity extends BaseActivity implements PlanCreationViewContract {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.editor_content)
    EditText mContentEditor;
    @BindView(R.id.spinner_type)
    Spinner mTypeSpinner;
    @BindView(R.id.item_deadline)
    ItemView mDeadlineItem;
    @BindView(R.id.item_reminder)
    ItemView mReminderItem;

    @BindColor(R.color.colorPrimaryLight)
    int mPrimaryLightColor;

    @Inject
    PlanCreationPresenter mPlanCreationPresenter;

    private MenuItem mStarMenuItem;
    private MenuItem mCreateMenuItem;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PlanCreationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlanCreationPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerPlanCreationComponent.builder()
                .planCreationPresenterModule(new PlanCreationPresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlanCreationPresenter.detach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_creation, menu);
        mStarMenuItem = menu.findItem(R.id.action_star);
        mCreateMenuItem = menu.findItem(R.id.action_create);
        //在这里改变图标的tint，因为没法在xml文件中改
        mStarMenuItem.getIcon().setTint(Color.WHITE);
        mCreateMenuItem.getIcon().setTint(mPrimaryLightColor);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mPlanCreationPresenter.notifyPlanCreationCanceled();
                break;
            case R.id.action_star:
                mPlanCreationPresenter.notifyStarStatusChanged();
                break;
            case R.id.action_create:
                mPlanCreationPresenter.notifyCreatingPlan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mPlanCreationPresenter.notifyPlanCreationCanceled();
    }

    @Override
    public void showInitialView(SimpleTypeAdapter simpleTypeAdapter) {
        //overridePendingTransition(0, 0);
        setContentView(R.layout.activity_plan_creation);
        ButterKnife.bind(this);

//        //TODO if (savedInstanceState == null)
//        mCircularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                mCircularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
//                placeCreateFab();
//                playCircularRevealAnimation();
//                return false;
//            }
//        });

        setSupportActionBar(mToolbar);
        setupActionBar();

        SystemUtil.showSoftInput(mContentEditor, 100);
        mContentEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPlanCreationPresenter.notifyContentChanged(s.toString());
            }
        });

        mTypeSpinner.setAdapter(simpleTypeAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPlanCreationPresenter.notifyTypeCodeChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        onStarStatusChanged(false);
        onContentChanged(false);
    }

    @Override
    public void onContentChanged(boolean isValid) {
        if (mCreateMenuItem != null) {
            mCreateMenuItem.getIcon().setTint(isValid ? Color.WHITE : mPrimaryLightColor);
        }
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        if (mStarMenuItem != null) {
            mStarMenuItem.setIcon(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
            mStarMenuItem.getIcon().setTint(Color.WHITE);
        }
    }

    @Override
    public void showDeadlinePickerDialog(long defaultDeadline) {
        DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(defaultDeadline);
        fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
            @Override
            public void onDateTimePicked(long timeInMillis) {
                mPlanCreationPresenter.notifyDeadlineChanged(timeInMillis);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.DEADLINE);
    }

    @Override
    public void onDeadlineChanged(boolean hasDeadline, String deadline) {
        mDeadlineItem.setDescriptionText(deadline, hasDeadline);
    }

    @Override
    public void showReminderTimePickerDialog(long defaultReminderTime) {
        DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(defaultReminderTime);
        fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
            @Override
            public void onDateTimePicked(long timeInMillis) {
                mPlanCreationPresenter.notifyReminderTimeChanged(timeInMillis);
            }
        });
        fragment.show(getSupportFragmentManager(), Constant.REMINDER_TIME);
    }

    @Override
    public void onReminderTimeChanged(boolean hasReminder, String reminderTime) {
        mReminderItem.setDescriptionText(reminderTime, hasReminder);
    }

    @OnClick({R.id.item_deadline, R.id.item_reminder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_deadline:
                mPlanCreationPresenter.notifySettingDeadline();
                break;
            case R.id.item_reminder:
                mPlanCreationPresenter.notifySettingReminder();
                break;
        }
    }

//    private void placeCreateFab() {
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(
//                mCreateFab.getLeft(),
//                mCreateLayout.getBottom() - mCreateFab.getHeight() / 2,
//                0,
//                0
//        );
//        mCreateFab.setLayoutParams(params);
//    }
//
//    private void playCircularRevealAnimation() {
//        int fabCoordinateInPx = CommonUtil.convertDpToPx(Constant.FAB_COORDINATE);
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
//                CommonUtil.showSoftInput(mContentEditor);
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
//                /*.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        if (!isEnterAnim) {
//                            //TODO 有bug，若键盘显示，CR动画最终收回的位置与fab不对应
//                            hideInputMethodForContentEditor();
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        if (isEnterAnim) {
//                            mCardView.setVisibility(View.VISIBLE);
//                            showInputMethodForContentEditor();
//                        } else {
//                            mCircularRevealLayout.setVisibility(View.INVISIBLE);
//                            finish();
//                            overridePendingTransition(0, 0);
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                })*/
//    }
}
