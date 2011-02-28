package com.lehms;

import java.util.Date;

import com.google.inject.Inject;
import com.lehms.controls.*;
import com.lehms.messages.dataContracts.*;
import com.lehms.persistence.IRosterRepository;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class JobDetailsActivity extends RoboActivity {

	public final static String JOB_ID = "job_id";
	public final static String ROSTER_DATE = "ROSTER_DATE";
	public final static int BEGIN_JOB_ACTION = 1;
	public final static int END_JOB_ACTION = 2;
	
	@InjectExtra(ROSTER_DATE) long _rosterTime;
	@InjectExtra(JOB_ID) long _jobId;
	@InjectView(R.id.activity_job_details_title) TextView _title;
	@InjectView(R.id.activity_job_details_sub_title) TextView _subtitle;
	@InjectView(R.id.activity_job_details_sub_title2) TextView _subtitle2;
	@InjectView(R.id.activity_job_details_address_value) TextView _address;
	@InjectView(R.id.activity_job_details_date_of_birth_value) TextView _dateOfBirth;
	@InjectView(R.id.activity_job_details_identifier_value) TextView _identifier;
	@InjectView(R.id.activity_job_details_phone_value) TextView _phone;

	@InjectView(R.id.activity_job_details_client_details_container) LinearLayout _clientDetailsContainer;
	@InjectView(R.id.activity_job_details_service_type_value) TextView _serviceType;
	@InjectView(R.id.activity_job_details_service_details_value) TextView _serviceDetails;
	@InjectView(R.id.activity_job_details_duration_value) TextView _duration;
	@InjectView(R.id.activity_job_details_notes_value) TextView _notes;
	@InjectView(R.id.activity_job_details_begin_time_value) TextView _beginTime;
	@InjectView(R.id.activity_job_details_end_time_value) TextView _endTime;
	@InjectView(R.id.activity_job_details_status_value) TextView _status;
	@InjectView(R.id.activity_job_details_client_details_expander) ImageButton _clientDetailsExpander;

	@InjectView(R.id.activity_job_details_begin_job) Button _btnBeginJob;
	@InjectView(R.id.activity_job_details_end_job) Button _btnEndJob;
	@InjectView(R.id.activity_job_details_progress_notes) Button _btnProgressNotes;
	@InjectView(R.id.activity_job_details_complete_forms) Button _btnCompleteForms;

	
	@Inject IRosterRepository _rosterRepository; 
	
	private RosterDataContract _roster = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_details);
		
		JobDetailsDataContract job = GetJob();
		
		if(job != null)
			LoadJob(job);
		
		//CreateQuickActions();
	}
	
	private void LoadJob(JobDetailsDataContract job)
	{
		_subtitle.setText( UIHelper.FormatLongDate(job.ScheduledStartTime));
		_subtitle2.setText( UIHelper.FormatTime(job.ScheduledStartTime));
		_title.setText(job.Client.FirstName + " " + job.Client.LastName);
		
		_address.setText( UIHelper.FormatAddress(job.Client.Address) );
		if( job.Client.DateOfBirth != null )
			_dateOfBirth.setText(UIHelper.FormatLongDate(job.Client.DateOfBirth));
		_identifier.setText(job.Client.ClientId);
		if( job.Client.Phone != null )
			_phone.setText(job.Client.Phone);

		_serviceType.setText( job.Type );
		_duration.setText(job.ExtendOfService + " " + job.UnitOfMeasure);
		_notes.setText( job.Description );
		_serviceDetails.setText( job.Comments );
		if( job.StartTime != null )
			_beginTime.setText( UIHelper.FormatTime( job.StartTime ));
		if( job.EndTime != null )
			_endTime.setText( UIHelper.FormatTime( job.EndTime ));
		
		_status.setText( job.StatusEnum().name() );
		
		switch(job.StatusEnum())
		{
		case Finished:
			_status.setTextColor(Color.rgb(110,23,0));
			_btnBeginJob.setVisibility(View.GONE);
			_btnEndJob.setVisibility(View.GONE);
			break;
		case Pending:
			_status.setTextColor(Color.rgb(0,88,167));
			_btnBeginJob.setVisibility(View.VISIBLE);
			_btnEndJob.setVisibility(View.GONE);
			break;
		case Started:
			_status.setTextColor(Color.rgb(0,110,40));
			_btnBeginJob.setVisibility(View.GONE);
			_btnEndJob.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	public void onHomeClick(View view)
	{
		UIHelper.GoHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		
	}
	
	public void onBeginJobClick(View view)
	{
		ShowKilometersTravelledDialog(JobDetailsActivity.BEGIN_JOB_ACTION);
	}

	public void onEndJobClick(View view)
	{
		ShowKilometersTravelledDialog(JobDetailsActivity.END_JOB_ACTION);
	}
	
	public void onClientDetailsExpanderClick(View view)
	{
		ToggleClientDetailsVisibility();
	}
	
	private void ToggleClientDetailsVisibility()
	{
		if(_clientDetailsContainer.getVisibility() == View.GONE)
		{
			_clientDetailsContainer.setVisibility(View.VISIBLE);
			_clientDetailsExpander.setImageResource(R.drawable.expander_ic_maximized);
			
			AnimationSet set = new AnimationSet(true);

			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(200);
			set.addAnimation(animation);

			animation = new ScaleAnimation(1, 1, 0, 1);
			animation.setDuration(200);
			set.addAnimation(animation);

			//animation = new TranslateAnimation(
			//	      Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
			//	      Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
			//	  );

			_clientDetailsContainer.startAnimation(set);
		}
		else
		{
			_clientDetailsContainer.setVisibility(View.GONE);
			_clientDetailsExpander.setImageResource(R.drawable.expander_ic_minimized);
		}
	}
	
	private void KilometersTravelledDialogResult(int resultType, float kilometersTravelled)
	{
		if(resultType == JobDetailsActivity.END_JOB_ACTION)
			EndJob(kilometersTravelled);
		else
			BeginJob(kilometersTravelled);
	}


	private int _resultTypeInternal;

	private void ShowKilometersTravelledDialog(int resultType)
	{
		_resultTypeInternal = resultType;
		
        LayoutInflater factory = LayoutInflater.from(this);
        final View navigationLocationDialogView = factory.inflate(R.layout.kilometers_travelled_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Enter the location you are traveling to:")
            .setView(navigationLocationDialogView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	EditText killometers = (EditText)navigationLocationDialogView.findViewById(R.id.killometers_travelled_value);
                	try
                	{
                    	float kilometersTravelled = Float.parseFloat( killometers.getText().toString() );
                    	KilometersTravelledDialogResult(_resultTypeInternal, kilometersTravelled);
                	} catch (Exception ex)
                	{
                		UIHelper.ShowAlertDialog(JobDetailsActivity.this, "Error starting job", "Could not stary job: " + ex.getMessage());
                	}
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })
            .create();
		
        dialog.show();

	}

	
	public void BeginJob(float kilometersTravelled)
	{
		try {
			//TODO: get the result and do something with it;

			_rosterRepository.open();

			JobDetailsDataContract job = GetJob(); 
			job.Start(kilometersTravelled);
			
			_rosterRepository.saveRoster(GetRoster());

			LoadJob(job);

			Toast toast = Toast.makeText(this, "Job Started", Toast.LENGTH_SHORT);
			toast.show();

		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Job not started", "Job not started: " + e.getMessage() );
		} finally {
			_rosterRepository.close();
		}
	}
	
	private void EndJob(float kilometersTravelled)
	{
		try {
			_rosterRepository.open();

			JobDetailsDataContract job = GetJob(); 
			job.End(kilometersTravelled);
		
			_rosterRepository.saveRoster(GetRoster());

			LoadJob(job);

			Toast toast = Toast.makeText(this, "Job Finished", Toast.LENGTH_SHORT);
			toast.show();

		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Job not finished", "Job not finished: " + e.getMessage() );
		} finally {
			_rosterRepository.close();
		}		
	}
	
	public void onProgressNotesClick()
	{
		
	}

	public void onCompleteFormsClick()
	{
		
	}

	private RosterDataContract GetRoster()
	{
		if( _roster == null )
		{
			try {
				
				_rosterRepository.open();
				_roster = _rosterRepository.fetchRosterFor(new Date(_rosterTime));
				
			} catch (Exception e) {
				UIHelper.ShowAlertDialog(this, "Can not find roster", "Can not roster" + UIHelper.FormatShortDate(new Date(_rosterTime)) );
				Log.e("LEHMS", "Can not find roster " + UIHelper.FormatShortDate(new Date(_rosterTime)));
			}
			finally
			{
				_rosterRepository.close();
			}
		}
		return _roster;
	}
	
	private JobDetailsDataContract GetJob()
	{
		RosterDataContract roster = GetRoster();
		JobDetailsDataContract result = null;
		
		for(int i = 0; i < roster.Jobs.size(); i++ )
		{
			if( Long.parseLong( roster.Jobs.get(i).JobId ) == _jobId )
			{
				result = roster.Jobs.get(i);
				break;
			}
		}

		if( result == null )
		{
			UIHelper.ShowAlertDialog(this, "Can not find job", "Can not find job " + _jobId );
			Log.e("LEHMS", "Can not find job " + _jobId);
		}
		
		return result;
	}
	
	private void CreateQuickActions()
	{
		final ActionItem first = new ActionItem();
		
		first.setTitle("Dashboard");
		first.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_dashboard));
		first.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(JobDetailsActivity.this, "Dashboard" , Toast.LENGTH_SHORT).show();
			}
		});
		
		final ActionItem second = new ActionItem();
		
		second.setTitle("Users & Groups");
		//second.setIcon(getResources().getDrawable(R.drawable.kontak));
		second.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(JobDetailsActivity.this, "User & Group", Toast.LENGTH_SHORT).show();
			}
		});
		
		Button btn1 = (Button) this.findViewById(R.id.activity_job_details_begin_job);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuickAction qa = new QuickAction(v);
				
				qa.addActionItem(first);
				qa.addActionItem(second);
				
				qa.show();
			}
		});
	}
	
}
