package com.lehms.messages.formDefinition;

import java.io.Serializable;

public class FormElementOption implements Serializable  {

	public String Name;
	public String Value;
	// Legacy - used to map this item to the older style of form elements
	public String Code;
}
