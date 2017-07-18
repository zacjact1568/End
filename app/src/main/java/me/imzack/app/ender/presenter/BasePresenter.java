package me.imzack.app.ender.presenter;

public abstract class BasePresenter {

    public abstract void attach();

    public abstract void detach();

    protected String getPresenterName() {
        return getClass().getSimpleName();
    }
}
