package com.lehms;

import java.util.Date;

import com.google.inject.Inject;
import com.lehms.controls.*;
import com.lehms.messages.JobEndActionRequest;
import com.lehms.messages.JobStartActionRequest;
import com.lehms.messages.dataContracts.*;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.persistence.IRosterRepository;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IDutyManager;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IOfficeContactProvider;
import com.lehms.serviceInterface.ITracker;
import com.lehms.util.AppLog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.OnFinished;
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

public class JobDetailsActivity extends LehmsRoboActivity implements ISaveEventResultHandler  {

	public final static String EXTRA_JOB_ID = "job_id";
	public final static String EXTRA_ROSTER_DATE = "ROSTER_DATE";
	
	public final static int BEGIN_JOB_ACTION = 1;
	public final static int END_JOB_ACTION = 2;
	
	@InjectExtra(EXTRA_ROSTER_DATE) long _rosterTime;
	@InjectExtra(EXTRA_JOB_ID) long _jobId;
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
	@InjectView(R.id.activity_job_details_begin_time_km_value) TextView _beginTimeKm;
	@InjectView(R.id.activity_job_details_begin_time_container) View _beginTimeContainer;
	@InjectView(R.id.activity_job_details_end_time_value) TextView _endTime;
	@InjectView(R.id.activity_job_details_end_time_km_value) TextView _endTimeKm;
	@InjectView(R.id.activity_job_details_end_time_container) View _endTimeContainer;
	
	
	
	@InjectView(R.id.activity_job_details_status_value) TextView _status;
	@InjectView(R.id.activity_job_details_client_details_expander) ImageButton _clientDetailsExpander;

	@InjectView(R.id.activity_job_details_begin_job) Button _btnBeginJob;
	@InjectView(R.id.activity_job_details_end_job) Button _btnEndJob;
	@InjectView(R.id.activity_job_details_view) Button _btnView;
	@InjectView(R.id.activity_job_details_progress_notes) Button _btnProgressNote;
	@InjectView(R.id.activity_job_details_forms) Button _btnForms;
	
	@Inject IRosterRepository _rosterRepository; 
	@Inject IActiveJobProvider _activeJobProvider;
	@Inject IEventRepository _eventRepository;
	@Inject IEventExecuter _eventExecuter;
	@Inject IEventFactory _eventEventFactory;
	@Inject IDutyManager _dutyManager;
	@Inject IOfficeContactProvider _officeContactProvider;
	@Inject protected ITracker _tracker;

	private QuickAction _quickActions;
	private QuickAction _quickActionsPrgressNotes;
	
	private RosterDataContract _roster = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_details);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_JOB_ID) != null)
			_jobId = savedInstanceState.getLong(EXTRA_JOB_ID);
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_ROSTER_DATE) != null)
			_rosterTime = savedInstanceState.getLong(EXTRA_ROSTER_DATE);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_ROSTER_DATE) != null )
			_rosterTime = savedInstanceState.getLong(EXTRA_ROSTER_DATE);
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_JOB_ID) != null )
			_jobId = savedInstanceState.getLong(EXTRA_JOB_ID);
	
		JobDetailsDataContract job = null;
		job = GetJob();

		if(job != null)
		{
			LoadJob(job);
			_activeJobProvider.set(job);
		}
		CreateQuickActions();
	}
	

    @Override 
    protected void onSaveInstanceState(Bundle outState) { 
        super.onSaveInstanceState(outState); 
        outState.putSerializable(EXTRA_JOB_ID, _jobId); 
        outState.putSerializable(EXTRA_ROSTER_DATE, _rosterTime); 
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
		{
			_beginTime.setText( UIHelper.FormatTime( job.StartTime ));
			_beginTimeKm.setText( job.TKLM + " Km" );
			_beginTimeContainer.setVisibility(View.VISIBLE);
		}
		if( job.EndTime != null )
		{
			_endTime.setText( UIHelper.FormatTime( job.EndTime ));
			_endTimeKm.setText( job.SKLM + " Km" );
			_endTimeContainer.setVisibility(View.VISIBLE);
		}
		
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
		NavigationHelper.goHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		
	}
	
	public void onEmergencyClick()
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onBeginJobClick(View view)
	{
        LayoutInflater factory = LayoutInflater.from(this);
        final View navigationLocationDialogView = factory.inflate(R.layout.kilometers_travelled_dialog, null);
        
    	navigationLocationDialogView.findViewById(R.id.killometers_travelled_text_end).setVisibility(View.GONE);
    	navigationLocationDialogView.findViewById(R.id.killometers_travelled_text_start).setVisibility(View.VISIBLE);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Enter the kilometers you have traveled:")
            .setView(navigationLocationDialogView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                	EditText killometers = (EditText)navigationLocationDialogView.findViewById(R.id.killometers_travelled_value);
                	if(killometers.getText().toString().equals(""))
                	{
                		UIHelper.ShowAlertDialog(JobDetailsActivity.this, 
                				"Validation Error", "Please enter the number of kilometers travelled");
                		dialog.cancel();
                	}
                	else
                	{
	                	try
	                	{
	                    	float kilometersTravelled = Float.parseFloat( killometers.getText().toString() );
	                    	BeginJob(kilometersTravelled);
	                	} catch (Exception ex)
	                	{
	                		UIHelper.ShowAlertDialog(JobDetailsActivity.this, "Error starting job", "Could not start job: " + ex.getMessage());
	                	}
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

	public void onEndJobClick(View view)
	{
        LayoutInflater factory = LayoutInflater.from(this);
        final View navigationLocationDialogView = factory.inflate(R.layout.kilometers_travelled_dialog, null);

        navigationLocationDialogView.findViewById(R.id.killometers_travelled_text_end).setVisibility(View.VISIBLE);
    	navigationLocationDialogView.findViewById(R.id.killometers_travelled_text_start).setVisibility(View.GONE);
    	
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Enter the kilometers you have traveled:")
            .setView(navigationLocationDialogView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                	EditText killometers = (EditText)navigationLocationDialogView.findViewById(R.id.killometers_travelled_value);
                	if(killometers.getText().toString().equals(""))
                	{
                		UIHelper.ShowAlertDialog(JobDetailsActivity.this, 
                				"Validation Error", "Please enter the number of kilometers travelled");
                		dialog.cancel();
                	}
                	else
                	{
	                	try
	                	{
	                    	float kilometersTravelled = Float.parseFloat( killometers.getText().toString() );
	                    	EndJob(kilometersTravelled);
	                	} catch (Exception ex)
	                	{
	                		UIHelper.ShowAlertDialog(JobDetailsActivity.this, "Error starting job", "Could not stary job: " + ex.getMessage());
	                	}
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
	
	public void onClientDetailsExpanderClick(View view)
	{
		ToggleClientDetailsVisibility();
	}

	public void onFormsClick(View view)
	{
		NavigationHelper.goForms(this, GetJob().Client);
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
	
	public void BeginJob(float kilometersTravelled)
	{
		try {
			_rosterRepository.open();

			JobDetailsDataContract job = GetJob(); 
			job.Start(kilometersTravelled);

			float autoDistance = _tracker.endMeasuringDistance();
			_tracker.beginMeasuringDistance();

			JobStartActionRequest request = new JobStartActionRequest();
			request.KilometersTravelled = (float) kilometersTravelled;
			request.KilometersTravelledFromGps = autoDistance;
			request.JobId = job.JobId;
			request.ActionDate = new Date();
			
			Event event = _eventEventFactory.create(request, EventType.JobStarted);

			UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, this.getTitle().toString(), false);

			if(!_dutyManager.IsOnDuty())
				_dutyManager.OnDuty();
			
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
			
			float autoDistance = _tracker.endMeasuringDistance();
			_tracker.beginMeasuringDistance();

			JobEndActionRequest request = new JobEndActionRequest();
			request.KilometersTravelled = (float) kilometersTravelled;
			request.KilometersTravelledFromGps = autoDistance;
			request.JobId = job.JobId;
			request.ActionDate = new Date();
			
			Event event = _eventEventFactory.create(request, EventType.JobCompleted);

			UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, this.getTitle().toString(), false);
			
			_rosterRepository.saveRoster(GetRoster());

			LoadJob(job);
			
			NavigationHelper.goJobComplete(this, job);

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
				//AppLog.error("Can not find roster " + UIHelper.FormatShortDate(new Date(_rosterTime)));
			}
			finally
			{
				try { _rosterRepository.close(); } catch(Exception ex) {}
			}
		}
		return _roster;
	}
	
	private JobDetailsDataContract GetJob()
	{
		JobDetailsDataContract result = null;
		RosterDataContract roster = null;
try
{
			roster = GetRoster();			
}
catch(Exception ex)
{
	UIHelper.ShowAlertDialog(this, "Error", "Error getting roster: " + ex.getMessage());
}

if(roster == null )
{
	UIHelper.ShowAlertDialog(this, "Error", "Error roster is null");
	return null;
}
	

		for(int i = 0; i < roster.Jobs.size(); i++ )
		{
			if( Long.parseLong( roster.Jobs.get(i).JobId ) == _jobId )
			{
try
{
				result = roster.Jobs.get(i);
}
catch(Exception ex)
{
	UIHelper.ShowAlertDialog(this, "Error", "Error getting job from roster: " + ex.getMessage());
}

				break;
			}
		}

		if( result == null )
		{
			UIHelper.ShowAlertDialog(this, "Can not find job", "Can not find job " + _jobId );
			AppLog.error("Can not find job " + _jobId);
		}
		
		return result;
	}
	
	private void CreateQuickActions()
	{
		
		final ActionItem qaProgressNotes = new ActionItem();
		
		qaProgressNotes.setTitle("View Progress Notes");
		qaProgressNotes.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_progress_notes));
		qaProgressNotes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NavigationHelper.viewProgressNotes(
						JobDetailsActivity.this, 
						Long.parseLong( GetJob().Client.ClientId ), 
						GetJob().Client.FirstName + " " + GetJob().Client.LastName); 
				_quickActionsPrgressNotes.dismiss();
			}
		});

		final ActionItem qaProgressNoteNew = new ActionItem();
		
		qaProgressNoteNew.setTitle("New Progress Note");
		qaProgressNoteNew.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_progress_notes_new));
		qaProgressNoteNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NavigationHelper.createProgressNote(JobDetailsActivity.this, 
						Long.parseLong( GetJob().Client.ClientId ), 
						GetJob().Client.FirstName + " " + GetJob().Client.LastName); 
				_quickActionsPrgressNotes.dismiss();
			}
		});

		
		
		final ActionItem qaClient = new ActionItem();
		
		qaClient.setTitle("View Client");
		qaClient.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_client));
		qaClient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if( ! UIHelper.IsOnline(JobDetailsActivity.this))
				{
					UIHelper.ShowAlertDialog(JobDetailsActivity.this, 
							"Unable to connect to server", 
							"You must be connected to the internet in order to be able to access this information.");
				}
				else
				{
					NavigationHelper.openClient(JobDetailsActivity.this, Long.parseLong( JobDetailsActivity.this.GetJob().Client.ClientId));
				}
				_quickActions.dismiss();
			}
		});

		final ActionItem qaTakeAPicture = new ActionItem();
		
		qaTakeAPicture.setTitle("Take A Picture");
		qaTakeAPicture.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_camera));
		qaTakeAPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientSummaryDataContract client = new ClientSummaryDataContract();
				client.ClientId = GetJob().Client.ClientId;
				client.FirstName = GetJob().Client.FirstName;
				client.LastName = GetJob().Client.LastName;
				NavigationHelper.goTakePicture(JobDetailsActivity.this, client);
				_quickActions.dismiss();
			}
		});
		
		final ActionItem qaClinicalDetails = new ActionItem();
		
		qaClinicalDetails.setTitle("Clinical Details");
		qaClinicalDetails.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_nurse));
		qaClinicalDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NavigationHelper.goCliniclaDetails(JobDetailsActivity.this, GetJob().Client);
				_quickActions.dismiss();
			}
		});

		final ActionItem qaContact = new ActionItem();
		
		qaContact.setTitle("Contact ...");
		qaContact.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_contact));
		qaContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				_quickActions.dismiss();

				AlertDialog dialog = new AlertDialog.Builder(JobDetailsActivity.this)
		        .setTitle("Please select how you would like to contact the office")
		        .setItems(R.array.job_contact_selection, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	switch(which)
		            	{
		            	case 0:
		            		UIHelper.MakeCall( GetJob().Client.Phone, JobDetailsActivity.this);
		            		break;
		            	case 1:
		            		UIHelper.MakeCall( _officeContactProvider.getOfficePhoneNumber(), JobDetailsActivity.this);
		            		break;
		            	case 2:
		            		UIHelper.OpenCall(JobDetailsActivity.this);
		            		break;
		            	}
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
		});

		final ActionItem qaNavigate = new ActionItem();
		
		qaNavigate.setTitle("Navigate to Client's Home");
		qaNavigate.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_navigate));
		qaNavigate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.LaunchNavigation(GetJob().Client.Address.getAddressNameForGeocoding(), JobDetailsActivity.this);
			}
		});

		_btnView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				_quickActions = new QuickAction(_btnView);
				
				if( isAuthrosied(Permission.ClientDetailsView))
					_quickActions.addActionItem(qaClient);
				if( isAuthrosied(Permission.ClinicalDetailsEdit))
					_quickActions.addActionItem(qaClinicalDetails);
				if( isAuthrosied(Permission.TakePhoto))
					_quickActions.addActionItem(qaTakeAPicture);
				if( isAuthrosied(Permission.Contact))
					_quickActions.addActionItem(qaContact);
				if( isAuthrosied(Permission.Navigate))
					_quickActions.addActionItem(qaNavigate);

				_quickActions.show();

			}
		});
		
		if(isAuthrosied(Permission.ProgressNotesEdit))
		{
			_btnProgressNote.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
	
					_quickActionsPrgressNotes = new QuickAction(_btnProgressNote);
					
					_quickActionsPrgressNotes.addActionItem(qaProgressNotes);
					_quickActionsPrgressNotes.addActionItem(qaProgressNoteNew);
	
					_quickActionsPrgressNotes.show();
	
				}
			});
		}
		else
			_btnProgressNote.setVisibility(View.GONE);
		
		if(!isAuthrosied(Permission.FormsView))
			_btnForms.setVisibility(View.GONE);
	}
	
	@Override
	public void onBackPressed() {
		_activeJobProvider.set(null);
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		_activeJobProvider.set(null);
		super.onDestroy();
	}


	@Override
	public void onSuccess(Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception e) {
		UIHelper.ShowAlertDialog(this, "Error saving job", "Error ssving job: " + e.getMessage());
		AppLog.error("Error ssving job", e);
	}
	

}
