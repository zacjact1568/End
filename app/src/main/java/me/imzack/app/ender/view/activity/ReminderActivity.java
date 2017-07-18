package me.imzack.app.ender.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import me.imzack.app.ender.App;
import me.imzack.app.ender.R;
import me.imzack.app.ender.util.TimeUtil;
import me.imzack.app.ender.util.ViewUtil;
import me.imzack.app.ender.injector.component.DaggerReminderComponent;
import me.imzack.app.ender.injector.module.ReminderPresenterModule;
import me.imzack.app.ender.view.dialog.DateTimePickerDialogFragment;
import me.imzack.app.ender.presenter.ReminderPresenter;
import me.imzack.app.ender.view.contract.ReminderViewContract;
import me.imzack.app.ender.common.Constant;
import me.imzack.app.ender.view.widget.ImageTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReminderActivity extends BaseActivity implements ReminderViewContract {

    @BindView(R.id.layout_reminder)
    LinearLayout mReminderLayout;
    @BindView(R.id.text_content)
    TextView mContentText;
    @BindView(R.id.layout_deadline)
    ImageTextView mDeadlineLayout;
    @BindView(R.id.switcher_delay)
    ViewAnimator mDelaySwitcher;

    @Inject
    ReminderPresenter mReminderPresenter;

    public static void start(Context context, int planListPosition) {
        context.startActivity(
                new Intent(context, ReminderActivity.class)
                        .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReminderPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerReminderComponent.builder()
                .reminderPresenterModule(new ReminderPresenterModule(this, getIntent().getIntExtra(Constant.PLAN_LIST_POSITION, -1)))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReminderPresenter.detach();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mReminderPresenter.notifyTouchFinished(event.getRawY());
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void showInitialView(String content, boolean hasDeadline, String deadline) {
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);

        mReminderLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mReminderLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                mReminderPresenter.notifyPreDrawingReminder(ViewUtil.getScreenCoordinateY(mReminderLayout));
                return false;
            }
        });

        mContentText.setText(content);

        mDeadlineLayout.setVisibility(hasDeadline ? View.VISIBLE : View.GONE);
        mDeadlineLayout.setText(deadline);
    }

    @Override
    public void playEnterAnimation() {
        mReminderLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_enter_up));
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void enterPlanDetail(int position) {
        //可能会有两个PlanDetailActivity同时存在
        PlanDetailActivity.start(this, position);
    }

    @OnClick({R.id.btn_delay, R.id.btn_detail, R.id.btn_complete, R.id.btn_back, R.id.btn_1_hour, R.id.btn_tomorrow, R.id.btn_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delay:
                mDelaySwitcher.showNext();
                break;
            case R.id.btn_detail:
                mReminderPresenter.notifyEnteringPlanDetail();
                break;
            case R.id.btn_complete:
                mReminderPresenter.notifyPlanCompleted();
                break;
            case R.id.btn_back:
                mDelaySwitcher.showPrevious();
                break;
            case R.id.btn_1_hour:
                mReminderPresenter.notifyDelayingReminder(Constant.ONE_HOUR);
                break;
            case R.id.btn_tomorrow:
                mReminderPresenter.notifyDelayingReminder(Constant.TOMORROW);
                break;
            case R.id.btn_more:
                DateTimePickerDialogFragment.newInstance(
                        TimeUtil.getDateTimePickerDefaultTime(Constant.UNDEFINED_TIME),
                        new DateTimePickerDialogFragment.OnDateTimePickedListener() {
                            @Override
                            public void onDateTimePicked(long timeInMillis) {
                                mReminderPresenter.notifyUpdatingReminderTime(timeInMillis);
                            }
                        }
                ).show(getSupportFragmentManager());
                break;
        }
    }

    @Override
    public void exit() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_exit_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mReminderLayout.startAnimation(animation);
    }
}
