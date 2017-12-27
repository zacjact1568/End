package me.imzack.app.end

import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationManager
import android.os.Build
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import me.imzack.app.end.common.Constant
import me.imzack.app.end.event.EventBusIndex
import me.imzack.app.end.injector.component.AppComponent
import me.imzack.app.end.injector.component.DaggerAppComponent
import me.imzack.app.end.injector.module.AppModule
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.SystemUtil
import org.greenrobot.eventbus.EventBus
import java.io.FileOutputStream
import java.io.IOException

class App : Application() {

    companion object {

        lateinit var appComponent: AppComponent
            // 默认是 public get 和 public set，然而需要限制在内部set（直接赋值也算），所以要单独写
            private set

        // 这里不能直接初始化context，因为此时appComponent还未初始化
        val context
            get() = appComponent.context

        // 每调用一次AppComponent的getEventBus()方法，都会调用一次AppModule的provideEventBus()方法
        val eventBus
            get() = appComponent.eventBus
    }

    override fun onCreate() {
        super.onCreate()

        initAppComponent()

        initEventBus()

        initPreferences()

        initNotificationChannels()

        initDatabase()

        initData()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(applicationContext))
                .build()
    }

    private fun initEventBus() {
        EventBus.builder().addIndex(EventBusIndex()).installDefaultEventBus()
    }

    /** 通过Preference中的数据初始化某些设置 */
    private fun initPreferences() {
        //设定preferences默认值（仅执行一次）
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        //设定白天夜间模式
        AppCompatDelegate.setDefaultNightMode(if (DataManager.preferenceHelper.nightModeValue) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    /** 初始化 notification channels，仅当API为26或更高时有效 */
    @TargetApi(Build.VERSION_CODES.O)
    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !DataManager.preferenceHelper.needNotificationChannelsInitializationValue) return
        SystemUtil.addNotificationChannel(Constant.NOTIFICATION_CHANNEL_ID_REMINDER, getString(R.string.notification_channel_name_reminder), NotificationManager.IMPORTANCE_HIGH, getString(R.string.notification_channel_description_reminder))
        // 其他channels在这里添加
        DataManager.preferenceHelper.needNotificationChannelsInitializationValue = false
    }

    /** 初始化类型标记数据库 */
    private fun initDatabase() {
        val typeMarkDBFile = getDatabasePath(Constant.DB_TYPE_MARK)
        if (typeMarkDBFile.exists()) return
        val typeMarkDBDir = typeMarkDBFile.parentFile
        if (typeMarkDBDir.exists() || typeMarkDBDir.mkdir()) {
            try {
                val iStream = resources.openRawResource(R.raw.type_mark)
                val foStream = FileOutputStream(typeMarkDBFile)
                // 400000 is BUFFER_SIZE
                val buffer = ByteArray(400000)
                var count: Int
                while (true) {
                    count = iStream.read(buffer)
                    if (count <= 0) break
                    foStream.write(buffer, 0, count)
                }
                foStream.close()
                iStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun initData() {
        DataManager.loadData()
    }
}
