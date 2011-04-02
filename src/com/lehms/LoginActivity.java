package com.lehms;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import roboguice.activity.RoboActivity;
import roboguice.inject.*;

import com.google.inject.Inject;
import com.lehms.domain.ProgressNote;
import com.lehms.messages.LoginResponse;
import com.lehms.service.DataSyncService;
import com.lehms.serviceInterface.*;
import com.lehms.util.AppLog;
import com.lehms.util.StreamExtentions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends RoboActivity {
	
	@InjectView(R.id.login_username_edit) protected EditText _usernameEditText;
	@InjectView(R.id.login_password_edit) protected EditText _passwordEditText;
	@InjectView(R.id.version_label) protected TextView _vaersonLabel;

		
    @Inject protected IAuthenticationProvider _authenticationService;
    @Inject protected IIdentityProvider _identityProvider;
    @Inject protected IProfileProvider _profileProvider;
    @Inject protected IApkResource _apkResource;
    @Inject protected DataSyncService _dataSyncService;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
                
        //_usernameEditText.setText("Test1.t");
        //_passwordEditText.setText("utz6");
        _usernameEditText.setText("claude.r");
        _passwordEditText.setText("claude.r");
        
        _vaersonLabel.setText("Version " + UIHelper.getVersionNumber(this));
        
        /*
        ProgressNote note = new ProgressNote(this);
        note.AttachmentId = UUID.randomUUID();
        note.Note = "Test";
        note.Subject = "Hello World";
        note.CreatedDate = new Date();
        note.Identity = UUID.randomUUID();
        note.save();
        
        long id = note.getId();
        
        ProgressNote note2 = ProgressNote.load(this, ProgressNote.class, id);
        long id2 = note2.getId();
        */
    }

    public void OnLoginClick(View view)
    {
        int usersize = _usernameEditText.getText().length();
        int passsize = _passwordEditText.getText().length();
        if(usersize > 0 && passsize > 0) {
           
            LoginTask loginTask = new LoginTask(this);
            loginTask.execute(
            		_usernameEditText.getText().toString(), 
            		_passwordEditText.getText().toString());
            
        } 
        else 
        	createDialog("Error","Please enter Username and Password");
    }
    
    public void OnCancelClick(View view) {
        setResult(-1);
        finish();
    }

    public void onSettingsClick(View view)
    {
        Intent i = new Intent(this, ApplicationSettings.class);
        startActivity(i);
    }

    public void onHomeClick(View view)
    {
    	
    }
    
    private void createDialog(String title, String text) {
        AlertDialog ad = new AlertDialog.Builder(this)
        	.setPositiveButton("Ok", null)
        	.setTitle(title)
        	.setMessage(text)
        	.create();
        ad.show();
    }
    
    
    private class LoginTask extends AsyncTask<String, Integer, LoginResponse>
    {
    	private Activity _context;
    	public ProgressDialog _progressDialog;
    	private Exception _exception;

    	public LoginTask(Activity context)
    	{
    		_context = context;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
            _progressDialog = new ProgressDialog(_context);
            _progressDialog.setMessage("Please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(true);
    	
            _progressDialog.setOnCancelListener(new OnCancelListener() 
            {             
            	@Override             
            	public void onCancel(DialogInterface dialog) {                 
            		cancel(true);             
            	}         
            });
            
            _progressDialog.show();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		super.onCancelled();
    	}

    	private Boolean _updateRequired = false;
    	
		@Override
		protected LoginResponse doInBackground(String... arg0) {
			try {
				LoginResponse response = _authenticationService.Login(arg0[0], arg0[1]);
				
				if( response.IsAuthenticated )
				{
					_identityProvider.setCurrent(response.User);
					String version = UIHelper.getVersionNumber(_context);

					_updateRequired = ! (_apkResource.GetCurrentVersion().Version.equals(version));
				}

				return response;

			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(LoginResponse result) {
			super.onPostExecute(result);
			
			if(isCancelled())
				return;
			
			if( result == null )
			{
				_progressDialog.dismiss();
				createDialog("Error", "Couldn't establish a connection: " + _exception.getMessage());
			}
			else if( result.IsAuthenticated )
			{
				try {
					//_identityProvider.setCurrent(result.User);
					_progressDialog.dismiss();

					if(_updateRequired)
					{
						AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
				        .setTitle("Update Required")
				        .setMessage("A new version of the application has been found and needs to be installed. Click OK to download the update or cancel to quit the application.")
				        .setPositiveButton("OK", new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								DownloadFile downloadFile = new DownloadFile(); 
								downloadFile.execute(null); 
							}
						})
						.setNegativeButton("Cancel", new OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								LoginActivity.this.finish();
							}})
				        .create();
						dialog.show();
					}
					else
					{
			            // Here we start the next activity, and then call finish()
			            // so that our own will stop running and be removed from the history stack.
			            Intent intent = new Intent(_context, Dashboard.class);
			            _context.startActivity(intent);
			            _context.finish();
					}
					
					
				} catch (Exception e) {
					_progressDialog.dismiss();
					createDialog("Login Failed", "Unable to parse responce from service.");
					return;
				}
			}
			else
			{
				_progressDialog.dismiss();
				createDialog("Login Failed", "Username or password is incorrect, please try again.");
			}
		}
		
    }
    
    private class DownloadFile extends AsyncTask<Void, Integer, Boolean>{     
    	
    	private final static String APK_FILE_NAME = "LEHMS V2.apk";
    	private Exception _exception;
    	private ProgressDialog _progressDialog;
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		
			_progressDialog = new ProgressDialog(LoginActivity.this); 
			_progressDialog.setMessage("Synchronising pending updates..."); 
			_progressDialog.setIndeterminate(false); 
			_progressDialog.setMax(100); 
			_progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			_progressDialog.setCancelable(true);
			_progressDialog.show();
			
            _progressDialog.setOnCancelListener(new OnCancelListener() 
            {             
            	@Override             
            	public void onCancel(DialogInterface dialog) {                 
            		cancel(true);             
            	}         
            });
    	}
    	
    	
    	@Override
    	protected void onCancelled() {
    		super.onCancelled();
    	}
    	
    	@Override     
    	protected Boolean doInBackground(Void... v) {         
    		int count;         
    		try {
    			
    			_dataSyncService.RunSynch();

				publishProgress(0);                 

    			ContentInputStream inputStream = _apkResource.GetUpdate(UIHelper.getVersionNumber(LoginActivity.this));
				FileOutputStream outStream = openFileOutput(APK_FILE_NAME, Context.MODE_WORLD_READABLE);

    			// this will be useful so that you can show a tipical 0-100% progress bar             
    			int lenghtOfFile = (int)inputStream.length;
    			_progressDialog.setMax(lenghtOfFile);

    			// downlod the file             
    			InputStream input = new BufferedInputStream(inputStream.InputStream);
    			byte data[] = new byte[1024];              
    			long total = 0;              
    			while ((count = input.read(data)) != -1) {                 
    				total += count;                 
    				// publishing the progress....                 
    				//publishProgress((int)(total*100/lenghtOfFile));
    				publishProgress((int)total);
    				outStream.write(data, 0, count);
    			}              
    			outStream.flush();      
    			outStream.close();
    			input.close();
    		} catch (Exception e) 
    		{
    			_exception = e;
    			return false;
    		}
    		
    		return true;     
    	}
    	
        @Override     
        protected void onProgressUpdate(Integer[] values) 
        {
			_progressDialog.setMessage("Donwloading Update...");
        	_progressDialog.setProgress(values[0]);
        };
    	
    	@Override
    	protected void onPostExecute(Boolean result) {

    		super.onPostExecute(result);
    		
    		if(! result )
    		{
    			UIHelper.ShowAlertDialog(LoginActivity.this, "Error downloding update", "Error downloding update: " + _exception);
    			AppLog.error("Error downloding update", _exception);
    		}
    		else
    		{
				// create and post an intent to install
				Intent intent = new Intent();
	
				String fileAbsPath = "file://" + getFilesDir().getAbsolutePath() + "/" + APK_FILE_NAME; 
	
				intent.setAction(android.content.Intent.ACTION_VIEW); 
				intent.setDataAndType(Uri.parse(fileAbsPath), "application/vnd.android.package-archive"); 
				startActivity(intent);
	            LoginActivity.this.finish();
    		}
    		
    		_progressDialog.dismiss();
    		
    	}
    }

}