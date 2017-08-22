package me.imzack.app.end.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerGuideComponent
import me.imzack.app.end.injector.module.GuidePresenterModule
import me.imzack.app.end.presenter.GuidePresenter
import me.imzack.app.end.util.LogUtil
import me.imzack.app.end.view.adapter.GuidePagerAdapter
import me.imzack.app.end.view.contract.GuideViewContract
import me.imzack.app.end.view.widget.CircleColorView
import me.imzack.app.end.view.widget.EnhancedViewPager
import javax.inject.Inject

class GuideActivity : BaseActivity(), GuideViewContract {

    companion object {

        fun start(activity: Activity) {
            activity.startActivityForResult(Intent(activity, GuideActivity::class.java), 0)
        }
    }

    @BindView(R.id.pager_guide)
    lateinit var mGuidePager: EnhancedViewPager
    @BindView(R.id.btn_start)
    lateinit var mStartButton: CircleColorView
    @BindView(R.id.btn_end)
    lateinit var mEndButton: CircleColorView

    @Inject
    lateinit var mGuidePresenter: GuidePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGuidePresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerGuideComponent.builder()
                .guidePresenterModule(GuidePresenterModule(this, supportFragmentManager))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onBackPressed() {
        mGuidePresenter.notifyBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mGuidePresenter.detach()
    }

    override fun showInitialView(guidePagerAdapter: GuidePagerAdapter) {
        setContentView(R.layout.activity_guide)
        ButterKnife.bind(this)

        mGuidePager.adapter = guidePagerAdapter
        mGuidePager.scrollingEnabled = false
        mGuidePager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mGuidePresenter.notifyPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        onPageSelected(true, guidePagerAdapter.count == 1)
    }

    override fun onPageSelected(isFirstPage: Boolean, isLastPage: Boolean) {
        mStartButton.visibility = if (isFirstPage) View.GONE else View.VISIBLE
        mEndButton.visibility = if (isFirstPage && isLastPage) View.GONE else View.VISIBLE
        mEndButton.setInnerIcon(getDrawable(if (isLastPage) R.drawable.ic_check_black_24dp else R.drawable.ic_arrow_forward_black_24dp))
    }

    override fun navigateToPage(page: Int) {
        mGuidePager.currentItem = page
    }

    override fun exitWithResult(isNormally: Boolean) {
        setResult(if (isNormally) RESULT_OK else RESULT_CANCELED)
        super.exit()
    }

    @OnClick(R.id.btn_start, R.id.btn_end)
    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start -> mGuidePresenter.notifyNavigationButtonClicked(true, mGuidePager.currentItem)
            R.id.btn_end -> mGuidePresenter.notifyNavigationButtonClicked(false, mGuidePager.currentItem)
        }
    }
}
