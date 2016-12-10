package com.zack.enderplan.domain.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.DateTimePickerDialogFragment;
import com.zack.enderplan.interactor.adapter.SimpleTypeAdapter;
import com.zack.enderplan.interactor.presenter.CreatePlanPresenter;
import com.zack.enderplan.domain.view.CreatePlanView;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.widget.ItemView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePlanActivity extends BaseActivity implements CreatePlanView {

    @BindView(R.id.layout_circular_reveal)
    LinearLayout mCircularRevealLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.layout_create)
    LinearLayout mCreateLayout;
    @BindView(R.id.editor_content)
    EditText mContentEditor;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.item_deadline)
    ItemView mDeadlineItem;
    @BindView(R.id.item_reminder)
    ItemView mReminderItem;
    @BindView(R.id.fab_create)
    FloatingActionButton mCreateFab;

    @BindColor(R.color.colorAccent)
    int mAccentColor;
    @BindColor(R.color.grey)
    int mGreyColor;

    private CreatePlanPresenter mCreatePlanPresenter;
    private MenuItem mStarMenuItem;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CreatePlanActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCreatePlanPresenter = new CreatePlanPresenter(this);
        mCreatePlanPresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCreatePlanPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_plan, menu);
        mStarMenuItem = menu.findItem(R.id.action_star);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mCreatePlanPresenter.notifyPlanCreationCanceled();
                break;
            case R.id.action_star:
                mCreatePlanPresenter.notifyStarStatusChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mCreatePlanPresenter.notifyPlanCreationCanceled();
    }

    @Override
    public void showInitialView(SimpleTypeAdapter simpleTypeAdapter) {
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_create_plan);
        ButterKnife.bind(this);

        //TODO if (savedInstanceState == null)
        mCircularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //TODO typeDetailActivity里，把removeListener放在最前
                mCircularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                placeCreateFab();
                playCircularRevealAnimation();
                return false;
            }
        });

        setSupportActionBar(mToolbar);
        setupActionBar();

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
            }
        });

        mSpinner.setAdapter(simpleTypeAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCreatePlanPresenter.notifyTypeCodeChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onContentChanged(boolean isValid) {
        mCreateFab.setBackgroundTintList(ColorStateList.valueOf(isValid ? mAccentColor : mGreyColor));
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        mStarMenuItem.setIcon(isStarred ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_border_white_24dp);
    }

    @Override
    public void showDeadlinePickerDialog(long defaultDeadline) {
        DateTimePickerDialogFragment fragment = DateTimePickerDialogFragment.newInstance(defaultDeadline);
        fragment.setOnDateTimePickedListener(new DateTimePickerDialogFragment.OnDateTimePickedListener() {
            @Override
            public void onDateTimePicked(long timeInMillis) {
                mCreatePlanPresenter.notifyDeadlineChanged(timeInMillis);
            }
        });
        fragment.show(getSupportFragmentManager(), "deadline");
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
                mCreatePlanPresenter.notifyReminderTimeChanged(timeInMillis);
            }
        });
        fragment.show(getSupportFragmentManager(), "reminder");
    }

    @Override
    public void onReminderTimeChanged(boolean hasReminder, String reminderTime) {
        mReminderItem.setDescriptionText(reminderTime, hasReminder);
    }

    @Override
    public void onDetectedEmptyContent() {
        Toast.makeText(this, R.string.toast_empty_content, Toast.LENGTH_SHORT).show();
        mCreateFab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_shake_cpa));
    }

    @Override
    public void exitCreatePlan() {
        finish();
    }

    @OnClick({R.id.item_deadline, R.id.item_reminder, R.id.fab_create})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_deadline:
                mCreatePlanPresenter.notifySettingDeadline();
                break;
            case R.id.item_reminder:
                mCreatePlanPresenter.notifySettingReminder();
                break;
            case R.id.fab_create:
                mCreatePlanPresenter.notifyCreatingPlan();
                break;
        }
    }

    private void placeCreateFab() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(
                mCreateFab.getLeft(),
                mCreateLayout.getBottom() - mCreateFab.getHeight() / 2,
                0,
                0
        );
        mCreateFab.setLayoutParams(params);
    }

    private void playCircularRevealAnimation() {
        int fabCoordinateInPx = (int) (44 * getResources().getDisplayMetrics().density + 0.5f);
        int centerX = mCircularRevealLayout.getWidth() - fabCoordinateInPx;
        int centerY = mCircularRevealLayout.getHeight() - fabCoordinateInPx;

        Animator anim = ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, centerX, centerY, 0, (float) Math.hypot(centerX, centerY));
        anim.setDuration(400);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Util.showSoftInput(mContentEditor);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
                /*.addListener(new Animator.AnimatorListener() {
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
                })*/
    }
}
