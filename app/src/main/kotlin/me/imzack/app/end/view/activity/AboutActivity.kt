package me.imzack.app.end.view.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.hardware.SensorManager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.MenuItem
import android.view.ViewTreeObserver
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_about.*
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerAboutComponent
import me.imzack.app.end.injector.module.AboutPresenterModule
import me.imzack.app.end.presenter.AboutPresenter
import me.imzack.app.end.view.adapter.AboutPagerAdapter
import me.imzack.app.end.view.contract.AboutViewContract
import javax.inject.Inject

class AboutActivity : BaseActivity(), AboutViewContract {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }

    @Inject
    lateinit var mAboutPresenter: AboutPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAboutPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerAboutComponent.builder()
                .aboutPresenterModule(AboutPresenterModule(this, supportFragmentManager, getSystemService(Context.SENSOR_SERVICE) as SensorManager))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onResume() {
        super.onResume()
        mAboutPresenter.notifyRegisteringSensorListener()
    }

    override fun onPause() {
        super.onPause()
        mAboutPresenter.notifyUnregisteringSensorListener()
    }

    override fun onStop() {
        super.onStop()
        //将位置恢复放在这，就看不到移动的过程了
        mAboutPresenter.notifyResetingViewTranslation()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAboutPresenter.detach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mAboutPresenter.notifyBackPressed()
    }

    override fun showInitialView(versionName: String, aboutPagerAdapter: AboutPagerAdapter) {
        setContentView(R.layout.activity_about)
        ButterKnife.bind(this)

        setSupportActionBar(xToolbar)
        setupActionBar()

        //注释掉这一句使AppBar可折叠
        (xCollapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0

        xAppBarLayout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                xAppBarLayout.viewTreeObserver.removeOnPreDrawListener(this)
                mAboutPresenter.notifyPreDrawingAppBar(xAppBarLayout.totalScrollRange)
                return false
            }
        })

        xAppBarLayout.addOnOffsetChangedListener { _, verticalOffset -> mAboutPresenter.notifyAppBarScrolled(verticalOffset) }

        xVersionText.text = versionName
        xAboutPager.adapter = aboutPagerAdapter
        xAboutIndicator.setViewPager(xAboutPager)
    }

    override fun onAppBarScrolled(headerLayoutAlpha: Float) {
        xHeaderLayout.alpha = headerLayoutAlpha
    }

    override fun onAppBarScrolledToCriticalPoint(toolbarTitle: String) {
        xToolbar.title = toolbarTitle
    }

    override fun translateViewWhenIncline(shouldTranslateX: Boolean, translationX: Float, shouldTranslateY: Boolean, translationY: Float) {
        if (!shouldTranslateX && !shouldTranslateY) return
        val path = Path()
        //start point
        path.moveTo(xHeaderLayout.translationX, xHeaderLayout.translationY)
        //end point
        if (shouldTranslateX && shouldTranslateY) {
            //x和y均合适
            path.lineTo(translationX, translationY)
        } else if (shouldTranslateX) {
            //x合适，y不合适
            path.lineTo(translationX, xHeaderLayout.translationY)
        } else {
            //x不合适，y合适
            path.lineTo(xHeaderLayout.translationX, translationY)
        }
        ObjectAnimator.ofFloat(xHeaderLayout, "translationX", "translationY", path).setDuration(80).start()
    }

    override fun resetViewTranslation() {
        xHeaderLayout.translationX = 0f
        xHeaderLayout.translationY = 0f
    }

    override fun backToTop() {
        xAppBarLayout.setExpanded(true)
    }

    override fun pressBack() {
        super.onBackPressed()
    }
}
