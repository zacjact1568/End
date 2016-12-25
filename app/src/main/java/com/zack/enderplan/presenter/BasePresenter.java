package com.zack.enderplan.presenter;

public abstract class BasePresenter {

    public abstract void attach();

    public abstract void detach();

    protected String getPresenterName() {
        return getClass().getSimpleName();
    }
}
