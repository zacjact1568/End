package me.imzack.app.end.view.contract

interface HomeViewContract : BaseViewContract {

    fun showInitialView(planCount: String, textSize: Int, planCountDscpt: String)

    fun showInitialFragment(restored: Boolean, shownTag: String)

    fun switchFragment(fromTag: String, toTag: String)

    fun changePlanCount(planCount: String, textSize: Int)

    fun changeDrawerHeaderDisplay(planCount: String, textSize: Int, planCountDscpt: String)

    fun closeDrawer()

    fun onPressBackKey()

    fun startActivity(tag: String)

    fun showToast(msg: String)
}
