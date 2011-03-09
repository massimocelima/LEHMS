package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


public class ProgressNoteDataContract implements Serializable {
	
	public ProgressNoteDataContract()
	{
		Id = UUID.randomUUID();
	}
	
	public UUID Id;
    public String CreatedBy;
    public Date CreatedDate;
    public String Subject;
    public String ClientId;
    public String ClientName;
    public String Note;
    
    public String Transcript;
    public UUID AttachmentId;
    public String VoiceMemoUri;
    
    public Boolean hasRecordedMessage()
    {
    	return AttachmentId != null && ! AttachmentId.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }
    
}
