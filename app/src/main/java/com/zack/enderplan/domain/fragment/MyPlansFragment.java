package com.zack.enderplan.domain.fragment;

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
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.activity.HomeActivity;
import com.zack.enderplan.domain.activity.PlanDetailActivity;
import com.zack.enderplan.domain.view.MyPlansView;
import com.zack.enderplan.interactor.adapter.PlanAdapter;
import com.zack.enderplan.interactor.presenter.MyPlansPresenter;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.utility.Util;
import com.zack.enderplan.widget.EnhancedRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlansFragment extends Fragment implements MyPlansView {

    private static final String LOG_TAG = "MyPlansFragment";

    @BindView(R.id.list_my_plans)
    EnhancedRecyclerView mMyPlansList;
    @BindView(R.id.text_empty_view)
    TextView mEmptyViewText;

    private MyPlansPresenter mMyPlansPresenter;

    public MyPlansFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyPlansPresenter = new MyPlansPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_plans, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mMyPlansPresenter.setInitialView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMyPlansPresenter.detachView();
    }

    @Override
    public void showInitialView(PlanAdapter planAdapter) {
        //初始化RecyclerView
        mMyPlansList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMyPlansList.setHasFixedSize(true);
        mMyPlansList.setEmptyView(mEmptyViewText);
        mMyPlansList.setAdapter(planAdapter);
        new ItemTouchHelper(new PlanListItemTouchCallback()).attachToRecyclerView(mMyPlansList);
    }

    @Override
    public void onPlanItemClicked(int position) {
        Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
        intent.putExtra("position", position);
        getActivity().startActivityForResult(intent, HomeActivity.REQ_CODE_PLAN_DETAIL);
    }

    @Override
    public void onPlanDeleted(String content, final int position, final Plan planUseForTakingBack) {
        Util.makeShortVibrate();
        String text = content + " " + getResources().getString(R.string.deleted_prompt);
        Snackbar snackbar = Snackbar.make(mMyPlansList, text, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyPlansPresenter.notifyPlanRecreated(position, planUseForTakingBack);
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
                    mMyPlansPresenter.notifyPlanDeleted(position);
                    break;
                case ItemTouchHelper.END:
                    mMyPlansPresenter.notifyPlanStatusChanged(position);
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
}
