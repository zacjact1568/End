package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerPlanCreationComponent
import me.imzack.app.end.injector.module.PlanCreationPresenterModule
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.presenter.PlanCreationPresenter
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.view.adapter.TypeGalleryAdapter
import me.imzack.app.end.view.contract.PlanCreationViewContract
import me.imzack.app.end.view.dialog.DateTimePickerDialogFragment
import me.imzack.app.end.view.widget.CircleColorView
import me.imzack.app.end.view.widget.ItemView
import javax.inject.Inject

class PlanCreationActivity : BaseActivity(), PlanCreationViewContract {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, PlanCreationActivity::class.java))
        }
    }

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.ic_plan)
    lateinit var mPlanIcon: ImageView
    @BindView(R.id.editor_content)
    lateinit var mContentEditor: EditText
    @BindView(R.id.ic_type_mark)
    lateinit var mTypeMarkIcon: CircleColorView
    @BindView(R.id.text_type_name)
    lateinit var mTypeNameText: TextView
    @BindView(R.id.gallery_type)
    lateinit var mTypeGallery: RecyclerView
    @BindView(R.id.item_deadline)
    lateinit var mDeadlineItem: ItemView
    @BindView(R.id.item_reminder)
    lateinit var mReminderItem: ItemView

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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
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

        setSupportActionBar(mToolbar)
        setupActionBar()

        mContentEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mPlanCreationPresenter.notifyContentChanged(s.toString())
            }
        })

        (mTypeGallery.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
        mTypeGallery.adapter = typeGalleryAdapter
        mTypeGallery.setHasFixedSize(true)

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
        mToolbar.setBackgroundColor(ColorUtil.reduceSaturation(typeMarkColorInt, 0.85f))
        mPlanIcon.imageTintList = ColorStateList.valueOf(typeMarkColorInt)
        mTypeMarkIcon.setFillColor(typeMarkColorInt)
        mTypeMarkIcon.setInnerIcon(if (formattedType.hasTypeMarkPattern) getDrawable(formattedType.typeMarkPatternResId) else null)
        mTypeMarkIcon.setInnerText(formattedType.firstChar)
        mTypeNameText.text = formattedType.typeName
        mDeadlineItem.setThemeColor(typeMarkColorInt)
        mReminderItem.setThemeColor(typeMarkColorInt)
    }

    override fun onTypeCreationItemClicked() {
        TypeCreationActivity.start(this)
    }

    override fun showDeadlinePickerDialog(defaultDeadline: Long) {
        DateTimePickerDialogFragment.newInstance(
                defaultDeadline,
                object : DateTimePickerDialogFragment.OnDateTimePickedListener {
                    override fun onDateTimePicked(timeInMillis: Long) {
                        mPlanCreationPresenter.notifyDeadlineChanged(timeInMillis)
                    }
                }
        ).show(supportFragmentManager)
    }

    override fun onDeadlineChanged(deadline: CharSequence) {
        mDeadlineItem.setDescriptionText(deadline)
    }

    override fun showReminderTimePickerDialog(defaultReminderTime: Long) {
        DateTimePickerDialogFragment.newInstance(
                defaultReminderTime,
                object : DateTimePickerDialogFragment.OnDateTimePickedListener {
                    override fun onDateTimePicked(timeInMillis: Long) {
                        mPlanCreationPresenter.notifyReminderTimeChanged(timeInMillis)
                    }
                }
        ).show(supportFragmentManager)
    }

    override fun onReminderTimeChanged(reminderTime: CharSequence) {
        mReminderItem.setDescriptionText(reminderTime)
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
