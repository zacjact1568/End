package me.imzack.app.end.view.callback

import android.support.v7.widget.helper.ItemTouchHelper

import me.imzack.app.end.model.DataManager

class PlanListItemTouchCallback : BaseItemTouchCallback() {

    //其实移动已完成计划到未完成计划区域（或相反）的限制也可以在这里完成，但是为了有拖动特效，就不在这里完成了
    override fun getMoveDirs(position: Int) = when (position) {
        //first item
        0 -> ItemTouchHelper.DOWN
        //last item
        DataManager.planCount - 1 -> ItemTouchHelper.UP
        //footer item
        DataManager.planCount -> 0
        //ordinary items
        else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    override fun getSwipeDirs(position: Int) =
            if (position == DataManager.planCount) 0 else ItemTouchHelper.START or ItemTouchHelper.END
}
