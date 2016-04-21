package com.zack.enderplan.presenter;

import android.content.ContentValues;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCompletedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.PlanItemClickedEvent;
import com.zack.enderplan.event.ReminderTimeChangedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.util.LogUtil;
import com.zack.enderplan.view.AllPlansView;
import com.zack.enderplan.widget.PlanAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class AllPlansPresenter implements Presenter<AllPlansView> {

    private static final String LOG_TAG = "AllPlansPresenter";

    private AllPlansView allPlansView;
    private DataManager dataManager;
    private PlanAdapter planAdapter;
    private EnderPlanDB enderplanDB;

    public AllPlansPresenter(AllPlansView allPlansView) {
        attachView(allPlansView);
        dataManager = DataManager.getInstance();
        enderplanDB = EnderPlanDB.getInstance();
    }

    @Override
    public void attachView(AllPlansView view) {
        allPlansView = view;
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView() {
        allPlansView = null;
        EventBus.getDefault().unregister(this);
    }

    public void createPlanAdapter() {
        planAdapter = new PlanAdapter(dataManager.getPlanList(), dataManager.getTypeCodeAndColorResMap());
        planAdapter.setOnPlanItemClickListener(new PlanAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(View itemView, int position) {
                allPlansView.onPlanItemClicked(position);
                //allPlansView.onStartPlanDetailActivity(dataManager.getPlan(position));
            }
        });
        /*planAdapter.setOnPlanItemLongClickListener(new PlanAdapter.OnPlanItemLongClickListener() {
            @Override
            public void onPlanItemLongClick(View itemView, int position) {
                planItemClickPosition = position;
                DateTimePickerDialogFragment dialog = DateTimePickerDialogFragment.newInstance(planList.get(position).getReminderTime());
                dialog.show(getFragmentManager(), "reminder");
            }
        });*/
        planAdapter.setOnStarMarkClickListener(new PlanAdapter.OnStarMarkClickListener() {
            @Override
            public void onStarMarkClick(ImageView starMark, int itemPosition) {
                Plan plan = dataManager.getPlan(itemPosition);
                boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
                int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
                dataManager.getPlan(itemPosition).setStarStatus(newStarStatus);
                starMark.setImageResource(isStarred ? R.drawable.ic_star_outline_grey600_24dp :
                        R.drawable.ic_star_color_accent_24dp);
                //TODO 上面这段再改改？

                ContentValues values = new ContentValues();
                values.put("star_status", newStarStatus);
                enderplanDB.editPlan(plan.getPlanCode(), values);
            }
        });
        //allPlansView.onPlanAdapterCreated(planAdapter);
    }

    public PlanAdapter getPlanAdapter() {
        return planAdapter;
    }

    public void initDataLists() {
        dataManager.loadFromDatabase();
    }

    //滑动删除时（可以撤销）
    public void notifyPlanDeleted(int position) {
        Plan plan = dataManager.getPlan(position);
        if (plan.getCompletionTime() == 0) {
            //说明该计划还未完成
            dataManager.updateUcPlanCount(-1);
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要将提醒取消
            removeReminder(plan.getPlanCode());
        }
        dataManager.updatePlanCountOfEachType(plan.getTypeCode(), -1);
        dataManager.removeFromPlanList(position);
        planAdapter.notifyItemRemoved(position);
        allPlansView.onPlanDeleted(plan.getContent(), position, plan);

        enderplanDB.deletePlan(plan.getPlanCode());
    }

    //通过PlanDetailActivity删除时（不能撤销）
    public void notifyPlanDeleted(int position, String content) {
        planAdapter.notifyItemRemoved(position);
        allPlansView.onPlanDeleted(content);
    }

    public void notifyPlanRecreated(int position, Plan plan) {
        dataManager.addToPlanList(position, plan);
        if (plan.getCompletionTime() == 0) {
            //说明该计划还未完成
            dataManager.updateUcPlanCount(1);
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要重新设置提醒
            setReminder(plan.getPlanCode(), plan.getReminderTime());
        }
        dataManager.updatePlanCountOfEachType(plan.getTypeCode(), 1);
        planAdapter.notifyItemInserted(position);
        //这里如果有需要，可以添加向view的回调

        enderplanDB.savePlan(plan);
    }

    public void notifyPlanEdited(int position) {
        planAdapter.notifyItemChanged(position);

        //通知TypeDetailDialogFragment中的list更新（如果有的话）TODO 这里总有点不爽
        EventBus.getDefault().post(new PlanDetailChangedEvent());
    }

    public void notifyPlanStatusChanged(int position) {
        Plan plan = dataManager.getPlan(position);
        boolean isCompleted = plan.getCompletionTime() != 0;
        dataManager.updateUcPlanCount(isCompleted ? 1 : -1);
        EventBus.getDefault().post(new UcPlanCountChangedEvent());
        if (plan.getReminderTime() != 0) {
            removeReminder(plan.getPlanCode());
        }
        dataManager.removeFromPlanList(position);
        planAdapter.notifyItemRemoved(position);

        long currentTimeMillis = System.currentTimeMillis();

        long newCreationTime = isCompleted ? currentTimeMillis : 0;
        long newCompletionTime = isCompleted ? 0 : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompleted ? 0 : dataManager.getUcPlanCount();
        dataManager.addToPlanList(newPosition, plan);
        planAdapter.notifyItemInserted(newPosition);
        //这里如果有需要，可以添加向view的回调

        ContentValues values = new ContentValues();
        values.put(EnderPlanDB.DB_STR_CREATION_TIME, newCreationTime);
        values.put(EnderPlanDB.DB_STR_COMPLETION_TIME, newCompletionTime);
        enderplanDB.editPlan(plan.getPlanCode(), values);

        //planModel.updateUncompletedPlanCount(isCompleted);
        //uncompletedPlanCount = uncompletedPlanCount + (isCompleted ? 1 : -1);
        //onUncompletedPlanCountChanged(uncompletedPlanCount);
        //int newPosition = isCompleted ? 0 : uncompletedPlanCount;
    }

    private void removeReminder(String planCode) {
        ReminderManager manager = new ReminderManager();
        manager.cancelAlarm(planCode);
    }

    private void setReminder(String planCode, long reminderTime) {
        ReminderManager manager = new ReminderManager();
        manager.setAlarm(planCode, reminderTime);
    }

    @Subscribe
    public void onPlanListLoaded(DataLoadedEvent event) {
        planAdapter.notifyDataSetChanged();
    }

    //TODO 使plan的任一属性改变时都可以用此Event类发送通知
    @Subscribe
    public void onReminderTimeChanged(ReminderTimeChangedEvent event) {
        planAdapter.notifyItemChanged(event.changingPosition);
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        planAdapter.notifyDataSetChanged();
        //有一定几率报错
        //planAdapter.notifyItemInserted(0);
    }

    @Subscribe
    public void onPlanItemClicked(PlanItemClickedEvent event) {
        allPlansView.onPlanItemClicked(event.position);
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        List<Integer> singleTypeUcPlanPosList = dataManager.getSingleTypeUcPlanLocations(event.typeCode);
        for (int position : singleTypeUcPlanPosList) {
            planAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanCompleted(PlanCompletedEvent event) {
        notifyPlanStatusChanged(event.position);
    }
}
