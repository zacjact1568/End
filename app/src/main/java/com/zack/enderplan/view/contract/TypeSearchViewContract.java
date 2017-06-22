package com.zack.enderplan.view.contract;

import com.zack.enderplan.view.adapter.TypeSearchListAdapter;

public interface TypeSearchViewContract extends BaseViewContract {

    void showInitialView(int typeCount, TypeSearchListAdapter typeSearchListAdapter);

    void onSearchChanged(boolean isNoSearchInput, boolean isTypeSearchEmpty);

    void onTypeItemClicked(int typeListPos);
}
