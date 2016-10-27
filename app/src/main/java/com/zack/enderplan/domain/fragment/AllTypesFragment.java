package com.zack.enderplan.domain.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.activity.TypeDetailActivity;
import com.zack.enderplan.domain.view.AllTypesView;
import com.zack.enderplan.interactor.adapter.TypeAdapter;
import com.zack.enderplan.interactor.presenter.AllTypesPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllTypesFragment extends Fragment implements AllTypesView {

    @BindView(R.id.list_all_types)
    RecyclerView mAllTypesList;

    private AllTypesPresenter mAllTypesPresenter;

    public AllTypesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAllTypesPresenter = new AllTypesPresenter(this);
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

        mAllTypesPresenter.setInitialView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAllTypesPresenter.syncWithDatabase();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAllTypesPresenter.detachView();
    }

    @Override
    public void showInitialView(TypeAdapter typeAdapter) {

        typeAdapter.setOnTypeItemClickListener(new TypeAdapter.OnTypeItemClickListener() {
            @Override
            public void onTypeItemClick(int position, View typeItem) {
                mAllTypesPresenter.notifyTypeItemClicked(position, typeItem);
            }
        });

        mAllTypesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllTypesList.setHasFixedSize(true);
        mAllTypesList.setAdapter(typeAdapter);
        new ItemTouchHelper(new TypeListItemTouchCallback()).attachToRecyclerView(mAllTypesList);
    }

    @Override
    public void onTypeItemClicked(int position, View typeItem) {
        Intent intent = new Intent(getActivity(), TypeDetailActivity.class);
        intent.putExtra("position", position);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                getActivity(),
                typeItem.findViewById(R.id.ic_type_mark),
                getResources().getString(R.string.name_type_mark_shared_element_transition)
        );
        getActivity().startActivity(intent, options.toBundle());
    }

    private class TypeListItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAllTypesPresenter.notifyTypeSequenceChanged(viewHolder.getLayoutPosition(), target.getLayoutPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }
}
