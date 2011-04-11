package com.lehms.serviceInterface.implementation;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.UpdateTrackingRequest;
import com.lehms.messages.UpdateTrackingResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.serviceInterface.ITrackingResource;

public class TrackingResource implements ITrackingResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	private TrackingResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}
	
	@Override
	public UpdateTrackingResponse Update(UpdateTrackingRequest request) throws Exception {
		return GetChannel().ExecuteCommand(request, UpdateTrackingResponse.class);
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetTrackingEndPoint() + "/");
	}
}
