package net.zackzhang.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.common.Constant
import net.zackzhang.app.end.injector.component.DaggerHomeComponent
import net.zackzhang.app.end.injector.module.HomePresenterModule
import net.zackzhang.app.end.presenter.HomePresenter
import net.zackzhang.app.end.view.contract.HomeViewContract
import net.zackzhang.app.end.view.fragment.AllTypesFragment
import net.zackzhang.app.end.view.fragment.MyPlansFragment
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeViewContract {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    @Inject
    lateinit var homePresenter: HomePresenter

    // 不使用synthetic，因为不会从cache中获取子view
    private val planCountText by lazy { navigator.getHeaderView(0).findViewById<TextView>(R.id.text_plan_count) }
    private val planCountDscptText by lazy { navigator.getHeaderView(0).findViewById<TextView>(R.id.text_plan_count_dscpt) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homePresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerHomeComponent.builder()
                .homePresenterModule(HomePresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        homePresenter.notifyInstanceStateRestored(savedInstanceState.getString(Constant.CURRENT_FRAGMENT, Constant.MY_PLANS))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        homePresenter.notifyStartingUpCompleted()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        homePresenter.notifySavingInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        homePresenter.detach()
    }

    override fun onBackPressed() {
        homePresenter.notifyBackPressed(
                layout_drawer.isDrawerOpen(GravityCompat.START),
                isFragmentShowing(Constant.MY_PLANS)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menu.findItem(R.id.action_search).icon.setTint(Color.WHITE)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> homePresenter.notifySearchButtonTouched()
        }
        return super.onOptionsItemSelected(item)
    }

    // 参数data必须声明成可选类型，否则崩溃，因为若没有数据返回，data为null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            //正常结束引导
            RESULT_OK -> { }
            //中途退出引导
            RESULT_CANCELED -> exit()
        }
    }

    override fun showInitialView(planCount: String, textSize: Int, planCountDscpt: String) {
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, layout_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        layout_drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigator.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_my_plans -> homePresenter.notifySwitchingFragment(Constant.MY_PLANS)
                R.id.nav_all_types -> homePresenter.notifySwitchingFragment(Constant.ALL_TYPES)
                R.id.nav_settings -> startActivity(Constant.SETTINGS)
                R.id.nav_about -> startActivity(Constant.ABOUT)
            }
            layout_drawer.closeDrawer(GravityCompat.START)
            true
        }

        fab_create.setOnClickListener { homePresenter.notifyCreationButtonTouched() }

        changeDrawerHeaderDisplay(planCount, textSize, planCountDscpt)
    }

    override fun showInitialFragment(restored: Boolean, shownTag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        if (restored) {
            supportFragmentManager.fragments
                    .map { it.tag }
                    // 不隐藏 tag 为 null 的 fragment，因为这是 WeatherFragment 的子页（每个城市）
                    .filter { it != null && it != shownTag }
                    .forEach { it?.let { transaction.hide(it) } }
        } else {
            // 如果 activity 正常启动，仅添加 shownTag 对应的 fragment
            transaction.add(shownTag)
        }
        transaction.commit()

        updateAfterUpdatingFragment(shownTag)
    }

    override fun switchFragment(fromTag: String, toTag: String) {
        if (!isFragmentExist(toTag)) {
            // 如果要显示的 fragment 还未创建，创建
            supportFragmentManager.beginTransaction().hide(fromTag).add(toTag).commit()
        } else if (!isFragmentShowing(toTag)) {
            // 如果要显示的 fragment 已创建但未显示，显示
            supportFragmentManager.beginTransaction().hide(fromTag).show(toTag).commit()
        }
        // 如果要显示的 fragment 已创建且已显示，不做任何操作

        updateAfterUpdatingFragment(toTag)
    }

    override fun changePlanCount(planCount: String, textSize: Int) {
        planCountText.text = planCount
        planCountText.textSize = textSize.toFloat()
    }

    override fun changeDrawerHeaderDisplay(planCount: String, textSize: Int, planCountDscpt: String) {
        changePlanCount(planCount, textSize)
        planCountDscptText.text = planCountDscpt
    }

    override fun closeDrawer() {
        layout_drawer.closeDrawer(GravityCompat.START)
    }

    override fun onPressBackKey() {
        super.onBackPressed()
    }

    override fun startActivity(tag: String) {
        when (tag) {
            Constant.GUIDE -> GuideActivity.start(this)
            Constant.PLAN_SEARCH -> PlanSearchActivity.start(this)
            Constant.TYPE_SEARCH -> TypeSearchActivity.start(this)
            Constant.SETTINGS -> SettingsActivity.start(this)
            Constant.ABOUT -> AboutActivity.start(this)
            Constant.PLAN_CREATION -> PlanCreationActivity.start(this)
            Constant.TYPE_CREATION -> TypeCreationActivity.start(this)
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun updateAfterUpdatingFragment(tag: String) {
        val titleResId: Int
        val checkedItemId: Int
        when (tag) {
            Constant.MY_PLANS -> {
                titleResId = R.string.title_fragment_my_plans
                checkedItemId = R.id.nav_my_plans
            }
            Constant.ALL_TYPES -> {
                titleResId = R.string.title_fragment_all_types
                checkedItemId = R.id.nav_all_types
            }
            else -> throw IllegalArgumentException("The argument tag cannot be \"$tag\"")
        }
        toolbar.setTitle(titleResId)
        fab_create.translationY = 0f
        navigator.setCheckedItem(checkedItemId)
    }

    private fun findFragment(tag: String) = supportFragmentManager.findFragmentByTag(tag)

    private fun isFragmentExist(tag: String) = findFragment(tag) != null

    private fun isFragmentShowing(tag: String): Boolean {
        val fragment = findFragment(tag)
        return fragment != null && !fragment.isHidden
    }

    private fun FragmentTransaction.add(tag: String): FragmentTransaction {
        add(
                R.id.layout_fragment,
                when (tag) {
                    Constant.MY_PLANS -> MyPlansFragment()
                    Constant.ALL_TYPES -> AllTypesFragment()
                    // 在这里添加新 fragment
                    else -> throw IllegalArgumentException("The argument tag cannot be \"$tag\"")
                },
                tag
        )
        return this
    }

    private fun FragmentTransaction.hide(tag: String): FragmentTransaction {
        hide(findFragment(tag) ?: throw IllegalArgumentException("No Fragment with tag \"$tag\" found"))
        return this
    }

    private fun FragmentTransaction.show(tag: String): FragmentTransaction {
        show(findFragment(tag) ?: throw IllegalArgumentException("No Fragment with tag \"$tag\" found"))
        return this
    }
}
