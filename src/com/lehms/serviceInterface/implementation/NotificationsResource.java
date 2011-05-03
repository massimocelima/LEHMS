package com.lehms.serviceInterface.implementation;

import com.google.inject.Inject;
import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.GetNotificationsRequest;
import com.lehms.messages.GetNotificationsResponse;
import com.lehms.serviceInterface.IAlarmResource;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.INotificationsResource;
import com.lehms.serviceInterface.IProfileProvider;

public class NotificationsResource implements INotificationsResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	private NotificationsResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}
	
	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetNotificationsResourceEndPoint());
	}

	@Override
	public GetNotificationsResponse Get(GetNotificationsRequest request)
			throws Exception {
		return GetChannel().Get(GetNotificationsResponse.class);
	}
}
