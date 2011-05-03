package com.lehms.ui.widget;

import com.lehms.R;
import com.lehms.RaiseAlarmActivity;

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
        // For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId, "Lehms Widget");
        }
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            //ExampleAppWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // When the first widget is created, register for the TIMEZONE_CHANGED and TIME_CHANGED
        // broadcasts.  We don't want to be listening for these if nobody has our widget active.
        // This setting is sticky across reboots, but that doesn't matter, because this will
        // be called after boot if there is a widget instance for this provider.
        //PackageManager pm = context.getPackageManager();
        //pm.setComponentEnabledSetting(
        //        new ComponentName("com.example.android.apis", ".appwidget.ExampleBroadcastReceiver"),
        //        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        //        PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onDisabled(Context context) {
        // When the first widget is created, stop listening for the TIMEZONE_CHANGED and
        // TIME_CHANGED broadcasts.
        //PackageManager pm = context.getPackageManager();
        //pm.setComponentEnabledSetting(
        //        new ComponentName("com.example.android.apis", ".appwidget.ExampleBroadcastReceiver"),
        //        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        //        PackageManager.DONT_KILL_APP);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId, String titlePrefix) {
    	
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_home);

        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, RaiseAlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener to the button
        views.setOnClickPendingIntent(R.id.emergency_button, pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current App Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

	public static class UpdateService extends Service {
		@Override
		public void onStart(Intent intent, int startId) {
/*
			ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);

			int[] ids = manager.getAppWidgetIds(thisWidget);

			for (int i = 0; i < ids.length; i++) {
				RemoteViews updateViews = buildUpdate(this, ids[i]);
				manager.updateAppWidget(ids[i], updateViews);
			}
*/
		}

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		public RemoteViews buildUpdate(Context context, int widgetid) {
			//String url = WidgetConfiguration.loadTitlePref(context, widgetid);
			RemoteViews updateViews = null;
/*
			
			updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_home);

			Intent intent = new Intent(context, WidgetConfiguration.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetid);
			//intent.setData(ContentUris.withAppendedId(Uri.EMPTY, widgetid));

			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			updateViews.setOnClickPendingIntent(R.id.widget_home, pendingIntent);

			//} else {
			//	updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_message);
			//	CharSequence errorMessage = context.getText(R.string.widget_error);
			//	updateViews.setTextViewText(R.id.message, errorMessage);
*/
			return updateViews;
		}

	}

}
