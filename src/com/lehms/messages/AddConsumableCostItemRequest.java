package com.lehms.messages;

import java.io.Serializable;

import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ConsumableCostItem;

public class AddConsumableCostItemRequest implements Serializable {

	public String ClientId;
	public String JobId; 
	public ConsumableCostItem Item;
	public int Quantity;
}
