package com.zack.enderplan.domain.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.interactor.presenter.PlanDetailPresenter;
import com.zack.enderplan.domain.view.PlanDetailView;
import com.zack.enderplan.interactor.adapter.TypeSpinnerAdapter;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanDetailActivity extends BaseActivity
        implements PlanDetailView, CalendarDialogFragment.OnDateChangedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener {

    private PlanDetailPresenter planDetailPresenter;
    private boolean flag = true;
    //private RemindedReceiver remindedReceiver;

    private static final String TAG_DEADLINE = "deadline";
    private static final String TAG_REMINDER = "reminder";

    private static final String CLASS_NAME = "PlanDetailActivity";

    @Bind(R.id.text_content)
    TextView contentText;
    @Bind(R.id.star_mark)
    ImageView starMark;
    @Bind(R.id.deadline_mark)
    ImageView deadlineMark;
    @Bind(R.id.reminder_mark)
    ImageView reminderMark;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.spinner)
    Spinner spinner;
    @Bind(R.id.text_deadline_description)
    TextView deadlineDescriptionText;
    @Bind(R.id.text_reminder_description)
    TextView reminderDescriptionText;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.btn_switch_plan_status)
    TextView switchPlanStatusButton;

    @BindColor(R.color.colorPrimary)
    int primaryColor;
    @BindColor(android.R.color.tertiary_text_light)
    int lightTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupActionBar();

        planDetailPresenter = new PlanDetailPresenter(this, getIntent().getIntExtra("position", 0));

        planDetailPresenter.setInitialView();

        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zack.enderplan.ACTION_REMINDED");
        intentFilter.setPriority(1);
        remindedReceiver = new RemindedReceiver();
        registerReceiver(remindedReceiver, intentFilter);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //planDetailPresenter.syncWithDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(remindedReceiver);
        planDetailPresenter.detachView();
    }

    @Override
    public void onBackPressed() {
        planDetailPresenter.notifyActivityFinished();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                planDetailPresenter.notifyActivityFinished();
                finish();
                break;
            case R.id.action_delete:
                planDetailPresenter.notifyPlanDeletion();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSelected(long newDateInMillis) {
        planDetailPresenter.notifyDeadlineChanged(newDateInMillis);
    }

    @Override
    public void onDateRemoved() {
        planDetailPresenter.notifyDeadlineRemoved();
    }

    @Override
    public void onDateTimeSelected(long newTimeInMillis) {
        planDetailPresenter.notifyReminderTimeChanged(newTimeInMillis);
    }

    @Override
    public void onDateTimeRemoved() {
        planDetailPresenter.notifyReminderRemoved();
    }

    @OnClick({R.id.text_content, R.id.item_view_deadline, R.id.item_view_reminder, R.id.fab, R.id.btn_switch_plan_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_content:
                planDetailPresenter.notifyContentEdit();
                break;
            case R.id.item_view_deadline:
                planDetailPresenter.createDeadlineDialog();
                break;
            case R.id.item_view_reminder:
                planDetailPresenter.createReminderDialog();
                break;
            case R.id.fab:
                planDetailPresenter.notifyStarStatusChanged();
                break;
            case R.id.btn_switch_plan_status:
                planDetailPresenter.notifyPlanStatusChanged();
                break;
        }
    }

    @Override
    public void showInitialView(String content, boolean isStarred, TypeSpinnerAdapter typeSpinnerAdapter,
                                int posInSpinner, boolean hasDeadline, String deadline, boolean hasReminder,
                                String reminderTime, boolean isCompleted, String spsButtonText) {

        contentText.setText(content);

        if (isStarred) {
            fab.setImageResource(R.drawable.ic_star_color_accent_24dp);
            starMark.setVisibility(View.VISIBLE);
        }

        spinner.setAdapter(typeSpinnerAdapter);
        spinner.setSelection(posInSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag) {
                    //防止重复执行
                    flag = false;
                    return;
                }
                planDetailPresenter.notifyTypeCodeChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (hasDeadline) {
            deadlineMark.setVisibility(View.VISIBLE);
            deadlineDescriptionText.setText(deadline);
            deadlineDescriptionText.setTextColor(primaryColor);
        }

        if (hasReminder) {
            reminderMark.setVisibility(View.VISIBLE);
            reminderDescriptionText.setText(reminderTime);
            reminderDescriptionText.setTextColor(primaryColor);
        }

        toolbar.setBackgroundResource(isCompleted ? R.drawable.bg_c_plan_detail : R.drawable.bg_uc_plan_detail);

        switchPlanStatusButton.setText(spsButtonText);
    }

    @Override
    public void showPlanDeletionAlertDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = getResources().getString(R.string.msg_dialog_delete_plan_pt1) +
                content + getResources().getString(R.string.msg_dialog_delete_plan_pt2);
        builder.setTitle(R.string.title_dialog_delete_plan).setMessage(message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                planDetailPresenter.notifyPlanDeleted();
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    @Override
    public void onPlanDeleted(int position, String planCode, String content, boolean isCompleted) {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.putExtra("plan_code", planCode);
        intent.putExtra("content", content);
        intent.putExtra("is_completed", isCompleted);
        setResult(RESULT_PLAN_DELETED, intent);
        finish();
    }

    @Override
    public void onPlanStatusChanged(boolean isCompleted, String newSpsButtonText) {
        toolbar.setBackgroundResource(isCompleted ? R.drawable.bg_c_plan_detail : R.drawable.bg_uc_plan_detail);
        switchPlanStatusButton.setText(newSpsButtonText);
    }

    @Override
    public void showContentEditDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View contentEditorView = getLayoutInflater().inflate(R.layout.dialog_content_editor, null);
        final EditText contentEditor = (EditText) contentEditorView.findViewById(R.id.editor_content);
        contentEditor.setText(content);
        contentEditor.setSelection(contentEditor.length());
        builder.setView(contentEditorView).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                planDetailPresenter.notifyContentEdited(contentEditor.getText().toString());
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    @Override
    public void onContentEditSuccess(String content) {
        contentText.setText(content);
    }

    @Override
    public void onContentEditFailed() {
        Toast.makeText(this, R.string.prompt_empty_content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        fab.setImageResource(isStarred ? R.drawable.ic_star_color_accent_24dp :
                R.drawable.ic_star_grey600_24dp);
        starMark.setVisibility(isStarred ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreateDeadlineDialog(CalendarDialogFragment deadlineDialog) {
        deadlineDialog.show(getFragmentManager(), TAG_DEADLINE);
    }

    @Override
    public void onCreateReminderDialog(DateTimePickerDialogFragment reminderDialog) {
        reminderDialog.show(getFragmentManager(), TAG_REMINDER);
    }

    @Override
    public void onActivityFinished(Intent intent) {
        setResult(RESULT_PLAN_DETAIL_CHANGED, intent);
    }

    @Override
    public void onDeadlineSelected(boolean isSetFirstTime, String deadline) {
        if (isSetFirstTime) {
            //deadlineIcon.setImageResource(R.drawable.ic_schedule_color_primary_24dp);
            //deadlineText.setTextColor(colorPrimary);
            deadlineDescriptionText.setTextColor(primaryColor);
            deadlineMark.setVisibility(View.VISIBLE);
        }
        deadlineDescriptionText.setText(deadline);
    }

    @Override
    public void onDeadlineRemoved() {
        deadlineDescriptionText.setText(R.string.unsettled);
        deadlineDescriptionText.setTextColor(lightTextColor);
        deadlineMark.setVisibility(View.GONE);
    }

    @Override
    public void onReminderTimeSelected(boolean isSetFirstTime, String reminderTime) {
        if (isSetFirstTime) {
            //reminderIcon.setImageResource(R.drawable.ic_notifications_color_primary_24dp);
            //reminderText.setTextColor(colorPrimary);
            reminderDescriptionText.setTextColor(primaryColor);
            reminderMark.setVisibility(View.VISIBLE);
        }
        reminderDescriptionText.setText(reminderTime);
    }

    @Override
    public void onReminderRemoved() {
        reminderDescriptionText.setText(R.string.unsettled);
        reminderDescriptionText.setTextColor(lightTextColor);
        reminderMark.setVisibility(View.GONE);
    }

    /*class RemindedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            planDetailPresenter.notifyReminderOff(intent.getStringExtra("plan_code"));
            abortBroadcast();
        }
    }*/
}
