package net.zackzhang.app.end.view.fragment

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_all_types.*
import net.zackzhang.app.end.App
import net.zackzhang.app.end.R
import net.zackzhang.app.end.injector.component.DaggerAllTypesComponent
import net.zackzhang.app.end.injector.module.AllTypesPresenterModule
import net.zackzhang.app.end.presenter.AllTypesPresenter
import net.zackzhang.app.end.view.activity.TypeDetailActivity
import net.zackzhang.app.end.view.adapter.TypeListAdapter
import net.zackzhang.app.end.view.contract.AllTypesViewContract
import javax.inject.Inject

class AllTypesFragment : BaseListFragment(), AllTypesViewContract {

    @Inject
    lateinit var mAllTypesPresenter: AllTypesPresenter

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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
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
                activity!!,
                position,
                true,
                typeMarkIcon,
                typeMarkIcon.transitionName
        )
    }

    override fun onTypeCreated(scrollTo: Int) {
        list_type.scrollToPosition(scrollTo)
    }
}
