package com.zack.enderplan.domain.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.interactor.presenter.ReminderPresenter;
import com.zack.enderplan.domain.view.ReminderView;
import com.zack.enderplan.utility.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReminderActivity extends BaseActivity implements ReminderView {

    @BindView(R.id.text_content)
    TextView mContentText;
    @BindView(R.id.switcher_delay)
    ViewAnimator mDelaySwitcher;

    private ReminderPresenter mReminderPresenter;

    public static PendingIntent getPendingIntentForStart(Context context, String planCode, int position) {
        Intent intent = new Intent(context, ReminderActivity.class);
        //plan_code总是有效的，position可能无效（-1），但优先考虑position
        intent.putExtra(Constant.PLAN_CODE, planCode);
        intent.putExtra(Constant.POSITION, position);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String planCode = intent.getStringExtra(Constant.PLAN_CODE);

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(planCode, 0);

        mReminderPresenter = new ReminderPresenter(this, intent.getIntExtra(Constant.POSITION, -1), planCode);

        mReminderPresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReminderPresenter.detachView();
    }

    @Override
    public void showInitialView(String content) {
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);

        mContentText.setText(content);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void enterPlanDetail(int position) {
        //可能会有两个PlanDetailActivity同时存在
        PlanDetailActivity.start(this, position, false);
    }

    @Override
    public void exitReminder() {
        finish();
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
                mReminderPresenter.notifyDelayingReminder("1_hour");
                break;
            case R.id.btn_tomorrow:
                mReminderPresenter.notifyDelayingReminder("tomorrow");
                break;
            case R.id.btn_more:
                //TODO 修改DateTimePickerDialogFragment，显示当前时间不传0
                DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(0);
                fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
                    @Override
                    public void onDateTimePicked(long timeInMillis) {
                        mReminderPresenter.notifyUpdatingReminderTime(timeInMillis);
                    }
                });
                fragment.show(getSupportFragmentManager(), "reminder");
                break;
        }
    }
}
