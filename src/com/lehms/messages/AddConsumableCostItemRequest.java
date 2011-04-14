package com.lehms.messages;

import java.io.Serializable;
import java.util.Date;

import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ConsumableCostItem;

public class AddConsumableCostItemRequest implements Serializable {

	public AddConsumableCostItemRequest()
	{
		Date = new Date();		
	}
	
	public String ClientId;
	public String JobId; 
	public ConsumableCostItem Item;
	public int Quantity;
	public Date Date;
}
