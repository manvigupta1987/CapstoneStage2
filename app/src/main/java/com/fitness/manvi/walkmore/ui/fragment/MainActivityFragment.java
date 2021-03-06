package com.fitness.manvi.walkmore.ui.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitness.manvi.walkmore.R;
import com.fitness.manvi.walkmore.WalkMore;
import com.fitness.manvi.walkmore.data.FitnessContract;
import com.fitness.manvi.walkmore.data.WalkMorePreferences;
import com.fitness.manvi.walkmore.other.SwagPoints;
import com.fitness.manvi.walkmore.utils.ConstantUtils;
import com.fitness.manvi.walkmore.utils.DateUtils;
import com.fitness.manvi.walkmore.utils.DistanceUtils;
import com.fitness.manvi.walkmore.utils.HeightUtils;
import com.fitness.manvi.walkmore.utils.NotificationUtils;
import com.fitness.manvi.walkmore.utils.WeightUtils;
import com.fitness.manvi.walkmore.widget.FitnessWidgetIntentService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.fitness.manvi.walkmore.utils.WeightUtils.calculateDistanceFromSteps;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MainActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainActivityFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String AUTH_PENDING = "auth_state_pending";
    @BindView(R.id.today_date)
    TextView mTodayDate;
    @BindView(R.id.imageFlag)
    ImageView mImageViewFlag;
    @BindView(R.id.seekbar_point)
    SwagPoints mSwagPoints;
    @BindView(R.id.remainingSteps_Count)
    TextView mRemainingSteps;
    @BindView(R.id.total_Steps)
    TextView mTotalSteps;
    @BindView(R.id.distance)
    TextView mDistance;
    @BindView(R.id.calorie_Count)
    TextView mCaloriesCount;
    @BindView(R.id.duration)
    TextView mDuration;
    @BindView(R.id.bmi_count)
    TextView mBmiCount;
    @BindView(R.id.bmi_value)
    TextView mBmiValue;
    private GoogleApiClient mApiClient = null;
    private ProgressDialog progressDialog;
    private static GoogleApiAvailability mApiAvailability;

    private int mDailyStepsGoal;
    private int mDailyStepsCount;
    private double mTotalDailyDistance;
    private int mTotalCalorieCount;
    private DataSet mDataSet;
    private float mHeightInInch;
    private float mWeightInPounds;
    private Context mContext;
    private boolean isKilos;
    private final int REQUEST_OATH = 1;
    private boolean authInProgress = false;

    private OnDataPointListener mListenerSteps;
    private CompositeSubscription mSubscription = new CompositeSubscription();;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainActivityFragment.
     */
    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);
        ButterKnife.bind(this, rootView);

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        if (mAdView != null) {
            MobileAds.initialize(getActivity(), "ca-app-pub-4862241919033566~1207803733");
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        progressDialog = new ProgressDialog(getActivity());

        Context context = getContext();
        Intent intent1 = new Intent(context, FitnessWidgetIntentService.class);
        intent1.setAction(ConstantUtils.ACTION_DATA_STOPED).setPackage(context.getPackageName());
        context.startService(intent1);

        mSwagPoints.setEnabled(false);
        buildFitnessApiClient();
        updateTodaysDate();
        readDataFromLocalStorage();
        calculateBmi();

        mSwagPoints.setMax(mDailyStepsGoal);
        WalkMore application = (WalkMore) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.home_screen));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void readDataFromLocalStorage() {
        mDailyStepsGoal = WalkMorePreferences.getDailyGoal(getActivity());
        mHeightInInch = WalkMorePreferences.getUserHeight(getActivity());
        mWeightInPounds = WalkMorePreferences.getUserWeight(getActivity());
        isKilos = WalkMorePreferences.isKiloMeter(getActivity());
    }

    private void updateTodaysDate() {
        Date date = new Date();
        long timeInMilli = date.getTime();
        if (!DateUtils.isDateNormalized(timeInMilli)) {
            timeInMilli = DateUtils.getNormalizedUtcDateForToday();
        }
        String todayDate = DateUtils.getFriendlyDateString(getActivity(), timeInMilli);
        mTodayDate.setText(todayDate);
        mTodayDate.setContentDescription(getString(R.string.a11y_today_date, mTodayDate.getText().toString()));
    }


    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(mContext).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(mContext).registerOnSharedPreferenceChangeListener(this);
    }

    private void calculateBmi() {
        if (mWeightInPounds != 0 && mHeightInInch != 0) {
            double weightInKg = WeightUtils.convertWeightFromPoundsToKg(mWeightInPounds);
            double heightInMeter = HeightUtils.convertInchtoMeter(mHeightInInch);
            heightInMeter = heightInMeter * heightInMeter;
            double bmi = weightInKg / heightInMeter;
            if (bmi != 0) {
                mBmiCount.setText(getString(R.string.total_bmi, bmi));
                if (bmi < 18.5) {
                    mBmiValue.setText(getString(R.string.underweight));
                } else if (bmi >= 18.5 & bmi < 25) {
                    mBmiValue.setText(getString(R.string.normal));
                } else if (bmi >= 25) {
                    mBmiValue.setText(getString(R.string.overweight));
                }
                mBmiValue.setContentDescription(getString(R.string.a11y_bmi, mBmiValue.getText().toString()));
            }
        } else {
            mBmiCount.setText(getString(R.string.zero));
        }
    }

    private void buildFitnessApiClient() {
        if (progressDialog == null) {
            return;
        }
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();

        if (mApiClient == null && ConstantUtils.checkPermissions(getActivity())) {
            mApiAvailability = GoogleApiAvailability.getInstance();
            mApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Fitness.SENSORS_API)
                    .addApi(Fitness.RECORDING_API)
                    .addApi(Fitness.HISTORY_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .useDefaultAccount()
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    private void countCalories(int Steps) {
        mTotalCalorieCount = WeightUtils.countCalories(mWeightInPounds, mHeightInInch, Steps);
        mCaloriesCount.setText(getString(R.string.total_calories, mTotalCalorieCount));
        mCaloriesCount.setContentDescription(getString(R.string.a11y_daily_calorie, mTotalCalorieCount));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mApiClient != null) {
            mApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        subscribe();
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(@NonNull DataSourcesResult dataSourcesResult) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
        createDistanceObservable();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (WalkMorePreferences.PREF_DAILY_GOAL.equals(key)) {
            mDailyStepsGoal = sharedPreferences.getInt(key, getResources().getInteger(R.integer.default_daily_goal));

            //if daily goal is greater than daily walled step count, update the remaining steps and circle progress.
            if (mDailyStepsGoal > mDailyStepsCount) {
                int remainingSteps = (mDailyStepsGoal - mDailyStepsCount);
                mSwagPoints.setMax(mDailyStepsGoal);
                mSwagPoints.setPoints(mDailyStepsCount);
                mRemainingSteps.setText(getString(R.string.remainingSteps, remainingSteps));
                WalkMorePreferences.setNotificationSent(getActivity(), false);
            } else {
                mRemainingSteps.setText(getString(R.string.remainingSteps, 0));
            }
        } else if (key.equals(getString(R.string.pref_distance_key))) {
            String distanceValue = sharedPreferences.getString(key, getString(R.string.pref_units_km));
            isKilos = distanceValue.equals(getString(R.string.pref_units_km));
        } else if (key.equals(getString(R.string.pref_weight_value_key)) ||
                key.equals(getString(R.string.pref_height_value_key))) {
            mHeightInInch = sharedPreferences.getFloat(key, 0);
            mWeightInPounds = sharedPreferences.getFloat(key, 0);
            mTotalCalorieCount = WeightUtils.countCalories(mWeightInPounds, mHeightInInch, mDailyStepsCount);
            mCaloriesCount.setText(getString(R.string.total_calories, mTotalCalorieCount));

        }
    }

    private void createDistanceObservable() {

        Observable<DataSet> distanceDataSetObservable = Observable.fromCallable(new Callable<DataSet>() {
            @Override
            public DataSet call() {
                PendingResult<DailyTotalResult> result =
                        Fitness.HistoryApi.readDailyTotal(mApiClient, DataType.TYPE_DISTANCE_DELTA);
                DailyTotalResult totalCalorieResult = result.await(30, TimeUnit.SECONDS);
                if (totalCalorieResult.getStatus().isSuccess()) {
                    DataSet totalSet = totalCalorieResult.getTotal();
                    if (totalSet == null || totalSet.isEmpty()) {
                        return null;
                    }
                    return totalSet;
                }
                return null;
            }
        });

        Subscription subscription = distanceDataSetObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataSet>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DataSet aLong) {
                        showDataSet(aLong);
                        mDataSet = aLong;
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubscription.clear();
        if(mSubscription!=null && mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    private void showDataSet(DataSet dataSet) {
        if (dataSet != null) {
            for (DataPoint dp : dataSet.getDataPoints()) {
                for (Field field : dp.getDataType().getFields()) {
                    if (dp.getValue(Field.FIELD_DISTANCE) != null) {
                        mTotalDailyDistance = DistanceUtils.covertMetersToKiloMeters(dp.getValue(Field.FIELD_DISTANCE).asFloat(), isKilos);
                        if (isKilos) {
                            mDistance.setText(getString(R.string.total_distance, mTotalDailyDistance));
                        } else {
                            mDistance.setText(getString(R.string.total_distanceInmiles, mTotalDailyDistance));
                        }
                    }
                }
            }
        }
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        mListenerSteps = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {

                for (final Field field : dataPoint.getDataType().getFields()) {
                    final Value value = dataPoint.getValue(field);
                    if (getActivity() != null && isAdded()) {   //to avoid crash
                        getActivity().runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                if (field.getName().equals(getString(R.string.steps_key))) {
                                    int steps = value.asInt();
                                    if (steps == 0) {
                                        int lastDaySteps = WalkMorePreferences.getLastDayTotalSteps(getActivity());
                                        int todaySteps = WalkMorePreferences.getTotalSteps(getActivity());
                                        mDailyStepsCount = todaySteps - lastDaySteps;
                                    } else {
                                        WalkMorePreferences.updateLastDaySteps(getActivity(), steps);
                                        WalkMorePreferences.setTotalSteps(getActivity(), steps);
                                        int lastDaySteps = WalkMorePreferences.getLastDayTotalSteps(getActivity());
                                        mDailyStepsCount = (steps - lastDaySteps);
                                        if (mDailyStepsCount < 0) {
                                            mDailyStepsCount = steps;
                                        }
                                    }
                                    UpdateUI();
                                    insertData();
                                    updateWidgets();
                                    checkIfGoalIsMet();
                                }
                            }
                        });
                    }
                }
            }
        };

        SensorRequest request = new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(30, TimeUnit.SECONDS)
                .build();

        Fitness.SensorsApi.add(mApiClient, request, mListenerSteps);
    }

    @Override
    public void onConnectionSuspended(int i) {
        WalkMorePreferences.updateLoginRequired(getActivity(), true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        WalkMorePreferences.updateLoginRequired(getActivity(), true);
        if (!connectionResult.hasResolution()) {
            mApiAvailability.getErrorDialog(getActivity(), connectionResult.getErrorCode(), 0).show();
        }
        if (!authInProgress) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult(getActivity(), REQUEST_OATH);
            } catch (IntentSender.SendIntentException e) {
                Timber.e(e.getMessage());
            }
        } else {
            Timber.e("GoogleFit", "authInProgress");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkIfGoalIsMet() {
        if (!WalkMorePreferences.isNotificationSent(getActivity())) {
            if (mDailyStepsCount >= mDailyStepsGoal) {
                mSwagPoints.animateRevealShow(mSwagPoints, mImageViewFlag, mRemainingSteps);
                NotificationUtils.sendNotification(getActivity());
                WalkMorePreferences.setNotificationSent(getActivity(), true);
            }
        }
    }

    private void UpdateUI() {
        mSwagPoints.setPoints(mDailyStepsCount);
        mTotalSteps.setText(getString(R.string.totalSteps, mDailyStepsCount));
        mTotalSteps.setContentDescription((mContext.getString(R.string.a11y_daily_steps, mDailyStepsCount)));
        int remainingStep = mDailyStepsGoal - mDailyStepsCount;
        if (mDailyStepsGoal < mDailyStepsCount) {
            remainingStep = 0;
        }
        mRemainingSteps.setText(getString(R.string.remainingSteps, remainingStep));
        mRemainingSteps.setContentDescription(getString(R.string.a11y_remaining_step, String.valueOf(remainingStep)));
        countCalories(mDailyStepsCount);
        if (mDataSet == null) {
            mTotalDailyDistance = calculateDistanceFromSteps(mDailyStepsCount, mHeightInInch, isKilos);
            if (isKilos) {
                mDistance.setText(getString(R.string.total_distance, mTotalDailyDistance));
            } else {
                mDistance.setText(getString(R.string.total_distanceInmiles, mTotalDailyDistance));
            }
        }
        mDistance.setContentDescription(getString(R.string.a11y_daily_distance, mTotalDailyDistance));
        double timeInMin = (mDailyStepsCount / getResources().getInteger(R.integer.steps_per_min));
        mDuration.setText(getString(R.string.total_duration, timeInMin));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mApiClient != null && mApiClient.isConnected()) {
            Fitness.SensorsApi.remove(mApiClient, mListenerSteps);
            mApiClient.disconnect();
        }
        updateWidgetsForStartGoogleService(mContext);
    }

    //Send intent to start the google client in the background to detect the step count.
    private static void updateWidgetsForStartGoogleService(Context context) {
        Intent intent = new Intent(context, FitnessWidgetIntentService.class);
        intent.setAction(ConstantUtils.ACTION_DATA_STARTED).setPackage(context.getPackageName());
        context.startService(intent);
    }

    /**
     * Subscribe to an available {@link DataType}. Subscriptions can exist across application
     * instances (so data is recorded even after the application closes down).  When creating
     * a new subscription, it may already exist from a previous invocation of this app.  If
     * the subscription already exists, the method is a no-op.  However, you can check this with
     * a special success code
     */
    private void subscribe() {
        Fitness.RecordingApi.subscribe(mApiClient, DataType.TYPE_DISTANCE_CUMULATIVE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    private void insertData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                String dateString = DateUtils.simpleDateFormat.format(date);

                ContentValues cv = new ContentValues();
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_DISTANCE, mTotalDailyDistance);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_CALORIES, mTotalCalorieCount);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_STEPS, mDailyStepsCount);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_DATE, dateString);
                cv.put(FitnessContract.fitnessDataEntry.COLUMN_DURATION, mDuration.getText().toString());
                getActivity().getContentResolver().insert(FitnessContract.fitnessDataEntry.CONTENT_URI, cv);
            }
        }).start();
    }

    private void updateWidgets() {
        Context context = getContext();
        Intent intent = new Intent(ConstantUtils.ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    public int getmDailyStepsCount() {
        return mDailyStepsCount;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OATH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                    mApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Timber.e("GoogleFit", "RESULT_CANCELED");
            }
        } else {
            Timber.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }
}
