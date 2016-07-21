package com.zack.enderplan.interactor.presenter;

import android.view.View;
import android.widget.ImageView;

import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseManager;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.interactor.adapter.PlanAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.domain.view.MyPlansView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MyPlansPresenter implements Presenter<MyPlansView> {

    private static final String LOG_TAG = "MyPlansPresenter";

    private MyPlansView mMyPlansView;
    private DataManager mDataManager;
    private DatabaseManager mDatabaseManager;
    private PlanAdapter mPlanAdapter;

    public MyPlansPresenter(MyPlansView myPlansView) {
        attachView(myPlansView);
        mDataManager = DataManager.getInstance();
        mDatabaseManager = DatabaseManager.getInstance();
    }

    @Override
    public void attachView(MyPlansView view) {
        mMyPlansView = view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        mMyPlansView = null;
        EventBus.getDefault().unregister(this);
    }

    public void setInitialView() {
        //初始化adapter
        mPlanAdapter = new PlanAdapter(mDataManager.getPlanList(), mDataManager.getTypeCodeAndColorResMap());
        mPlanAdapter.setOnPlanItemClickListener(new PlanAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(View itemView, int position) {
                mMyPlansView.onPlanItemClicked(position);
            }
        });
        mPlanAdapter.setOnStarMarkClickListener(new PlanAdapter.OnStarMarkClickListener() {
            @Override
            public void onStarMarkClick(ImageView starMark, int itemPosition) {
                Plan plan = mDataManager.getPlan(itemPosition);
                int newStarStatus = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
                plan.setStarStatus(newStarStatus);
                mPlanAdapter.notifyItemChanged(itemPosition);
                mDatabaseManager.editStarStatus(plan.getPlanCode(), newStarStatus);
            }
        });

        mMyPlansView.showInitialView(mPlanAdapter);

        //开始从数据库加载数据
        mDataManager.loadFromDatabase();
    }

    //滑动删除时（可以撤销）
    public void notifyPlanDeleted(int position) {

        Plan plan = mDataManager.getPlan(position);

        mDataManager.notifyPlanDeleted(position);

        if (plan.getCompletionTime() == 0) {
            //说明该计划还未完成
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }

        mPlanAdapter.notifyItemRemoved(position);
        mMyPlansView.onPlanDeleted(plan.getContent(), position, plan);

        //这里不用postPlanDeletedEvent了，因为当可以滑动删除（即调用这个方法）时，其他需要更新的界面都不存在
    }

    public void notifyPlanRecreated(int position, Plan plan) {

        mDataManager.notifyPlanCreated(position, plan);

        mPlanAdapter.notifyItemInserted(position);

        if (mDataManager.getPlan(position).getCompletionTime() == 0) {
            //说明还未完成
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        //这里如果有需要，可以添加向view的回调
    }

    public void notifyPlanStatusChanged(int position) {

        boolean isCompletedPast = mDataManager.getPlan(position).getCompletionTime() != 0;

        //执行以下语句时，只是在view上让position处的plan删除了，实际上还未被删除但也即将被删除
        //NOTE: 不能用notifyItemRemoved，会没有效果
        mPlanAdapter.notifyItemRemoved(position);

        mDataManager.notifyPlanStatusChanged(position);

        mPlanAdapter.notifyItemInserted(isCompletedPast ? 0 : mDataManager.getUcPlanCount());

        EventBus.getDefault().post(new UcPlanCountChangedEvent());
    }

    @Subscribe
    public void onPlanListLoaded(DataLoadedEvent event) {
        mPlanAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        mPlanAdapter.notifyDataSetChanged();
        //有一定几率报错
        //mPlanAdapter.notifyItemInserted(0);
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        List<Integer> singleTypeUcPlanPosList = mDataManager.getSingleTypeUcPlanLocations(event.getTypeCode());
        for (int position : singleTypeUcPlanPosList) {
            //所有属于这个类型的计划都需要刷新
            mPlanAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.isPlanStatusChanged()) {
            //如果有完成情况的改变，直接全部刷新
            //有点麻烦，直接使用全部刷新了
            //TODO 可以用event.position配合算出的未完成计划数量，使用notifyItemMoved刷新
            mPlanAdapter.notifyDataSetChanged();
        } else {
            //普通、类型改变的刷新
            //根据event中的成员变量刷新界面
            mPlanAdapter.notifyItemChanged(event.getPosition());
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        mPlanAdapter.notifyItemRemoved(event.getPosition());
    }

    @Subscribe
    public void onReminded(RemindedEvent event) {
        //不用进行数据库存储了，在广播接收的时候已经处理过了
        mPlanAdapter.notifyItemChanged(event.position);
    }
}
