package com.lehms.serviceInterface.implementation;

import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.CreateProgressNoteRequest;
import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.UploadProgressNoteRecordingResponse;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.serviceInterface.IActiveJobProvider;
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
	public ProgressNoteDataContract Create(CreateProgressNoteRequest request) throws Exception {
		return GetChannel().Create(request, ProgressNoteDataContract.class);
	}

	@Override
	public ProgressNoteDataContract Create(CreateProgressNoteRequest request, AttachmentDataContract attachment) throws Exception {
		UploadProgressNoteRecordingResponse response = UploadRecording(request.ProgressNote.ClientId, request.JobId, attachment.Name, attachment.Data);
		request.DocumentId = response.DocumentId;
		request.ProgressNote.AttachmentId = response.AttachmentId;
		ProgressNoteDataContract note = GetChannel().Create(request, ProgressNoteDataContract.class);
		return note;
	}

	@Override
	public ProgressNoteDataContract Get(UUID progressNoteId) throws Exception
	{
		return GetChannel().Get(progressNoteId.toString(), ProgressNoteDataContract.class);
	}
	
	private UploadProgressNoteRecordingResponse UploadRecording(String clientId, String jobId, String fileName, byte[] data) throws Exception 
	{
		String id = clientId;
		if( jobId != null && ! jobId.equals(""))
			id += "_" + jobId;
		return GetRecordingChannel().UploadAttachment(id, 
				fileName, 
				data, 
				UploadProgressNoteRecordingResponse.class);
	}
	
	private IChannel GetListChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetProgressNotesResourceEndPoint());
	}

	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetProgressNoteResourceEndPoint());
	}

	private IChannel GetRecordingChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetProgressNoteRecordingResourceEndPoint());
	}
}
