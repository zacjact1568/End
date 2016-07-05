package com.zack.enderplan.interactor.presenter;

public interface Presenter<V> {

    void attachView(V view);

    void detachView();

}
