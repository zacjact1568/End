package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
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

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.editor_plan_search)
    lateinit var mPlanSearchEditor: EditText
    @BindView(R.id.ic_clear_text)
    lateinit var mClearTextIcon: ImageView
    @BindView(R.id.list_plan_search)
    lateinit var mPlanSearchList: RecyclerView
    @BindView(R.id.img_no_input)
    lateinit var mNoInputImage: ImageView
    @BindView(R.id.text_empty)
    lateinit var mEmptyText: TextView

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

        setSupportActionBar(mToolbar)
        setupActionBar()

        mPlanSearchEditor.hint = ResourceUtil.getQuantityString(R.string.hint_editor_plan_search, R.plurals.text_plan_count, planCount)
        mPlanSearchEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mPlanSearchPresenter.notifySearchTextChanged(s.toString())
            }
        })

        mPlanSearchList.adapter = planSearchListAdapter
        mPlanSearchList.setHasFixedSize(true)
    }

    override fun onSearchChanged(isNoSearchInput: Boolean, isPlanSearchEmpty: Boolean) {
        mClearTextIcon.visibility = if (isNoSearchInput) View.GONE else View.VISIBLE
        mPlanSearchList.visibility = if (isNoSearchInput || isPlanSearchEmpty) View.GONE else View.VISIBLE
        mNoInputImage.visibility = if (isNoSearchInput) View.VISIBLE else View.GONE
        mEmptyText.visibility = if (!isNoSearchInput && isPlanSearchEmpty) View.VISIBLE else View.GONE
    }

    override fun onPlanItemClicked(planListPos: Int) {
        PlanDetailActivity.start(this, planListPos)
        exit()
    }

    @OnClick(R.id.ic_clear_text)
    fun onClick(view: View) {
        when (view.id) {
            R.id.ic_clear_text -> mPlanSearchEditor.setText(null)
        }
    }
}
