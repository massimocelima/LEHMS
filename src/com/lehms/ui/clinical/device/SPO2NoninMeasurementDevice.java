package com.lehms.ui.clinical.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import com.lehms.ui.clinical.model.SPO2Measurement;

import android.bluetooth.BluetoothDevice;

public class SPO2NoninMeasurementDevice extends BluetoothMeasurementDevice<SPO2Measurement>  {
	
	private String _data = "";
	
	public SPO2NoninMeasurementDevice(BluetoothDevice device)
	{
		super(device);
	}

	@Override
	public void connected(android.bluetooth.BluetoothSocket socket) throws IOException {
		OutputStream stream = socket.getOutputStream();
        byte[] buffer = new byte[] { 2, 0x70, 2, 2, 2, 3 };
        stream.write(buffer, 0, 6);
	};
	
	@Override
	public SPO2Measurement createMeasurement(byte[] data) {
		
		_data += HexConverter.encode(data);
		
        int index = _data.indexOf("01");
        while (index >= 0 && _data.length() >= 250)
        {
            //0x81 == 129
            if (_data.substring(index + 2, (index + 2) + 2).equals("81"))
            {
	            _data = _data.substring(index);
		        if (_data.length() >= 250)
		        {
		            index = 250;
		            String message = _data.substring(0, index);
		            _data = _data.substring(index);
		            
		    		SPO2Measurement result = new SPO2Measurement();
		    		result.CreatedDate = new Date();
		    		result.OxegenPercent = GetParameter(message, 2, 3);
		    		result.Pulse = GetIntParameter(message, 0, 3);
		    		return result;
				}
            }
            index++;
            index = _data.indexOf("01", index);
        }

		return null;
	}
	
    private int GetParameter(String message, int iRow, int iCol)
    {
        return this.HexValue(message, (iRow * 5) + iCol);
    }

    private int HexValue(String message, int iIndex)
    {
    	message = message.substring(iIndex * 2, (iIndex * 2) + 2);
    	return HexConverter.decode(message)[0];
    }

    private int GetIntParameter(String message, int iRow, int iCol)
    {
        int num = this.GetParameter(message, iRow, iCol);
        int num2 = this.GetParameter(message, iRow + 1, iCol);
        return ((num << 8) | num2);
    }

    /*
    private class Packet
    {
    	public Packet() 
    	{
    		Frames = new ArrayList<SPO2NoninMeasurementDevice.Frame>();
    	}
    	
    	public ArrayList<Frame> Frames;
    }
    
    private class Frame
    {
    	public final int FAME_LENGTH = 5;
    	
    	public int Status;
    	public Byte Pleth;
    	public int Measurment;
    }
    
	public class FrameStatus
	{
		public final int MeasurmentTaken = 128;
		public final int FrameSync = 129;
		public final int SensorDisconnect = 192;
	}
*/
}
