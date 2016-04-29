package com.zack.enderplan.activity;

import android.content.Intent;
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
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.presenter.AllPlansPresenter;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.view.AllPlansView;
import com.zack.enderplan.widget.EnhancedRecyclerView;

public class AllPlansFragment extends Fragment implements AllPlansView {

    private static final String CLASS_NAME = "AllPlansFragment";

    private AllPlansPresenter allPlansPresenter;
    private EnhancedRecyclerView recyclerView;
    //private int planItemClickPosition;

    public AllPlansFragment() {
        // Required empty public constructor
    }

    /*public static AllPlansFragment newInstance(List<Plan> planList) {
        AllPlansFragment fragment = new AllPlansFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PLAN_LIST, (ArrayList<Plan>) planList);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allPlansPresenter = new AllPlansPresenter(this);

        allPlansPresenter.createPlanAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_plans, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (EnhancedRecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(view.findViewById(R.id.text_empty_view));
        recyclerView.setAdapter(allPlansPresenter.getPlanAdapter());
        allPlansPresenter.initDataLists();
        new ItemTouchHelper(new PlanListItemTouchCallback()).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        allPlansPresenter.detachView();
    }

    @Override
    public void onPlanItemClicked(int position) {
        //planItemClickPosition = position;
        Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
        intent.putExtra("position", position);
        getActivity().startActivityForResult(intent, HomeActivity.REQ_CODE_PLAN_DETAIL);
    }

    @Override
    public void onPlanDeleted(String content, final int position, final Plan planUseForTakingBack) {
        Util.makeShortVibrate();
        String text = content + " " + getResources().getString(R.string.deleted_prompt);
        Snackbar snackbar = Snackbar.make(recyclerView, text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allPlansPresenter.notifyPlanRecreated(position, planUseForTakingBack);
            }
        });
        snackbar.show();
    }

    private class PlanListItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition();
            switch (direction) {
                case ItemTouchHelper.START:
                    allPlansPresenter.notifyPlanDeleted(position);
                    break;
                case ItemTouchHelper.END:
                    allPlansPresenter.notifyPlanStatusChanged(position);
                    break;
                default:
                    break;
            }
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return .7f;
        }
    }

    /*public void onUncompletedPlanCountChanged(int newUncompletedPlanCount) {
        if (onUncompletedPlanCountChangedListener != null) {
            onUncompletedPlanCountChangedListener.onUncompletedPlanCountChanged(newUncompletedPlanCount);
        }
    }

    public interface OnUncompletedPlanCountChangedListener {
        void onUncompletedPlanCountChanged(int newUncompletedPlanCount);
    }

    public void setOnUncompletedPlanCountChangedListener(OnUncompletedPlanCountChangedListener listener) {
        this.onUncompletedPlanCountChangedListener = listener;
    }*/
}
