package com.zack.enderplan.domain.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.domain.fragment.EditorDialogFragment;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.interactor.presenter.PlanDetailPresenter;
import com.zack.enderplan.domain.view.PlanDetailView;
import com.zack.enderplan.model.bean.FormattedPlan;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanDetailActivity extends BaseActivity
        implements PlanDetailView, CalendarDialogFragment.OnDateChangedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener {

    private static final String LOG_TAG = "PlanDetailActivity";

    @BindView(R.id.text_content)
    TextView contentText;
    @BindView(R.id.star_mark)
    ImageView starMark;
    @BindView(R.id.deadline_mark)
    ImageView deadlineMark;
    @BindView(R.id.reminder_mark)
    ImageView reminderMark;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.text_deadline_description)
    TextView deadlineDescriptionText;
    @BindView(R.id.text_reminder_description)
    TextView reminderDescriptionText;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.btn_switch_plan_status)
    TextView switchPlanStatusButton;

    @BindColor(R.color.colorPrimary)
    int primaryColor;
    @BindColor(android.R.color.tertiary_text_light)
    int lightTextColor;

    private PlanDetailPresenter planDetailPresenter;
    private boolean flag = true;

    private static final String TAG_DEADLINE = "deadline";
    private static final String TAG_REMINDER = "reminder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        planDetailPresenter = new PlanDetailPresenter(this, getIntent().getIntExtra("position", 0));
        planDetailPresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        planDetailPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        planDetailPresenter.notifyMenuItemSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showInitialView(FormattedPlan formattedPlan, SimpleTypeAdapter simpleTypeAdapter) {

        setContentView(R.layout.activity_plan_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupActionBar();

        contentText.setText(formattedPlan.getContent());

        if (formattedPlan.isStarred()) {
            fab.setImageResource(R.drawable.ic_star_color_accent_24dp);
            starMark.setVisibility(View.VISIBLE);
        }

        spinner.setAdapter(simpleTypeAdapter);
        spinner.setSelection(formattedPlan.getSpinnerPos());
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

        if (formattedPlan.getDeadline() == null) {
            deadlineMark.setVisibility(View.VISIBLE);
            deadlineDescriptionText.setText(formattedPlan.getDeadline());
            deadlineDescriptionText.setTextColor(primaryColor);
        }

        if (formattedPlan.getReminderTime() == null) {
            reminderMark.setVisibility(View.VISIBLE);
            reminderDescriptionText.setText(formattedPlan.getReminderTime());
            reminderDescriptionText.setTextColor(primaryColor);
        }

        toolbar.setBackgroundResource(formattedPlan.isCompleted() ? R.drawable.bg_c_plan_detail : R.drawable.bg_uc_plan_detail);

        switchPlanStatusButton.setText(formattedPlan.isCompleted() ? R.string.text_make_plan_uc : R.string.text_make_plan_c);
    }

    @Override
    public void showPlanDeletionDialog(String content) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_dialog_delete_plan)
                .setMessage(getResources().getString(R.string.msg_dialog_delete_plan_pt1) + content + getResources().getString(R.string.msg_dialog_delete_plan_pt2))
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
        toolbar.setBackgroundResource(isCompleted ? R.drawable.bg_c_plan_detail : R.drawable.bg_uc_plan_detail);
        switchPlanStatusButton.setText(isCompleted ? R.string.text_make_plan_uc : R.string.text_make_plan_c);
    }

    @Override
    public void showContentEditorDialog(String content) {
        EditorDialogFragment fragment = EditorDialogFragment.newInstance(App.getGlobalContext().getString(R.string.title_dialog_content_editor), content);
        fragment.setOnPositiveButtonClickListener(new EditorDialogFragment.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(String editorText) {
                planDetailPresenter.notifyContentChanged(editorText);
            }
        });
        fragment.show(getSupportFragmentManager(), "content_editor");
    }

    @Override
    public void onContentEditedSuccessfully(String newContent) {
        contentText.setText(newContent);
    }

    @Override
    public void onContentEditedAbortively() {
        Toast.makeText(this, R.string.toast_empty_content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        fab.setImageResource(isStarred ? R.drawable.ic_star_color_accent_24dp : R.drawable.ic_star_grey600_24dp);
        starMark.setVisibility(isStarred ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTypeOfPlanChanged(int posInTypeList) {
        spinner.setSelection(posInTypeList);
    }

    @Override
    public void showDeadlineDialog(long deadline) {
        CalendarDialogFragment.newInstance(deadline).show(getFragmentManager(), TAG_DEADLINE);
    }

    @Override
    public void showReminderTimeDialog(long reminderTime) {
        DateTimePickerDialogFragment.newInstance(reminderTime).show(getFragmentManager(), TAG_REMINDER);
    }

    @Override
    public void onDeadlineSelected(String newDeadlineText) {
        deadlineMark.setVisibility(View.VISIBLE);
        deadlineDescriptionText.setText(newDeadlineText);
        deadlineDescriptionText.setTextColor(primaryColor);
    }

    @Override
    public void onDeadlineRemoved() {
        deadlineMark.setVisibility(View.GONE);
        deadlineDescriptionText.setText(R.string.dscpt_unsettled);
        deadlineDescriptionText.setTextColor(lightTextColor);
    }

    @Override
    public void onReminderTimeSelected(String newReminderTimeText) {
        reminderMark.setVisibility(View.VISIBLE);
        reminderDescriptionText.setText(newReminderTimeText);
        reminderDescriptionText.setTextColor(primaryColor);
    }

    @Override
    public void onReminderRemoved() {
        reminderMark.setVisibility(View.GONE);
        reminderDescriptionText.setText(R.string.dscpt_unsettled);
        reminderDescriptionText.setTextColor(lightTextColor);
    }

    @Override
    public void exitPlanDetail() {
        finish();
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

    @OnClick({R.id.item_view_deadline, R.id.item_view_reminder, R.id.fab, R.id.btn_switch_plan_status})
    public void onClick(View view) {
        planDetailPresenter.notifyViewClicked(view.getId());
    }
}
