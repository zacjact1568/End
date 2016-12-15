package com.zack.enderplan.interactor.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.domain.view.FirstPlanView;
import com.zack.enderplan.event.GuideEndedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.common.Util;

import org.greenrobot.eventbus.EventBus;

public class FirstPlanPresenter extends BasePresenter implements Presenter<FirstPlanView> {

    private FirstPlanView mFirstPlanView;
    private DataManager mDataManager;
    private EventBus mEventBus;

    public FirstPlanPresenter(FirstPlanView firstPlanView) {
        attachView(firstPlanView);
        mDataManager = DataManager.getInstance();
        mEventBus = EventBus.getDefault();
    }

    @Override
    public void attachView(FirstPlanView view) {
        mFirstPlanView = view;
    }

    @Override
    public void detachView() {
        mFirstPlanView = null;
    }

    public void setInitialView(Bundle savedInstanceState) {
        mFirstPlanView.showInitialView(savedInstanceState == null);
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
            mFirstPlanView.onFirstPlanCreated();
            mEventBus.post(new PlanCreatedEvent(getPresenterName(), plan.getPlanCode(), mDataManager.getRecentlyCreatedPlanLocation()));
        } else {
            mFirstPlanView.onDetectedEmptyContent();
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
