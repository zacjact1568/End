package net.zackzhang.app.end.view.callback

import android.support.v7.widget.helper.ItemTouchHelper

import net.zackzhang.app.end.model.DataManager

class TypeListItemTouchCallback : BaseItemTouchCallback() {

    override fun getMoveDirs(position: Int) = when (position) {
        0 -> ItemTouchHelper.DOWN
        DataManager.typeCount - 1 -> ItemTouchHelper.UP
        else -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    override fun getSwipeDirs(position: Int) = 0
}
