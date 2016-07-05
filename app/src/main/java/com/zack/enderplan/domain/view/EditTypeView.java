package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.TypeMarkAdapter;

public interface EditTypeView {

    void showInitialView(String typeName, TypeMarkAdapter typeMarkAdapter, int position);

    //void onTypeMarkClicked(boolean isSelected, int resId);

    void onUpdateSaveButton(boolean isEnable);
}
