package com.lehms.serviceInterface.implementation;

import java.lang.reflect.Type; 
import java.util.UUID;
import com.google.gson.JsonDeserializationContext; 
import com.google.gson.JsonDeserializer; 
import com.google.gson.JsonElement; 
import com.google.gson.JsonParseException;   
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UUIDSerializer implements JsonDeserializer<UUID>, JsonSerializer<UUID> {       
	
	@Override
	public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)           
			throws JsonParseException { 
		return UUID.fromString(json.getAsJsonPrimitive().getAsString());
	}
	
	@Override
	public JsonElement serialize(UUID id, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(id.toString());
	}


} 

