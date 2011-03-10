package com.lehms.messages.formDefinition;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FormDefinition implements Serializable {

	public UUID Id;
	public String Name;
	public String Title;
	public String Description;
	public List<FormPage> Pages;
	public Date CreatedDate;
	public Date LastUpdated;
	// Legacy - used to map this item to the older style of form elements
	public String Code;

}
