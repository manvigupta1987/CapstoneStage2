package com.example.manvi.walkmore.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.manvi.walkmore.utils.ConstantUtils;

/**
 * Implementation of App Widget functionality.
 */
public final class FitnessAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent serviceIntent = new Intent(context, FitnessWidgetIntentService.class);
        serviceIntent.setAction(ConstantUtils.ACTION_DATA_STARTED);
        context.startService(serviceIntent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Intent serviceIntent = new Intent(context,FitnessWidgetIntentService.class);
        if(intent!=null && intent.getAction()!=null){
            serviceIntent.setAction(intent.getAction());
        }
        context.startService(serviceIntent);
    }
}

