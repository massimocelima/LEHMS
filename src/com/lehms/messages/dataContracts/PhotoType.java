package com.lehms.messages.dataContracts;

import java.io.Serializable;

public enum PhotoType implements Serializable {
	
	Client,
	House,
	Wound,
	Other;
	
	public String GetDescription()
	{
		String result = "";
		switch(this)
		{
		case Client:
			result = "Client Photo";
			break;
		case House:
			result = "House Photo";
			break;
		case Wound:
			result = "Wound Photo";
			break;
		case Other:
			result = "Other Photo";
			break;
		}
		return result;
	}
}
