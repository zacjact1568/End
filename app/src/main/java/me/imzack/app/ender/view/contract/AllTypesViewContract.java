package me.imzack.app.ender.view.contract;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import me.imzack.app.ender.view.adapter.TypeListAdapter;

public interface AllTypesViewContract extends BaseViewContract {

    void showInitialView(TypeListAdapter typeListAdapter, ItemTouchHelper itemTouchHelper);

    void onTypeItemClicked(int position, View typeItem);

    void onTypeCreated(int scrollTo);
}
