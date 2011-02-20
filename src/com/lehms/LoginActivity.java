package com.lehms;

import roboguice.activity.RoboActivity;

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
	
	public ProgressDialog _progressDialog;
    private EditText _usernameEditText;
    private EditText _passwordEditText;

    @Inject protected IAuthenticationService _authenticationService;
    @Inject protected IIdentityProvider _identityProvider;
    @Inject protected IProfileProvider _profileProvider;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
                
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setMessage("Please wait...");
        _progressDialog.setIndeterminate(true);
        _progressDialog.setCancelable(false);
        
        _usernameEditText = (EditText) findViewById(R.id.login_username);
        _passwordEditText = (EditText) findViewById(R.id.login_password);
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
    	
    	public LoginTask(Activity context)
    	{
    		_context = context;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
		@Override
		protected LoginResponse doInBackground(String... arg0) {
			try {
				Thread.sleep(1000);
				return _authenticationService.Login(arg0[0], arg0[1]);
			} catch (Exception e) {
				Log.e("LEHMS", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(LoginResponse result) {
			super.onPostExecute(result);
			if( result == null )
			{
				_progressDialog.dismiss();
				createDialog("Error", "Couldn't establish a connection");
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
    
    /*
    
    private void doLogin(String username, String password)
    {
        Thread t = new Thread() {
            public void run() {
            	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	runOnUiThread(
            			new Runnable() {     
            				public void run() {         
            					_progressDialog.dismiss();
            					createDialog("Error", "Couldn't establish a connection");
            				} 
            			});
            	
                //Intent i = new Intent();
                //i.putExtra("userid", userID);
                //quit(true,i);
            	
                //Looper.prepare();
                //DefaultHttpClient client = new DefaultHttpClient(); 
                //HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);                 
                //HttpResponse response;
                //HttpEntity entity;         
                //try {
                //    HttpPost post = new HttpPost(UPDATE_URL);
                //    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
                //    nvps.add(new BasicNameValuePair("username", login));
                //    nvps.add(new BasicNameValuePair("password", pw));
                //    post.setHeader("Content-Type", "application/x-www-form-urlencoded");
                //    post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                //    response = client.execute(post);
                //    entity = response.getEntity();
                //    InputStream is = entity.getContent();
                //    read(is);
                //    is.close();
                //    if (entity != null) entity.consumeContent();
                //} catch (Exception e) {
                //    _progressDialog.dismiss();
                //    createDialog("Error", "Couldn't establish a connection");
                //}
                //Looper.loop();                
            }
        };
        t.start();
    }
        */

}