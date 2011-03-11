package com.lehms.serviceInterface.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.CreateProgressNoteRequest;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IProgressNoteResource;

public class EventExecuter implements IEventExecuter {

	private IEventRepository _eventRepository;
	private IProgressNoteResource _progressNoteResource;
	
	@Inject
	public EventExecuter(IProgressNoteResource progressNoteResource, 
			IEventRepository eventRepository)
	{
		_progressNoteResource = progressNoteResource;
		_eventRepository = eventRepository;
	}
	
	@Override
	public Object ExecuteEvent(Event event) throws Exception {
		
		if( event.Type == EventType.ProgressNoteAdded)
			return ExecuteCreateProgressNoteEvent(event);
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

}
