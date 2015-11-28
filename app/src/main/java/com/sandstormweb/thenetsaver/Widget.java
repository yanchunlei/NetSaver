package com.sandstormweb.thenetsaver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import com.sandstormweb.thenetsaver.ServicesAndReceivers.MonitorService;

public class Widget extends AppWidgetProvider
{
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        try{
            Intent i = new Intent();
            i.setAction("updateMe");
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private class IntentReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                setSelected(context, intent.getExtras().getString("mode"));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        try{
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
            ComponentName cn = new ComponentName(context, Widget.class);

            rv.setOnClickPendingIntent(R.id.widget_custom, getActionPendingIntent(context,"custom"));
            rv.setOnClickPendingIntent(R.id.widget_home, getActionPendingIntent(context,"home"));
            rv.setOnClickPendingIntent(R.id.widget_outdoor, getActionPendingIntent(context,"outdoor"));
            rv.setOnClickPendingIntent(R.id.widget_work, getActionPendingIntent(context,"work"));
            rv.setOnClickPendingIntent(R.id.widget_sleep, getActionPendingIntent(context,"sleep"));

            IntentFilter f = new IntentFilter("update");
            LocalBroadcastManager.getInstance(context).registerReceiver(new IntentReceiver(), f);

            Intent i = new Intent();
            i.setAction("updateMe");
            LocalBroadcastManager.getInstance(context).sendBroadcast(i);

            AppWidgetManager.getInstance(context).updateAppWidget(cn, rv);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        try{
            Intent i = new Intent("widgetSelection");

            switch(intent.getAction())
            {
                case "custom" :
                    i.putExtra("mode", MonitorService.MODE_CUSTOM);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                    break;
                case "home" :
                    i.putExtra("mode",MonitorService.MODE_HOME);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                    break;
                case "outdoor" :
                    i.putExtra("mode",MonitorService.MODE_OUTDOOR);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                    break;
                case "work" :
                    i.putExtra("mode",MonitorService.MODE_WORK);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                    break;
                case "sleep" :
                    i.putExtra("mode",MonitorService.MODE_SLEEP);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private PendingIntent getActionPendingIntent(Context context, String action)
    {
        try{
            Intent i = new Intent(context, getClass());
            i.setAction(action);

            return PendingIntent.getBroadcast(context,0,i,0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void setSelected(Context context, String mode)
    {
        try{
            ComponentName cn = new ComponentName(context, getClass());
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

            switch(Integer.parseInt(mode))
            {
                case MonitorService.MODE_CUSTOM :
                    rv.setImageViewResource(R.id.widget_custom, R.drawable.user_on);
                    rv.setImageViewResource(R.id.widget_home, R.drawable.home);
                    rv.setImageViewResource(R.id.widget_outdoor, R.drawable.outdoor);
                    rv.setImageViewResource(R.id.widget_work, R.drawable.work);
                    rv.setImageViewResource(R.id.widget_sleep, R.drawable.sleep);
                    break;
                case MonitorService.MODE_HOME :
                    rv.setImageViewResource(R.id.widget_custom, R.drawable.user);
                    rv.setImageViewResource(R.id.widget_home, R.drawable.home_on);
                    rv.setImageViewResource(R.id.widget_outdoor, R.drawable.outdoor);
                    rv.setImageViewResource(R.id.widget_work, R.drawable.work);
                    rv.setImageViewResource(R.id.widget_sleep, R.drawable.sleep);
                    break;
                case MonitorService.MODE_OUTDOOR :
                    rv.setImageViewResource(R.id.widget_custom, R.drawable.user);
                    rv.setImageViewResource(R.id.widget_home, R.drawable.home);
                    rv.setImageViewResource(R.id.widget_outdoor, R.drawable.outdoor_on);
                    rv.setImageViewResource(R.id.widget_work, R.drawable.work);
                    rv.setImageViewResource(R.id.widget_sleep, R.drawable.sleep);
                    break;
                case MonitorService.MODE_WORK :
                    rv.setImageViewResource(R.id.widget_custom, R.drawable.user);
                    rv.setImageViewResource(R.id.widget_home, R.drawable.home);
                    rv.setImageViewResource(R.id.widget_outdoor, R.drawable.outdoor);
                    rv.setImageViewResource(R.id.widget_work, R.drawable.work_on);
                    rv.setImageViewResource(R.id.widget_sleep, R.drawable.sleep);
                    break;
                case MonitorService.MODE_SLEEP :
                    rv.setImageViewResource(R.id.widget_custom, R.drawable.user);
                    rv.setImageViewResource(R.id.widget_home, R.drawable.home);
                    rv.setImageViewResource(R.id.widget_outdoor, R.drawable.outdoor);
                    rv.setImageViewResource(R.id.widget_work, R.drawable.work);
                    rv.setImageViewResource(R.id.widget_sleep, R.drawable.sleep_on);
                    break;
                case -1 :
                    rv.setImageViewResource(R.id.widget_custom, R.drawable.user);
                    rv.setImageViewResource(R.id.widget_home, R.drawable.home);
                    rv.setImageViewResource(R.id.widget_outdoor, R.drawable.outdoor);
                    rv.setImageViewResource(R.id.widget_work, R.drawable.work);
                    rv.setImageViewResource(R.id.widget_sleep, R.drawable.sleep);
                    break;
            }

            AppWidgetManager.getInstance(context).updateAppWidget(cn, rv);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
