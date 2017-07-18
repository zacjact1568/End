package me.imzack.app.ender.view.callback;

import android.support.v7.widget.helper.ItemTouchHelper;

import me.imzack.app.ender.model.DataManager;

public class PlanListItemTouchCallback extends BaseItemTouchCallback {

    private DataManager mDataManager;

    public PlanListItemTouchCallback(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public int getMoveDirs(int position) {
        //其实移动已完成计划到未完成计划区域（或相反）的限制也可以在这里完成，但是为了有拖动特效，就不在这里完成了
        //first item
        if (position == 0) return ItemTouchHelper.DOWN;
        //last item
        if (position == mDataManager.getPlanCount() - 1) return ItemTouchHelper.UP;
        //footer item
        if (position == mDataManager.getPlanCount()) return 0;
        //ordinary items
        return ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    }

    @Override
    public int getSwipeDirs(int position) {
        return position == mDataManager.getPlanCount() ? 0 : ItemTouchHelper.START | ItemTouchHelper.END;
    }
}
