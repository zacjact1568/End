package me.imzack.app.end.view.activity

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInjectPresenter()
    }

    protected open fun onInjectPresenter() {

    }

    protected val activityName
        get() = javaClass.simpleName!!

    /** Set up the [android.app.ActionBar], if the API is available. */
    protected fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun showToast(@StringRes msgResId: Int) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show()
    }

    open fun exit() {
        finish()
    }
}
