package me.imzack.app.end.view.contract

import me.imzack.app.end.view.adapter.GuidePagerAdapter

interface GuideViewContract : BaseViewContract {

    fun showInitialView(guidePagerAdapter: GuidePagerAdapter)

    fun onPageSelected(isFirstPage: Boolean, isLastPage: Boolean)

    fun navigateToPage(page: Int)

    fun exitWithResult(isNormally: Boolean)
}
