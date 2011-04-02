package com.lehms.messages;

import java.io.Serializable;
import java.util.UUID;

import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;

public class CreateProgressNoteRequest implements Serializable  {

	public ProgressNoteDataContract ProgressNote;
	public String JobId;
	public UUID DocumentId;

}
