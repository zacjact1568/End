package me.imzack.app.end.view.contract

import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View

import me.imzack.app.end.view.adapter.TypeListAdapter

interface AllTypesViewContract : BaseViewContract {

    fun showInitialView(typeListAdapter: TypeListAdapter, itemTouchHelper: ItemTouchHelper)

    fun onTypeItemClicked(position: Int, typeItem: View)

    fun onTypeCreated(scrollTo: Int)
}
