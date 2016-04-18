package com.zack.enderplan.presenter;

public interface Presenter<V> {

    void attachView(V view);

    void detachView();

}
