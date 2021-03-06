package net.zackzhang.app.end.receiver

import android.content.BroadcastReceiver

abstract class BaseReceiver : BroadcastReceiver() {

    protected val receiverName
        get() = javaClass.simpleName!!
}
