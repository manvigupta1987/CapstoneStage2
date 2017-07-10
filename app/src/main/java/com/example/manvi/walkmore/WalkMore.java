package com.example.manvi.walkmore;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import timber.log.Timber;

/**
 * Created by manvi on 16/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class WalkMore extends Application {
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        sAnalytics = GoogleAnalytics.getInstance(this);
        //enable auto tracking
        sAnalytics.enableAutoActivityReports(this);
        sAnalytics.enableAdvertisingIdCollection(true);

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.analytics_tracker);
        }
        return sTracker;
    }
}
