package net.zackzhang.app.end.view.callback

import android.support.v7.widget.helper.ItemTouchHelper

import net.zackzhang.app.end.model.bean.Plan

class SingleTypePlanListItemTouchCallback(private val mSingleTypePlanList: List<Plan>) : BaseItemTouchCallback() {

    override fun getMoveDirs(position: Int) = when (position) {
        0 -> ItemTouchHelper.DOWN
        mSingleTypePlanList.size - 1 -> ItemTouchHelper.UP
        else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    override fun getSwipeDirs(position: Int) = ItemTouchHelper.START or ItemTouchHelper.END
}
