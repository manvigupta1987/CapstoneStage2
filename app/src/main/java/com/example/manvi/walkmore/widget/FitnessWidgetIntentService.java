package com.example.manvi.walkmore.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;

import com.example.manvi.walkmore.data.WalkMorePreferences;
import com.example.manvi.walkmore.ui.activity.MainActivity;
import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.data.FitnessContract;
import com.example.manvi.walkmore.utils.ConstantUtils;
import com.example.manvi.walkmore.utils.DateUtils;
import com.example.manvi.walkmore.utils.NotificationUtils;
import com.example.manvi.walkmore.utils.WeightUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;


/**
 * Created by manvi on 8/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class FitnessWidgetIntentService extends IntentService implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static int mStepsCount = 0;
    private static int mCalorieCount = 0;
    private final String[] projection = {FitnessContract.fitnessDataEntry.COLUMN_STEPS,
            FitnessContract.fitnessDataEntry.COLUMN_CALORIES};

    private static final int INDEX_STEPS = 0;
    private static final int INDEX_CALORIES = 1;
    private int[] mAppWidgetIDs;
    private AppWidgetManager appWidgetManager;
    private GoogleApiClient mApiClient;
    private static int mGoal;

    public FitnessWidgetIntentService() {
        super("FitnessWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        appWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetIDs = appWidgetManager.getAppWidgetIds(new ComponentName(this, FitnessAppWidget.class));
        mGoal = WalkMorePreferences.getDailyGoal(this);
        if ((intent != null) && (intent.getAction() != null)) {
            if (intent.getAction().equals(ConstantUtils.ACTION_DATA_UPDATED)) {
                Date date = new Date();
                String dateString = DateUtils.simpleDateFormat.format(date);
                Uri urlWithDate = FitnessContract.fitnessDataEntry.buildFitnessDataUriWithDate(dateString);
                Cursor cursor = getContentResolver().query(urlWithDate, projection, null, null, FitnessContract.fitnessDataEntry.COLUMN_DATE + " ASC");

                if (cursor == null) {
                    return;
                }

                if (!cursor.moveToFirst()) {
                    cursor.close();
                    return;
                }

                mStepsCount = cursor.getInt(INDEX_STEPS);
                mCalorieCount = cursor.getInt(INDEX_CALORIES);
                cursor.close();
                UpdateUI();
            } else if (intent.getAction().equals(ConstantUtils.ACTION_DATA_STARTED)) {
                if (mApiClient == null && ConstantUtils.checkPermissions(this)) {
                    mApiClient = new GoogleApiClient.Builder(this)
                            .addApi(Fitness.SENSORS_API)
                            .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                            .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                            .useDefaultAccount()
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                }
                if (mApiClient != null) {
                    if (!mApiClient.isConnected()) {
                        mApiClient.connect();
                    }
                }
            } else if(intent.getAction().equals(ConstantUtils.ACTION_DATA_STOPED)){
                if(mApiClient!=null){
                    if(mApiClient.isConnected()){
                        mApiClient.disconnect();
                    }
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(@NonNull DataSourcesResult dataSourcesResult) {
                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        SensorRequest request = new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(30, TimeUnit.SECONDS)
                .build();
        Fitness.SensorsApi.add(mApiClient, request, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Timber.e("GoogleFit", "SensorApi successfully added");
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("=======================================onConnectionSuspended()=========================" + i);
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Timber.i("Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Timber.i("Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d("=======================================onConnectionFailed()=========================" + connectionResult.getErrorCode());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for (final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue(field);
            if (field.getName().equals("steps")) {
                int steps = value.asInt();
                Timber.i("=====================================Timeis=============================" + System.currentTimeMillis() + "Steps are "+ value.asInt());
                WalkMorePreferences.updateLastDaySteps(this, steps);
                WalkMorePreferences.setTotalSteps(this, steps);
                int lastDaySteps = WalkMorePreferences.getLastDayTotalSteps(this);
                mStepsCount = (value.asInt() - lastDaySteps);
                if (mStepsCount < 0) {
                    mStepsCount = steps;
                }
                float HeightInInch = WalkMorePreferences.getUserHeight(this);
                float WeightInPounds = WalkMorePreferences.getUserWeight(this);
                mCalorieCount = WeightUtils.countCalories(WeightInPounds, HeightInInch, mStepsCount);
                UpdateUI();
                checkIfGoalIsMet();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkIfGoalIsMet() {
        if (!WalkMorePreferences.isNotificationSent(this)) {
            if (mStepsCount >= mGoal) {
                NotificationUtils.sendNotification(this);
                WalkMorePreferences.setNotificationSent(this, true);
            }
        }
    }


    private void UpdateUI() {

        for (int appWidgetId : mAppWidgetIDs) {
            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.fitness_app_widget);
            views.setTextViewText(R.id.widget_steps, getString(R.string.totalSteps, mStepsCount));
            views.setTextViewText(R.id.widget_calories, getString(R.string.total_calories, mCalorieCount));

            views.setContentDescription(R.id.widget_steps, getString(R.string.a11y_daily_steps, mStepsCount));
            views.setContentDescription(R.id.widget_calories, getString(R.string.a11y_daily_calorie, mCalorieCount));
            // Create an Intent to launch ExampleActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button

            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

