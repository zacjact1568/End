package com.zack.enderplan.domain.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

    @BindView(R.id.layout_circular_reveal)
    LinearLayout mCircularRevealLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.editor_content)
    EditText mContentEditor;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.text_deadline)
    TextView mDeadlineText;
    @BindView(R.id.text_reminder)
    TextView mReminderText;
    @BindView(R.id.btn_create)
    TextView mCreateButton;

    @BindColor(R.color.colorAccent)
    int mAccentColor;
    @BindColor(R.color.grey)
    int mGreyColor;
    @BindColor(R.color.colorPrimary)
    int mPrimaryColor;
    @BindColor(android.R.color.tertiary_text_light)
    int mLightTextColor;

    private CreatePlanPresenter mCreatePlanPresenter;
    private MenuItem mStarMenuItem;

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
                finish();
                break;
            case R.id.action_star:
                mCreatePlanPresenter.notifyStarStatusChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
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

        //不能在xml中设置
        mCreateButton.setClickable(false);
    }

    @Override
    public void onContentChanged(boolean isValid) {
        mCreateButton.setClickable(isValid);
        mCreateButton.setBackgroundTintList(ColorStateList.valueOf(isValid ? mAccentColor : mGreyColor));
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
        mDeadlineText.setText(deadline);
        mDeadlineText.setTextColor(hasDeadline ? mPrimaryColor : mLightTextColor);
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
        mReminderText.setText(reminderTime);
        mReminderText.setTextColor(hasReminder ? mPrimaryColor : mLightTextColor);
    }

    @Override
    public void exitCreatePlan() {
        finish();
    }

    @OnClick({R.id.layout_deadline, R.id.layout_reminder, R.id.btn_create, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_deadline:
                mCreatePlanPresenter.notifySettingDeadline();
                break;
            case R.id.layout_reminder:
                mCreatePlanPresenter.notifySettingReminder();
                break;
            case R.id.btn_create:
                mCreatePlanPresenter.notifyCreatingPlan();
                break;
            case R.id.btn_cancel:
                mCreatePlanPresenter.notifyPlanCreationCanceled();
                break;
        }
    }

    private void playCircularRevealAnimation() {
        int fabCoordinateInPx = (int) (44 * getResources().getDisplayMetrics().density + 0.5f);
        int centerX = mCircularRevealLayout.getWidth() - fabCoordinateInPx;
        int centerY = mCircularRevealLayout.getHeight() - fabCoordinateInPx;

        ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, centerX, centerY, 0, (float) Math.hypot(centerX, centerY))
                .setDuration(400)
                .start();
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
}
