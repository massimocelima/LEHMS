package com.lehms.serviceInterface.implementation;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lehms.serviceInterface.ISerializer;

import org.json.*;

public class JsonSerializer implements ISerializer {

	@Override
	public String Serializer(Object o) throws Exception {
		
		GsonBuilder gsonb = new GsonBuilder(); 
		
		DateDeserializer ds = new DateDeserializer(); 
		gsonb.registerTypeAdapter(Date.class, ds);
		
		UUIDSerializer uidSerializer = new UUIDSerializer();
		Class<UUID> classUUID = UUID.class;
		gsonb.registerTypeAdapter( classUUID, uidSerializer); 
		
		Gson gson = gsonb.create(); 
		JSONObject jobject = new JSONObject(gson.toJson(o)); 
		return jobject.toString();
	}

	@Override
	public <T> T Deserializer(String data, Class<T> type) throws Exception {
	
		GsonBuilder gsonb = new GsonBuilder();
		
		DateDeserializer ds = new DateDeserializer(); 
		gsonb.registerTypeAdapter(Date.class, ds); 
		
		UUIDSerializer uidSerializer = new UUIDSerializer();
		Class<UUID> classUUID = UUID.class;
		gsonb.registerTypeAdapter( classUUID, uidSerializer); 

		Gson gson = gsonb.create(); 
		
		try {
			JSONObject j = new JSONObject(data);
			return gson.fromJson(j.toString(), type);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Unable to convert string to json");
		}
		
	}

	@Override
	public Object Deserializer(String data, Type type) throws Exception {
	
		GsonBuilder gsonb = new GsonBuilder(); 
		DateDeserializer ds = new DateDeserializer(); 
		gsonb.registerTypeAdapter(Date.class, ds); 
		Gson gson = gsonb.create(); 
		
		try {
			if( data.startsWith("[") )
			{
				JSONArray a = new JSONArray(data);
				return gson.fromJson(a.toString(), type);
			}
			else
			{
				JSONObject j = new JSONObject(data);
				return gson.fromJson(j.toString(), type);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Unable to convert string to json");
		}
		
	}

	@Override
	public String GetSerializerContentType() {
		return "application/json";
	}

	@Override
	public String GetDeserializerContentType() {
		return "application/json";
	}

}
