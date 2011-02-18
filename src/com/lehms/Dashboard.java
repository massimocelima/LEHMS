package com.lehms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import roboguice.activity.RoboActivity;

public class Dashboard extends RoboActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
	}
	
	public void onMyRosterClick(View view)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Please select the roster you would like to view")
            .setItems(R.array.dashboard_my_roster_selection, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

               }
            })
            .setCancelable(true)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })
            .create();
		
		dialog.show();
		
	}
	
	public void onClientsClick(View view)
	{
		
	}
	
	public void onMapClick(View view)
	{
		
	}
	
}
