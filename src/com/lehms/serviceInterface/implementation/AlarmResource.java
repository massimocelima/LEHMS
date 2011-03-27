package com.lehms.serviceInterface.implementation;

import com.google.inject.Inject;
import com.lehms.messages.RaiseAlarmRequest;
import com.lehms.messages.RaiseAlarmResponse;
import com.lehms.serviceInterface.IAlarmResource;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IProfileProvider;

public class AlarmResource implements IAlarmResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	private AlarmResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider, 
			IDepartmentProvider departmentProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}
	
	public RaiseAlarmResponse Raise(RaiseAlarmRequest request) throws Exception
	{
		return GetChannel().ExecuteCommand(request, RaiseAlarmResponse.class);
	}
	
	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetAlarmResourceEndPoint());
	}
}
