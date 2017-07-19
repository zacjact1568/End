package me.imzack.app.end.view.callback;

import android.support.v7.widget.helper.ItemTouchHelper;

import me.imzack.app.end.model.DataManager;

public class TypeListItemTouchCallback extends BaseItemTouchCallback {

    private DataManager mDataManager;

    public TypeListItemTouchCallback(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public int getMoveDirs(int position) {
        if (position == 0) return ItemTouchHelper.DOWN;
        if (position == mDataManager.getTypeCount() - 1) return ItemTouchHelper.UP;
        return ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    }

    @Override
    public int getSwipeDirs(int position) {
        return 0;
    }
}
