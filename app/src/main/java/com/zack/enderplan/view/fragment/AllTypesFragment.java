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
import com.zack.enderplan.view.adapter.TypeAdapter;
import com.zack.enderplan.view.callback.TypeItemTouchCallback;
import com.zack.enderplan.presenter.AllTypesPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllTypesFragment extends BaseListFragment implements AllTypesViewContract {

    @BindView(R.id.list_all_types)
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
    public void showInitialView(TypeAdapter typeAdapter) {

        typeAdapter.setOnTypeItemClickListener(new TypeAdapter.OnTypeItemClickListener() {
            @Override
            public void onTypeItemClick(int position, View typeItem) {
                mAllTypesPresenter.notifyTypeItemClicked(position, typeItem);
            }
        });

        mTypeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTypeList.setHasFixedSize(true);
        mTypeList.setAdapter(typeAdapter);
        mTypeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onListScrolled(dy);
            }
        });

        TypeItemTouchCallback typeItemTouchCallback = new TypeItemTouchCallback();
        typeItemTouchCallback.setOnItemMovedListener(new TypeItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                mAllTypesPresenter.notifyTypeSequenceChanged(fromPosition, toPosition);
            }
        });
        new ItemTouchHelper(typeItemTouchCallback).attachToRecyclerView(mTypeList);
    }

    @Override
    public void onTypeItemClicked(int position, View typeItem) {
        TypeDetailActivity.start(
                getActivity(),
                position,
                typeItem.findViewById(R.id.ic_type_mark),
                getResources().getString(R.string.name_type_mark_shared_element_transition)
        );
    }

    @Override
    public void exit() {
        remove();
    }
}
