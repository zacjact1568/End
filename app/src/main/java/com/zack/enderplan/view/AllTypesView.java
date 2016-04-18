package com.zack.enderplan.view;

import com.zack.enderplan.bean.Type;

public interface AllTypesView {

    void onShowTypeDetailDialogFragment(int position);

    void onTypeDeleted(String typeName, int position, Type typeUseForTakingBack);

    void onShowPlanCountOfOneTypeExistsDialog();
}
