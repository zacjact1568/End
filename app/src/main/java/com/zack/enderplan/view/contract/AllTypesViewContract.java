package com.zack.enderplan.view.contract;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.zack.enderplan.view.adapter.TypeListAdapter;

public interface AllTypesViewContract extends BaseViewContract {

    void showInitialView(TypeListAdapter typeListAdapter, ItemTouchHelper itemTouchHelper);

    void onTypeItemClicked(int position, View typeItem);

    void onTypeCreated(int scrollTo);
}
