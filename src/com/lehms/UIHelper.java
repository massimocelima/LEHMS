package com.lehms;

import android.content.Context;
import android.content.Intent;

public class UIHelper {

	private UIHelper() {}
	
	public static void GoHome(Context context)
	{
        final Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
	}
	
}
