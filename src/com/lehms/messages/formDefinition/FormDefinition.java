package com.lehms.messages.formDefinition;

import java.io.Serializable;
import java.util.ArrayList;
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

    public void fillFormData(FormData data)
    {
    	List<FormElement> formElements = GetEditableElements();
    	
    	for(int i = 0; i < formElements.size(); i++)
    	{
    		FormElement element = formElements.get(i);
    		FormDataElement dataElement = data.getDataElement(element.Name);
    		if( dataElement == null )
    		{
    			dataElement = new FormDataElement(element.Name, element.Value);
    			data.FormDataElements.add(dataElement);
    		}
    		else
    			dataElement.Value = element.Value;
    	}
    }
    
    public void loadFormData(FormData data)
    {
    	for(int i = 0; i < data.FormDataElements.size(); i++)
    	{
    		FormDataElement dataElement = data.FormDataElements.get(i);
    		FormElement element = GetEditableElement(dataElement.Name);
    		if( element != null )
    			element.Value = dataElement.Value;
    	}
    }
    
    public FormElement GetEditableElement(String name)
    {
    	for(int i = 0; i < Pages.size(); i++)
    	{
    		FormPage page = Pages.get(i);
    		for(int j = 0; j < page.Elements.size(); j++)
    		{
    			FormElement element = page.Elements.get(j);
    			if(element.Name.equals(name) && element.IsEditable())
    				return element;
    		}
    	}
    	
    	return null;
    }
    
    public List<FormElement> GetEditableElements()
    {
    	ArrayList<FormElement> elements = new ArrayList<FormElement>();
    	
    	for(int i = 0; i < Pages.size(); i++)
    	{
    		FormPage page = Pages.get(i);
    		for(int j = 0; j < page.Elements.size(); j++)
    		{
    			FormElement element = page.Elements.get(j);
    			if(element.IsEditable())
    				elements.add(element);
    		}
    	}
    	
    	return elements;
    }
    
    public void setElementValue(String name, String value)
    {
    	FormElement element = GetEditableElement(name);
    	element.Value = value;
    }
}
