package me.imzack.app.end.presenter

import android.text.TextUtils

import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.Type
import me.imzack.app.end.view.adapter.TypeSearchListAdapter
import me.imzack.app.end.view.contract.TypeSearchViewContract

import javax.inject.Inject

class TypeSearchPresenter @Inject constructor(
        private var mTypeSearchViewContract: TypeSearchViewContract?
) : BasePresenter() {

    private val mTypeSearchList = mutableListOf<Type>()
    private val mTypeSearchListAdapter = TypeSearchListAdapter(mTypeSearchList)

    override fun attach() {
        mTypeSearchListAdapter.mOnTypeItemClickListener = { mTypeSearchViewContract!!.onTypeItemClicked(it) }

        mTypeSearchViewContract!!.showInitialView(DataManager.typeCount, mTypeSearchListAdapter)
    }

    override fun detach() {
        mTypeSearchViewContract = null
    }

    fun notifySearchTextChanged(searchText: String) {
        DataManager.searchType(mTypeSearchList, searchText)
        mTypeSearchListAdapter.notifyDataSetChanged()
        mTypeSearchViewContract!!.onSearchChanged(TextUtils.isEmpty(searchText), mTypeSearchList.size == 0)
    }
}
