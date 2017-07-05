package com.example.manvi.walkmore.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.manvi.walkmore.R;
import com.google.common.base.Preconditions;


/**
 * Created by manvi on 22/5/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class WalkMorePreferences {

    public static final String PREF_DAILY_GOAL = "daily_goal";
    private static final String PREF_TOTAL_STEPS_VALUE = "total_steps";
    private static final String PREF_LAST_TOTAL_STEPS_VALUE = "last_total_steps";

    /**
     * Helper method to handle setting location details in Preferences (city name, latitude,
     * longitude)
     * <p>
     * When the location details are updated, the database should to be cleared.
     *
     * @param context   Context used to get the SharedPrefterences
     * @param dailyGoal Number of daily Steps
     */


    public static void editDailyGoal(Context context, int dailyGoal) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.daily_goal_key), dailyGoal);
        editor.apply();
    }

    /**
     * Helper method to handle setting location details in Preferences (city name, latitude,
     * longitude)
     * <p>
     * When the location details are updated, the database should to be cleared.
     *
     * @param context    Context used to get the SharedPrefterences
     * @param totalSteps Number of total Steps
     */


    public static void setTotalSteps(Context context, int totalSteps) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.total_steps_key), totalSteps);
        editor.apply();
    }

    /**
     * Helper method to handle setting location details in Preferences (city name, latitude,
     * longitude)
     * <p>
     * When the location details are updated, the database should to be cleared.
     *
     * @param context    Context used to get the SharedPrefterences
     * @param totalSteps Number of total Steps
     */


    public static void setLastDayTotalSteps(Context context, int totalSteps) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.last_total_steps_key), totalSteps);
        editor.apply();
    }

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "94043,USA", which is Mountain View, California. Mountain View is the
     * home of the headquarters of the Googleplex!
     *
     * @param context Context used to access SharedPreferences
     * @return total steps
     */
    public static int getTotalSteps(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.total_steps_key), -1);
    }

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "94043,USA", which is Mountain View, California. Mountain View is the
     * home of the headquarters of the Googleplex!
     *
     * @param context Context used to access SharedPreferences
     * @return total steps
     */
    public static int getLastDayTotalSteps(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.last_total_steps_key), -1);
    }

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "94043,USA", which is Mountain View, California. Mountain View is the
     * home of the headquarters of the Googleplex!
     *
     * @param context Context used to access SharedPreferences
     * @return Location The current user has set in SharedPreferences. Will default to
     * "94043,USA" if SharedPreferences have not been implemented yet.
     */
    public static int getDailyGoal(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.daily_goal_key), context.getResources().getInteger(R.integer.default_daily_goal));
    }

    public static void setHeightUnit(Context context, String unit) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.pref_height_key), unit);
        editor.apply();
    }

    public static float getUserHeight(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return (sp.getFloat(context.getString(R.string.pref_height_value_key), 0));
    }


    public static float getUserWeight(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return (sp.getFloat(context.getString(R.string.pref_weight_value_key), 0));
    }


    public static void setWeightUnit(Context context, String unit) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.pref_weight_key), unit);
        editor.apply();
    }

    public static void setUserHeight(Context context, float userHeightIninch) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putFloat(context.getString(R.string.pref_height_value_key), userHeightIninch);
        editor.apply();
    }

    public static void setUserWeight(Context context, float userWeightInPounds) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putFloat(context.getString(R.string.pref_weight_value_key), userWeightInPounds);
        editor.apply();
    }

    public static boolean isFeetNInch(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.pref_height_key);
        String defaultUnits = context.getString(R.string.pref_units_feet);
        String preferredUnits = sp.getString(keyForUnits, defaultUnits);
        String feet = context.getString(R.string.pref_units_feet);

        boolean userPrefersfeetNInch = false;
        if (feet.equals(preferredUnits)) {
            userPrefersfeetNInch = true;
        }
        return userPrefersfeetNInch;
    }

    public static boolean isPound(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.pref_weight_key);
        String defaultUnits = context.getString(R.string.pref_units_pound);
        String preferredUnits = sp.getString(keyForUnits, defaultUnits);
        String pound = context.getString(R.string.pref_units_pound);

        boolean userPrefersPound = false;
        if (pound.equals(preferredUnits)) {
            userPrefersPound = true;
        }
        return userPrefersPound;
    }

    public static boolean isKiloMeter(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.pref_distance_key);
        String defaultUnits = context.getString(R.string.pref_units_km);
        String preferredUnits = sp.getString(keyForUnits, defaultUnits);
        String kilometer = context.getString(R.string.pref_units_km);

        boolean userPrefersKilometer = false;
        if (kilometer.equals(preferredUnits)) {
            userPrefersKilometer = true;
        }
        return userPrefersKilometer;
    }

    public static void updateLastDaySteps(Context context, int stepsCountToday) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean firstRun = pref.getBoolean("firstTimeValue", true);
        if (firstRun) {
            WalkMorePreferences.setLastDayTotalSteps(context, stepsCountToday);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstTimeValue", false);
            editor.apply();
        }
    }

    public static void setNotificationSent(Context context, boolean notificationSent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(context.getString(R.string.pref_notification_sent_key), notificationSent);
        editor.apply();
    }

    public static boolean isNotificationSent(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return (sp.getBoolean(context.getString(R.string.pref_notification_sent_key), false));
    }


    public static boolean loginRequired(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);  //context.getSharedPreferences(context.getString(R.string.ActivityPREF1_key), Context.MODE_PRIVATE);
        return (pref.getBoolean(context.getString(R.string.firstTimeLogin_key), true));
    }

    public static void updateLoginRequired(Context context, boolean required) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context); //context.getSharedPreferences(context.getString(R.string.ActivityPREF1_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(context.getString(R.string.firstTimeLogin_key), required);
        editor.apply();
    }

    public static void storePersonformation(Context context, String personName) {
        if (!personName.equals("")) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(context.getString(R.string.person_name_key), personName);
            editor.apply();
        }
    }


    public static void storeEmailformation(Context context, String personEmail) {
        if (!personEmail.equals("")) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(context.getString(R.string.person_email_key), personEmail);
            editor.apply();
        }
    }

    public static void storePhotoLinkformation(Context context, Uri personPhoto){
        if(personPhoto!=null && !personPhoto.equals(Uri.EMPTY)){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(context.getString(R.string.person_photo_key), personPhoto.toString());
            editor.apply();
        }
    }



    public static Bundle getLoginInformation(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Bundle bundle  = new Bundle();
        bundle.putString(context.getString(R.string.person_name_key), pref.getString(context.getString(R.string.person_name_key),""));
        bundle.putString(context.getString(R.string.person_email_key), pref.getString(context.getString(R.string.person_email_key),""));
        bundle.putString(context.getString(R.string.person_photo_key), pref.getString(context.getString(R.string.person_photo_key),""));
        return bundle;
    }
}
