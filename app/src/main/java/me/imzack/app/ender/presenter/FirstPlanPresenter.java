package me.imzack.app.ender.presenter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import me.imzack.app.ender.App;
import me.imzack.app.ender.R;
import me.imzack.app.ender.util.ColorUtil;
import me.imzack.app.ender.util.CommonUtil;
import me.imzack.app.ender.view.contract.FirstPlanViewContract;
import me.imzack.app.ender.event.GuideEndedEvent;
import me.imzack.app.ender.event.PlanCreatedEvent;
import me.imzack.app.ender.event.TypeCreatedEvent;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.model.bean.Plan;
import me.imzack.app.ender.model.bean.Type;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class FirstPlanPresenter extends BasePresenter {

    private FirstPlanViewContract mFirstPlanViewContract;
    private DataManager mDataManager;
    private EventBus mEventBus;

    @Inject
    public FirstPlanPresenter(FirstPlanViewContract firstPlanViewContract, DataManager dataManager, EventBus eventBus) {
        mFirstPlanViewContract = firstPlanViewContract;
        mDataManager = dataManager;
        mEventBus = eventBus;
    }

    @Override
    public void attach() {
        mFirstPlanViewContract.showInitialView();
    }

    @Override
    public void detach() {
        mFirstPlanViewContract = null;
    }

    public void notifyEnterButtonClicked(String content) {
        if (!TextUtils.isEmpty(content)) {
            addDefaultTypes();
            addDefaultPlans();
            Plan plan = new Plan(CommonUtil.makeCode());
            plan.setContent(content);
            plan.setTypeCode(mDataManager.getType(0).getTypeCode());
            plan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(plan);
            mFirstPlanViewContract.onFirstPlanCreated();
            mEventBus.post(new PlanCreatedEvent(getPresenterName(), plan.getPlanCode(), mDataManager.getRecentlyCreatedPlanLocation()));
        } else {
            mFirstPlanViewContract.onDetectedEmptyContent();
        }
    }

    public void notifyExitAnimationEnded() {
        //通知GuidePresenter，正常结束向导
        mEventBus.post(new GuideEndedEvent(getPresenterName(), true));
    }

    /** 添加预置的几个type */
    private void addDefaultTypes() {
        int[] nameResIds = {R.string.def_type_1, R.string.def_type_2, R.string.def_type_3, R.string.def_type_4};
        int[] colorResIds = {R.color.indigo, R.color.red, R.color.orange, R.color.green};
        String[] patternFns = {"ic_computer_black_24dp", "ic_home_black_24dp", "ic_work_black_24dp", "ic_school_black_24dp"};
        Context context = App.getContext();
        for (int i = 0; i < 4; i++) {
            Type type = new Type(
                    CommonUtil.makeCode(),
                    context.getString(nameResIds[i]),
                    ColorUtil.parseColor(ContextCompat.getColor(context, colorResIds[i])),
                    patternFns[i],
                    i
            );
            mDataManager.notifyTypeCreated(type);
            mEventBus.post(new TypeCreatedEvent(getPresenterName(), type.getTypeCode(), mDataManager.getRecentlyCreatedTypeLocation()));
        }
    }

    /** 添加预置的几个plan */
    private void addDefaultPlans() {
        int[] contentResIds = {R.string.def_plan_1, R.string.def_plan_2, R.string.def_plan_3};
        Context context = App.getContext();
        String defaultTypeCode = mDataManager.getType(0).getTypeCode();
        for (int i = 0; i < 3; i++) {
            Plan plan = new Plan(CommonUtil.makeCode());
            plan.setContent(context.getString(contentResIds[i]));
            plan.setTypeCode(defaultTypeCode);
            plan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(plan);
            mEventBus.post(new PlanCreatedEvent(getPresenterName(), plan.getPlanCode(), mDataManager.getRecentlyCreatedPlanLocation()));
        }
    }
}
