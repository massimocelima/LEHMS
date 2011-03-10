package com.lehms.messages.formDefinition;

import java.util.List;
import java.util.UUID;

public class FormElement {

	public UUID Id;
	public String Name;
	public String Label;
	//public FormElementType Type;
	public Boolean IsRequired;
	public List<FormElementOption> Options;
	// Legacy - used to map this item to the older style of form elements
	public String Code;
	
}
