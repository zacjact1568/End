package me.imzack.app.end.view.callback

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

abstract class BaseItemTouchCallback : ItemTouchHelper.Callback() {

    companion object {
        val DIR_START = 0
        val DIR_END = 1
    }

    var mOnItemMovedListener: ((fromPosition: Int, toPosition: Int) -> Unit)? = null
    var mOnItemSwipedListener: ((position: Int, direction: Int) -> Unit)? = null

    abstract fun getMoveDirs(position: Int): Int

    abstract fun getSwipeDirs(position: Int): Int

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
            ItemTouchHelper.Callback.makeMovementFlags(getMoveDirs(viewHolder.layoutPosition), getSwipeDirs(viewHolder.layoutPosition))

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        mOnItemMovedListener?.invoke(viewHolder.layoutPosition, target.layoutPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.layoutPosition
        when (direction) {
            ItemTouchHelper.START -> mOnItemSwipedListener?.invoke(position, DIR_START)
            ItemTouchHelper.END -> mOnItemSwipedListener?.invoke(position, DIR_END)
            else -> { }
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder?) = .7f

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val alpha = 1 - Math.abs(dX) / viewHolder.itemView.width.toFloat()
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        }
    }
}
