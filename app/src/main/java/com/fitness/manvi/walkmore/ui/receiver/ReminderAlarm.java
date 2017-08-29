package com.fitness.manvi.walkmore.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.fitness.manvi.walkmore.R;
import com.fitness.manvi.walkmore.data.WalkMorePreferences;
import com.fitness.manvi.walkmore.data.fitnessColumns;
import com.fitness.manvi.walkmore.data.fitnessDataProvider;
import com.fitness.manvi.walkmore.utils.ConstantUtils;
import com.fitness.manvi.walkmore.utils.DateUtils;
import com.fitness.manvi.walkmore.utils.WeightUtils;

import java.util.Date;

import static com.fitness.manvi.walkmore.utils.WeightUtils.calculateDistanceFromSteps;

/**
 * Created by manvi on 5/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public final class ReminderAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int totalStepsCount = WalkMorePreferences.getTotalSteps(context);
        int stepsCountToday = (totalStepsCount - WalkMorePreferences.getLastDayTotalSteps(context));
        WalkMorePreferences.setLastDayTotalSteps(context,totalStepsCount);
        updateWidgetsForStartGoogleService(context);
        WalkMorePreferences.setNotificationSent(context,false);
        insertData(context, stepsCountToday);
    }

    private void insertData(final Context context, final int stepsCount) {
        final PendingResult pendingResult = goAsync();
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Date date = new Date();
                String dateString = DateUtils.simpleDateFormat.format(date);

                float height = WalkMorePreferences.getUserHeight(context);
                double timeInMin = (stepsCount / context.getResources().getInteger(R.integer.steps_per_min));
                float weight = WalkMorePreferences.getUserWeight(context);
                double distance = calculateDistanceFromSteps(stepsCount, height, WalkMorePreferences.isKiloMeter(context));
                int calorie = WeightUtils.countCalories(weight, height, stepsCount);
                ContentValues cv = new ContentValues();
                cv.put(fitnessColumns.COLUMN_DISTANCE, distance);
                cv.put(fitnessColumns.COLUMN_CALORIES, calorie);
                cv.put(fitnessColumns.COLUMN_STEPS, stepsCount);
                cv.put(fitnessColumns.COLUMN_DATE, dateString);
                cv.put(fitnessColumns.COLUMN_DURATION, timeInMin);
                context.getContentResolver().insert(fitnessDataProvider.fitness.CONTENT_URI, cv);
                pendingResult.finish();
                return null;
            }
        };
        asyncTask.execute();
    }

    private void updateWidgetsForStartGoogleService(Context context) {
        Intent intent = new Intent(ConstantUtils.ACTION_DATA_STARTED).setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
