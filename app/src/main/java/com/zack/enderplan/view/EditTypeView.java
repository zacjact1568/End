package com.zack.enderplan.view;

import com.zack.enderplan.widget.TypeMarkAdapter;

public interface EditTypeView {

    void showInitialView(String typeName, TypeMarkAdapter typeMarkAdapter, int position);

    //void onTypeMarkClicked(boolean isSelected, int resId);

    void onUpdateSaveButton(boolean isEnable);
}
