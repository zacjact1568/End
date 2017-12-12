package me.imzack.app.end.view.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_all_types.*
import me.imzack.app.end.App
import me.imzack.app.end.R
import me.imzack.app.end.injector.component.DaggerAllTypesComponent
import me.imzack.app.end.injector.module.AllTypesPresenterModule
import me.imzack.app.end.presenter.AllTypesPresenter
import me.imzack.app.end.view.activity.TypeDetailActivity
import me.imzack.app.end.view.adapter.TypeListAdapter
import me.imzack.app.end.view.contract.AllTypesViewContract
import me.imzack.app.end.view.widget.CircleColorView
import javax.inject.Inject

class AllTypesFragment : BaseListFragment(), AllTypesViewContract {

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
        mAllTypesPresenter.attach()
    }

    override fun onDetach() {
        super.onDetach()
        mAllTypesPresenter.detach()
    }

    override fun showInitialView(typeListAdapter: TypeListAdapter, itemTouchHelper: ItemTouchHelper) {
        list_type.setHasFixedSize(true)
        list_type.adapter = typeListAdapter
        list_type.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                onListScrolled(dy)
                mAllTypesPresenter.notifyPlanListScrolled(
                        !list_type.canScrollVertically(-1),
                        !list_type.canScrollVertically(1)
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(list_type)
    }

    override fun onTypeItemClicked(position: Int, typeItem: View) {
        val typeMarkIcon = typeItem.findViewById<View>(R.id.ic_type_mark)
        TypeDetailActivity.start(
                activity,
                position,
                true,
                typeMarkIcon,
                typeMarkIcon.transitionName
        )
    }

    override fun onTypeCreated(scrollTo: Int) {
        list_type.scrollToPosition(scrollTo)
    }

    override fun exit() {
        remove()
    }
}
