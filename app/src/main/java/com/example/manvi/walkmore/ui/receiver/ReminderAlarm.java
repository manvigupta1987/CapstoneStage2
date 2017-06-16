package com.example.manvi.walkmore.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.manvi.walkmore.data.WalkMorePreferences;
import com.example.manvi.walkmore.utils.ConstantUtils;

import timber.log.Timber;

/**
 * Created by manvi on 5/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public final class ReminderAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int stepsCountToday = WalkMorePreferences.getTotalSteps(context);
        WalkMorePreferences.setLastDayTotalSteps(context,stepsCountToday);
        updateWidgetsForStartGoogleService(context);
        WalkMorePreferences.setNotificationSent(context,false);
        //WalkMorePreferences.updateDuration(context, 0);
        Timber.d("======================================Manvi--------onReceive()======================================"+ stepsCountToday);
    }

    private void updateWidgetsForStartGoogleService(Context context) {
        Intent intent = new Intent(ConstantUtils.ACTION_DATA_STARTED).setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
