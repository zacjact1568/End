package me.imzack.app.end.view.contract

import me.imzack.app.end.view.adapter.AboutPagerAdapter

interface AboutViewContract : BaseViewContract {

    fun showInitialView(versionName: String, aboutPagerAdapter: AboutPagerAdapter)

    fun onAppBarScrolled(headerLayoutAlpha: Float)

    fun onAppBarScrolledToCriticalPoint(toolbarTitle: String)

    fun translateViewWhenIncline(shouldTranslateX: Boolean, translationX: Float, shouldTranslateY: Boolean, translationY: Float)

    fun resetViewTranslation()

    fun backToTop()

    fun pressBack()
}
