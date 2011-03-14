package com.lehms.serviceInterface.implementation;

import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.CreateFormDataRequest;
import com.lehms.messages.CreateFormDataResponse;
import com.lehms.messages.GetFormDataListResponse;
import com.lehms.messages.UploadAttachmentResponse;
import com.lehms.messages.UploadProgressNoteRecordingResponse;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
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
	public CreateFormDataResponse Create(CreateFormDataRequest request, AttachmentDataContract attachment) throws Exception {
		
		UploadAttachmentResponse response = UploadAttachment(request.Data.ClientId, request.JobId, attachment.Name, attachment.Data);
		request.DocumentId = response.DocumentId;
		request.Data.AttachmentId = response.AttachmentId;
		return GetChannel().Create(request, CreateFormDataResponse.class);
	}
	
	@Override
	public FormData Get(UUID formDataId) throws Exception {
		return GetChannel().Get(formDataId.toString(), FormData.class);
	}

	private UploadAttachmentResponse UploadAttachment(String clientId, String jobId, String fileName, byte[] data) throws Exception 
	{
		String id = clientId;
		if( jobId != null && ! jobId.equals(""))
			id += "_" + jobId;
		return GetAttachmentChannel().UploadAttachment(id, 
				fileName, 
				data, 
				UploadAttachmentResponse.class);
	}
	
	private IChannel GetChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetFormDataResourceEndPoint());
	}

	private IChannel GetListChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetFormDataListResourceEndPoint());
	}

	private IChannel GetAttachmentChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetFormDataAttachmentResourceEndPoint());
	}

}
