package me.imzack.app.end.view.contract

interface HomeViewContract : BaseViewContract {

    fun showInitialView(planCount: String, textSize: Int, planCountDscpt: String)

    fun changePlanCount(planCount: String, textSize: Int)

    fun changeDrawerHeaderDisplay(planCount: String, textSize: Int, planCountDscpt: String)

    fun closeDrawer()

    fun showFragment(tag: String)

    fun onPressBackKey()

    fun startActivity(tag: String)

    fun showToast(msg: String)
}
