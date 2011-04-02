package com.lehms.messages;

import java.io.Serializable;
import java.util.List;

import com.lehms.messages.formDefinition.FormDefinition;

public class GetFormDefinitionResponse implements Serializable  {
	public List<FormDefinition> FormDefinitions;
}
