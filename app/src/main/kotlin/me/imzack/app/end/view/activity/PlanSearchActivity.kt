package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_plan_search.*
import kotlinx.android.synthetic.main.content_plan_search.*
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerPlanSearchComponent
import me.imzack.app.end.injector.module.PlanSearchPresenterModule
import me.imzack.app.end.presenter.PlanSearchPresenter
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.adapter.PlanSearchListAdapter
import me.imzack.app.end.view.contract.PlanSearchViewContract
import javax.inject.Inject

class PlanSearchActivity : BaseActivity(), PlanSearchViewContract {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, PlanSearchActivity::class.java))
        }
    }

    @Inject
    lateinit var mPlanSearchPresenter: PlanSearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlanSearchPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerPlanSearchComponent.builder()
                .planSearchPresenterModule(PlanSearchPresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlanSearchPresenter.detach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> exit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showInitialView(planCount: Int, planSearchListAdapter: PlanSearchListAdapter) {
        setContentView(R.layout.activity_plan_search)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        setupActionBar()

        editor_plan_search.hint = ResourceUtil.getQuantityString(R.string.hint_editor_plan_search, R.plurals.text_plan_count, planCount)
        editor_plan_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mPlanSearchPresenter.notifySearchTextChanged(s.toString())
            }
        })

        list_plan_search.adapter = planSearchListAdapter
        list_plan_search.setHasFixedSize(true)
    }

    override fun onSearchChanged(isNoSearchInput: Boolean, isPlanSearchEmpty: Boolean) {
        ic_clear_text.visibility = if (isNoSearchInput) View.GONE else View.VISIBLE
        list_plan_search.visibility = if (isNoSearchInput || isPlanSearchEmpty) View.GONE else View.VISIBLE
        img_no_input.visibility = if (isNoSearchInput) View.VISIBLE else View.GONE
        text_empty.visibility = if (!isNoSearchInput && isPlanSearchEmpty) View.VISIBLE else View.GONE
    }

    override fun onPlanItemClicked(planListPos: Int) {
        PlanDetailActivity.start(this, planListPos)
        exit()
    }

    @OnClick(R.id.ic_clear_text)
    fun onClick(view: View) {
        when (view.id) {
            R.id.ic_clear_text -> editor_plan_search.text = null
        }
    }
}
