package com.lehms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.adapters.ProgressNoteAdapter;
import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import roboguice.activity.RoboActivity;
import roboguice.inject.*;;


public class ProgressNoteDetailsActivity extends RoboActivity {

	public static final String EXTRA_PROGRESS_NOTE_ID = "progress_note_id";
	
	@InjectExtra(EXTRA_PROGRESS_NOTE_ID) String _progressNoteId; 

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

	@Inject IProgressNoteResource _progressNoteResource;
	
	private MediaPlayer _mediaPlayer = new MediaPlayer();
	private ProgressNoteDataContract _progressNote;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_note_details);
        
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
		
	}
	
	public void onPlayClick(View view)
	{
		try {
			_mediaPlayer.setDataSource(_progressNote.VoiceMemoUri);
			_mediaPlayer.prepare(); 
			_mediaPlayer.start();
			
			_playButton.setVisibility(View.GONE);
			_stopButton.setVisibility(View.VISIBLE);
			
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Error trying to play memo", "The audio record for this progress not is corrupt. " + e.getMessage());
		} 
	}
	
	public void onStopClick(View view)
	{
		_mediaPlayer.stop();
		_playButton.setVisibility(View.VISIBLE);
		_stopButton.setVisibility(View.GONE);
	}

	public void onSaveClick(View view)
	{
		
	}

	public void onCancelClick(View view)
	{
		
	}

	public Boolean isViewingProgressNote()
	{
		return _progressNoteId != null && ! _progressNoteId.equals(""); 
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
			
			ProgressNoteDataContract result = null;
			
			try {
				result = _progressNoteResource.Get(UUID.fromString(_progressNoteId));
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
