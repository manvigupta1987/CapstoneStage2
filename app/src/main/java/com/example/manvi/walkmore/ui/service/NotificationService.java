package com.example.manvi.walkmore.ui.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by manvi on 12/6/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        ReminderTask.executeTask(this, action);
    }
}