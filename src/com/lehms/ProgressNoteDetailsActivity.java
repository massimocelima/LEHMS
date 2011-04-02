package com.lehms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.CreateProgressNoteRequest;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.util.AppLog;
import com.lehms.media.StreamingMediaPlayer;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;
import roboguice.activity.RoboActivity;
import roboguice.inject.*;;


public class ProgressNoteDetailsActivity extends LehmsRoboActivity implements ISaveEventResultHandler {

	//public static final String EXTRA_PROGRESS_NOTE_ID = "progress_note_id";
	public static final String EXTRA_CLIENT_ID = "client_id";
	public static final String EXTRA_CLIENT_NAME = "client_name";

	public static final String EXTRA_PROGRESS_NOTE = "progress_note";

	//private static final String STATE_PROGRESS_NOTE = "progress_note";
		
	//@InjectExtra(optional=true, value=EXTRA_PROGRESS_NOTE_ID ) String _progressNoteId; 
	@InjectExtra(optional=true, value=EXTRA_CLIENT_ID) Long _clientId; 
	@InjectExtra(optional=true, value=EXTRA_CLIENT_NAME) String _clientName; 

	@InjectExtra(optional=true, value=EXTRA_PROGRESS_NOTE) ProgressNoteDataContract _progressNote; 
	//private ProgressNoteDataContract _progressNote;

	@InjectView(R.id.activity_progress_note_button_bar) View _buttonBarView;
	@InjectView(R.id.activity_progress_note_author_value) TextView _authorTextView;
	@InjectView(R.id.activity_progress_note_note_edit) EditText _noteEditView;
	@InjectView(R.id.activity_progress_note_note_value) TextView _noteTextView;
	@InjectView(R.id.activity_progress_note_subject_edit) EditText _subjectEditView;
	@InjectView(R.id.activity_progress_note_subject_value) TextView _subjectTextView;
	@InjectView(R.id.activity_progress_note_title) TextView _titleTextView;
	@InjectView(R.id.activity_progress_note_sub_title) TextView _subTitleTextView;
	@InjectView(R.id.activity_progress_note_play) Button _playButton;
	@InjectView(R.id.activity_progress_note_stop) Button _stopButton;
	@InjectView(R.id.activity_progress_note_record) Button _recordButton;
	@InjectView(R.id.activity_progress_note_recording_info) TextView _recordingTextView;
	@InjectView(R.id.activity_progress_note_recording_progress_bar) ProgressBar _recordingProgressBar;
	
	
	@Inject IProgressNoteResource _progressNoteResource;
	@Inject IProfileProvider _profileProvider;
	@Inject IIdentityProvider _identityProvider;
	@Inject IDepartmentProvider _departmentProvider;
	@Inject IActiveJobProvider _activeJobProvider;

	@Inject IEventRepository _eventRepository;
	@Inject IEventFactory _eventEventFactory;
	@Inject IEventExecuter _eventExecuter;

	
	private MediaPlayer _mediaPlayer = new MediaPlayer();
	private MediaRecorder _recorder = new MediaRecorder();
	private StreamingMediaPlayer _audioStreamer;
	private Boolean _recordingMessage = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_note_details);
        
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT_ID) != null )
			_clientId = savedInstanceState.getLong(EXTRA_CLIENT_ID);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT_NAME) != null )
			_clientName = savedInstanceState.getString(EXTRA_CLIENT_NAME);

		//if(savedInstanceState != null && savedInstanceState.get(EXTRA_PROGRESS_NOTE_ID) != null )
		//	_progressNoteId = savedInstanceState.getString(EXTRA_PROGRESS_NOTE_ID);

		//if(savedInstanceState != null && savedInstanceState.get(STATE_PROGRESS_NOTE) != null )
		//	_progressNote = (ProgressNoteDataContract)savedInstanceState.get(STATE_PROGRESS_NOTE);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_PROGRESS_NOTE) != null )
			_progressNote = (ProgressNoteDataContract)savedInstanceState.get(EXTRA_PROGRESS_NOTE);

		_playButton.setVisibility(View.GONE);
		_stopButton.setVisibility(View.GONE);

		if( isViewingProgressNote() )
        {
			_noteEditView.setVisibility(View.GONE);
			_subjectEditView.setVisibility(View.GONE);
			
			_noteTextView.setVisibility(View.VISIBLE);
			_subjectTextView.setVisibility(View.VISIBLE);
			
			_buttonBarView.setVisibility(View.GONE);
			_recordButton.setVisibility(View.GONE);

			_recordingProgressBar.setVisibility(View.VISIBLE);

        	if( ! UIHelper.IsOnline(this) )
        	{
        		UIHelper.ShowAlertDialog(this, "No Internet Connection", "A internet connection cound not be esablished.");
        	}
        	else
        	{
	        	LoadProgressNoteTask task = new LoadProgressNoteTask(this);
	        	task.execute();
        	}
        }
		else
		{
			_progressNote = new ProgressNoteDataContract();
			
			_noteEditView.setVisibility(View.VISIBLE);
			_subjectEditView.setVisibility(View.VISIBLE);
			
			_noteTextView.setVisibility(View.GONE);
			_subjectTextView.setVisibility(View.GONE);
			
			_buttonBarView.setVisibility(View.VISIBLE);
			_recordButton.setVisibility(View.VISIBLE);
			
			_recordingProgressBar.setVisibility(View.GONE);
			
			_titleTextView.setText(_clientName);
			_subTitleTextView.setText(_clientId.toString());
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if( isViewingProgressNote())
		{
			outState.putSerializable(EXTRA_PROGRESS_NOTE, _progressNote);
			//outState.putSerializable(EXTRA_PROGRESS_NOTE_ID, _progressNoteId);
		}
		else
		{
			// Need to save the current values from the note
			outState.putSerializable(EXTRA_CLIENT_ID, _clientId);
			outState.putSerializable(EXTRA_CLIENT_NAME, _clientName);
			//outState.putSerializable(STATE_PROGRESS_NOTE, _progressNote);
			outState.putSerializable(EXTRA_PROGRESS_NOTE, _progressNote);
		}
	}
	
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onRecordClick(View view)
	{
		if( _audioStreamer != null )
			_audioStreamer.interrupt();

		try
		{
		    String state = android.os.Environment.getExternalStorageState();
		    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
		    	UIHelper.ShowAlertDialog(this, "Error tring to create file", "SD Card is not mounted.  It is " + state + ".");
		    }
	
	        String path = Environment.getExternalStorageDirectory().getAbsolutePath(); 
	        path += "/lehms/recording/" + _progressNote.Id.toString() +  ".3gp"; 
	        _progressNote.VoiceMemoUri = path;
	
		    // make sure the directory we plan to store the recording in exists
		    File directory = new File(path).getParentFile();
		    if (!directory.exists() && !directory.mkdirs()) {
		    	UIHelper.ShowAlertDialog(this, "Error tring to create file", "Path to file could not be created.");
		    }
	
		    _recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    _recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		    _recorder.setOutputFile(path);
		    _recorder.prepare();
		    _recorder.start();
		    
		    _recordingMessage = true;
		    
		    
			AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Recording Note...")
	        .setMessage("The progress note is being recorded. Press stop to finish recording.")
	        .setPositiveButton("Stop", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					_recorder.stop();
					_playButton.setVisibility(View.VISIBLE);
					_stopButton.setVisibility(View.GONE);
					_recordingProgressBar.setVisibility(View.VISIBLE);
					_recordingMessage = false;
				}
			})
	        .create();
		    
			dialog.show();
		    
		}
		catch(Exception e)
		{
	    	UIHelper.ShowAlertDialog(this, "Error tring to create file", "Unmable to start recording message: " + e.getMessage());
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if( _audioStreamer != null )
			_audioStreamer.interrupt();
		if(_recordingMessage)
			_recorder.stop();
	}
	
	public void onPlayClick(View view)
	{
		try {
			
			// If we are viewing the file and the file store is a external url then run the media streamer
			if( isViewingProgressNote() )
			{
	    		if ( _audioStreamer != null)
	    		{
	    			//Continue stream from where we stoped from
	    			_audioStreamer.getMediaPlayer().start();
	    			_audioStreamer.startPlayProgressUpdater();
	    		}
	    		else
	    		{
					String dataSourceUri = _profileProvider.getProfile().GetProgressNoteRecordingResourceEndPoint() + "/" + _progressNote.AttachmentId;
		    		_audioStreamer = new StreamingMediaPlayer(this,
		    				_identityProvider,
		    				_departmentProvider,
		    				_recordingTextView, 
		    				_recordingProgressBar);
		    		_audioStreamer.startStreaming(dataSourceUri);
	    		}
			}
			else
			{
				_mediaPlayer = new MediaPlayer();
				_mediaPlayer.setDataSource(_progressNote.VoiceMemoUri);    
				_mediaPlayer.prepare();    
				_mediaPlayer.start();
			}

			_playButton.setVisibility(View.GONE);
			_stopButton.setVisibility(View.VISIBLE);

		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Error trying to play memo", "The audio record for this progress not is corrupt. " + e.getMessage());
		}
		
	}
	
	public void onStopClick(View view)
	{
		if( isViewingProgressNote() )
		{
			try
			{
				_audioStreamer.getMediaPlayer().pause();
			} catch(Exception e) {}
		}
		else
		{
			_mediaPlayer.stop();
			_mediaPlayer.release();
		}
		
		_playButton.setVisibility(View.VISIBLE);
		_stopButton.setVisibility(View.GONE);
	}

	public void onSaveClick(View view)
	{
		_eventRepository.open();

		try
		{
			_progressNote.ClientId = _clientId + "";
			_progressNote.ClientName = _clientName;
			_progressNote.CreatedBy = _identityProvider.getCurrent().getCreatedByFormat();
			_progressNote.CreatedDate = new Date();
			_progressNote.Note = _noteEditView.getText().toString();
			_progressNote.Subject = _subjectEditView.getText().toString();
			
			CreateProgressNoteRequest createNoteRequest = new CreateProgressNoteRequest(); 
			createNoteRequest.ProgressNote =_progressNote;
			if(_activeJobProvider.get() != null)
				createNoteRequest.JobId = _activeJobProvider.get().JobId;
			
			Event event = _eventEventFactory.create(createNoteRequest, EventType.ProgressNoteAdded);
			UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, "Progress Note");
		}
		catch(Exception ex)
		{
			AppLog.error(ex.getMessage(), ex);
			UIHelper.ShowAlertDialog(this, "Error saving progress note", "An error has occured while saving the progress note: " + ex.getMessage());
		}
		finally
		{
			_eventRepository.close();
		}
	}

	@Override
	public void onSuccess(Object data) {
		// Returns the progress note to the parent activity
		Intent intent = this.getIntent(); 
		intent.putExtra(EXTRA_PROGRESS_NOTE, (ProgressNoteDataContract)data); 
		setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	public void onError(Exception e) {
		// Do nothing
	}
	
	public void onCancelClick(View view)
	{
		setResult(RESULT_CANCELED);
		this.finish();
	}

	public Boolean isViewingProgressNote()
	{
		return _clientId == null;
		//return _progressNoteId != null && ! _progressNoteId.equals(""); 
	}
	
   private class LoadProgressNoteTask extends AsyncTask<Void, Integer, ProgressNoteDataContract>
   {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Exception _exception;

    	public LoadProgressNoteTask(Activity context)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading progress notes for client...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(true);
            
            _progressDialog.setOnCancelListener(new OnCancelListener() 
            {             
            	@Override             
            	public void onCancel(DialogInterface dialog) {                 
            		cancel(true);             
            	}         
            });
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		super.onCancelled();
    		_context.finish();
    	}
    	
		@Override
		protected ProgressNoteDataContract doInBackground(Void... arg0) {
			
			// More efficiant, progress note is loaded from list of progress notes
			//return _progressNote;
			
			ProgressNoteDataContract result = null;
			
			try {
				result = _progressNoteResource.Get(_progressNote.Id);
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(ProgressNoteDataContract result) {
			super.onPostExecute(result);
			
			if(isCancelled())
				return;
			
			if( result == null )
			{
				if( _exception != null )
					createDialog("Error", "Error retriving progress note: " + _exception.getMessage());
				else
					createDialog("Error", "Error retriving progress note");
			}
			else
			{
				_progressNote = result;
				
				_noteTextView.setText(result.Note);
				_authorTextView.setText(result.CreatedBy);
				_subjectTextView.setText(result.Subject);
				_subTitleTextView.setText( UIHelper.FormatLongDate(result.CreatedDate));
				_titleTextView.setText(result.ClientName);
				
				if( result.hasRecordedMessage() )
					_playButton.setVisibility(View.VISIBLE);
			}
			if( _progressDialog.isShowing() )
				_progressDialog.dismiss();
		}
		
	    private void createDialog(String title, String text) {
	        AlertDialog ad = new AlertDialog.Builder(_context)
	        	.setPositiveButton("Ok", null)
	        	.setTitle(title)
	        	.setMessage(text)
	        	.create();
	        ad.show();
	    }
		
    }
	
}
