package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.TypeMarkAdapter;

public interface EditTypeView {

    void showInitialView(String typeName, TypeMarkAdapter typeMarkAdapter);

    void updateSaveButton(boolean isEnabled);

    void closeDialog(boolean isCanceled);
}
