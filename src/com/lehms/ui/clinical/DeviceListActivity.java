package com.lehms.ui.clinical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;

import com.google.inject.ImplementedBy;
import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.ui.clinical.device.IDeviceDiscoveredEventHandler;
import com.lehms.ui.clinical.device.IMeasurementDevice;
import com.lehms.ui.clinical.device.IMeasurementDeviceProvider;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends RoboActivity implements IDeviceDiscoveredEventHandler {
    
    // Required Intent extra
    public static final String EXTRA_DEVICE_PROVIDER_CLASS = "device_provider_class";
    public static final String EXTRA_SELECTED_DEVICE = "selected_device";

    @InjectExtra(EXTRA_DEVICE_PROVIDER_CLASS) String _deviceProviderClassName;
    private IMeasurementDeviceProvider _deviceProvider;

    // Return Intent extra
    //public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    //private BluetoothAdapter mBtAdapter;
    private MeasurementDeviceAdapter _pairedDevicesArrayAdapter;
    private MeasurementDeviceAdapter _newDevicesArrayAdapter;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        try
        {
            Class deviceProviderClass = Class.forName(_deviceProviderClassName);
            _deviceProvider = (IMeasurementDeviceProvider)deviceProviderClass.newInstance();
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Unable to create device provider", "Unable to create device provider: " + e.getMessage());
			AppLog.error("Unable to create device provider", e);
			finish();
		}
        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        _pairedDevicesArrayAdapter = new MeasurementDeviceAdapter(this, R.layout.device_item, new ArrayList<IMeasurementDevice>());
        _newDevicesArrayAdapter = new MeasurementDeviceAdapter(this, R.layout.device_item, new ArrayList<IMeasurementDevice>());

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(_pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(_deviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(_newDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(_deviceClickListener);


        try {
            // Get a set of currently paired devices
            List<IMeasurementDevice> availableDevices = _deviceProvider.getAvailableDevices();
			
	        // If there are paired devices, add each one to the ArrayAdapter
	        if (availableDevices.size() > 0) {
	            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
	            for (IMeasurementDevice device : availableDevices) {
	                _pairedDevicesArrayAdapter.add(device);
	            }
	        } else {
	            //mPairedDevicesArrayAdapter.add("No devices have been paired");
	        }
	        
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Unable to get available device", "Unable to get available device: " + e.getMessage());
			AppLog.error("Unable to get available device", e);
			finish();
		}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _deviceProvider.cancelDiscovery(this);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        try {
			_deviceProvider.beginDiscovery(this, this);
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Unable to start discovery", "Unable to start discovery: " + e.getMessage());
			AppLog.error("Unable to start discovery", e);
		}
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener _deviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            try {
           		_deviceProvider.cancelDiscovery(DeviceListActivity.this);

           		IMeasurementDevice device = (IMeasurementDevice)av.getAdapter().getItem(position);
           		
           		// Create the result Intent and include the MAC address
           		Intent intent = new Intent();
           		if( device != null )
           			intent.putExtra(EXTRA_SELECTED_DEVICE, device);

	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
            
			} catch (Exception e) 
			{
				UIHelper.ShowAlertDialog(DeviceListActivity.this, "Error geting device", "Error geting device: " + e.getMessage());
				AppLog.error("Error geting device", e);
			}
        }
    };
    
	@Override
	public void deviceDiscovered(IMeasurementDevice device) {
        _newDevicesArrayAdapter.add(device);
	}

	@Override
	public void discoveryFinished() {
        setProgressBarIndeterminateVisibility(false);
        setTitle("Select a device to connect");
        if (_newDevicesArrayAdapter.getCount() == 0) 
        {
        // TODO: do something here	
        }
	}

	@Override
	public void discoveryStarted() {
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle("Scanning for devices...");

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
	}

}
