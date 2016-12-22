package com.zack.enderplan.view.contract;

import android.view.View;

import com.zack.enderplan.view.adapter.TypeAdapter;

public interface AllTypesViewContract extends BaseViewContract {

    void showInitialView(TypeAdapter typeAdapter);

    void onTypeItemClicked(int position, View typeItem);
}
