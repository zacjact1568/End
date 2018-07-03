package net.zackzhang.app.end.presenter

abstract class BasePresenter {

    abstract fun attach()

    abstract fun detach()

    protected val presenterName
        get() = javaClass.simpleName!!
}
