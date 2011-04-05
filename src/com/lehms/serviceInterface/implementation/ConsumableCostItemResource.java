package com.lehms.serviceInterface.implementation;

import com.google.inject.Inject;
import com.lehms.messages.AddConsumableCostItemRequest;
import com.lehms.messages.AddConsumableCostItemResponse;
import com.lehms.messages.GetConsumableCostItemsResponse;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IConsumableCostItemResource;
import com.lehms.serviceInterface.IProfileProvider;

public class ConsumableCostItemResource implements IConsumableCostItemResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	public ConsumableCostItemResource(
			IChannelFactory channelFactory, 
			IProfileProvider profileProvider)
	{
		_channelFactory = channelFactory;
		_profileProvider = profileProvider;
	}
	
	@Override
	public GetConsumableCostItemsResponse Get() throws Exception {
		return GetChannel().Get(GetConsumableCostItemsResponse.class);
	}

	@Override
	public AddConsumableCostItemResponse Save(
			AddConsumableCostItemRequest request) throws Exception {
		return GetChannel().Create(request, AddConsumableCostItemResponse.class);
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetConsumableCostItemsResourceEndPoint());
	}

}
