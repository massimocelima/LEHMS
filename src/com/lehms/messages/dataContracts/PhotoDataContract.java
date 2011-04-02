package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.Date;

public class PhotoDataContract implements Serializable  {

	public String Name;
	public Date CreatedDate;
	public PhotoType Type;
	public String ClientId;

	@Override
	public String toString() {
		return Name;
	}
	
	public String GetId()
	{
		return Name.substring(0, Name.indexOf("."));
	}
}
