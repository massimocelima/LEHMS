package com.lehms.receivers;

import com.lehms.service.GPSLoggerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSLoggerAlarmReciever extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context,GPSLoggerService.class));		
	}

}
