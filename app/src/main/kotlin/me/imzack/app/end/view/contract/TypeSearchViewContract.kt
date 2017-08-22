package me.imzack.app.end.view.contract

import me.imzack.app.end.view.adapter.TypeSearchListAdapter

interface TypeSearchViewContract : BaseViewContract {

    fun showInitialView(typeCount: Int, typeSearchListAdapter: TypeSearchListAdapter)

    fun onSearchChanged(isNoSearchInput: Boolean, isTypeSearchEmpty: Boolean)

    fun onTypeItemClicked(typeListPos: Int)
}
