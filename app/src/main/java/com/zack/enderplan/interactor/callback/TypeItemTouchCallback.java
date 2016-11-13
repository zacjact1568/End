package com.zack.enderplan.interactor.callback;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class TypeItemTouchCallback extends ItemTouchHelper.Callback {

    private OnItemMovedListener mOnItemMovedListener;

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
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

    }

    public interface OnItemMovedListener {
        void onItemMoved(int fromPosition, int toPosition);
    }

    public void setOnItemMovedListener(OnItemMovedListener listener) {
        mOnItemMovedListener = listener;
    }
}
