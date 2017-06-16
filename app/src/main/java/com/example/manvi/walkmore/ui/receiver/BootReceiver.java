package com.example.manvi.walkmore.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.manvi.walkmore.ui.activity.MainActivity;

/**
 * Created by manvi on 5/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public final class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            MainActivity.scheduleAlarms(context);
        }
    }
}
