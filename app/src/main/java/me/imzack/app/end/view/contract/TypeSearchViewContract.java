package me.imzack.app.end.view.contract;

import me.imzack.app.end.view.adapter.TypeSearchListAdapter;

public interface TypeSearchViewContract extends BaseViewContract {

    void showInitialView(int typeCount, TypeSearchListAdapter typeSearchListAdapter);

    void onSearchChanged(boolean isNoSearchInput, boolean isTypeSearchEmpty);

    void onTypeItemClicked(int typeListPos);
}
