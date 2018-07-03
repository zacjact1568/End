package net.zackzhang.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_reminder.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.injector.component.DaggerReminderComponent
import net.zackzhang.app.end.injector.module.ReminderPresenterModule
import net.zackzhang.app.end.presenter.ReminderPresenter
import net.zackzhang.app.end.util.TimeUtil
import net.zackzhang.app.end.util.ViewUtil
import net.zackzhang.app.end.view.contract.ReminderViewContract
import net.zackzhang.app.end.view.dialog.DateTimePickerDialogFragment
import javax.inject.Inject

class ReminderActivity : BaseActivity(), ReminderViewContract {

    companion object {

        private const val TAG_MORE_PICKER = "more_picker"

        fun start(context: Context, planListPosition: Int) {
            context.startActivity(
                    Intent(context, ReminderActivity::class.java)
                            .putExtra(Constant.PLAN_LIST_POSITION, planListPosition)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

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

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment.tag == TAG_MORE_PICKER) {
            (fragment as DateTimePickerDialogFragment).dateTimePickedListener = {
                mReminderPresenter.notifyUpdatingReminderTime(it)
                // TODO 处理返回值
                true
            }
        }
    }

    override fun showInitialView(content: String, hasDeadline: Boolean, deadline: String?) {
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_reminder)
        ButterKnife.bind(this)

        layout_reminder.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                layout_reminder.viewTreeObserver.removeOnPreDrawListener(this)
                mReminderPresenter.notifyPreDrawingReminder(ViewUtil.getScreenCoordinateY(layout_reminder))
                return false
            }
        })

        text_content.text = content

        layout_deadline.visibility = if (hasDeadline) View.VISIBLE else View.GONE
        layout_deadline.mText = deadline
        layout_deadline.updateText()
    }

    override fun playEnterAnimation() {
        layout_reminder.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_enter_up))
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
            R.id.btn_delay -> switcher_delay.showNext()
            R.id.btn_detail -> mReminderPresenter.notifyEnteringPlanDetail()
            R.id.btn_complete -> mReminderPresenter.notifyPlanCompleted()
            R.id.btn_back -> switcher_delay.showPrevious()
            R.id.btn_1_hour -> mReminderPresenter.notifyDelayingReminder(Constant.ONE_HOUR)
            R.id.btn_tomorrow -> mReminderPresenter.notifyDelayingReminder(Constant.TOMORROW)
            R.id.btn_more -> DateTimePickerDialogFragment.newInstance(TimeUtil.getDateTimePickerDefaultTime(0L)).show(supportFragmentManager, TAG_MORE_PICKER)
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
        layout_reminder.startAnimation(animation)
    }
}
