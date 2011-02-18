package com.lehms;

import android.os.Bundle;
import android.view.Window;
import roboguice.activity.RoboActivity;

public class Dashboard extends RoboActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
	}
	
}
