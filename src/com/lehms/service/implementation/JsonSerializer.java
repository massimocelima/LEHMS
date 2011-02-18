package com.lehms.service.implementation;

import java.util.Date;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lehms.service.ISerializer;
import org.json.*;

public class JsonSerializer implements ISerializer {

	@Override
	public String serializer(Object o) throws Exception {
		GsonBuilder gsonb = new GsonBuilder(); 
		DateDeserializer ds = new DateDeserializer(); 
		gsonb.registerTypeAdapter(Date.class, ds); 
		Gson gson = gsonb.create(); 
		JSONObject jobject = new JSONObject(gson.toJson(o)); 
		return jobject.toString();
	}

	@Override
	public <T> T Deserializer(String data, Class<T> type) throws Exception {
	
		GsonBuilder gsonb = new GsonBuilder(); 
		DateDeserializer ds = new DateDeserializer(); 
		gsonb.registerTypeAdapter(Date.class, ds); 
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

}
