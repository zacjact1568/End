package com.zack.enderplan.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.widget.TypeAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanDetailActivity extends BaseActivity
        implements CalendarDialogFragment.OnDateChangedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener {

    private EnderPlanDB enderplanDB;
    private Plan plan;
    private List<Type> typeList;
    private boolean flag = true;
    private ContentValues contentValues;
    private ReminderManager reminderManager;
    private RemindedReceiver remindedReceiver;

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
    /*@Bind(R.id.ic_deadline)
    ImageView deadlineIcon;
    @Bind(R.id.text_deadline)
    TextView deadlineText;*/
    @Bind(R.id.text_deadline_description)
    TextView deadlineDescriptionText;
    /*@Bind(R.id.ic_reminder)
    ImageView reminderIcon;
    @Bind(R.id.text_reminder)
    TextView reminderText;*/
    @Bind(R.id.text_reminder_description)
    TextView reminderDescriptionText;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    @BindColor(R.color.colorPrimary)
    int primaryColor;
    @BindColor(android.R.color.tertiary_text_light)
    int lightTextColor;
    @BindString(R.string.date_format)
    String dateFormatStr;
    @BindString(R.string.date_time_format)
    String dateTimeFormatStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        ButterKnife.bind(this);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView contentText = (TextView) findViewById(R.id.text_content);
        ImageView starMark = (ImageView) findViewById(R.id.star_mark);
        ImageView deadlineMark = (ImageView) findViewById(R.id.deadline_mark);
        ImageView reminderMark = (ImageView) findViewById(R.id.reminder_mark);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        RelativeLayout deadlineItemView = (RelativeLayout) findViewById(R.id.item_view_deadline);
        RelativeLayout reminderItemView = (RelativeLayout) findViewById(R.id.item_view_reminder);*/

        enderplanDB = EnderPlanDB.getInstance(this);

        setSupportActionBar(toolbar);
        setupActionBar();

        Intent intent = getIntent();
        plan = intent.getParcelableExtra("plan_detail");

        //setResult(RESULT_CANCELED);

        if (plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED) {
            fab.setImageResource(R.drawable.ic_star_color_accent_24dp);
        }

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
                int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
                plan.setStarStatus(newStarStatus);
                ((FloatingActionButton) view).setImageResource(isStarred ?
                        R.drawable.ic_star_grey600_24dp : R.drawable.ic_star_color_accent_24dp);

                ContentValues values = new ContentValues();
                values.put("star_status", newStarStatus);
                enderplanDB.editPlan(plan.getPlanCode(), values);
            }
        });*/

        contentText.setText(plan.getContent());

        if (plan.getStarStatus() == Plan.PLAN_STAR_STATUS_NOT_STARRED) {
            starMark.setVisibility(View.GONE);
        }
        if (plan.getDeadline() == 0) {
            deadlineMark.setVisibility(View.GONE);
        }
        if (plan.getReminderTime() == 0) {
            reminderMark.setVisibility(View.GONE);
        }

        typeList = enderplanDB.loadType();
        spinner.setAdapter(new TypeAdapter(this, typeList));
        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i).getTypeCode().equals(plan.getTypeCode())) {
                spinner.setSelection(i);
                break;
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag) {
                    flag = false;
                    return;
                }
                String newTypeCode = typeList.get(position).getTypeCode();
                plan.setTypeCode(newTypeCode);
                getContentValues().put("type_code", newTypeCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (plan.getDeadline() != 0) {
            deadlineDescriptionText.setText(DateFormat.format(dateFormatStr, plan.getDeadline()));
            deadlineDescriptionText.setTextColor(primaryColor);
        }

        if (plan.getReminderTime() != 0) {
            reminderDescriptionText.setText(DateFormat.format(dateTimeFormatStr, plan.getReminderTime()));
            reminderDescriptionText.setTextColor(primaryColor);
        }

        /*deadlineItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialogFragment dialog = CalendarDialogFragment.newInstance(plan.getDeadline());
                dialog.show(getFragmentManager(), TAG_DEADLINE);
            }
        });

        reminderItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialogFragment dialog = DateTimePickerDialogFragment.newInstance(plan.getReminderTime());
                dialog.show(getFragmentManager(), TAG_REMINDER);
            }
        });*/

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zack.enderplan.ACTION_REMINDED");
        intentFilter.setPriority(1);
        remindedReceiver = new RemindedReceiver();
        registerReceiver(remindedReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (contentValues != null && contentValues.size() != 0) {
            enderplanDB.editPlan(plan.getPlanCode(), contentValues);
            contentValues.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(remindedReceiver);
    }

    @Override
    public void onBackPressed() {
        setEditedResult();
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
                setEditedResult();
                finish();
                break;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String message = getResources().getString(R.string.msg_dialog_delete_plan_pt1) +
                        plan.getContent() + getResources().getString(R.string.msg_dialog_delete_plan_pt2);
                builder.setTitle(R.string.title_dialog_delete_plan).setMessage(message);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (plan.getReminderTime() != 0) {
                            getReminderManager().cancelAlarm(plan.getPlanCode());
                        }
                        enderplanDB.deletePlan(plan.getPlanCode());
                        if (contentValues != null) {
                            contentValues.clear();
                        }
                        setResult(RESULT_FIRST_USER);
                        finish();
                    }
                }).setNegativeButton(R.string.cancel, null).show();
                break;
            /*case R.id.action_database_info:
                Intent intent = new Intent(this, DatabaseInfoActivity.class);
                intent.putExtra("plan_detail", plan);
                startActivity(intent);
                break;*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSelected(long newDateInMillis) {
        if (plan.getDeadline() == 0) {
            //deadlineIcon.setImageResource(R.drawable.ic_schedule_color_primary_24dp);
            //deadlineText.setTextColor(colorPrimary);
            deadlineDescriptionText.setTextColor(primaryColor);
            deadlineMark.setVisibility(View.VISIBLE);
        }
        plan.setDeadline(newDateInMillis);
        deadlineDescriptionText.setText(DateFormat.format(dateFormatStr, newDateInMillis));
        getContentValues().put("deadline", newDateInMillis);
    }

    @Override
    public void onDateRemoved() {
        if (plan.getDeadline() != 0) {
            deadlineDescriptionText.setText(R.string.unsettled);
            deadlineDescriptionText.setTextColor(lightTextColor);
            deadlineMark.setVisibility(View.GONE);
            plan.setDeadline(0);
            getContentValues().put("deadline", 0);
        }
    }

    @Override
    public void onDateTimeSelected(long newTimeInMillis) {
        getReminderManager().setAlarm(plan.getPlanCode(), newTimeInMillis);
        if (plan.getReminderTime() == 0) {
            //reminderIcon.setImageResource(R.drawable.ic_notifications_color_primary_24dp);
            //reminderText.setTextColor(colorPrimary);
            reminderDescriptionText.setTextColor(primaryColor);
            reminderMark.setVisibility(View.VISIBLE);
        }
        plan.setReminderTime(newTimeInMillis);
        reminderDescriptionText.setText(DateFormat.format(dateTimeFormatStr, newTimeInMillis));
        getContentValues().put("reminder_time", newTimeInMillis);
    }

    @Override
    public void onDateTimeRemoved() {
        if (plan.getReminderTime() != 0) {
            getReminderManager().cancelAlarm(plan.getPlanCode());
            reminderDescriptionText.setText(R.string.unsettled);
            reminderDescriptionText.setTextColor(lightTextColor);
            reminderMark.setVisibility(View.GONE);
            plan.setReminderTime(0);
            getContentValues().put("reminder_time", 0);
        }
    }

    @OnClick({R.id.text_content, R.id.item_view_deadline, R.id.item_view_reminder, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_content:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View contentEditorView = getLayoutInflater().inflate(R.layout.dialog_content_editor, null);
                final EditText contentEditor = (EditText) contentEditorView.findViewById(R.id.editor_content);
                contentEditor.setText(plan.getContent());
                contentEditor.setSelection(contentEditor.length());
                builder.setView(contentEditorView).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newContent = contentEditor.getText().toString();
                        if (!TextUtils.isEmpty(newContent)) {
                            plan.setContent(newContent);
                            contentText.setText(newContent);
                            getContentValues().put("content", newContent);
                        } else {
                            Toast.makeText(PlanDetailActivity.this, R.string.prompt_empty_content, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton(R.string.cancel, null).show();
                break;
            case R.id.item_view_deadline:
                CalendarDialogFragment deadlineDialog = CalendarDialogFragment.newInstance(plan.getDeadline());
                deadlineDialog.show(getFragmentManager(), TAG_DEADLINE);
                break;
            case R.id.item_view_reminder:
                DateTimePickerDialogFragment reminderDialog = DateTimePickerDialogFragment.newInstance(plan.getReminderTime());
                reminderDialog.show(getFragmentManager(), TAG_REMINDER);
                break;
            case R.id.fab:
                boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
                int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
                plan.setStarStatus(newStarStatus);
                ((FloatingActionButton) view).setImageResource(isStarred ? R.drawable.ic_star_grey600_24dp :
                        R.drawable.ic_star_color_accent_24dp);
                starMark.setVisibility(isStarred ? View.GONE : View.VISIBLE);
                getContentValues().put("star_status", newStarStatus);
                break;
        }
    }

    private void setEditedResult() {
        if (contentValues != null) {
            Intent intent = new Intent();
            intent.putExtra("plan_detail", plan);
            setResult(RESULT_OK, intent);
        }
    }

    //TODO 把以下两个方法写到BaseActivity中
    private ContentValues getContentValues() {
        if (contentValues == null) {
            contentValues = new ContentValues();
        }
        return contentValues;
    }

    private ReminderManager getReminderManager() {
        if (reminderManager == null) {
            reminderManager = new ReminderManager(this);
        }
        return reminderManager;
    }

    class RemindedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("plan_code").equals(plan.getPlanCode())) {
                reminderDescriptionText.setText(R.string.unsettled);
                reminderDescriptionText.setTextColor(lightTextColor);
                reminderMark.setVisibility(View.GONE);
                plan.setReminderTime(0);
                if (getContentValues().containsKey("reminder_time")) {
                    getContentValues().remove("reminder_time");
                }
                abortBroadcast();
            }
        }
    }
}
