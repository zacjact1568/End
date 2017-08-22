package me.imzack.app.end.view.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.view.adapter.LibraryListAdapter

class ThanksFragment : BaseFragment() {

    @BindView(R.id.list_library)
    lateinit var mLibraryList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_thanks, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        mLibraryList.adapter = LibraryListAdapter(activity)
        mLibraryList.setHasFixedSize(true)
    }

    override fun onDetach() {
        super.onDetach()
    }
}
