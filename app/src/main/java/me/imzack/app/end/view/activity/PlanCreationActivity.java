package me.imzack.app.end.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import me.imzack.app.end.App;
import me.imzack.app.end.R;
import me.imzack.app.end.model.bean.FormattedType;
import me.imzack.app.end.util.ColorUtil;
import me.imzack.app.end.injector.component.DaggerPlanCreationComponent;
import me.imzack.app.end.injector.module.PlanCreationPresenterModule;
import me.imzack.app.end.view.adapter.TypeGalleryAdapter;
import me.imzack.app.end.view.contract.PlanCreationViewContract;
import me.imzack.app.end.view.dialog.DateTimePickerDialogFragment;
import me.imzack.app.end.presenter.PlanCreationPresenter;
import me.imzack.app.end.view.widget.CircleColorView;
import me.imzack.app.end.view.widget.ItemView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanCreationActivity extends BaseActivity implements PlanCreationViewContract {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ic_plan)
    ImageView mPlanIcon;
    @BindView(R.id.editor_content)
    EditText mContentEditor;
    @BindView(R.id.ic_type_mark)
    CircleColorView mTypeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView mTypeNameText;
    @BindView(R.id.gallery_type)
    RecyclerView mTypeGallery;
    @BindView(R.id.item_deadline)
    ItemView mDeadlineItem;
    @BindView(R.id.item_reminder)
    ItemView mReminderItem;

    @Inject
    PlanCreationPresenter mPlanCreationPresenter;

    private MenuItem mStarMenuItem;
    private MenuItem mCreateMenuItem;

    public static final int ALPHA_OPACITY = 255;
    public static final int ALPHA_TRANSLUCENCE = 155;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PlanCreationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlanCreationPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerPlanCreationComponent.builder()
                .planCreationPresenterModule(new PlanCreationPresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlanCreationPresenter.detach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan_creation, menu);
        mStarMenuItem = menu.findItem(R.id.action_star);
        mCreateMenuItem = menu.findItem(R.id.action_create);
        //在这里改变图标的tint和alpha，因为没法在xml文件中改
        mStarMenuItem.getIcon().setTint(Color.WHITE);
        mCreateMenuItem.getIcon().setTint(Color.WHITE);
        mCreateMenuItem.getIcon().setAlpha(ALPHA_TRANSLUCENCE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mPlanCreationPresenter.notifyPlanCreationCanceled();
                break;
            case R.id.action_star:
                mPlanCreationPresenter.notifyStarStatusChanged();
                break;
            case R.id.action_create:
                mPlanCreationPresenter.notifyCreatingPlan();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mPlanCreationPresenter.notifyPlanCreationCanceled();
    }

    @Override
    public void showInitialView(TypeGalleryAdapter typeGalleryAdapter, FormattedType formattedType) {
        //overridePendingTransition(0, 0);
        setContentView(R.layout.activity_plan_creation);
        ButterKnife.bind(this);

//        //TODO if (savedInstanceState == null)
//        mCircularRevealLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                mCircularRevealLayout.getViewTreeObserver().removeOnPreDrawListener(this);
//                placeCreateFab();
//                playCircularRevealAnimation();
//                return false;
//            }
//        });

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
                mPlanCreationPresenter.notifyContentChanged(s.toString());
            }
        });

        ((LinearLayoutManager) mTypeGallery.getLayoutManager()).setOrientation(LinearLayoutManager.HORIZONTAL);
        mTypeGallery.setAdapter(typeGalleryAdapter);
        mTypeGallery.setHasFixedSize(true);

        onContentChanged(false);
        onStarStatusChanged(false);
        onTypeOfPlanChanged(formattedType);
    }

    @Override
    public void onContentChanged(boolean isValid) {
        if (mCreateMenuItem != null) {
            mCreateMenuItem.getIcon().setAlpha(isValid ? ALPHA_OPACITY : ALPHA_TRANSLUCENCE);
        }
    }

    @Override
    public void onStarStatusChanged(boolean isStarred) {
        if (mStarMenuItem != null) {
            mStarMenuItem.setIcon(isStarred ? R.drawable.ic_star_black_24dp : R.drawable.ic_star_border_black_24dp);
            mStarMenuItem.getIcon().setTint(Color.WHITE);
        }
    }

    @Override
    public void onTypeOfPlanChanged(FormattedType formattedType) {
        int typeMarkColorInt = formattedType.getTypeMarkColorInt();
        getWindow().setNavigationBarColor(typeMarkColorInt);
        getWindow().setStatusBarColor(typeMarkColorInt);
        mToolbar.setBackgroundColor(ColorUtil.reduceSaturation(typeMarkColorInt, 0.85f));
        mPlanIcon.setImageTintList(ColorStateList.valueOf(typeMarkColorInt));
        mTypeMarkIcon.setFillColor(typeMarkColorInt);
        mTypeMarkIcon.setInnerIcon(formattedType.isHasTypeMarkPattern() ? getDrawable(formattedType.getTypeMarkPatternResId()) : null);
        mTypeMarkIcon.setInnerText(formattedType.getFirstChar());
        mTypeNameText.setText(formattedType.getTypeName());
        mDeadlineItem.setThemeColor(typeMarkColorInt);
        mReminderItem.setThemeColor(typeMarkColorInt);
    }

    @Override
    public void onTypeCreationItemClicked() {
        TypeCreationActivity.start(this);
    }

    @Override
    public void showDeadlinePickerDialog(long defaultDeadline) {
        DateTimePickerDialogFragment.newInstance(
                defaultDeadline,
                new DateTimePickerDialogFragment.OnDateTimePickedListener() {
                    @Override
                    public void onDateTimePicked(long timeInMillis) {
                        mPlanCreationPresenter.notifyDeadlineChanged(timeInMillis);
                    }
                }
        ).show(getSupportFragmentManager());
    }

    @Override
    public void onDeadlineChanged(CharSequence deadline) {
        mDeadlineItem.setDescriptionText(deadline);
    }

    @Override
    public void showReminderTimePickerDialog(long defaultReminderTime) {
        DateTimePickerDialogFragment.newInstance(
                defaultReminderTime,
                new DateTimePickerDialogFragment.OnDateTimePickedListener() {
                    @Override
                    public void onDateTimePicked(long timeInMillis) {
                        mPlanCreationPresenter.notifyReminderTimeChanged(timeInMillis);
                    }
                }
        ).show(getSupportFragmentManager());
    }

    @Override
    public void onReminderTimeChanged(CharSequence reminderTime) {
        mReminderItem.setDescriptionText(reminderTime);
    }

    @OnClick({R.id.item_deadline, R.id.item_reminder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_deadline:
                mPlanCreationPresenter.notifySettingDeadline();
                break;
            case R.id.item_reminder:
                mPlanCreationPresenter.notifySettingReminder();
                break;
        }
    }

//    private void placeCreateFab() {
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(
//                mCreateFab.getLeft(),
//                mCreateLayout.getBottom() - mCreateFab.getHeight() / 2,
//                0,
//                0
//        );
//        mCreateFab.setLayoutParams(params);
//    }
//
//    private void playCircularRevealAnimation() {
//        int fabCoordinateInPx = CommonUtil.convertDpToPx(Constant.FAB_COORDINATE);
//        int centerX = mCircularRevealLayout.getWidth() - fabCoordinateInPx;
//        int centerY = mCircularRevealLayout.getHeight() - fabCoordinateInPx;
//
//        Animator anim = ViewAnimationUtils.createCircularReveal(mCircularRevealLayout, centerX, centerY, 0, (float) Math.hypot(centerX, centerY));
//        anim.setDuration(400);
//        anim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                CommonUtil.showSoftInput(mContentEditor);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        anim.start();
//                /*.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        if (!isEnterAnim) {
//                            //TODO 有bug，若键盘显示，CR动画最终收回的位置与fab不对应
//                            hideInputMethodForContentEditor();
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        if (isEnterAnim) {
//                            mCardView.setVisibility(View.VISIBLE);
//                            showInputMethodForContentEditor();
//                        } else {
//                            mCircularRevealLayout.setVisibility(View.INVISIBLE);
//                            finish();
//                            overridePendingTransition(0, 0);
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                })*/
//    }
}
