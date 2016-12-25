package com.zack.enderplan.injector.module;

import com.zack.enderplan.view.contract.ReminderViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class ReminderPresenterModule {

    private final ReminderViewContract mReminderViewContract;
    private final int mPlanListPosition;
    private final String mPlanCode;

    public ReminderPresenterModule(ReminderViewContract reminderViewContract, int planListPosition, String planCode) {
        mReminderViewContract = reminderViewContract;
        mPlanListPosition = planListPosition;
        mPlanCode = planCode;
    }

    @Provides
    ReminderViewContract provideReminderViewContract() {
        return mReminderViewContract;
    }

    @Provides
    int providePlanListPosition() {
        return mPlanListPosition;
    }

    @Provides
    String providePlanCode() {
        return mPlanCode;
    }
}
