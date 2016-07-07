package com.zack.enderplan.domain.fragment;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.view.AllTypesView;
import com.zack.enderplan.interactor.adapter.TypeAdapter;
import com.zack.enderplan.interactor.presenter.AllTypesPresenter;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.util.Util;

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
        mAllTypesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllTypesList.setHasFixedSize(true);
        mAllTypesList.setAdapter(typeAdapter);
        new ItemTouchHelper(new TypeListItemTouchCallback()).attachToRecyclerView(mAllTypesList);
    }

    @Override
    public void onShowTypeDetailDialogFragment(int position) {
        TypeDetailDialogFragment bottomSheet = TypeDetailDialogFragment.newInstance(position);
        bottomSheet.show(getFragmentManager(), "type_detail");
    }

    @Override
    public void onTypeDeleted(String typeName, final int position, final Type typeUseForTakingBack) {
        Util.makeShortVibrate();
        String text = typeName + " " + getResources().getString(R.string.deleted_prompt);
        Snackbar snackbar = Snackbar.make(mAllTypesList, text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllTypesPresenter.notifyTypeRecreated(position, typeUseForTakingBack);
            }
        });
        snackbar.show();
    }

    @Override
    public void onShowPlanCountOfOneTypeExistsDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_type_not_empty)
                .setMessage(R.string.msg_dialog_type_not_empty)
                .setPositiveButton(R.string.dialog_button_ok, null)
                .show();
    }

    private class TypeListItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAllTypesPresenter.notifyTypeSequenceChanged(viewHolder.getLayoutPosition(), target.getLayoutPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAllTypesPresenter.notifyTypeDeleted(viewHolder.getLayoutPosition());
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return .7f;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            }
        }
    }
}
