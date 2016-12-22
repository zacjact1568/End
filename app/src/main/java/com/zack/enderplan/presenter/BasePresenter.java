package com.zack.enderplan.presenter;

import com.zack.enderplan.view.contract.BaseViewContract;

public abstract class BasePresenter<VC extends BaseViewContract> {

    public abstract void attachView(VC viewContract);

    public abstract void detachView();

    protected String getPresenterName() {
        return getClass().getSimpleName();
    }
}
