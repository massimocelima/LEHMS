package com.lehms.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamExtentions {

	private StreamExtentions() { }
	
	public static byte[] readToEnd(InputStream stream, long length) throws IOException
	{
		byte[] buff = new byte[(int)length];
		int index = 0;
		int read = 0;
		
		index = stream.read(buff);
		
		while(index < length)
		{
			read = stream.read(buff);
			if(read == -1)
				break;
			index += read;
		}

		return buff;
	}

	public static void copyStream(InputStream instream, OutputStream outStream) throws IOException
	{
		byte[] buffer = new byte[1024];     
		int len = 0;     
		while ( (len = instream.read(buffer)) > 0 ) {          
			outStream.write(buffer,0, len);  
		} 
		
		outStream.close(); 
		instream.close();
	}

}
