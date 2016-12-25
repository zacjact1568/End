package com.zack.enderplan.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.view.contract.FirstPlanViewContract;
import com.zack.enderplan.event.GuideEndedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.common.Util;

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
            Plan plan = new Plan(Util.makeCode());
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
        Context context = App.getGlobalContext();
        for (int i = 0; i < 4; i++) {
            Type type = new Type(
                    Util.makeCode(),
                    context.getString(nameResIds[i]),
                    Util.parseColor(ContextCompat.getColor(context, colorResIds[i])),
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
        Context context = App.getGlobalContext();
        String defaultTypeCode = mDataManager.getType(0).getTypeCode();
        for (int i = 0; i < 3; i++) {
            Plan plan = new Plan(Util.makeCode());
            plan.setContent(context.getString(contentResIds[i]));
            plan.setTypeCode(defaultTypeCode);
            plan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(plan);
            mEventBus.post(new PlanCreatedEvent(getPresenterName(), plan.getPlanCode(), mDataManager.getRecentlyCreatedPlanLocation()));
        }
    }
}
