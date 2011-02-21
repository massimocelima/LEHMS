package com.lehms;

import android.app.AlertDialog;
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

	public static void ShowUnderConstructionMessage(Context context)
	{
		AlertDialog dialog = new AlertDialog.Builder(context)
        .setTitle("Under Construction")
        .setMessage("This functionality is under construction")
        .create();
		
		dialog.show();
	}

}
