package com.lehms.messages.formDefinition;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class FormPage implements Serializable  {

	public UUID Id;
	public String Title;
	public List<FormElement> Elements;
}
