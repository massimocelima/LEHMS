package com.lehms.ui.widget;

import java.util.Date;

import roboguice.service.RoboService;

import com.google.inject.Inject;
import com.lehms.R;
import com.lehms.RaiseAlarmActivity;
import com.lehms.UIHelper;
import com.lehms.messages.GetNotificationsRequest;
import com.lehms.messages.GetNotificationsResponse;
import com.lehms.serviceInterface.INotificationsResource;
import com.lehms.util.AppLog;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		try { context.stopService(new Intent(context, UpdateService.class)); } catch (Exception ex ) {}
		context.startService(new Intent(context, UpdateService.class));

		// For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
		context.stopService(new Intent(context, UpdateService.class));
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private void updateAppWidget(Context context, 
    		AppWidgetManager appWidgetManager,
            int appWidgetId ) {

        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, RaiseAlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_home);

        // Get the layout for the App Widget and attach an on-click listener to the button
        views.setOnClickPendingIntent(R.id.emergency_button, pendingIntent);
        
        views.setTextViewText(R.id.widget_home_message, "Loading...");
        
        // Tell the AppWidgetManager to perform an update on the current App Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
	public static class UpdateService extends RoboService {
		
		@Inject private INotificationsResource _notificationsResource;
		
		@Override
		public void onStart(Intent intent, int startId) {
			
			ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			int[] ids = manager.getAppWidgetIds(thisWidget);
			for (int i = 0; i < ids.length; i++) {
				RemoteViews updateViews = buildUpdate(this, ids[i]);
				manager.updateAppWidget(ids[i], updateViews);
			}
		}

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		public RemoteViews buildUpdate(Context context, int widgetid) {

			RemoteViews updateViews = null;

			updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_home);

	        // Create an Intent to launch ExampleActivity
	        Intent intent = new Intent(context, RaiseAlarmActivity.class);
	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

	        // Get the layout for the App Widget and attach an on-click listener to the button
	        updateViews.setOnClickPendingIntent(R.id.emergency_button, pendingIntent);

			try
			{
				GetNotificationsResponse response = _notificationsResource.Get(new GetNotificationsRequest()); 
				
				if(response.Notifications.size() > 0)
				{
					updateViews.setTextViewText(R.id.widget_home_message, response.Notifications.get(0).Message + " - " +
							UIHelper.FormatShortDate(response.Notifications.get(0).CreatedDate));
				}
				
			} catch(Exception ex)
			{
				AppLog.error(ex.getMessage(), ex);
			}

	        
			return updateViews;
		}

	}

}
