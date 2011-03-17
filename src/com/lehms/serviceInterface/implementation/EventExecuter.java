package com.lehms.serviceInterface.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.UIHelper;
import com.lehms.messages.CreateFormDataRequest;
import com.lehms.messages.CreateFormDataResponse;
import com.lehms.messages.CreateProgressNoteRequest;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.PhotoType;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.messages.formDefinition.FormData;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IFormDataResource;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.util.AppLog;
import com.lehms.util.StreamExtentions;

public class EventExecuter implements IEventExecuter {

	private IEventRepository _eventRepository;
	private IProgressNoteResource _progressNoteResource;
	private IFormDataResource _formDataResource;
	
	@Inject
	public EventExecuter(IProgressNoteResource progressNoteResource,
			IFormDataResource formDataResource,
			IEventRepository eventRepository)
	{
		_progressNoteResource = progressNoteResource;
		_eventRepository = eventRepository;
		_formDataResource = formDataResource;
	}
	
	@Override
	public Object ExecuteEvent(Event event) throws Exception {
		
		if( event.Type == EventType.ProgressNoteAdded)
			return ExecuteCreateProgressNoteEvent(event);
		else if(event.Type == EventType.FormCompleted)
			return ExecuteCreateFormEvent(event);
		else
			throw new Exception("Unimplemented event executer for " + event.Type.toString());
	}
	
	public Object ExecuteCreateProgressNoteEvent(Event event) throws Exception
	{
		CreateProgressNoteRequest request = (CreateProgressNoteRequest)event.Data;
		
		// Attach the recording to the object
		if( request.ProgressNote.VoiceMemoUri != null && ! request.ProgressNote.VoiceMemoUri.equals(""))
		{
			File file = new File(request.ProgressNote.VoiceMemoUri);
			if( ! file.exists() )
				throw new Exception("Unable to find recorded message.");
			
			FileInputStream stream = new FileInputStream(file);

			byte[] buff = new byte[(int)file.length()];
			int index = 0;
			while(index < file.length())
				index += stream.read(buff);

			AttachmentDataContract attachment = new AttachmentDataContract();
			attachment.Data = buff;
			attachment.Id=  UUID.randomUUID();
			attachment.Name = attachment.Id.toString();
			ProgressNoteDataContract note =  _progressNoteResource.Create(request, attachment);
			
			stream.close();
			file.delete();
			return note;
		}
		else
			return _progressNoteResource.Create(request);
	}
	
	public Object ExecuteCreateFormEvent(Event event) throws Exception
	{
		CreateFormDataRequest request = (CreateFormDataRequest)event.Data;
		CreateFormDataResponse response = null;
		
		if( request.Data.AttachmentId != null )
		{
			AttachmentDataContract attachment = new AttachmentDataContract();
			attachment.Name = request.Data.AttachmentId.toString();
			attachment.Id = request.Data.AttachmentId;
			try {
				String path = UIHelper.GetFormImagePath(request.Data.AttachmentId);
				File file = new File(path);
				if( ! file.exists() )
				{
					path = UIHelper.GetClientPhotoPath(request.Data.ClientId, request.Data.AttachmentId, PhotoType.Wound);
					file = new File(path);
				}
				
				attachment.Data = StreamExtentions.readToEnd(new FileInputStream(path), file.length());
				response = _formDataResource.Create(request, attachment);
				
				file.delete();
				
			} catch (Exception e) {
				AppLog.error("Error getting attachemtn for form", e);
				response = _formDataResource.Create(request);
			}
		}
		else
			response = _formDataResource.Create(request);
		
		return response;
	}

}
