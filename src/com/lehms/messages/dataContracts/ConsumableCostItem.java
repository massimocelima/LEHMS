package com.lehms.messages.dataContracts;

import java.io.Serializable;

public class ConsumableCostItem implements Serializable {

    public String Name;
    public double Cost;
    
	@Override
	public String toString()
	{
		return Name;
	}
}
