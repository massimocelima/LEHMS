package com.lehms;

import roboguice.activity.RoboActivity;
import roboguice.inject.*;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.lehms.IoC.ContainerFactory;
import com.lehms.messages.LoginResponse;
import com.lehms.service.*;
import com.lehms.service.implementation.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends RoboActivity {
	
	@InjectView(R.id.login_username) protected EditText _usernameEditText;
	@InjectView(R.id.login_password) protected EditText _passwordEditText;

    @Inject protected IAuthenticationService _authenticationService;
    @Inject protected IIdentityProvider _identityProvider;
    @Inject protected IProfileProvider _profileProvider;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        _usernameEditText.setText("Test1.t");
        _passwordEditText.setText("utz6");
        
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
            _progressDialog.setCancelable(false);
    		
            _progressDialog.show();
    	}
    	
		@Override
		protected LoginResponse doInBackground(String... arg0) {
			try {
				return _authenticationService.Login(arg0[0], arg0[1]);
			} catch (Exception e) {
				Log.e("LEHMS", e.getMessage());
				_exception = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(LoginResponse result) {
			super.onPostExecute(result);
			if( result == null )
			{
				_progressDialog.dismiss();
				createDialog("Error", "Couldn't establish a connection: " + _exception.getMessage());
			}
			else if( result.IsAuthenticated )
			{
				try {
					_identityProvider.setCurrent(result.User);
					_progressDialog.dismiss();
					
		            // Here we start the next activity, and then call finish()
		            // so that our own will stop running and be removed from the history stack.
		            Intent intent = new Intent(_context, Dashboard.class);
		            //intent.setClass(LoginActivity.this, Dashboard.class);
		            _context.startActivity(intent);
		            _context.finish();
					
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

}