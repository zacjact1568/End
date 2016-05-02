package com.zack.enderplan.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.presenter.ReminderPresenter;
import com.zack.enderplan.view.ReminderView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReminderActivity extends AppCompatActivity implements ReminderView {

    @Bind(R.id.text_content)
    TextView contentText;
    @Bind(R.id.layout_title)
    LinearLayout titleLayout;
    @Bind(R.id.btn_delay_reminder)
    FloatingActionButton delayReminderButton;
    @Bind(R.id.btn_cancel_reminder)
    FloatingActionButton cancelReminderButton;
    @Bind(R.id.btn_complete_plan)
    FloatingActionButton completePlanButton;

    private ReminderPresenter reminderPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reminder);
        ButterKnife.bind(this);

        Plan plan = getIntent().getParcelableExtra("plan_detail");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(plan.getPlanCode(), 0);

        reminderPresenter = new ReminderPresenter(this, plan);

        reminderPresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reminderPresenter.detachView();
    }

    @OnClick({R.id.btn_delay_reminder, R.id.btn_cancel_reminder, R.id.btn_complete_plan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delay_reminder:
                reminderPresenter.notifyReminderDelayed();
                break;
            case R.id.btn_cancel_reminder:
                reminderPresenter.notifyReminderCanceled();
                break;
            case R.id.btn_complete_plan:
                reminderPresenter.notifyPlanCompleted();
                break;
        }
    }

    @Override
    public void showInitialView(String contentStr, int titleBgColorInt) {
        contentText.setText(contentStr);
        titleLayout.setBackgroundColor(titleBgColorInt);
    }

    @Override
    public void onReminderDelayed(String nextReminderTime) {
        Toast.makeText(this, "我将在" + nextReminderTime + "再次提醒你", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReminderCanceled() {
        Toast.makeText(this, "提醒已取消", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlanCompleted(String content) {
        Toast.makeText(this, "您已完成计划：" + content, Toast.LENGTH_SHORT).show();
    }
}
