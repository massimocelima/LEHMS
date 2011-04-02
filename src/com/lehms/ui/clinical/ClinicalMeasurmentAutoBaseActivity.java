package com.lehms.ui.clinical;


import java.io.Serializable;

import com.lehms.ISaveEventResultHandler;
import com.lehms.LehmsRoboActivity;
import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.controls.ActionItem;
import com.lehms.controls.QuickAction;
import com.lehms.serviceInterface.ISerializer;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public abstract class ClinicalMeasurmentAutoBaseActivity<T> extends LehmsRoboActivity {

	public final static String EXTRA_DEVICE =  "device"; 
	public final static String EXTRA_MEASUREMENT = "measurement"; 
	private static Boolean _running = false;
	
	@InjectExtra(EXTRA_DEVICE) IMeasurementDevice _device;

	@InjectView(R.id.activity_measurment_status_auto) TextView _statusTextView;
	@InjectView(R.id.activity_measurment_auto_save) Button _saveButton;
		
	private TakeMeasurementTask _task;
	private T _measurement;

	
	public abstract void loadMeasurement(T measurement);

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_DEVICE) != null)
			_device = (IMeasurementDevice) savedInstanceState.get(EXTRA_DEVICE);
	}
	
	protected void start()
	{
		_saveButton.setEnabled(false);

		_running = true;
		_task = new TakeMeasurementTask(_device);
		_task.execute(null);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_DEVICE, _device);
	}
	
	public void onCancelClick(View view)
	{
		if(_task.getStatus() == Status.RUNNING)
			_task.cancel(true);
		finish();
	}
	
	public void onSaveClick(View view)
	{
   		Intent intent = new Intent();
		intent.putExtra(EXTRA_MEASUREMENT, (Serializable) _measurement);

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
	}
	
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		_running = false;
		if(_task.getStatus() == Status.RUNNING)
			_task.cancel(true);
		try { _device.close(); } catch(Exception e) { }
	};
	
	private class TakeMeasurementTask extends AsyncTask<Void, Integer, T>
	{
		private Exception _exception = null;
	    private IMeasurementDevice _device;

	    public TakeMeasurementTask(IMeasurementDevice device)
	    {
	    	_device = device;
	    }
	    	
	    @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		super.onCancelled();
    	}

    	private Boolean _updateRequired = false;
    	
		@Override
		protected T doInBackground(Void... arg0) {
			try {
				return (T)_device.readMeasurement();
			} catch (Exception e) {
				_exception = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(T result) {
			super.onPostExecute(result);
			
			if(isCancelled() || ! _running)
				return;
			
			if( result != null )
			{
				_statusTextView.setText("Getting measurements...");

				_measurement = result;
				loadMeasurement(_measurement);
				
				_saveButton.setEnabled(true);
			}
			else if(_exception != null )
			{
				_statusTextView.setText("Error getting measurement, attempting to reconnect...");
				_exception = null;
				try { _device.close(); } catch (Exception e ) {}
			}
			
			_task = new TakeMeasurementTask(_device);
			_task.execute(null);

		}
	}
	
	public T getMeasurement()
	{
		return _measurement;
	}
	

}
