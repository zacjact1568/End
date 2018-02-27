package me.imzack.app.end.view.fragment

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.widget.Toast

abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInjectPresenter()
    }

    open fun onInjectPresenter() {

    }

    protected val fragmentName
        get() = javaClass.simpleName!!

    fun showToast(@StringRes msgResId: Int) {
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show()
    }

    protected fun remove() {
        fragmentManager!!.beginTransaction().remove(this).commit()
    }

    open fun exit() {
        remove()
    }
}
