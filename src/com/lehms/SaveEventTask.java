package com.lehms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.lehms.persistence.Event;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.util.AppLog;

public class SaveEventTask extends AsyncTask<Event, Integer, Object>
{
 	private Activity _context;
 	private ProgressDialog _progressDialog;
 	private Exception _exception;
 	private IEventExecuter _executer;
 	private String _title;
 	private ISaveEventResultHandler _handler;
 	private Boolean _exitOnSuccess;
 	
 	public SaveEventTask(Activity context, IEventExecuter executer, String title, 
 			ISaveEventResultHandler handler,
 			Boolean exitOnSuccess)
 	{
 		_context = context;
 		_executer = executer;
 		_title = title;
 		_handler = handler;
 		_exitOnSuccess = exitOnSuccess;
 		
         _progressDialog = new ProgressDialog(context);
         _progressDialog.setMessage("Saving " + title + "...");
         _progressDialog.setIndeterminate(true);
         _progressDialog.setCancelable(false);
         
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
         _progressDialog.hide();
 		//_context.finish();
 	}
 	
 	
		@Override
		protected Object doInBackground(Event... arg0) {
			
			try {
				return _executer.ExecuteEvent(arg0[0]);
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			if(isCancelled())
				return;

			if( _progressDialog.isShowing() )
			{
				try { _progressDialog.dismiss(); } catch (Exception e) {}
			}

			if( _exception != null )
			{
				createDialog("Error", "Error saving " + _title + ": " + _exception.getMessage());
				_handler.onError(_exception);
			}
			else
			{
				_handler.onSuccess(result);
				if(_exitOnSuccess)
					_context.finish();
			}
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