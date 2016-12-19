package com.zack.enderplan.domain.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.activity.PlanDetailActivity;
import com.zack.enderplan.domain.view.MyPlansView;
import com.zack.enderplan.interactor.adapter.PlanAdapter;
import com.zack.enderplan.interactor.callback.PlanItemTouchCallback;
import com.zack.enderplan.interactor.presenter.MyPlansPresenter;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.widget.EnhancedRecyclerView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlansFragment extends BaseListFragment implements MyPlansView {

    @BindView(R.id.list_plan)
    EnhancedRecyclerView mPlanList;
    @BindView(R.id.text_empty_view)
    TextView mEmptyViewText;

    @BindString(R.string.snackbar_delete_format)
    String mSnackbarDeleteFormat;

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
    public void onResume() {
        super.onResume();
        mMyPlansPresenter.notifySwitchingViewVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMyPlansPresenter.notifySwitchingViewVisibility(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMyPlansPresenter.detachView();
    }

    @Override
    public void showInitialView(PlanAdapter planAdapter) {

        mPlanList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlanList.setHasFixedSize(true);
        mPlanList.setEmptyView(mEmptyViewText);
        mPlanList.setAdapter(planAdapter);
        mPlanList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onListScrolled(dy);
            }
        });

        PlanItemTouchCallback planItemTouchCallback = new PlanItemTouchCallback();
        planItemTouchCallback.setOnItemSwipedListener(new PlanItemTouchCallback.OnItemSwipedListener() {
            @Override
            public void onItemSwiped(int position, int direction) {
                switch (direction) {
                    case PlanItemTouchCallback.DIR_START:
                        mMyPlansPresenter.notifyDeletingPlan(position);
                        break;
                    case PlanItemTouchCallback.DIR_END:
                        mMyPlansPresenter.notifyPlanStatusChanged(position);
                        break;
                }
            }
        });
        planItemTouchCallback.setOnItemMovedListener(new PlanItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                mMyPlansPresenter.notifyPlanSequenceChanged(fromPosition, toPosition);
            }
        });
        new ItemTouchHelper(planItemTouchCallback).attachToRecyclerView(mPlanList);
    }

    @Override
    public void onPlanItemClicked(int position) {
        PlanDetailActivity.start(getActivity(), position, true);
    }

    @Override
    public void onPlanDeleted(final Plan deletedPlan, final int position, boolean shouldShowSnackbar) {
        if (shouldShowSnackbar) {
            Snackbar.make(mPlanList, String.format(mSnackbarDeleteFormat, deletedPlan.getContent()), Snackbar.LENGTH_LONG)
                    .setAction(R.string.button_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMyPlansPresenter.notifyCreatingPlan(deletedPlan, position);
                        }
                    })
                    .show();
        }
    }
}
