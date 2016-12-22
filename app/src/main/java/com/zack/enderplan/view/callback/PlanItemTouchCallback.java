package com.zack.enderplan.view.callback;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class PlanItemTouchCallback extends ItemTouchHelper.Callback {

    public static final int DIR_START = 0;
    public static final int DIR_END = 1;

    private OnItemSwipedListener mOnItemSwipedListener;
    private OnItemMovedListener mOnItemMovedListener;

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (mOnItemMovedListener != null) {
            mOnItemMovedListener.onItemMoved(viewHolder.getLayoutPosition(), target.getLayoutPosition());
        }
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getLayoutPosition();
        switch (direction) {
            case ItemTouchHelper.START:
                if (mOnItemSwipedListener != null) {
                    mOnItemSwipedListener.onItemSwiped(position, DIR_START);
                }
                break;
            case ItemTouchHelper.END:
                if (mOnItemSwipedListener != null) {
                    mOnItemSwipedListener.onItemSwiped(position, DIR_END);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return .7f;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
    }

    public interface OnItemSwipedListener {
        void onItemSwiped(int position, int direction);
    }

    public void setOnItemSwipedListener(OnItemSwipedListener listener) {
        mOnItemSwipedListener = listener;
    }

    public interface OnItemMovedListener {
        void onItemMoved(int fromPosition, int toPosition);
    }

    public void setOnItemMovedListener(OnItemMovedListener listener) {
        mOnItemMovedListener = listener;
    }
}
