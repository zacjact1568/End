package com.zack.enderplan.interactor.presenter;

import android.content.ContentValues;
import android.view.View;
import android.widget.ImageView;

import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.database.DatabaseDispatcher;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.interactor.adapter.PlanAdapter;
import com.zack.enderplan.model.ram.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.domain.view.MyPlansView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MyPlansPresenter implements Presenter<MyPlansView> {

    private static final String LOG_TAG = "MyPlansPresenter";

    private MyPlansView mMyPlansView;
    private DataManager mDataManager;
    private DatabaseDispatcher mDatabaseDispatcher;
    private ReminderManager mReminderManager;
    private PlanAdapter mPlanAdapter;

    public MyPlansPresenter(MyPlansView myPlansView) {
        attachView(myPlansView);
        mDataManager = DataManager.getInstance();
        mDatabaseDispatcher = DatabaseDispatcher.getInstance();
        mReminderManager = ReminderManager.getInstance();
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
                mDatabaseDispatcher.editStarStatus(plan.getPlanCode(), newStarStatus);
            }
        });

        mMyPlansView.showInitialView(mPlanAdapter);

        //开始从数据库加载数据
        mDataManager.loadFromDatabase();
    }

    //滑动删除时（可以撤销）
    public void notifyPlanDeleted(int position) {
        Plan plan = mDataManager.getPlan(position);
        if (plan.getCompletionTime() == 0) {
            //说明该计划还未完成
            mDataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), -1);
            mDataManager.updateUcPlanCount(-1);
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要将提醒取消
            mReminderManager.cancelAlarm(plan.getPlanCode());
            //NOTE：不用plan.setReminderTime(0)，因为这个时间还要在撤销的时候被用来重设提醒
            //同样，也不用修改数据库了，因为最终这个plan是要在数据库中删除的
            //removeReminder(plan.getPlanCode());
        }
        mDataManager.removeFromPlanList(position);
        mPlanAdapter.notifyItemRemoved(position);
        mMyPlansView.onPlanDeleted(plan.getContent(), position, plan);

        mDatabaseDispatcher.deletePlan(plan.getPlanCode());

        //这里不用postPlanDeletedEvent了，因为当可以滑动删除（即调用这个方法）时，其他需要更新的界面都不存在
    }

    public void notifyPlanRecreated(int position, Plan plan) {
        mDataManager.addToPlanList(position, plan);
        if (plan.getCompletionTime() == 0) {
            //说明该计划还未完成
            mDataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), 1);
            mDataManager.updateUcPlanCount(1);
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要重新设置提醒
            mReminderManager.setAlarm(plan.getPlanCode(), plan.getReminderTime());
            //setReminder(plan.getPlanCode(), plan.getReminderTime());
        }
        mPlanAdapter.notifyItemInserted(position);
        //这里如果有需要，可以添加向view的回调

        mDatabaseDispatcher.savePlan(plan);
    }

    public void notifyPlanStatusChanged(int position) {

        ContentValues values = new ContentValues();

        Plan plan = mDataManager.getPlan(position);
        boolean isCompleted = plan.getCompletionTime() != 0;
        mDataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), isCompleted ? 1 : -1);
        mDataManager.updateUcPlanCount(isCompleted ? 1 : -1);
        EventBus.getDefault().post(new UcPlanCountChangedEvent());

        mDataManager.removeFromPlanList(position);
        mPlanAdapter.notifyItemRemoved(position);

        //Remove reminder (if has)
        if (plan.getReminderTime() != 0) {
            mReminderManager.cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            values.put(DatabaseDispatcher.DB_STR_REMINDER_TIME, 0);
            //removeReminder(plan.getPlanCode());
        }

        long currentTimeMillis = System.currentTimeMillis();

        long newCreationTime = isCompleted ? currentTimeMillis : 0;
        long newCompletionTime = isCompleted ? 0 : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompleted ? 0 : mDataManager.getUcPlanCount();
        mDataManager.addToPlanList(newPosition, plan);
        mPlanAdapter.notifyItemInserted(newPosition);

        //数据库存储
        values.put(DatabaseDispatcher.DB_STR_CREATION_TIME, newCreationTime);
        values.put(DatabaseDispatcher.DB_STR_COMPLETION_TIME, newCompletionTime);
        mDatabaseDispatcher.editPlan(plan.getPlanCode(), values);
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
        List<Integer> singleTypeUcPlanPosList = mDataManager.getSingleTypeUcPlanLocations(event.typeCode);
        for (int position : singleTypeUcPlanPosList) {
            //所有属于这个类型的计划都需要刷新
            mPlanAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.isPlanStatusChanged) {
            //如果有完成情况的改变，直接全部刷新
            //有点麻烦，直接使用全部刷新了
            //TODO 可以用event.position配合算出的未完成计划数量，使用notifyItemMoved刷新
            mPlanAdapter.notifyDataSetChanged();
        } else {
            //普通、类型改变的刷新
            //根据event中的成员变量刷新界面
            mPlanAdapter.notifyItemChanged(event.position);
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        mPlanAdapter.notifyItemRemoved(event.position);
    }

    @Subscribe
    public void onReminded(RemindedEvent event) {
        //不用进行数据库存储了，在广播接收的时候已经处理过了
        mPlanAdapter.notifyItemChanged(event.position);
    }
}
