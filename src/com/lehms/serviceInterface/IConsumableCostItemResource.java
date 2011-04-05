package com.lehms.serviceInterface;

import com.lehms.messages.AddConsumableCostItemRequest;
import com.lehms.messages.AddConsumableCostItemResponse;
import com.lehms.messages.GetConsumableCostItemsResponse;


public interface IConsumableCostItemResource {

	GetConsumableCostItemsResponse Get() throws Exception;
	AddConsumableCostItemResponse Save(AddConsumableCostItemRequest request) throws Exception;
	
}
