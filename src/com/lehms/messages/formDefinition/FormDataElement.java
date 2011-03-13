package com.lehms.messages.formDefinition;

import java.io.Serializable;

public class FormDataElement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public FormDataElement(String name)
	{
		Name = name;
	}

	public FormDataElement(String name, String value)
	{
		Name = name;
		Value = value;
	}

	public FormDataElement()
	{
	}

	public String Name;
	public String Value;
}
