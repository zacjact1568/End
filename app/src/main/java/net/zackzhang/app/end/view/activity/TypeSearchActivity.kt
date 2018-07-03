package net.zackzhang.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_type_search.*
import kotlinx.android.synthetic.main.content_type_search.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.injector.component.DaggerTypeSearchComponent
import net.zackzhang.app.end.injector.module.TypeSearchPresenterModule
import net.zackzhang.app.end.presenter.TypeSearchPresenter
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.view.adapter.TypeSearchListAdapter
import net.zackzhang.app.end.view.contract.TypeSearchViewContract
import javax.inject.Inject

class TypeSearchActivity : BaseActivity(), TypeSearchViewContract {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, TypeSearchActivity::class.java))
        }
    }

    @Inject
    lateinit var mTypeSearchPresenter: TypeSearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTypeSearchPresenter.attach()
    }

    override fun onInjectPresenter() {
        DaggerTypeSearchComponent.builder()
                .typeSearchPresenterModule(TypeSearchPresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTypeSearchPresenter.detach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> exit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showInitialView(typeCount: Int, typeSearchListAdapter: TypeSearchListAdapter) {
        setContentView(R.layout.activity_type_search)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        setupActionBar()

        editor_type_search.hint = ResourceUtil.getQuantityString(R.string.hint_editor_type_search, R.plurals.text_type_count, typeCount)
        editor_type_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mTypeSearchPresenter.notifySearchTextChanged(s.toString())
            }
        })

        list_type_search.adapter = typeSearchListAdapter
        list_type_search.setHasFixedSize(true)
    }

    override fun onSearchChanged(isNoSearchInput: Boolean, isTypeSearchEmpty: Boolean) {
        ic_clear_text.visibility = if (isNoSearchInput) View.GONE else View.VISIBLE
        list_type_search.visibility = if (isNoSearchInput || isTypeSearchEmpty) View.GONE else View.VISIBLE
        img_no_input.visibility = if (isNoSearchInput) View.VISIBLE else View.GONE
        text_empty.visibility = if (!isNoSearchInput && isTypeSearchEmpty) View.VISIBLE else View.GONE
    }

    override fun onTypeItemClicked(typeListPos: Int) {
        TypeDetailActivity.start(this, typeListPos, false, null, null)
        exit()
    }

    @OnClick(R.id.ic_clear_text)
    fun onClick(view: View) {
        when (view.id) {
            R.id.ic_clear_text -> editor_type_search.text = null
        }
    }
}
