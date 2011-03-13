package com.lehms.serviceInterface.implementation;

import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.CreateFormDataRequest;
import com.lehms.messages.CreateFormDataResponse;
import com.lehms.messages.GetFormDataListResponse;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.formDefinition.FormData;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IFormDataResource;
import com.lehms.serviceInterface.IProfileProvider;

public class FormDataResource implements IFormDataResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;

	@Inject
	public FormDataResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider,
			IActiveJobProvider jobProvider)
	{
		_channelFactory = channelFactory;
		_profileProvider = profileProvider;
	}

	@Override
	public GetFormDataListResponse Get(long clientId, UUID formDefinitionId,
			int skip, int take) throws Exception {
		return GetListChannel().Get(skip, take, "", 
				"ClientId Eq " + clientId + " AND FormDefinitionId Eq " + formDefinitionId.toString(), 
				GetFormDataListResponse.class);
	}

	@Override
	public CreateFormDataResponse Create(CreateFormDataRequest request) throws Exception {
		return GetChannel().Create(request, CreateFormDataResponse.class);
	}

	@Override
	public FormData Get(UUID formDataId) throws Exception {
		return GetChannel().Get(formDataId.toString(), FormData.class);
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetFormDataResourceEndPoint());
	}

	private IChannel GetListChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetFormDataListResourceEndPoint());
	}

}
