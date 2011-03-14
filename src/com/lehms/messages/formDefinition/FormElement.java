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
    public Boolean IsReadonly;

    
    public FormElement()
    {
    	IsReadonly = false;
    }
	
	public Boolean IsEditable()
	{
		return Type != FormElementType.Label;
	}
	
	public FormElementOption GetOpionByName(String name)
	{
		for(int i = 0; i < Options.size(); i++)
		{
			if( Options.get(i).Name.equals(name) )
				return Options.get(i);
		}
		return null;
	}

	public FormElementOption GetOpion(String value)
	{
		for(int i = 0; i < Options.size(); i++)
		{
			if( Options.get(i).Value.equals(value) )
				return Options.get(i);
		}
		return null;
	}

	public int GetIndexByName(String name)
	{
		int result = -1;
		for(int i = 0; i < Options.size(); i++)
		{
			if( Options.get(i).Name.equals(name) )
				return i;
		}
		return result;
	}

	public int GetIndexByValue(String value)
	{
		int result = -1;
		for(int i = 0; i < Options.size(); i++)
		{
			if( Options.get(i).Value.equals(value) )
				return i;
		}
		return result;
	}

}
