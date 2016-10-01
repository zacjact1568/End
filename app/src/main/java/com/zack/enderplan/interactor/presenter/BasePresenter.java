package com.zack.enderplan.interactor.presenter;

public abstract class BasePresenter {

    protected String getPresenterName() {
        return getClass().getSimpleName();
    }
}
