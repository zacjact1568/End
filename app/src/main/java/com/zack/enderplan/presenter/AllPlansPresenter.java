package com.zack.enderplan.presenter;

import android.content.ContentValues;
import android.view.View;
import android.widget.ImageView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.PlanStatusChangedEvent;
import com.zack.enderplan.event.RemindedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.event.UcPlanCountChangedEvent;
import com.zack.enderplan.manager.DataManager;
import com.zack.enderplan.manager.ReminderManager;
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
    private ReminderManager reminderManager;

    public AllPlansPresenter(AllPlansView allPlansView) {
        attachView(allPlansView);
        dataManager = DataManager.getInstance();
        enderplanDB = EnderPlanDB.getInstance();
        reminderManager = ReminderManager.getInstance();
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
                plan.setStarStatus(newStarStatus);
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
            dataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), -1);
            dataManager.updateUcPlanCount(-1);
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要将提醒取消
            reminderManager.cancelAlarm(plan.getPlanCode());
            //NOTE：不用plan.setReminderTime(0)，因为这个时间还要在撤销的时候被用来重设提醒
            //同样，也不用修改数据库了，因为最终这个plan是要在数据库中删除的
            //removeReminder(plan.getPlanCode());
        }
        dataManager.removeFromPlanList(position);
        planAdapter.notifyItemRemoved(position);
        allPlansView.onPlanDeleted(plan.getContent(), position, plan);

        enderplanDB.deletePlan(plan.getPlanCode());

        //这里不用postPlanDeletedEvent了，因为当可以滑动删除（即调用这个方法）时，其他需要更新的界面都不存在
    }

    public void notifyPlanRecreated(int position, Plan plan) {
        dataManager.addToPlanList(position, plan);
        if (plan.getCompletionTime() == 0) {
            //说明该计划还未完成
            dataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), 1);
            dataManager.updateUcPlanCount(1);
            EventBus.getDefault().post(new UcPlanCountChangedEvent());
        }
        if (plan.getReminderTime() != 0) {
            //说明该计划有提醒，需要重新设置提醒
            reminderManager.setAlarm(plan.getPlanCode(), plan.getReminderTime());
            //setReminder(plan.getPlanCode(), plan.getReminderTime());
        }
        planAdapter.notifyItemInserted(position);
        //这里如果有需要，可以添加向view的回调

        enderplanDB.savePlan(plan);
    }

    public void notifyPlanStatusChanged(int position) {

        ContentValues values = new ContentValues();

        Plan plan = dataManager.getPlan(position);
        boolean isCompleted = plan.getCompletionTime() != 0;
        dataManager.updateUcPlanCountOfEachTypeMap(plan.getTypeCode(), isCompleted ? 1 : -1);
        dataManager.updateUcPlanCount(isCompleted ? 1 : -1);
        EventBus.getDefault().post(new UcPlanCountChangedEvent());

        dataManager.removeFromPlanList(position);
        planAdapter.notifyItemRemoved(position);

        //Remove reminder (if has)
        if (plan.getReminderTime() != 0) {
            reminderManager.cancelAlarm(plan.getPlanCode());
            plan.setReminderTime(0);
            values.put(EnderPlanDB.DB_STR_REMINDER_TIME, 0);
            //removeReminder(plan.getPlanCode());
        }

        long currentTimeMillis = System.currentTimeMillis();

        long newCreationTime = isCompleted ? currentTimeMillis : 0;
        long newCompletionTime = isCompleted ? 0 : currentTimeMillis;

        plan.setCreationTime(newCreationTime);
        plan.setCompletionTime(newCompletionTime);

        int newPosition = isCompleted ? 0 : dataManager.getUcPlanCount();
        dataManager.addToPlanList(newPosition, plan);
        planAdapter.notifyItemInserted(newPosition);

        //数据库存储
        values.put(EnderPlanDB.DB_STR_CREATION_TIME, newCreationTime);
        values.put(EnderPlanDB.DB_STR_COMPLETION_TIME, newCompletionTime);
        enderplanDB.editPlan(plan.getPlanCode(), values);
    }

    /*private void removeReminder(String planCode) {
        ReminderManager manager = new ReminderManager();
        manager.cancelAlarm(planCode);
    }

    private void setReminder(String planCode, long reminderTime) {
        ReminderManager manager = new ReminderManager();
        manager.setAlarm(planCode, reminderTime);
    }*/

    /*private ReminderManager getReminderManager() {
        if (reminderManager == null) {
            reminderManager = new ReminderManager();
        }
        return reminderManager;
    }*/

    @Subscribe
    public void onPlanListLoaded(DataLoadedEvent event) {
        planAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        planAdapter.notifyDataSetChanged();
        //有一定几率报错
        //planAdapter.notifyItemInserted(0);
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        List<Integer> singleTypeUcPlanPosList = dataManager.getSingleTypeUcPlanLocations(event.typeCode);
        for (int position : singleTypeUcPlanPosList) {
            //所有属于这个类型的计划都需要刷新
            planAdapter.notifyItemChanged(position);
        }
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        //根据event中的成员变量刷新界面
        planAdapter.notifyItemChanged(event.position);
    }

    @Subscribe
    public void onPlanStatusChanged(PlanStatusChangedEvent event) {
        //有点麻烦，直接使用全部刷新了
        //TODO 可以用event.position配合算出的未完成计划数量，使用notifyItemMoved刷新
        planAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        planAdapter.notifyItemRemoved(event.position);
    }

    @Subscribe
    public void onReminded(RemindedEvent event) {
        int position = dataManager.getPlanLocationInPlanList(event.planCode);
        dataManager.getPlan(position).setReminderTime(0);
        //不用进行数据库存储了，在广播接收的时候已经处理过了
        planAdapter.notifyItemChanged(position);
    }
}
