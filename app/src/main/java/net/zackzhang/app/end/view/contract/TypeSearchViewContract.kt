package net.zackzhang.app.end.view.contract

import net.zackzhang.app.end.view.adapter.TypeSearchListAdapter

interface TypeSearchViewContract : BaseViewContract {

    fun showInitialView(typeCount: Int, typeSearchListAdapter: TypeSearchListAdapter)

    fun onSearchChanged(isNoSearchInput: Boolean, isTypeSearchEmpty: Boolean)

    fun onTypeItemClicked(typeListPos: Int)
}
