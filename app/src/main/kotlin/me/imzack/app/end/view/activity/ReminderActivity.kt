package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewAnimator
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.injector.component.DaggerReminderComponent
import me.imzack.app.end.injector.module.ReminderPresenterModule
import me.imzack.app.end.presenter.ReminderPresenter
import me.imzack.app.end.util.TimeUtil
import me.imzack.app.end.util.ViewUtil
import me.imzack.app.end.view.contract.ReminderViewContract
import me.imzack.app.end.view.dialog.DateTimePickerDialogFragment
import me.imzack.app.end.view.widget.ImageTextView
import javax.inject.Inject

class ReminderActivity : BaseActivity(), ReminderViewContract {

    companion object {

        fun start(context: Context, planListPosition: Int) {
            context.startActivity(
                    Intent(context, ReminderActivity::class.java)
                            .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    @BindView(R.id.layout_reminder)
    lateinit var mReminderLayout: LinearLayout
    @BindView(R.id.text_content)
    lateinit var mContentText: TextView
    @BindView(R.id.layout_deadline)
    lateinit var mDeadlineLayout: ImageTextView
    @BindView(R.id.switcher_delay)
    lateinit var mDelaySwitcher: ViewAnimator

    @Inject
    lateinit var mReminderPresenter: ReminderPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mReminderPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerReminderComponent.builder()
                .reminderPresenterModule(ReminderPresenterModule(this, intent.getIntExtra(Constant.PLAN_LIST_POSITION, -1)))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mReminderPresenter.detach()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            mReminderPresenter.notifyTouchFinished(event.rawY)
        }
        return super.onTouchEvent(event)
    }

    override fun showInitialView(content: String, hasDeadline: Boolean, deadline: String?) {
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_reminder)
        ButterKnife.bind(this)

        mReminderLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mReminderLayout.viewTreeObserver.removeOnPreDrawListener(this)
                mReminderPresenter.notifyPreDrawingReminder(ViewUtil.getScreenCoordinateY(mReminderLayout))
                return false
            }
        })

        mContentText.text = content

        mDeadlineLayout.visibility = if (hasDeadline) View.VISIBLE else View.GONE
        mDeadlineLayout.mText = deadline
        mDeadlineLayout.updateText()
    }

    override fun playEnterAnimation() {
        mReminderLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_enter_up))
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun enterPlanDetail(position: Int) {
        //可能会有两个PlanDetailActivity同时存在
        PlanDetailActivity.start(this, position)
    }

    @OnClick(R.id.btn_delay, R.id.btn_detail, R.id.btn_complete, R.id.btn_back, R.id.btn_1_hour, R.id.btn_tomorrow, R.id.btn_more)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_delay -> mDelaySwitcher.showNext()
            R.id.btn_detail -> mReminderPresenter.notifyEnteringPlanDetail()
            R.id.btn_complete -> mReminderPresenter.notifyPlanCompleted()
            R.id.btn_back -> mDelaySwitcher.showPrevious()
            R.id.btn_1_hour -> mReminderPresenter.notifyDelayingReminder(Constant.ONE_HOUR)
            R.id.btn_tomorrow -> mReminderPresenter.notifyDelayingReminder(Constant.TOMORROW)
            R.id.btn_more -> {
                DateTimePickerDialogFragment.newInstance(
                        TimeUtil.getDateTimePickerDefaultTime(0L),
                        object : DateTimePickerDialogFragment.OnDateTimePickedListener {
                            override fun onDateTimePicked(timeInMillis: Long) {
                                mReminderPresenter.notifyUpdatingReminderTime(timeInMillis)
                            }
                        }
                ).show(supportFragmentManager)
            }
        }
    }

    override fun exit() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim_exit_down)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                finish()
                overridePendingTransition(0, 0)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        mReminderLayout.startAnimation(animation)
    }
}
