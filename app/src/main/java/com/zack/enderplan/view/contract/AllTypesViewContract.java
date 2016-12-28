package com.zack.enderplan.view.contract;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.zack.enderplan.view.adapter.TypeAdapter;

public interface AllTypesViewContract extends BaseViewContract {

    void showInitialView(TypeAdapter typeAdapter, ItemTouchHelper itemTouchHelper);

    void onTypeItemClicked(int position, View typeItem);
}
