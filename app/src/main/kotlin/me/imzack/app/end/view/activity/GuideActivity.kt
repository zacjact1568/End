package me.imzack.app.end.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerGuideComponent
import me.imzack.app.end.injector.module.GuidePresenterModule
import me.imzack.app.end.presenter.GuidePresenter
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.contract.GuideViewContract
import me.imzack.lib.baseguideactivity.BaseGuideActivity
import me.imzack.lib.baseguideactivity.SimpleGuidePageFragment
import javax.inject.Inject

class GuideActivity : BaseGuideActivity(), GuideViewContract {

    companion object {

        fun start(activity: Activity) {
            activity.startActivityForResult(Intent(activity, GuideActivity::class.java), 0)
        }
    }

    @Inject
    lateinit var mGuidePresenter: GuidePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGuidePresenter.attach()

        DaggerGuideComponent.builder()
                .guidePresenterModule(GuidePresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun provideFragmentList() = listOf(
            //欢迎页
            SimpleGuidePageFragment.newInstance(
                    R.drawable.img_logo_with_bg,
                    getString(R.string.title_guide_page_welcome),
                    getString(R.string.text_slogan),
                    getString(R.string.button_start),
                    ResourceUtil.getColor(R.color.colorAccent),
                    object : SimpleGuidePageFragment.OnButtonClickListener {
                        override fun onClick(v: View) {
                            onLastPageTurned()
                        }
                    }
            ),
            //引导结束页
            SimpleGuidePageFragment.newInstance(
                    R.drawable.ic_check_black_24dp,
                    getString(R.string.title_guide_page_ready),
                    getString(R.string.dscpt_guide_page_ready)
            )
    )

    override fun onBackPressedOnce() {
        showToast(R.string.toast_double_press_exit)
    }

    override fun onBackPressedTwice() {
        mGuidePresenter.notifyEndingGuide(false)
    }

    override fun onLastPageTurned() {
        mGuidePresenter.notifyEndingGuide(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mGuidePresenter.detach()
    }

    override fun showInitialView() {
        setBackgroundColor(ResourceUtil.getColor(R.color.colorPrimary))
        setStartButtonColor(ResourceUtil.getColor(R.color.colorAccent))
        setEndButtonColor(ResourceUtil.getColor(R.color.colorAccent))
    }

    override fun exitWithResult(isNormally: Boolean) {
        setResult(if (isNormally) RESULT_OK else RESULT_CANCELED)
        exit()
    }

    override fun showToast(msgResId: Int) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show()
    }

    override fun exit() {
        finish()
    }
}
