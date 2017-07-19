package me.imzack.app.end.injector.module;

import me.imzack.app.end.view.contract.MyPlansViewContract;

import dagger.Module;
import dagger.Provides;

@Module
public class MyPlansPresenterModule {

    private final MyPlansViewContract mMyPlansViewContract;

    public MyPlansPresenterModule(MyPlansViewContract myPlansViewContract) {
        mMyPlansViewContract = myPlansViewContract;
    }

    @Provides
    MyPlansViewContract provideMyPlansViewContract() {
        return mMyPlansViewContract;
    }
}
