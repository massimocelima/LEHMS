package com.lehms.messages.formDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FormData implements Serializable {
	
	private static final long serialVersionUID = 7293993351254329258L;
	public FormData(FormDefinition formDefinition)
	{
		Id = UUID.randomUUID();
		FormId = formDefinition.Id;
		
		FormDataElements = new ArrayList<FormDataElement>();
		CreatedDate = new Date();
	}
	
	public FormData()
	{
	}
	
	public UUID Id;
    public UUID FormId;
    public String ClientId;

    public List<FormDataElement> FormDataElements;

    public Date CreatedDate;
    public String CreatedBy;
    public UUID AttachmentId;
    
    public FormDataElement getDataElement(String name)
    {
    	for(int i = 0; i < FormDataElements.size(); i++)
    	{
    		if(FormDataElements.get(i).Name.equals(name))
    			return FormDataElements.get(i);
    	}
    	return null;
    }

}
