package com.example.manvi.walkmore.utils;

import android.support.annotation.RequiresApi;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.ui.activity.MainActivity;
import com.example.manvi.walkmore.ui.service.NotificationService;
import com.example.manvi.walkmore.ui.service.ReminderTask;


/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

/**
 * Utility class for creating hydration notifications
 */
public class NotificationUtils {

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private static final int NOTIFICATION_ID = 1138;
    private static final int ACTION_INCREMENT_GOAL_INTENT_ID = 1;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 14;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void sendNotification(Context context) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setSmallIcon(R.drawable.ic_footsteps)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.congratulations))
                .setContentText(context.getString(R.string.message))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.message)))
                .setContentIntent(contentIntent(context))
                .addAction(increaseDailyGoal(context))
                .addAction(ignoreNotification(context))
                .setAutoCancel(true);


        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* WATER_REMINDER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    private static Action ignoreNotification(Context context) {
        Intent ignoreReminderIntent = new Intent(context, NotificationService.class);
        ignoreReminderIntent.setAction(ReminderTask.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new Action(R.drawable.ic_cancel,
                context.getString(R.string.no_thanks),
                ignoreReminderPendingIntent);
    }

    private static Action increaseDailyGoal(Context context) {
        Intent goalIncrementIntent = new Intent(context, NotificationService.class);
        goalIncrementIntent.setAction(ReminderTask.ACTION_INCREMENT_GOAL);
        PendingIntent incrementWaterPendingIntent = PendingIntent.getService(
                context,
                ACTION_INCREMENT_GOAL_INTENT_ID,
                goalIncrementIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        return new Action(R.drawable.ic_target,
                context.getString(R.string.yes),
                incrementWaterPendingIntent);
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(startActivityIntent);
        return stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, R.drawable.ic_pedestrian_walking);
    }
}
