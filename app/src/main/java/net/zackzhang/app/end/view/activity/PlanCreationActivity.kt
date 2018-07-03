package net.zackzhang.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_plan_creation.*
import kotlinx.android.synthetic.main.content_plan_creation.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.injector.component.DaggerPlanCreationComponent
import net.zackzhang.app.end.injector.module.PlanCreationPresenterModule
import net.zackzhang.app.end.model.bean.FormattedType
import net.zackzhang.app.end.presenter.PlanCreationPresenter
import net.zackzhang.app.end.util.ColorUtil
import net.zackzhang.app.end.view.adapter.TypeGalleryAdapter
import net.zackzhang.app.end.view.contract.PlanCreationViewContract
import net.zackzhang.app.end.view.dialog.DateTimePickerDialogFragment
import javax.inject.Inject

class PlanCreationActivity : BaseActivity(), PlanCreationViewContract {

    companion object {

        private const val TAG_DEADLINE_PICKER = "deadline_picker"
        private const val TAG_REMINDER_TIME_PICKER = "reminder_time_picker"

        fun start(context: Context) {
            context.startActivity(Intent(context, PlanCreationActivity::class.java))
        }
    }

    @Inject
    lateinit var mPlanCreationPresenter: PlanCreationPresenter

    private val ALPHA_OPACITY = 255
    private val ALPHA_TRANSLUCENCE = 155

    // 需要在初始化前访问，不能用lateinit
    private var mStarMenuItem: MenuItem? = null
    private var mCreateMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlanCreationPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerPlanCreationComponent.builder()
                .planCreationPresenterModule(PlanCreationPresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlanCreationPresenter.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_plan_creation, menu)
        mStarMenuItem = menu.findItem(R.id.action_star)
        mCreateMenuItem = menu.findItem(R.id.action_create)
        //在这里改变图标的tint和alpha，因为没法在xml文件中改
        mStarMenuItem?.icon?.setTint(Color.WHITE)
        mCreateMenuItem?.icon?.setTint(Color.WHITE)
        mCreateMenuItem?.icon?.alpha = ALPHA_TRANSLUCENCE
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mPlanCreationPresenter.notifyPlanCreationCanceled()
            R.id.action_star -> mPlanCreationPresenter.notifyStarStatusChanged()
            R.id.action_create -> mPlanCreationPresenter.notifyCreatingPlan()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        //super.onBackPressed();
        mPlanCreationPresenter.notifyPlanCreationCanceled()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        when (fragment.tag) {
            TAG_DEADLINE_PICKER -> (fragment as DateTimePickerDialogFragment).dateTimePickedListener = {
                mPlanCreationPresenter.notifyDeadlineChanged(it)
                // TODO 该返回值是默认的，分情况处理返回值，下同
                true
            }
            TAG_REMINDER_TIME_PICKER -> (fragment as DateTimePickerDialogFragment).dateTimePickedListener = {
                mPlanCreationPresenter.notifyReminderTimeChanged(it)
                true
            }
        }
    }

    override fun showInitialView(typeGalleryAdapter: TypeGalleryAdapter, formattedType: FormattedType) {
        //overridePendingTransition(0, 0);
        setContentView(R.layout.activity_plan_creation)
        ButterKnife.bind(this)

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

        setSupportActionBar(toolbar)
        setupActionBar()

        editor_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mPlanCreationPresenter.notifyContentChanged(s.toString())
            }
        })

        (gallery_type.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
        gallery_type.adapter = typeGalleryAdapter
        gallery_type.setHasFixedSize(true)

        item_deadline.setOnClickListener { mPlanCreationPresenter.notifySettingDeadline() }
        item_reminder.setOnClickListener { mPlanCreationPresenter.notifySettingReminder() }

        onContentChanged(false)
        onStarStatusChanged(false)
        onTypeOfPlanChanged(formattedType)
    }

    override fun onContentChanged(isValid: Boolean) {
        mCreateMenuItem?.icon?.alpha = if (isValid) ALPHA_OPACITY else ALPHA_TRANSLUCENCE
    }

    override fun onStarStatusChanged(isStarred: Boolean) {
        mStarMenuItem?.setIcon(if (isStarred) R.drawable.ic_star_black_24dp else R.drawable.ic_star_border_black_24dp)
        mStarMenuItem?.icon?.setTint(Color.WHITE)
    }

    override fun onTypeOfPlanChanged(formattedType: FormattedType) {
        val typeMarkColorInt = formattedType.typeMarkColorInt
        window.navigationBarColor = typeMarkColorInt
        window.statusBarColor = typeMarkColorInt
        toolbar.setBackgroundColor(ColorUtil.reduceSaturation(typeMarkColorInt, 0.85f))
        ic_plan.imageTintList = ColorStateList.valueOf(typeMarkColorInt)
        ic_type_mark.setFillColor(typeMarkColorInt)
        ic_type_mark.setInnerIcon(if (formattedType.hasTypeMarkPattern) getDrawable(formattedType.typeMarkPatternResId) else null)
        ic_type_mark.setInnerText(formattedType.firstChar)
        text_type_name.text = formattedType.typeName
        item_deadline.setThemeColor(typeMarkColorInt)
        item_reminder.setThemeColor(typeMarkColorInt)
    }

    override fun onTypeCreationItemClicked() {
        TypeCreationActivity.start(this)
    }

    override fun showDeadlinePickerDialog(defaultDeadline: Long) {
        DateTimePickerDialogFragment.newInstance(defaultDeadline).show(supportFragmentManager, TAG_DEADLINE_PICKER)
    }

    override fun onDeadlineChanged(deadline: CharSequence) {
        item_deadline.setDescriptionText(deadline)
    }

    override fun showReminderTimePickerDialog(defaultReminderTime: Long) {
        DateTimePickerDialogFragment.newInstance(defaultReminderTime).show(supportFragmentManager, TAG_REMINDER_TIME_PICKER)
    }

    override fun onReminderTimeChanged(reminderTime: CharSequence) {
        item_reminder.setDescriptionText(reminderTime)
    }

    @OnClick(R.id.item_deadline, R.id.item_reminder)
    fun onClick(view: View) {
        when (view.id) {
            R.id.item_deadline -> mPlanCreationPresenter.notifySettingDeadline()
            R.id.item_reminder -> mPlanCreationPresenter.notifySettingReminder()
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
    //                CommonUtil.showSoftInput(editor_content);
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
