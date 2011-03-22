package com.lehms.ui.clinical.device;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public abstract class BluetoothMeasurementDevice<T> implements IMeasurementDevice<T> {

	private String _address;
	private String _name;
	private transient BluetoothSocket _socket;

	public BluetoothMeasurementDevice(BluetoothDevice device)
	{
		_address = device.getAddress();
		_name = device.getName();
	}
	
	@Override
	public String getName() {
		return _name;
	}
	
	public void connect() throws Exception
	{
		try
		{
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter(); 
	        BluetoothDevice device = adapter.getRemoteDevice(getAddress());
			
			Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});          
			_socket = (BluetoothSocket) m.invoke(device, 1); 
	
			// In case this is running
			adapter.cancelDiscovery();
	
			_socket.connect();
			
			connected(_socket);
			
		} catch(Exception e)
		{
			_socket = null;	
			throw e;
		}
	}
	
	public void connected(BluetoothSocket socket) throws Exception
	{
		
	}
	
	public void close() throws Exception
	{
		try
		{
			if(_socket != null )
				_socket.close();
		}
		catch(Exception e) {}
		finally
		{
			_socket = null;
		}
	}

	protected byte[] getData(BluetoothSocket socket) throws IOException
	{
		byte [] data = null;
        byte[] buffer = new byte[1024];
        int bytes;
        
        bytes = socket.getInputStream().read(buffer);
        if( bytes > 0 )
        {
        	data = buffer;
        	if( bytes < buffer.length)
        	{
        		data = new byte[bytes];
        		System.arraycopy(buffer, 0, data, 0, bytes);;
        	}
        }
        
        return data;
	}
	
	public T readMeasurement() throws Exception
	{
		if( _socket == null )
			connect();
		
		T result = null;
		
		try
		{
        	result = createMeasurement(_socket);
		} catch(Exception e) {
			close();	
			throw e;
		}
        
        return result;
	}
	
	public abstract T createMeasurement(BluetoothSocket socket) throws Exception;

	@Override
	public Boolean isDefault()
	{
		return false;
	}

	@Override
	public String getAddress() {
		return _address;
	}

}
