package com.lehms.ui.clinical;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.controls.*;

import com.lehms.UIHelper;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.ui.clinical.model.MeasurementType;
import com.lehms.ui.clinical.model.MeasurementTypeAdapter;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class ClinicalDetailsListActivity  extends RoboListActivity { //implements AsyncQueryListener 

	public static final String EXTRA_CLIENT = "client";
	public static final int REQUEST_CODE_MEASUREMENT = 0; 
	public static final int REQUEST_ENABLE_BT = 1; 
	public static final int REQUEST_CONNECT_DEVICE = 2; 
	
	@InjectExtra(EXTRA_CLIENT) ClientSummaryDataContract _client;
	
	@InjectView(R.id.activity_clinical_details_list_title) TextView _title;
	@InjectView(R.id.activity_clinical_details_list_sub_title) TextView _subtitle;
	@InjectView(R.id.activity_clinical_details_list_sub_title2) TextView _subtitle2;

	private MeasurementTypeAdapter _adapter;
	
	private ListQuickAction _qa;
	private MeasurementType _selectedMeasurmentType;
	private BluetoothAdapter _bluetoothAdapter;
	
	private BluetoothDevice _device;
	//private ArrayAdapter _bluetoothDeviceListAdapter;;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clinical_details_list);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_client = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true); 
		
		loadClinicalDetailsList();

		_subtitle.setText(_client.FirstName + " " + _client.LastName);
		_subtitle2.setText(_client.ClientId);

		_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//_bluetoothDeviceListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		
		final ActionItem qaViewMeasurments = new ActionItem();
		
		qaViewMeasurments.setTitle("View");
		qaViewMeasurments.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_folder));
		qaViewMeasurments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_qa.dismiss();
			}
		});
				
		final ActionItem qaTakeManualMeasurment = new ActionItem();
		
		qaTakeManualMeasurment.setTitle("Manual");
		qaTakeManualMeasurment.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_nurse));
		qaTakeManualMeasurment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openManualEntryForm(_selectedMeasurmentType);
				_qa.dismiss();
			}
		});

		final ActionItem qaTakeAutoMeasurment = new ActionItem();
		
		qaTakeAutoMeasurment.setTitle("Auto");
		qaTakeAutoMeasurment.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_bluetooth));
		qaTakeAutoMeasurment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( ! UIHelper.HasBluetoth() )
					UIHelper.ShowAlertDialog(ClinicalDetailsListActivity.this, "No bluetooth found on device", "No bluetooth found on device");
				else
					openAutoEntryForm();		
				_qa.dismiss();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() { 
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
		    	_selectedMeasurmentType = _adapter.getItem(position);
		    	
				_qa = new ListQuickAction(view);
				
				_qa.addActionItem(qaViewMeasurments);
				_qa.addActionItem(qaTakeManualMeasurment);
				_qa.addActionItem(qaTakeAutoMeasurment);
				_qa.setAnimStyle(ListQuickAction.ANIM_AUTO);
				
				_qa.show();
		    }}); 
	}
	
	private void openManualEntryForm(MeasurementType _selectedMeasurmentType) {
		
		Intent intent = null;
		
		switch (_selectedMeasurmentType.Type) {
		case Weight:
			intent = new Intent(this, WeightMeasurementActivity.class);
	        intent.putExtra(WeightMeasurementActivity.EXTRA_CLIENT, _client);
			break;
		case BP:
			intent = new Intent(this, BloodPressureMeasurementActivity.class);
	        intent.putExtra(BloodPressureMeasurementActivity.EXTRA_CLIENT, _client);
			break;
		case BSL:
			intent = new Intent(this, BloodSugerLevelMeasurementActivity.class);
	        intent.putExtra(BloodSugerLevelMeasurementActivity.EXTRA_CLIENT, _client);
			break;
		case ECG:
			intent = new Intent(this, ECGMeasurementActivity.class);
	        intent.putExtra(ECGMeasurementActivity.EXTRA_CLIENT, _client);
			break;
		case Temp:
			intent = new Intent(this, TemperatureMeasurementActivity.class);
	        intent.putExtra(TemperatureMeasurementActivity.EXTRA_CLIENT, _client);
			break;
		case SPO2:
			intent = new Intent(this, SPO2MeasurementActivity.class);
	        intent.putExtra(SPO2MeasurementActivity.EXTRA_CLIENT, _client);
			break;
		case INR:
			intent = new Intent(this, INRMeasurementActivity.class);
	        intent.putExtra(INRMeasurementActivity.EXTRA_CLIENT, _client);
			break;
		case Urine:
			intent = new Intent(this, UrinetMeasurementActivity.class);
	        intent.putExtra(UrinetMeasurementActivity.EXTRA_CLIENT, _client);
			break;
		default:
			break;
		}
		
		if( intent != null )
			this.startActivityForResult(intent, REQUEST_CODE_MEASUREMENT);
	}
	
	public void openAutoEntryForm()
	{
		if ( !_bluetoothAdapter.isEnabled()) { 

			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); 
		    
		    //Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); 
		    //discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); 
		    //startActivity(discoverableIntent);
		}
		else
		{
			_device = null;
			
			/*
			Set<BluetoothDevice> pairedDevices = _bluetoothAdapter.getBondedDevices();
		    for (BluetoothDevice device : pairedDevices) {
		    	// If you have already paird with this device then you are good to go
		    	_device = device;
		    	//_bluetoothDeviceListAdapter.add(device.getName() + "\n" + device.getAddress());
		    } 
		    */

		    // If we do not have a pared device then initiate descovery
		    if( _device == null )
		    {
	            Intent serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	            
		    	// Register the BroadcastReceiver 
		    	//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 
		    	//registerReceiver(_receiver, filter); // Don't forget to unregister during onDestroy
		    }
		}
	}
	
	private void GetDataDromDevice(BluetoothDevice device)
	{
		try {
			
			//BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UIHelper.getApplicationUUID());
			
			Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});          
			BluetoothSocket socket = (BluetoothSocket) m.invoke(device, 1); 

			// In case this is running
	        _bluetoothAdapter.cancelDiscovery();

	        socket.connect(); 

	        
	        
	        InputStream socketInputStream = socket.getInputStream(); 
	        byte[] buffer = new byte[1024];  
	        int bytes;
	        //while (true) { 
	            try { 
	                // Read from the InputStream 
	                bytes = socketInputStream.read(buffer); 
	                // Send the obtained bytes to the UI Activity 
	                //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer) 
	                        //.sendToTarget(); 
	            } catch (IOException e) { 
	                //break; 
	            } 
	        //} 
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override 
	protected void onDestroy() {
		unregisterReceiver(_receiver);
	};

	// Create a BroadcastReceiver for ACTION_FOUND 
	private final BroadcastReceiver _receiver = new BroadcastReceiver() { 
	    public void onReceive(Context context, Intent intent) { 
	        String action = intent.getAction(); 
	        // When discovery finds a device 
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) { 
	            // Get the BluetoothDevice object from the Intent 
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            
	            // If this is the device we are looking for then 
	            // 1dd the name and address to an array adapter to show in a ListView 
	            //_bluetoothDeviceListAdapter.add(device.getName() + "\n" + device.getAddress());
	            _device = device;
	            
				try {
					BluetoothSocket socket = _device.createRfcommSocketToServiceRecord(UIHelper.getApplicationUUID());
					socket.connect();
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } 
	    } 
	}; 

	
	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if( requestCode == REQUEST_CODE_MEASUREMENT)
			{
				// do something with this result
			}
			else if( requestCode == REQUEST_ENABLE_BT )
			{
				if(resultCode == RESULT_OK)
				{
					openAutoEntryForm();
				}
			}
			else if(requestCode == REQUEST_CONNECT_DEVICE)
			{
	            if (resultCode == Activity.RESULT_OK) {
	                // Get the device MAC address
	                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	                // Get the BLuetoothDevice object
	                BluetoothDevice device = _bluetoothAdapter.getRemoteDevice(address);
	                GetDataDromDevice(device);
	                // Attempt to connect to the device
	                //mChatService.connect(device);
	            }
			}
		}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_CLIENT, _client);
	}
	
	public void onSendMeasurementsToClick(View view)
	{
		// TODO: send measurement dialog then send via email
	}
	
	private void loadClinicalDetailsList(){
		
		ArrayList<MeasurementType> items = getClinicalMeasurmentTypes();
		
		_adapter = new MeasurementTypeAdapter(this, R.layout.clinical_measurment_type_item, items);

		ListView listView = getListView();
		listView.setAdapter(_adapter);
	}
	
	private ArrayList<MeasurementType> getClinicalMeasurmentTypes()
	{
		ArrayList<MeasurementType> items = new ArrayList<MeasurementType>();
		
		MeasurementType type = null;		

		type = new MeasurementType();
		type.Name = "Blood Pressure";
		type.Description = "Blood Pressure (BP)";
		type.Type = MeasurementTypeEnum.BP;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_blood_pressure;
		items.add(type);

		type = new MeasurementType();
		type.Name = "Blood Sugar Levels (BSL)";
		type.Description = "Blood Sugar Levels (BSL)";
		type.Type = MeasurementTypeEnum.BSL;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_needle;
		items.add(type);

		type = new MeasurementType();
		type.Name = "Electrocardiogram (ECG)";
		type.Description = "Detects cardiac (heart) abnormalities by measuring the electrical activity generated by the heart as it contracts";
		type.Type = MeasurementTypeEnum.ECG;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_heart;
		items.add(type);

		type = new MeasurementType();
		type.Name = "International Normalized Ratio (INR)";
		type.Description = "Results of blood coagulation";
		type.Type = MeasurementTypeEnum.INR;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_device;
		items.add(type);

		type = new MeasurementType();
		type.Name = "SPO2";
		type.Description = "Oxygen saturation measured by pulse ";
		type.Type = MeasurementTypeEnum.SPO2;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_o2;
		items.add(type);

		type = new MeasurementType();
		type.Name = "Temperature";
		type.Description = "Temperature";
		type.Type = MeasurementTypeEnum.Temp;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_temp;
		items.add(type);

		type = new MeasurementType();
		type.Name = "Urine";
		type.Description = "Urine";
		type.Type = MeasurementTypeEnum.Urine;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_default;
		items.add(type);

		type = new MeasurementType();
		type.Name = "Weight";
		type.Description = "Weight";
		type.Type = MeasurementTypeEnum.Weight;
		type.ImageDrawableId = R.drawable.dashboard_btn_clinical_details_weight;
		items.add(type);
		
		return items;
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
		
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	/*
	private class LoadClientsTask extends AsyncTask<Void, Void, List<ClientSummaryDataContract>>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Exception _exception;

    	public LoadClientsTask(Activity context)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading clients please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(false);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
		@Override
		protected List<ClientSummaryDataContract> doInBackground(Void... arg0) {
			
			List<ClientSummaryDataContract> clients = null;
			
			try {
				
				clients = _clientResource.GetClientSummaries();
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			} 
			
			return clients;
		}
		
		@Override
		protected void onPostExecute(List<ClientSummaryDataContract> result) {
			super.onPostExecute(result);
			if( result == null )
			{
				if( _exception != null )
					createDialog("Error", "Error retriving clients: " + _exception.getMessage());
				else
					createDialog("Error", "Error retriving clients");
			}
			else
			{
				ClientSummaryAdapter adapter = new ClientSummaryAdapter(_context, R.layout.client_item, result);
				ListView listView = getListView();
				listView.setAdapter(adapter);
				listView.invalidate();
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
		*/
	
}
