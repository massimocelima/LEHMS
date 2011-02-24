package com.lehms;

import java.util.Date;

import com.google.inject.Inject;
import com.lehms.messages.dataContracts.JobDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.persistence.IRosterRepository;
import com.lehms.service.IRosterResource;

import android.R.integer;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class JobDetailsActivity extends RoboActivity {

	public final static String JOB_ID = "job_id";
	public final static String ROSTER_DATE = "ROSTER_DATE";
	
	@InjectExtra(ROSTER_DATE) long _rosterTime;
	@InjectExtra(JOB_ID) long _jobId;
	@InjectView(R.id.activity_job_details_title) TextView _title;
	@InjectView(R.id.activity_job_details_sub_title) TextView _subtitle;

	@Inject IRosterRepository _rosterRepository; 
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_details);
		
		JobDataContract job = GetJob();
		
		if(job != null)
		{
			_subtitle.setText( UIHelper.FormatLongDateTime(job.ScheduledStartTime));
			_title.setText(job.Client.FirstName + " " + job.Client.LastName);
		}
	}
	
	public void onHomeClick(View view)
	{
		UIHelper.GoHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		
	}
	
	private JobDataContract GetJob()
	{
		JobDataContract result = null;
		
		try {
			
			_rosterRepository.open();
			RosterDataContract roster = _rosterRepository.fetchRosterFor(new Date(_rosterTime));
			
			for(int i = 0; i < roster.Jobs.size(); i++ )
			{
				if( Long.parseLong( roster.Jobs.get(i).JobId ) == _jobId )
				{
					result = roster.Jobs.get(i);
					break;
				}
			}
			
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Can not find job", "Can not find job " + _jobId );
			Log.e("LEHMS", "Can not find job " + _jobId);
		}
		finally
		{
			_rosterRepository.close();
		}
		
		return result;
	}
	
}
