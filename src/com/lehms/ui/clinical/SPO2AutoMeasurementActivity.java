package com.lehms.ui.clinical;

import com.lehms.LoginActivity;
import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.model.SPO2Measurement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class SPO2AutoMeasurementActivity extends RoboActivity {

	public final static String EXTRA_DEVICE =  "device"; 
	public final static String EXTRA_MEASUREMENT = "measurement"; 
	
	@InjectExtra(EXTRA_DEVICE) IMeasurementDevice _device;

	@InjectView(R.id.activity_measurment_spo2_status_auto_edit) TextView _statusTextView;
	@InjectView(R.id.activity_measurment_spo2_auto_edit) TextView _spo2TextView;
	@InjectView(R.id.activity_measurment_spo2_pulse_auto_edit) TextView _pulseTextView;
	@InjectView(R.id.activity_measurment_spo2_save) Button _saveButton;
		
	private TakeMeasurementTask _task;
	private SPO2Measurement _measurement;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_measurement_spo2_auto);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_DEVICE) != null)
			_device = (IMeasurementDevice) savedInstanceState.get(EXTRA_DEVICE);
		
		_saveButton.setEnabled(false);

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
		intent.putExtra(EXTRA_MEASUREMENT, _measurement);

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
	}
	
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		if(_task.getStatus() == Status.RUNNING)
			_task.cancel(true);
		try { _device.close(); } catch(Exception e) { }
	};
	
	private class TakeMeasurementTask extends AsyncTask<Void, Integer, SPO2Measurement>
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
		protected SPO2Measurement doInBackground(Void... arg0) {
			try {
				return (SPO2Measurement)_device.readMeasurement();
			} catch (Exception e) {
				_exception = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(SPO2Measurement result) {
			super.onPostExecute(result);
			
			if(isCancelled())
				return;
			
			if( result != null )
			{
				_statusTextView.setText("Getting measurements...");

				_measurement = result;
				_pulseTextView.setText(result.Pulse + "");
				_spo2TextView.setText(result.OxegenPercent + "");
				
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
	
}
