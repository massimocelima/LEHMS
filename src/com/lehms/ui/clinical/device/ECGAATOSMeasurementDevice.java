package com.lehms.ui.clinical.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ECGAATOSMeasurementDevice extends BluetoothMeasurementDevice<ECGMeasurement>  {
	
	private MeasurmentBuffer _measurementBuffer;
	
	// PIN: 3448
	public ECGAATOSMeasurementDevice(BluetoothDevice device)
	{
		super(device);
		_measurementBuffer = new MeasurmentBuffer(4);
	}
	
	@Override
	public void connected(android.bluetooth.BluetoothSocket socket) throws Exception {
		
		OutputStream outputStream = socket.getOutputStream();
		InputStream inputStream = socket.getInputStream();
		this.TransmitCommand("wbaom3\r", outputStream);
        byte[] buffer = new byte[7];
       	inputStream.read(buffer, 0, 7);
       	
       	// Stop Command
        //this.TransmitCommand("wbaom0\r");
	}
	
	@Override
	public ECGMeasurement createMeasurement(BluetoothSocket socket) throws Exception {

		byte[] data = getData(socket, 65);
		
		if( data.length == 65 )
		{
		   int num4 = 9;
		   int num2 = 0;
		   
		   int packetNumber;
		   
		   if( checkSynchronizing(data) )
		   {
	           while (num4 < 0x3b)
	           {
	               num2 = data[num4++];
	               num2 = (short) (num2 + ((short) (data[num4++] << 8)));
	               //this.m_iSampleBuffer[this.m_iSampleCount++] = num2;
	           }
	           
	           packetNumber = data[5];
	           packetNumber += data[6] << 8;
	           packetNumber += data[7] << 0x10;
	           packetNumber += data[8] << 0x18;
	           
	           if (_rrIntervalAvailable)
	           {
	        	   int value = 0;
	        	   value = data[0x3d];
	        	   value += data[0x3e] << 8;
	        	   value += data[0x3f] << 0x10;
	        	   value += data[0x40] << 0x18;
	               //this.m_iRAbsolute = iValue;
	        	   
	        	   int value2 = 0;
	        	   value2 = data[0x3b];
	        	   value2 += data[60] << 8;
	               //this.m_iRRInterval = iValue;
	        	   
	        	   _measurementBuffer.addValue(value2);
	               if (_measurementBuffer.isFilled())
	               {
	            	   ECGMeasurement measurement = new ECGMeasurement();
	            	   measurement.Pulse = 60000 / _measurementBuffer.getAverage();
	            	   _pulseAvailable = true;
	            	   return measurement;
	               //    this.m_iPulse = 0xea60 / iValue; // 60000
	               }
                   _rrIntervalAvailable = true;
	           }
	           else
	           {
                   _rrIntervalAvailable = false;
    	           _pulseAvailable = false;
    	           //this.m_bFlagRAbsoluteAvailable = false;
	           }
		    }
		}
		
		return null;
	}
	
    private void TransmitCommand(String command, OutputStream stream) throws Exception
    {
        int length = command.length();
        byte[] commandData = EncodingUtils.getAsciiBytes(command);
        this.TransmitCommand(commandData, length, stream);
    }

    private void TransmitCommand(byte[] command, int length, OutputStream stream) throws Exception
    {
        for (int i = 0; i < length; i++)
        {
            stream.write(command[i]);
            Thread.sleep(40);
        }
        Thread.sleep(40);
    }
    
    private Boolean _pulseAvailable = false;
    private Boolean _rrIntervalAvailable = false;
    private Boolean _adaptationError = false;
    private Boolean _rrError = false;
    private Boolean _batteryLow = false;
    
    private Boolean checkSynchronizing(byte[] buffer)
    {
        if (buffer[0] != 0)
        {
            return false;
        }
        if (buffer[1] != 0)
        {
            return false;
        }
        if ((buffer[2] & 1) == 1)
        {
        	_rrIntervalAvailable = true;
        }
        else
        {
        	_rrIntervalAvailable = false;
        }
        if ((buffer[2] & 8) == 8)
        {
        	_adaptationError = true;
        }
        else
        {
        	_adaptationError = false;
        }
        if ((buffer[2] & 0x20) == 0x20)
        {
        	_rrError = true;
        }
        else
        {
        	_rrError = false;
        }
        if ((buffer[2] & 0x80) == 0x80)
        {
        	_batteryLow = true;
        }
        else
        {
        	_batteryLow = false;
        }
        if (buffer[3] != 0)
        {
            return false;
        }
        if (buffer[4] != 5)
        {
            return false;
        }
        return true;
    }

    
    private class MeasurmentBuffer implements Serializable
    {
        private Boolean m_bBufferFilled;
        private int m_iItemCount;
        private int m_iRingBufferHead;
        private int[] m_iRingBufferItems;
        private int m_iRingBufferSum;
        private int m_iRingBufferTail;
        private int m_iSizeOfBuffer;

        public MeasurmentBuffer(int iSizeOfBuffer)
        {
            this.m_iRingBufferItems = new int[iSizeOfBuffer];
            this.m_iSizeOfBuffer = iSizeOfBuffer;
            this.init();
        }

        public void addValue(int iValue)
        {
            int index = (this.m_iRingBufferHead + 1) % this.m_iSizeOfBuffer;
            int iRingBufferTail = this.m_iRingBufferTail;
            if (index == iRingBufferTail)
            {
                this.m_bBufferFilled = true;
                this.m_iItemCount = this.m_iSizeOfBuffer;
                this.m_iRingBufferSum -= this.m_iRingBufferItems[iRingBufferTail];
                this.m_iRingBufferTail = ++iRingBufferTail;
                this.m_iRingBufferTail = this.m_iRingBufferTail % this.m_iSizeOfBuffer;
            }
            else
            {
                this.m_iItemCount++;
            }
            this.m_iRingBufferItems[index] = iValue;
            this.m_iRingBufferHead = index;
            this.m_iRingBufferSum += iValue;
        }

        public void init()
        {
            this.m_iRingBufferSum = 0;
            this.m_iRingBufferHead = 0;
            this.m_iRingBufferTail = 0;
            for (int i = 0; i < this.m_iSizeOfBuffer; i++)
            {
                this.m_iRingBufferItems[i] = 0;
            }
            this.m_bBufferFilled = false;
            this.m_iItemCount = 0;
        }

        public int getAverage()
        {
            return (this.m_iRingBufferSum / this.m_iItemCount);
        }

        public Boolean isFilled()
        {
        	return this.m_bBufferFilled;
        }

        public int getItemCount()
        {
            return this.m_iItemCount;
        }
    }
}
