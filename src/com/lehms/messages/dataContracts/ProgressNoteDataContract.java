package com.lehms.messages.dataContracts;

import java.util.Date;
import java.util.UUID;

public class ProgressNoteDataContract {
	
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
    
    public String VoiceMemoUri;
    public String VoiceMemoFileName;
    public String Transcript;
    
    public Boolean hasRecordedMessage()
    {
    	return VoiceMemoUri != null && ! VoiceMemoUri.equals("");
    }
    
}
