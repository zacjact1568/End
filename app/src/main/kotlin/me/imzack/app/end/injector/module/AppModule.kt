package me.imzack.app.end.injector.module

import android.content.Context
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

//TODO https://www.kotlincn.net/docs/tutorials/android-frameworks.html

//仅能通过App提供的才需要通过构造方法传入
@Module
class AppModule(private val mContext: Context) {

    //AppComponent构造过程中并不会调用以下的provide方法
    @Provides
    @Singleton
    fun provideContext() = mContext

    @Provides
    @Singleton
    fun provideEventBus() = EventBus.getDefault()!!
}
