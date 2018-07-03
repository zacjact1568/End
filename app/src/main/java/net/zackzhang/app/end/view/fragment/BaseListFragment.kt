package net.zackzhang.app.end.view.fragment

abstract class BaseListFragment : BaseFragment() {

    var mOnListScrolledListener: ((variation: Int) -> Unit)? = null

    fun onListScrolled(variation: Int) {
        mOnListScrolledListener?.invoke(variation)
    }
}
