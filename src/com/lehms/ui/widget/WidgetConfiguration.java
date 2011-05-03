/**
 * If you use this in something cool please let me know :)
 * 
 * @author pedrodias - petermdias@gmail.com - www.pedromdias.com
 */
package com.lehms.ui.widget;

import roboguice.activity.RoboActivity;

import com.google.inject.Inject;
import com.lehms.R;
import com.lehms.serviceInterface.INotificationsResource;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;

public class WidgetConfiguration extends RoboActivity {
	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(R.layout.widget_configure);

        // Bind the action for the save button.
        findViewById(R.id.save_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
	}
	
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WidgetConfiguration.this;

			Intent widgetUpdate = new Intent();
			widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { mAppWidgetId });

			PendingIntent newPending = PendingIntent.getBroadcast(getApplicationContext(), 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			int mins = Integer.parseInt(((EditText) findViewById(R.id.EditText01)).getText().toString());
			alarms.cancel(newPending);
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),  mins*1000*60,newPending);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
	
}
