package com.zack.enderplan.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerMyPlansComponent;
import com.zack.enderplan.injector.module.MyPlansPresenterModule;
import com.zack.enderplan.view.activity.PlanDetailActivity;
import com.zack.enderplan.view.contract.MyPlansViewContract;
import com.zack.enderplan.view.adapter.PlanAdapter;
import com.zack.enderplan.presenter.MyPlansPresenter;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.view.widget.EnhancedRecyclerView;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlansFragment extends BaseListFragment implements MyPlansViewContract {

    @BindView(R.id.list_my_plans)
    EnhancedRecyclerView mMyPlansList;
    @BindView(R.id.text_empty_view)
    TextView mEmptyViewText;

    @BindString(R.string.snackbar_delete_format)
    String mSnackbarDeleteFormat;

    @Inject
    MyPlansPresenter mMyPlansPresenter;

    public MyPlansFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInjectPresenter() {
        DaggerMyPlansComponent.builder()
                .myPlansPresenterModule(new MyPlansPresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
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
        mMyPlansPresenter.attach();
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
        mMyPlansPresenter.detach();
    }

    @Override
    public void showInitialView(PlanAdapter planAdapter, ItemTouchHelper itemTouchHelper) {
        mMyPlansList.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mMyPlansList.setHasFixedSize(true);
        mMyPlansList.setEmptyView(mEmptyViewText);
        mMyPlansList.setAdapter(planAdapter);
        mMyPlansList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onListScrolled(dy);
            }
        });
        itemTouchHelper.attachToRecyclerView(mMyPlansList);
    }

    @Override
    public void onPlanItemClicked(int position) {
        PlanDetailActivity.start(getActivity(), position, true);
    }

    @Override
    public void onPlanCreated() {
        mMyPlansList.scrollToPosition(0);
    }

    @Override
    public void onPlanDeleted(final Plan deletedPlan, final int position, boolean shouldShowSnackbar) {
        if (shouldShowSnackbar) {
            Snackbar.make(mMyPlansList, String.format(mSnackbarDeleteFormat, deletedPlan.getContent()), Snackbar.LENGTH_LONG)
                    .setAction(R.string.button_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMyPlansPresenter.notifyCreatingPlan(deletedPlan, position);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void exit() {
        remove();
    }
}
