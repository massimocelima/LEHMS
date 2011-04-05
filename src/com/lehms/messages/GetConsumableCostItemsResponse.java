package com.lehms.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lehms.messages.dataContracts.ConsumableCostItem;

public class GetConsumableCostItemsResponse implements Serializable {
	
    public GetConsumableCostItemsResponse()
    {
    	Items = new ArrayList<ConsumableCostItem>();
    }

    public List<ConsumableCostItem> Items;
}
