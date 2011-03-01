package com.lehms.service.implementation;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.service.IChannel;
import com.lehms.service.IChannelFactory;
import com.lehms.service.IClientResource;
import com.lehms.service.IDepartmentProvider;
import com.lehms.service.IProfileProvider;

public class ClientResource implements IClientResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	private ClientResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider, 
			IDepartmentProvider departmentProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}

	@Override
	public List<ClientSummaryDataContract> GetClientSummaries() throws Exception {
		
		Type collectionType = new TypeToken<List<ClientSummaryDataContract>>(){}.getType();
		return (List<ClientSummaryDataContract>)GetSummaryChannel().Get(collectionType);
	}

	@Override
	public GetClientDetailsResponse GetClientDetails(long id) throws Exception {
		return GetChannel().Get(id + "", GetClientDetailsResponse.class);
	}
	
	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetClientResourceEndPoint());
	}

	private IChannel GetSummaryChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetClientsResourceEndPoint());
	}

}
