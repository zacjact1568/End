package com.zack.enderplan.receiver;

import android.content.BroadcastReceiver;

public abstract class BaseReceiver extends BroadcastReceiver {

    protected String getReceiverName() {
        return getClass().getSimpleName();
    }
}
