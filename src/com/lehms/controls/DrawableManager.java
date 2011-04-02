package com.lehms.controls;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.lehms.FormDetailsListActivity;
import com.lehms.NavigationHelper;
import com.lehms.UIHelper;
import com.lehms.messages.dataContracts.UserDataContract;
import com.lehms.messages.formDefinition.FormData;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class DrawableManager {     
	
	private final Map<String, Drawable> _drawableMap;      
	private IIdentityProvider _identityProvider;
	private IDepartmentProvider _departemtProvider;
	
	public DrawableManager(IIdentityProvider identityProvider, 
			IDepartmentProvider departemtProvider) 
	{         
		_drawableMap = new HashMap<String, Drawable>();
		_identityProvider = identityProvider;
		_departemtProvider = departemtProvider;
	}
	
	public Drawable fetchDrawable(String urlString) 
	{         
		if (_drawableMap.containsKey(urlString)) 
		{             
			return _drawableMap.get(urlString);         
		}          
		Log.d(this.getClass().getSimpleName(), "image url:" + urlString);         
		try {             
			InputStream is = fetch(urlString);             
			Drawable drawable = Drawable.createFromStream(is, "src");             
			_drawableMap.put(urlString, drawable);             
			//AppLog.debug("got a thumbnail drawable: " + drawable.getBounds() + ", "                     
			//		+ drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "                     
			//		+ drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());             
			return drawable;         
		} catch (MalformedURLException e) {             
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);             
			return null;         
		} catch (IOException e) {             
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);             
			return null;         
		}     
	}      
	
	public void fetchDrawableOnThread(final String urlString, final ImageView imageView) 
	{         
		if (_drawableMap.containsKey(urlString)) {             
			imageView.setImageDrawable(_drawableMap.get(urlString));         
		}
		
		LoadImageTask task = new LoadImageTask(imageView.getContext(), urlString, imageView);
		task.execute((Void[])null);
	}
	
	private InputStream fetch(String urlString) throws MalformedURLException, IOException {         
		DefaultHttpClient httpClient = new DefaultHttpClient();         
		HttpGet request = new HttpGet(urlString);         
		
		try {
			UserDataContract user = _identityProvider.getCurrent();
			request.addHeader("Authorization", 
					//"basic " + Base64.encode( (_departmentProvider.getDepartment() + "\\" + user.Username + ":" + user.Password).getBytes(), Base64.DEFAULT ));
					"basic " + _departemtProvider.getDepartment() + "\\" + user.Username + ":" + user.Password);
			
		} catch (Exception e) {}
		
		HttpResponse response = httpClient.execute(request);
		
		return response.getEntity().getContent();     
	}
	
	
	   private class LoadImageTask extends AsyncTask<Void, Integer, Drawable>
	   {
	    	private Context _context;
	    	private ProgressDialog _progressDialog;
	    	private Exception _exception;
	    	private String _url;
	    	private ImageView _imageView;
	    	
	    	public LoadImageTask(Context context, String url, ImageView imageView)
	    	{
	    		_context = context;
	    		
	            _progressDialog = new ProgressDialog(context);
	            _progressDialog.setMessage("Loading image...");
	            _progressDialog.setIndeterminate(true);
	            _progressDialog.setCancelable(true);
	            
	            _progressDialog.setOnCancelListener(new OnCancelListener() 
	            {             
	            	@Override             
	            	public void onCancel(DialogInterface dialog) {                 
	            		cancel(true);             
	            	}         
	            });
	            
	            _url = url;
	            _imageView = imageView;
	    	}
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		super.onPreExecute();
	            _progressDialog.show();
	    	}
	    	
	    	@Override
	    	protected void onCancelled() {
	    		super.onCancelled();
	    	}
	    	
			@Override
			protected Drawable doInBackground(Void... arg0) {
				
				Drawable result = null;
				
				try {
					result = fetchDrawable(_url);
				} catch (Exception e) {
					AppLog.error(e.getMessage());
					_exception = e;
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				
				if(isCancelled())
					return;
				
				if( result == null )
				{
					if( _exception != null )
						createDialog("Error", "Error retriving image: " + _exception.getMessage());
					else
						createDialog("Error", "Error retriving image");
				}
				
				try
				{
					_imageView.setImageDrawable(result);
				}
				catch(Exception e)
				{
					UIHelper.ShowAlertDialog(_context, "Error retriving image", "Error retriving image: " + e);
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
