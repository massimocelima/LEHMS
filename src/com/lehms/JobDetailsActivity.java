package com.lehms;

import android.os.Bundle;
import android.view.View;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;

public class JobDetailsActivity extends RoboActivity {

	public final static String JOB_ID = "job_id";
	
	@InjectExtra(JOB_ID) long _jobId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_details);
	}
	
	public void onHomeClick(View view)
	{
		UIHelper.GoHome(this);
	}
	
	public void onRefreshClick(View view)
	{
	}
	
}
