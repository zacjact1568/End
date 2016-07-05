package com.zack.enderplan.domain.activity;

import android.animation.Animator;
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
import com.zack.enderplan.domain.fragment.CalendarDialogFragment;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.interactor.presenter.CreatePlanPresenter;
import com.zack.enderplan.domain.view.CreatePlanView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePlanActivity extends BaseActivity
        implements CreatePlanView, CalendarDialogFragment.OnDateChangedListener,
        DateTimePickerDialogFragment.OnDateTimeChangedListener {

    @BindView(R.id.button_save)
    ImageView saveButton;
    @BindView(R.id.editor_content)
    EditText contentEditor;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.star_mark)
    ImageView starMark;
    @BindView(R.id.deadline_mark)
    ImageView deadlineMark;
    @BindView(R.id.reminder_mark)
    ImageView reminderMark;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.circular_reveal_layout)
    LinearLayout circularRevealLayout;

    private CreatePlanPresenter createPlanPresenter;

    private static final int FAB_COORDINATE_IN_DP = 44;
    private static final int CR_ANIM_DURATION = 400;

    private static final String CLASS_NAME = "CreatePlanActivity";
    private static final String TAG_DEADLINE = "deadline";
    private static final String TAG_REMINDER = "reminder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_create_plan);
        ButterKnife.bind(this);

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

        createPlanPresenter = new CreatePlanPresenter(this);

        contentEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                createPlanPresenter.notifyContentChanged(s.toString());
                saveButton.setVisibility(TextUtils.isEmpty(s.toString()) ? View.INVISIBLE : View.VISIBLE);
            }
        });

        spinner.setAdapter(createPlanPresenter.createTypeSpinnerAdapter());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideInputMethodForContentEditor();
                createPlanPresenter.notifyTypeCodeChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        createPlanPresenter.detachView();
    }

    @Override
    public void onDateSelected(long newDateInMillis) {
        createPlanPresenter.notifyDeadlineChanged(newDateInMillis);
        deadlineMark.setImageResource(R.drawable.ic_schedule_color_accent_24dp);
    }

    @Override
    public void onDateRemoved() {
        createPlanPresenter.notifyDeadlineChanged(0);
        deadlineMark.setImageResource(R.drawable.ic_schedule_grey600_24dp);
    }

    @Override
    public void onDateTimeSelected(long newTimeInMillis) {
        createPlanPresenter.notifyReminderTimeChanged(newTimeInMillis);
        reminderMark.setImageResource(R.drawable.ic_notifications_color_accent_24dp);
    }

    @Override
    public void onDateTimeRemoved() {
        createPlanPresenter.notifyReminderTimeChanged(0);
        reminderMark.setImageResource(R.drawable.ic_notifications_none_grey600_24dp);
    }

    //显示键盘
    private void showInputMethodForContentEditor() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (contentEditor.hasFocus()) {
            manager.showSoftInput(contentEditor, 0);
        }
    }

    //隐藏键盘
    private void hideInputMethodForContentEditor() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isActive(contentEditor)) {
            manager.hideSoftInputFromWindow(contentEditor.getWindowToken(), 0);
        }
    }

    //圆形Reveal动画
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

    @OnClick({R.id.button_cancel, R.id.button_save, R.id.star_mark, R.id.deadline_mark, R.id.reminder_mark})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_cancel:
                makeCircularRevealAnimation(false);
                break;
            case R.id.button_save:
                //enderplanDB.savePlan(plan);
                createPlanPresenter.createNewPlan();
                /*Intent intent = new Intent();
                intent.putExtra("plan_detail", plan);*/
                setResult(RESULT_OK);
                makeCircularRevealAnimation(false);
                break;
            case R.id.star_mark:
                createPlanPresenter.notifyStarStatusChanged();
                break;
            case R.id.deadline_mark:
                createPlanPresenter.createDeadlineDialog();
                break;
            case R.id.reminder_mark:
                createPlanPresenter.createReminderDialog();
                break;
        }
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        starMark.setImageResource(isStarred ? R.drawable.ic_star_color_accent_24dp :
                R.drawable.ic_star_outline_grey600_24dp);
    }

    @Override
    public void onCreateDeadlineDialog(CalendarDialogFragment deadlineDialog) {
        deadlineDialog.show(getFragmentManager(), TAG_DEADLINE);
    }

    @Override
    public void onCreateReminderDialog(DateTimePickerDialogFragment reminderDialog) {
        reminderDialog.show(getFragmentManager(), TAG_REMINDER);
    }
}
