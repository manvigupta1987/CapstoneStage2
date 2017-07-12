package com.fitness.manvi.walkmore.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;

import com.google.common.base.Preconditions;

/**
 * Created by manvi on 2/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ConstantUtils {
    public static final int WEEK_TAB = 0;
    public static final int MONTH_TAB = 1;
    public static final int YEAR_TAB = 2;

    public static final int TIME_MIN = 59;
    public static final int TIME_SEC = 58;
    public static final int TIME_HOUR = 23;

    public static final String FIRST_TIME = "first_time";
    public static final String ACTION_DATA_UPDATED= "com.fitness.manvi.walkmore.ACTION_DATA_UPDATED";
    public static final String ACTION_DATA_STARTED= "com.fitness.manvi.walkmore.ACTION_DATA_STARTED";
    public static final String ACTION_DATA_STOPED= "com.fitness.manvi.walkmore.ACTION_CLIENT_STOPED";

    public static boolean checkPermissions(Context context) {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return (permissionState == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isConnectedToInternet(Context context) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(context, "context is null");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
