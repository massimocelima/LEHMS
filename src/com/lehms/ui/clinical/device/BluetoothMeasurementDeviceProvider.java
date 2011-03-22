package com.lehms.ui.clinical.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.lehms.R.color;
import com.lehms.serviceInterface.IDefualtDeviceAddressProvider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class BluetoothMeasurementDeviceProvider<T> implements IMeasurementDeviceProvider<T> {

	private IDeviceDiscoveredEventHandler<T> _handler;
	private IDefualtDeviceAddressProvider _defualtDeviceAddressProvider;
	
	@Inject
	public BluetoothMeasurementDeviceProvider()
	{
		_defualtDeviceAddressProvider = com.lehms.IoC.ContainerFactory.Create().resolve(IDefualtDeviceAddressProvider.class);
	}
		
	public abstract Boolean isMeasurmentDevice(BluetoothDevice device);
	public abstract IMeasurementDevice<T> createDevice(BluetoothDevice device);
	
	public List<IMeasurementDevice<T>> getAvailableDevices() throws Exception
	{
		AssertIsEnabled();
		
		List<IMeasurementDevice<T>> result = new ArrayList<IMeasurementDevice<T>>();
		
		Set<BluetoothDevice> pairedDevices = getAdapter().getBondedDevices();
		for(BluetoothDevice device : pairedDevices)
		{
			if(isMeasurmentDevice(device))
			{
				result.add(createDevice(device));
			}
		}
		
		return result;
	}
	
	public void beginDiscovery(IDeviceDiscoveredEventHandler<T> handler, Context context) throws Exception
	{
        //context.unregisterReceiver(_receiver);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(_receiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(_receiver, filter);

		AssertIsEnabled();
		
		_handler = handler;
		
        // If we're already discovering, stop it
        if (getAdapter().isDiscovering())
            getAdapter().cancelDiscovery();

        // Request discover from BluetoothAdapter
        getAdapter().startDiscovery();
		
        _handler.discoveryStarted();
	}
	
	public void cancelDiscovery(Context context)
	{
        // If we're already discovering, stop it
        if (getAdapter() != null && getAdapter().isDiscovering())
            getAdapter().cancelDiscovery();
        
        try
        {
            context.unregisterReceiver(_receiver);
        } catch(Exception e) { }
        
        _handler = null;
	}

	public IMeasurementDevice<T> getDefaultDevice()
	{
		IMeasurementDevice<T> result = null;
		String address = _defualtDeviceAddressProvider.getDeafultDeviceAddress(this.getClass().getName());
		if(! address.equals(""))
		{
			BluetoothDevice device = getAdapter().getRemoteDevice(address);
			result = createDevice(device);
		}
		return result;	
	}
	
	public void setDefaultDevice(IMeasurementDevice<T> device)
	{
		_defualtDeviceAddressProvider.setDeafultDeviceAddress(this.getClass().getName(), device.getAddress());
	}
	
	public Boolean isEnabled()
	{
		return BluetoothAdapter.getDefaultAdapter() != null; 
	}
	
	private BluetoothAdapter getAdapter()
	{
		return BluetoothAdapter.getDefaultAdapter();
	}
	
	private void AssertIsEnabled() throws Exception
	{
		if( ! isEnabled() )
			throw new Exception("Bluetooth has not been enabled for this device");
	}
	
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver _receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
               		if( isMeasurmentDevice(device) )
               			_handler.deviceDiscovered(createDevice(device));
               	}
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	_handler.discoveryFinished();
            }
        }
    };
}
