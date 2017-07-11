package com.example.manvi.walkmore.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.data.FitnessContract;
import com.example.manvi.walkmore.data.WalkMorePreferences;
import com.example.manvi.walkmore.utils.ConstantUtils;
import com.example.manvi.walkmore.utils.DateUtils;
import com.example.manvi.walkmore.utils.WeightUtils;

import java.util.Date;

import timber.log.Timber;

import static com.example.manvi.walkmore.utils.WeightUtils.calculateDistanceFromSteps;

/**
 * Created by manvi on 5/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public final class ReminderAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int stepsCountToday = WalkMorePreferences.getTotalSteps(context);
        insertData(context, stepsCountToday);
        WalkMorePreferences.setLastDayTotalSteps(context,stepsCountToday);
        updateWidgetsForStartGoogleService(context);
        WalkMorePreferences.setNotificationSent(context,false);
        Timber.d("======================================Manvi--------onReceive()======================================"+ stepsCountToday);
    }

    private void insertData(final Context context, final int stepsCount) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                String dateString = DateUtils.simpleDateFormat.format(date);

                float height = WalkMorePreferences.getUserHeight(context);
                double timeInMin = (stepsCount / context.getResources().getInteger(R.integer.steps_per_min));
                float weight = WalkMorePreferences.getUserWeight(context);
                double distance = calculateDistanceFromSteps(stepsCount, height, WalkMorePreferences.isKiloMeter(context));
                int calorie = WeightUtils.countCalories(weight, height, stepsCount);
                ContentValues cv = new ContentValues();
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_DISTANCE, distance);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_CALORIES, calorie);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_STEPS, stepsCount);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_DATE, dateString);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_DURATION, timeInMin);
                context.getContentResolver().insert(FitnessContract.fitnessDataEntry.CONTENT_URI, cv);
            }
        }).start();
    }

    private void updateWidgetsForStartGoogleService(Context context) {
        Intent intent = new Intent(ConstantUtils.ACTION_DATA_STARTED).setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
