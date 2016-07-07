package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.TypeMarkAdapter;

public interface CreateTypeView {

    void showInitialView(TypeMarkAdapter typeMarkAdapter);

    void updateSaveButton(boolean isEnabled);

    void closeDialog();
}
