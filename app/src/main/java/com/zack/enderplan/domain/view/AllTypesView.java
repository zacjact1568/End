package com.zack.enderplan.domain.view;

import android.view.View;

import com.zack.enderplan.interactor.adapter.TypeAdapter;
import com.zack.enderplan.model.bean.Type;

public interface AllTypesView {

    void showInitialView(TypeAdapter typeAdapter);

    void onTypeItemClicked(int position, View typeItem);
}
