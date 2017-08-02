package me.imzack.app.end.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.imzack.app.end.App;

import me.imzack.app.end.R;
import me.imzack.app.end.injector.component.DaggerMyPlansComponent;
import me.imzack.app.end.injector.module.MyPlansPresenterModule;
import me.imzack.app.end.view.activity.PlanDetailActivity;
import me.imzack.app.end.view.adapter.PlanListAdapter;
import me.imzack.app.end.view.contract.MyPlansViewContract;
import me.imzack.app.end.presenter.MyPlansPresenter;
import me.imzack.app.end.model.bean.Plan;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPlansFragment extends BaseListFragment implements MyPlansViewContract {

    @BindView(R.id.list_plan)
    RecyclerView mPlanList;
    @BindView(R.id.layout_empty)
    LinearLayout mEmptyLayout;

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
    public void showInitialView(PlanListAdapter planListAdapter, ItemTouchHelper itemTouchHelper, boolean isPlanItemEmpty) {
        mPlanList.setAdapter(planListAdapter);
        mPlanList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onListScrolled(dy);
                mMyPlansPresenter.notifyPlanListScrolled(
                        !mPlanList.canScrollVertically(-1),
                        !mPlanList.canScrollVertically(1)
                );
            }
        });
        itemTouchHelper.attachToRecyclerView(mPlanList);

        onPlanItemEmptyStateChanged(isPlanItemEmpty);
    }

    @Override
    public void onPlanItemClicked(int position) {
        PlanDetailActivity.start(getContext(), position);
    }

    @Override
    public void onPlanCreated() {
        mPlanList.scrollToPosition(0);
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

    @Override
    public void onPlanItemEmptyStateChanged(boolean isEmpty) {
        mPlanList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mEmptyLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void exit() {
        remove();
    }
}