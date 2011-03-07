package com.lehms.serviceInterface;

import java.util.UUID;

import com.lehms.messages.CreateProgressNoteRequest;
import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;

public interface IProgressNoteResource {

	GetProgressNotesResponse Get(long clientId, int skip, int take) throws Exception;
	ProgressNoteDataContract Get(UUID progressNoteId) throws Exception;
	ProgressNoteDataContract Create(CreateProgressNoteRequest progressNote) throws Exception;

}
