package com.lehms.serviceInterface.implementation;

import java.lang.reflect.Type; 
import java.util.Calendar;
import java.util.Date; 
import java.util.TimeZone;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;   
import com.google.gson.JsonDeserializationContext; 
import com.google.gson.JsonDeserializer; 
import com.google.gson.JsonElement; 
import com.google.gson.JsonParseException;   
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {       
	
	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)           
			throws JsonParseException { 
		String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";           
		Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
		Matcher matcher = pattern.matcher(json.getAsJsonPrimitive().getAsString());
		String result = matcher.replaceAll("$2");
		Date dateResult = new Date(new Long(result));
		return dateResult;       
	}
	
	@Override
	public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
		return new JsonPrimitive("/Date(" + date.getTime() + ")/");
	}


} 

