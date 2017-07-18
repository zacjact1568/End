package me.imzack.app.ender.view.contract;

import me.imzack.app.ender.view.adapter.TypeSearchListAdapter;

public interface TypeSearchViewContract extends BaseViewContract {

    void showInitialView(int typeCount, TypeSearchListAdapter typeSearchListAdapter);

    void onSearchChanged(boolean isNoSearchInput, boolean isTypeSearchEmpty);

    void onTypeItemClicked(int typeListPos);
}
