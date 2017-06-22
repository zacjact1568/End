package com.zack.enderplan.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerAllTypesComponent;
import com.zack.enderplan.injector.module.AllTypesPresenterModule;
import com.zack.enderplan.view.activity.TypeDetailActivity;
import com.zack.enderplan.view.contract.AllTypesViewContract;
import com.zack.enderplan.view.adapter.TypeListAdapter;
import com.zack.enderplan.presenter.AllTypesPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllTypesFragment extends BaseListFragment implements AllTypesViewContract {

    @BindView(R.id.list_type)
    RecyclerView mTypeList;

    @Inject
    AllTypesPresenter mAllTypesPresenter;

    public AllTypesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInjectPresenter() {
        DaggerAllTypesComponent.builder()
                .allTypesPresenterModule(new AllTypesPresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_types, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mAllTypesPresenter.attach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAllTypesPresenter.detach();
    }

    @Override
    public void showInitialView(TypeListAdapter typeListAdapter, ItemTouchHelper itemTouchHelper) {
        mTypeList.setHasFixedSize(true);
        mTypeList.setAdapter(typeListAdapter);
        mTypeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onListScrolled(dy);
                mAllTypesPresenter.notifyPlanListScrolled(
                        !mTypeList.canScrollVertically(-1),
                        !mTypeList.canScrollVertically(1)
                );
            }
        });
        itemTouchHelper.attachToRecyclerView(mTypeList);
    }

    @Override
    public void onTypeItemClicked(int position, View typeItem) {
        View typeMarkIcon = typeItem.findViewById(R.id.ic_type_mark);
        TypeDetailActivity.start(
                getActivity(),
                position,
                true,
                typeMarkIcon,
                typeMarkIcon.getTransitionName()
        );
    }

    @Override
    public void onTypeCreated(int scrollTo) {
        mTypeList.scrollToPosition(scrollTo);
    }

    @Override
    public void exit() {
        remove();
    }
}
