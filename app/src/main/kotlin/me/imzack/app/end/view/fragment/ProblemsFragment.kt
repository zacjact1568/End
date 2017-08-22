package me.imzack.app.end.view.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.R
import me.imzack.app.end.view.adapter.ProblemListAdapter

class ProblemsFragment : BaseFragment() {

    @BindView(R.id.list_problem)
    lateinit var mProblemList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_problems, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        mProblemList.adapter = ProblemListAdapter()
    }

    override fun onDetach() {
        super.onDetach()
    }
}
