package com.lehms.messages;

import java.util.List;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;


public class GetProgressNotesResponse {

	public List<ProgressNoteDataContract> ProgressNotes;
	public ClientSummaryDataContract Client;
	
}
