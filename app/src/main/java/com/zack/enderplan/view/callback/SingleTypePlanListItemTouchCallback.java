package com.zack.enderplan.view.callback;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;

import java.util.List;

public class SingleTypePlanListItemTouchCallback extends BaseItemTouchCallback {

    private List<Plan> mSingleTypePlanList;

    public SingleTypePlanListItemTouchCallback(List<Plan> singleTypePlanList) {
        mSingleTypePlanList = singleTypePlanList;
    }

    @Override
    public int getMoveDirs(int position) {
        if (position == 0) return ItemTouchHelper.DOWN;
        if (position == mSingleTypePlanList.size() - 1) return ItemTouchHelper.UP;
        return ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    }

    @Override
    public int getSwipeDirs(int position) {
        return ItemTouchHelper.START | ItemTouchHelper.END;
    }
}
