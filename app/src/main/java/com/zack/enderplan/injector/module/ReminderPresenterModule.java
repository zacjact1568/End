package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.ReminderViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class ReminderPresenterModule {

    private final ReminderViewContract mReminderViewContract;
    private final int mPlanListPosition;

    public ReminderPresenterModule(ReminderViewContract reminderViewContract, int planListPosition) {
        mReminderViewContract = reminderViewContract;
        mPlanListPosition = planListPosition;
    }

    @Provides
    ReminderViewContract provideReminderViewContract() {
        return mReminderViewContract;
    }

    @Provides
    int providePlanListPosition() {
        return mPlanListPosition;
    }
}
