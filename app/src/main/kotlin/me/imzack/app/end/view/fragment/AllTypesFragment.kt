package me.imzack.app.end.view.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerAllTypesComponent
import me.imzack.app.end.injector.module.AllTypesPresenterModule
import me.imzack.app.end.presenter.AllTypesPresenter
import me.imzack.app.end.view.activity.TypeDetailActivity
import me.imzack.app.end.view.adapter.TypeListAdapter
import me.imzack.app.end.view.contract.AllTypesViewContract
import javax.inject.Inject

class AllTypesFragment : BaseListFragment(), AllTypesViewContract {

    @BindView(R.id.list_type)
    lateinit var mTypeList: RecyclerView

    @Inject
    lateinit var mAllTypesPresenter: AllTypesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onInjectPresenter() {
        DaggerAllTypesComponent.builder()
                .allTypesPresenterModule(AllTypesPresenterModule(this))
                .appComponent(App.appComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_all_types, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        mAllTypesPresenter.attach()
    }

    override fun onDetach() {
        super.onDetach()
        mAllTypesPresenter.detach()
    }

    override fun showInitialView(typeListAdapter: TypeListAdapter, itemTouchHelper: ItemTouchHelper) {
        mTypeList.setHasFixedSize(true)
        mTypeList.adapter = typeListAdapter
        mTypeList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                onListScrolled(dy)
                mAllTypesPresenter.notifyPlanListScrolled(
                        !mTypeList.canScrollVertically(-1),
                        !mTypeList.canScrollVertically(1)
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(mTypeList)
    }

    override fun onTypeItemClicked(position: Int, typeItem: View) {
        val typeMarkIcon = typeItem.findViewById(R.id.ic_type_mark)
        TypeDetailActivity.start(
                activity,
                position,
                true,
                typeMarkIcon,
                typeMarkIcon.transitionName
        )
    }

    override fun onTypeCreated(scrollTo: Int) {
        mTypeList.scrollToPosition(scrollTo)
    }

    override fun exit() {
        remove()
    }
}
