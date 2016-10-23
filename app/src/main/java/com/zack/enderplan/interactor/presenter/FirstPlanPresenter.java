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
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.utility.Util;

import org.greenrobot.eventbus.EventBus;

public class FirstPlanPresenter extends BasePresenter implements Presenter<FirstPlanView> {

    private FirstPlanView mFirstPlanView;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private Plan mPlan;

    public FirstPlanPresenter(FirstPlanView firstPlanView) {
        attachView(firstPlanView);
        mDataManager = DataManager.getInstance();
        mEventBus = EventBus.getDefault();
        mPlan = new Plan(Util.makeCode());
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
            mPlan.setContent(content);
            mPlan.setTypeCode(mDataManager.getType(0).getTypeCode());
            mPlan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(mPlan);
            mFirstPlanView.onFirstPlanCreated();
            mEventBus.post(new PlanCreatedEvent(getPresenterName(), mPlan.getPlanCode(), mDataManager.getRecentlyCreatedPlanLocation()));
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
        int[] typeNameResIds = {R.string.to_do, R.string.family, R.string.work, R.string.study};
        int[] typeMarkResIds = {R.color.indigo, R.color.red, R.color.orange, R.color.green};
        Context context = App.getGlobalContext();
        for (int i = 0; i < 4; i++) {
            mDataManager.notifyTypeCreated(new Type(
                    Util.makeCode(),
                    context.getResources().getString(typeNameResIds[i]),
                    Util.parseColor(ContextCompat.getColor(context, typeMarkResIds[i])),
                    null,//TODO default pattern
                    i
            ));
        }
    }

    /** 添加预置的几个plan */
    private void addDefaultPlans() {
        //TODO add default plans (as guides)
    }
}
