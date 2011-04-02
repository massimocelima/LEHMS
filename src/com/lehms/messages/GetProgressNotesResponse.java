package com.lehms.messages;

import java.io.Serializable;
import java.util.List;

import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;


public class GetProgressNotesResponse implements Serializable  {

	public List<ProgressNoteDataContract> ProgressNotes;
	
}
