package net.zackzhang.app.end.view.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.hardware.SensorManager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.MenuItem
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_about.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.injector.component.DaggerAboutComponent
import net.zackzhang.app.end.injector.module.AboutPresenterModule
import net.zackzhang.app.end.presenter.AboutPresenter
import net.zackzhang.app.end.view.adapter.AboutPagerAdapter
import net.zackzhang.app.end.view.contract.AboutViewContract
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

        setSupportActionBar(toolbar)
        setupActionBar()

        //注释掉这一句使AppBar可折叠
        (layout_collapsing_toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0

        layout_app_bar.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                layout_app_bar.viewTreeObserver.removeOnPreDrawListener(this)
                mAboutPresenter.notifyPreDrawingAppBar(layout_app_bar.totalScrollRange)
                return false
            }
        })

        layout_app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset -> mAboutPresenter.notifyAppBarScrolled(verticalOffset) })

        text_version.text = versionName
        pager_about.adapter = aboutPagerAdapter
        indicator_about.setViewPager(pager_about)
    }

    override fun onAppBarScrolled(headerLayoutAlpha: Float) {
        layout_header.alpha = headerLayoutAlpha
    }

    override fun onAppBarScrolledToCriticalPoint(toolbarTitle: String) {
        toolbar.title = toolbarTitle
    }

    override fun translateViewWhenIncline(shouldTranslateX: Boolean, translationX: Float, shouldTranslateY: Boolean, translationY: Float) {
        if (!shouldTranslateX && !shouldTranslateY) return
        val path = Path()
        //start point
        path.moveTo(layout_header.translationX, layout_header.translationY)
        //end point
        if (shouldTranslateX && shouldTranslateY) {
            //x和y均合适
            path.lineTo(translationX, translationY)
        } else if (shouldTranslateX) {
            //x合适，y不合适
            path.lineTo(translationX, layout_header.translationY)
        } else {
            //x不合适，y合适
            path.lineTo(layout_header.translationX, translationY)
        }
        ObjectAnimator.ofFloat(layout_header, "translationX", "translationY", path).setDuration(80).start()
    }

    override fun resetViewTranslation() {
        layout_header.translationX = 0f
        layout_header.translationY = 0f
    }

    override fun backToTop() {
        layout_app_bar.setExpanded(true)
    }

    override fun pressBack() {
        super.onBackPressed()
    }
}
