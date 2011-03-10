package com.lehms.messages.formDefinition;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class FormElement implements Serializable  {

	public int Id;
	public String Name;
	public String Label;
	public String Value;
	public FormElementType Type;
	public Boolean IsRequired;
	public List<FormElementOption> Options;
	// Legacy - used to map this item to the older style of form elements
	public String Code;
	
	
	public Boolean IsEditable()
	{
		return Type != FormElementType.Label;
	}
	
}
