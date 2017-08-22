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
import me.imzack.app.end.injector.component.DaggerTypeSearchComponent
import me.imzack.app.end.injector.module.TypeSearchPresenterModule
import me.imzack.app.end.presenter.TypeSearchPresenter
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.view.adapter.TypeSearchListAdapter
import me.imzack.app.end.view.contract.TypeSearchViewContract
import javax.inject.Inject

class TypeSearchActivity : BaseActivity(), TypeSearchViewContract {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, TypeSearchActivity::class.java))
        }
    }

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.editor_type_search)
    lateinit var mTypeSearchEditor: EditText
    @BindView(R.id.ic_clear_text)
    lateinit var mClearTextIcon: ImageView
    @BindView(R.id.list_type_search)
    lateinit var mTypeSearchList: RecyclerView
    @BindView(R.id.img_no_input)
    lateinit var mNoInputImage: ImageView
    @BindView(R.id.text_empty)
    lateinit var mEmptyText: TextView

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

        setSupportActionBar(mToolbar)
        setupActionBar()

        mTypeSearchEditor.hint = ResourceUtil.getQuantityString(R.string.hint_editor_type_search, R.plurals.text_type_count, typeCount)
        mTypeSearchEditor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mTypeSearchPresenter.notifySearchTextChanged(s.toString())
            }
        })

        mTypeSearchList.adapter = typeSearchListAdapter
        mTypeSearchList.setHasFixedSize(true)
    }

    override fun onSearchChanged(isNoSearchInput: Boolean, isTypeSearchEmpty: Boolean) {
        mClearTextIcon.visibility = if (isNoSearchInput) View.GONE else View.VISIBLE
        mTypeSearchList.visibility = if (isNoSearchInput || isTypeSearchEmpty) View.GONE else View.VISIBLE
        mNoInputImage.visibility = if (isNoSearchInput) View.VISIBLE else View.GONE
        mEmptyText.visibility = if (!isNoSearchInput && isTypeSearchEmpty) View.VISIBLE else View.GONE
    }

    override fun onTypeItemClicked(typeListPos: Int) {
        TypeDetailActivity.start(this, typeListPos, false, null, null)
        exit()
    }

    @OnClick(R.id.ic_clear_text)
    fun onClick(view: View) {
        when (view.id) {
            R.id.ic_clear_text -> mTypeSearchEditor.setText(null)
        }
    }
}
