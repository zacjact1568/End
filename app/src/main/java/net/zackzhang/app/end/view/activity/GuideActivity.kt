package net.zackzhang.app.end.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.injector.component.DaggerGuideComponent
import net.zackzhang.app.end.injector.module.GuidePresenterModule
import net.zackzhang.app.end.presenter.GuidePresenter
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import net.zackzhang.app.end.view.contract.GuideViewContract
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
    lateinit var guidePresenter: GuidePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerGuideComponent.builder()
                .guidePresenterModule(GuidePresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)

        guidePresenter.attach()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment.tag == getPageFragmentTag(0)) {
            (fragment as SimpleGuidePageFragment).buttonClickListener = { onLastPageTurned() }
        }

    }

    override fun getPageFragmentList() = listOf(
            //欢迎页
            SimpleGuidePageFragment.Builder()
                    .setImage(R.drawable.img_logo_with_bg)
                    .setTitle(StringUtil.addWhiteColorSpan(getString(R.string.title_guide_page_welcome)))
                    .setDescription(StringUtil.addWhiteColorSpan(getString(R.string.text_slogan)))
                    .setButton(StringUtil.addWhiteColorSpan(getString(R.string.button_start)), ResourceUtil.getColor(R.color.colorAccent))
                    .build()
            //引导结束页
//            SimpleGuidePageFragment.Builder()
//                    .setImage(R.drawable.ic_check_black_24dp)
//                    .setTitle(StringUtil.addWhiteColorSpan(getString(R.string.title_guide_page_ready)))
//                    .setDescription(StringUtil.addWhiteColorSpan(getString(R.string.dscpt_guide_page_ready)))
//                    .build()
    )

    override fun onBackPressedOnce() {
        showToast(R.string.toast_double_press_exit)
    }

    override fun onBackPressedTwice() {
        guidePresenter.notifyEndingGuide(false)
    }

    override fun onLastPageTurned() {
        guidePresenter.notifyEndingGuide(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        guidePresenter.detach()
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
