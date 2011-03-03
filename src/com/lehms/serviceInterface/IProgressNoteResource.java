package com.lehms.serviceInterface;

import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;

public interface IProgressNoteResource {

	GetProgressNotesResponse Get(long clientId) throws Exception;
	ProgressNoteDataContract Create(ProgressNoteDataContract progressNote) throws Exception;

}
