package com.lehms.serviceInterface.implementation;

import java.util.Date;

import com.google.inject.Inject;
import com.lehms.messages.GetRosterRequest;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.serviceInterface.IRosterResource;

public class RosterResource implements IRosterResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	private IDepartmentProvider _departmentProvider;
	
	@Inject
	private RosterResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider, 
			IDepartmentProvider departmentProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
		_departmentProvider = departmentProvider;
	}

	@Override
	public RosterDataContract GetRosterFor(Date date) throws Exception {
		
		GetRosterRequest request = new GetRosterRequest();
		request.DateTime = date;
		
		return GetChannel().Get(request, RosterDataContract.class);
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetRosterResourceEndPoint());
	}
}
