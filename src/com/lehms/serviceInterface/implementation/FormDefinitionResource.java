package com.lehms.serviceInterface.implementation;

import com.google.inject.Inject;
import com.lehms.messages.GetFormDefinitionResponse;
import com.lehms.messages.GetRosterRequest;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IFormDefinitionResource;
import com.lehms.serviceInterface.IProfileProvider;

public class FormDefinitionResource implements IFormDefinitionResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;

	@Inject
	public FormDefinitionResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}
	
	@Override
	public GetFormDefinitionResponse Get() throws Exception {
		return GetChannel().Get(GetFormDefinitionResponse.class);
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetFormDefinitionsResourceEndPoint());
	}
}
