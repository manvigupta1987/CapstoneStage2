package com.fitness.manvi.walkmore.ui.service;

import android.content.Context;
import android.content.Intent;

import com.fitness.manvi.walkmore.ui.activity.MainActivity;
import com.fitness.manvi.walkmore.utils.NotificationUtils;

/**
 * Created by manvi on 12/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ReminderTask {

    public static final String ACTION_INCREMENT_GOAL = "increment-goal-count";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {
        if (ACTION_INCREMENT_GOAL.equals(action)) {
            incrementGoalCount(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }
    }

    private static void incrementGoalCount(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(ACTION_INCREMENT_GOAL);
        context.startActivity(intent);
        NotificationUtils.clearAllNotifications(context);
    }
}