package com.lehms.ui.clinical.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BloodPressureTaiDocMeasurementDevice extends BluetoothMeasurementDevice<BloodPressureMeasurement>  {
	
	public BloodPressureTaiDocMeasurementDevice(BluetoothDevice device)
	{
		super(device);
	}

	@Override
	public void connected(android.bluetooth.BluetoothSocket socket) throws IOException {
		try {
			OutputStream stream = socket.getOutputStream();
			stream.write( createPacket((byte)0x25, (byte)0, (byte)0, (byte)0, (byte)0), 0, 8);
			Thread.sleep(290);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	};
	
	@Override
	public BloodPressureMeasurement createMeasurement(BluetoothSocket socket) throws Exception {

		OutputStream outputStream = socket.getOutputStream();
		
		byte [] data = getData(socket, 8);
		BloodPressureMeasurement result = null; 

		if(validateMessage(data))
		{
            if (data[1] != 0x25)
            {
                if (data[1] == 0x26)
                {
                    result = new BloodPressureMeasurement();
                    result.CreatedDate = new Date();
                    
                    result.Systolic = (data[3] * 0x100) + data[2];
                    result.Diastolic = data[4];
                    result.Pulse = data[5]; 
                    
                    // TODO Test this, also need something to say measurement completed
                    outputStream.write(createPacket((byte)80, (byte)0, (byte)0, (byte)0, (byte)0), 0, 8);
                }
            }
            else
            {
            	// This is the date, i do not care about this
            	outputStream.write(createPacket((byte)0x26, (byte)0, (byte)0, (byte)0, (byte)0), 0, 8);
            }

		}
		
		return result;
	}
	
	
	private Boolean validateMessage(byte[] buffer)
	{
        if (buffer.length != 8)
        {
            return false;
        }
        byte num2 = 0;
        for (int i = 0; i < 7; i++)
        {
            num2 = (byte) (num2 + buffer[i]);
        }
        if (num2 != buffer[7])
        {
            return false;
        }
        return true;
	}
		
	private byte[] createPacket(byte command, byte data0, byte data1, byte data2, byte data3)
	{
        byte[] buffer = new byte[8];
        byte num = 0x51;
        num = (byte) (num + command);
        num = (byte) (num + data0);
        num = (byte) (num + data1);
        num = (byte) (num + data2);
        num = (byte) (num + data3);
        num = (byte) (num + 0xa3);
        buffer[0] = 0x51;
        buffer[1] = command;
        buffer[2] = data0;
        buffer[3] = data1;
        buffer[4] = data2;
        buffer[5] = data3;
        buffer[6] = (byte) 0xa3;
        buffer[7] = num;
        return buffer;
	}
}
