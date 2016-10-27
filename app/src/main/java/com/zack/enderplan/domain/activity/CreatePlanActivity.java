package com.zack.enderplan.domain.activity;

import android.animation.Animator;
import android.content.res.ColorStateList;
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
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.interactor.presenter.CreatePlanPresenter;
import com.zack.enderplan.domain.view.CreatePlanView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePlanActivity extends BaseActivity implements CreatePlanView {

    @BindView(R.id.button_save)
    ImageView mSaveButton;
    @BindView(R.id.editor_content)
    EditText mContentEditor;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.star_mark)
    ImageView mStarMark;
    @BindView(R.id.deadline_mark)
    ImageView mDeadlineMark;
    @BindView(R.id.reminder_mark)
    ImageView mReminderMark;
    @BindView(R.id.card_view)
    CardView mCardView;
    @BindView(R.id.circular_reveal_layout)
    LinearLayout mCircularRevealLayout;

    @BindColor(R.color.colorAccent)
    int mAccentColor;
    @BindColor(R.color.grey)
    int mGreyColor;

    private CreatePlanPresenter mCreatePlanPresenter;

    private static final int FAB_COORDINATE_IN_DP = 44;
    private static final int CR_ANIM_DURATION = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_create_plan);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mCardView.setVisibility(View.INVISIBLE);
            mCircularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mCircularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    makeCircularRevealAnimation(true);
                    return false;
                }
            });
        }

        mCreatePlanPresenter = new CreatePlanPresenter(this);

        mCreatePlanPresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCreatePlanPresenter.detachView();
    }

    //显示键盘
    private void showInputMethodForContentEditor() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (mContentEditor.hasFocus()) {
            manager.showSoftInput(mContentEditor, 0);
        }
    }

    //隐藏键盘
    private void hideInputMethodForContentEditor() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isActive(mContentEditor)) {
            manager.hideSoftInputFromWindow(mContentEditor.getWindowToken(), 0);
        }
    }

    //圆形Reveal动画
    private void makeCircularRevealAnimation(final boolean isEnterAnim) {
        float scale = getResources().getDisplayMetrics().density;
        int fabCoordinateInPx = (int) (FAB_COORDINATE_IN_DP * scale + 0.5f);
        int centerX = mCircularRevealLayout.getWidth() - fabCoordinateInPx;
        int centerY = mCircularRevealLayout.getHeight() - fabCoordinateInPx;
        float radius = (float) Math.hypot(centerX, centerY);
        float startRadius = isEnterAnim ? 0 : radius;
        float endRadius = isEnterAnim ? radius : 0;

        Animator circularRevealAnim = ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, centerX, centerY, startRadius, endRadius);
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
                    mCardView.setVisibility(View.VISIBLE);
                    showInputMethodForContentEditor();
                } else {
                    mCircularRevealLayout.setVisibility(View.INVISIBLE);
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
                mCreatePlanPresenter.notifyCreatingNewPlan();
                makeCircularRevealAnimation(false);
                break;
            case R.id.star_mark:
                mCreatePlanPresenter.notifyStarStatusChanged();
                break;
            case R.id.deadline_mark:
                mCreatePlanPresenter.notifyDeadlineButtonClicked();
                break;
            case R.id.reminder_mark:
                mCreatePlanPresenter.notifyReminderButtonClicked();
                break;
        }
    }

    @Override
    public void showInitialView(SimpleTypeAdapter simpleTypeAdapter) {
        mContentEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCreatePlanPresenter.notifyContentChanged(s.toString());
                mSaveButton.setVisibility(TextUtils.isEmpty(s.toString()) ? View.INVISIBLE : View.VISIBLE);
            }
        });

        mSpinner.setAdapter(simpleTypeAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideInputMethodForContentEditor();
                mCreatePlanPresenter.notifyTypeCodeChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        mStarMark.setImageResource(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
        mStarMark.setImageTintList(ColorStateList.valueOf(isStarred ? mAccentColor : mGreyColor));
    }

    @Override
    public void showDeadlinePickerDialog(long defaultDeadline) {
        DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(defaultDeadline);
        fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
            @Override
            public void onDateTimePicked(long timeInMillis) {
                mCreatePlanPresenter.notifyDeadlineChanged(timeInMillis);
                mDeadlineMark.setImageTintList(ColorStateList.valueOf(timeInMillis == 0 ? mGreyColor : mAccentColor));
            }
        });
        fragment.show(getSupportFragmentManager(), "deadline");
    }

    @Override
    public void showReminderTimePickerDialog(long defaultReminderTime) {
        DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(defaultReminderTime);
        fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
            @Override
            public void onDateTimePicked(long timeInMillis) {
                mCreatePlanPresenter.notifyReminderTimeChanged(timeInMillis);
                mReminderMark.setImageResource(timeInMillis == 0 ? R.drawable.ic_notifications_none_black_24dp : R.drawable.ic_notifications_black_24dp);
                mReminderMark.setImageTintList(ColorStateList.valueOf(timeInMillis == 0 ? mGreyColor : mAccentColor));
            }
        });
        fragment.show(getSupportFragmentManager(), "reminder");
    }
}
