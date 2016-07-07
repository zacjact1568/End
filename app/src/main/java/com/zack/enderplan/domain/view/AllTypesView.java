package com.zack.enderplan.domain.view;

import com.zack.enderplan.interactor.adapter.TypeAdapter;
import com.zack.enderplan.model.bean.Type;

public interface AllTypesView {

    void showInitialView(TypeAdapter typeAdapter);

    void onShowTypeDetailDialogFragment(int position);

    void onTypeDeleted(String typeName, int position, Type typeUseForTakingBack);

    void onShowPlanCountOfOneTypeExistsDialog();
}
