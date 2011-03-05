package com.lehms.serviceInterface.implementation;

import com.google.inject.Inject;
import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.serviceInterface.IProgressNoteResource;

public class ProgressNoteResource implements IProgressNoteResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	private ProgressNoteResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}

	@Override
	public GetProgressNotesResponse Get(long clientId, int skip, int take) throws Exception {
		return GetListChannel().Get( skip, take, "", "ClientId Eq " + clientId, GetProgressNotesResponse.class);
	}

	@Override
	public ProgressNoteDataContract Create(ProgressNoteDataContract progressNote) throws Exception {
		return GetChannel().Create(progressNote, ProgressNoteDataContract.class);
	}

	
	private IChannel GetListChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetProgressNotesResourceEndPoint());
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetProgressNoteResourceEndPoint());
	}
}
