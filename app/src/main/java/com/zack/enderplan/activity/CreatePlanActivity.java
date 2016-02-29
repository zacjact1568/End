package com.zack.enderplan.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.zack.enderplan.R;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.widget.TypeAdapter;

import java.util.List;

public class CreatePlanActivity extends BaseActivity
        implements CalendarDialogFragment.OnDateChangedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener {

    private static final String CLASS_NAME = "CreatePlanActivity";

    private EnderPlanDB enderplanDB;
    private Plan plan;
    private List<Type> typeList;
    private LinearLayout circularRevealLayout;
    private CardView cardView;
    private EditText contentEditor;
    private ImageView deadlineMark, reminderMark;
    private ImageView saveButton;
    //private PlanOptionalItemsFragment planOptionalItemsFragment;

    private static final int FAB_COORDINATE_IN_DP = 44;
    private static final int CR_ANIM_DURATION = 400;

    private static final String TAG_PRIORITY_LEVEL = "priority_level";
    private static final String TAG_DEADLINE = "deadline";
    private static final String TAG_REMINDER = "reminder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_create_plan);

        circularRevealLayout = (LinearLayout) findViewById(R.id.circular_reveal_layout);
        cardView = (CardView) findViewById(R.id.card_view);
        if (savedInstanceState == null) {
            cardView.setVisibility(View.INVISIBLE);
            circularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    makeCircularRevealAnimation(true);
                    return false;
                }
            });
        }

        ImageView cancelButton = (ImageView) findViewById(R.id.button_cancel);
        saveButton = (ImageView) findViewById(R.id.button_save);
        contentEditor = (EditText) findViewById(R.id.editor_content);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ImageView starMark = (ImageView) findViewById(R.id.star_mark);
        deadlineMark = (ImageView) findViewById(R.id.deadline_mark);
        reminderMark = (ImageView) findViewById(R.id.reminder_mark);

        enderplanDB = EnderPlanDB.getInstance(this);
        plan = new Plan(Util.makeCode());

        //setResult(RESULT_CANCELED);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCircularRevealAnimation(false);
            }
        });

        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plan.setCreationTime(System.currentTimeMillis());
                enderplanDB.savePlan(plan);
                Intent intent = new Intent();
                intent.putExtra("plan_detail", plan);
                setResult(RESULT_OK, intent);
                makeCircularRevealAnimation(false);
            }
        });

        contentEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                plan.setContent(s.toString());
                saveButton.setVisibility(TextUtils.isEmpty(s.toString()) ? View.INVISIBLE : View.VISIBLE);
            }
        });

        typeList = enderplanDB.loadType();
        spinner.setAdapter(new TypeAdapter(this, typeList));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideInputMethodForContentEditor();
                plan.setTypeCode(typeList.get(position).getTypeCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        starMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
                plan.setStarStatus(isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED);
                ((ImageView) v).setImageResource(isStarred ? R.drawable.ic_star_outline_grey600_24dp :
                        R.drawable.ic_star_color_accent_24dp);
            }
        });

        deadlineMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialogFragment dialog = CalendarDialogFragment.newInstance(plan.getDeadline());
                dialog.show(getFragmentManager(), TAG_DEADLINE);
            }
        });

        reminderMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialogFragment dialog = DateTimePickerDialogFragment.newInstance(plan.getReminderTime());
                dialog.show(getFragmentManager(), TAG_REMINDER);
            }
        });

        /*planOptionalItemsFragment = PlanOptionalItemsFragment.newInstance(plan.getPriorityLevel(),
                plan.getDeadline(), plan.getReminderTime());
        getFragmentManager().beginTransaction().replace(R.id.frame_layout, planOptionalItemsFragment).commit();*/
    }

    /*@Override
    public void onItemViewClick(View itemView) {
        hideInputMethodForContentEditor();
        switch (itemView.getId()) {
            case R.id.item_view_priority_level:
                RatingBarDialogFragment priorityLevelDialog = RatingBarDialogFragment.newInstance(
                        plan.getStarStatus(), getResources().getString(R.string.dialog_title_priority_level));
                priorityLevelDialog.show(getFragmentManager(), TAG_PRIORITY_LEVEL);
                break;
            case R.id.item_view_deadline:

                break;
            case R.id.item_view_reminder:

                break;
            default:
                break;
        }
    }*/

    /*@Override
    public void onRatingChanged(int newRating) {
        plan.setStarStatus(newRating);

        String[] priorityLevelDescriptions = getResources().getStringArray(R.array.descriptions_priority_level);

        RelativeLayout view = planOptionalItemsFragment.getPriorityLevelItemView();
        ((TextView) view.findViewById(R.id.text_priority_level_description))
                .setText(priorityLevelDescriptions[newRating]);
    }*/

    @Override
    public void onDateSelected(long newDateInMillis) {
        plan.setDeadline(newDateInMillis);
        deadlineMark.setImageResource(R.drawable.ic_schedule_color_accent_24dp);
        //RelativeLayout view = planOptionalItemsFragment.getDeadlineItemView();
        /*String dateFormatStr = getResources().getString(R.string.date_format);
        ((TextView) view.findViewById(R.id.text_deadline_description))
                .setText(DateFormat.format(dateFormatStr, newDateInMillis));*/
    }

    @Override
    public void onDateRemoved() {
        plan.setDeadline(0);
        deadlineMark.setImageResource(R.drawable.ic_schedule_grey600_24dp);
    }

    @Override
    public void onDateTimeSelected(long newTimeInMillis) {
        plan.setReminderTime(newTimeInMillis);
        reminderMark.setImageResource(R.drawable.ic_notifications_color_accent_24dp);
        /*RelativeLayout view = planOptionalItemsFragment.getReminderItemView();
        String dateTimeFormatStr = getResources().getString(R.string.date_time_format);
        ((TextView) view.findViewById(R.id.text_reminder_description))
                .setText(DateFormat.format(dateTimeFormatStr, newTimeInMillis));*/
    }

    @Override
    public void onDateTimeRemoved() {
        plan.setReminderTime(0);
        reminderMark.setImageResource(R.drawable.ic_notifications_none_grey600_24dp);
    }

    private void showInputMethodForContentEditor() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (contentEditor.hasFocus()) {
            manager.showSoftInput(contentEditor, 0);
        }
    }

    private void hideInputMethodForContentEditor() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isActive(contentEditor)) {
            manager.hideSoftInputFromWindow(contentEditor.getWindowToken(), 0);
        }
    }

    private void makeCircularRevealAnimation(final boolean isEnterAnim) {
        float scale = getResources().getDisplayMetrics().density;
        int fabCoordinateInPx = (int) (FAB_COORDINATE_IN_DP * scale + 0.5f);
        int centerX = circularRevealLayout.getWidth() - fabCoordinateInPx;
        int centerY = circularRevealLayout.getHeight() - fabCoordinateInPx;
        float radius = (float) Math.hypot(centerX, centerY);
        float startRadius = isEnterAnim ? 0 : radius;
        float endRadius = isEnterAnim ? radius : 0;

        Animator circularRevealAnim = ViewAnimationUtils.createCircularReveal(circularRevealLayout, centerX, centerY, startRadius, endRadius);
        circularRevealAnim.setDuration(CR_ANIM_DURATION);
        circularRevealAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!isEnterAnim) {
                    //TODO 有bug，若键盘显示，CR动画最终收回的位置与fab不对应
                    hideInputMethodForContentEditor();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isEnterAnim) {
                    cardView.setVisibility(View.VISIBLE);
                    showInputMethodForContentEditor();
                } else {
                    circularRevealLayout.setVisibility(View.INVISIBLE);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        circularRevealAnim.start();
    }
}
