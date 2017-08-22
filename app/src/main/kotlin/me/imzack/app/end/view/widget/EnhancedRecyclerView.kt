package me.imzack.app.end.view.widget

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class EnhancedRecyclerView : RecyclerView {

    private var mEmptyView: View? = null

    private val mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private fun checkIfEmpty() {
        if (mEmptyView == null || adapter == null) return
        val isAdapterEmpty = adapter.itemCount == 0
        mEmptyView!!.visibility = if (isAdapterEmpty) View.VISIBLE else View.GONE
        visibility = if (isAdapterEmpty) View.GONE else View.VISIBLE
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(mAdapterDataObserver)

        super.setAdapter(adapter)

        adapter?.registerAdapterDataObserver(mAdapterDataObserver)

        checkIfEmpty()
    }

    fun setEmptyView(emptyView: View) {
        mEmptyView = emptyView
        checkIfEmpty()
    }
}
