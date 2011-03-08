package com.lehms.serviceInterface.implementation;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.lehms.messages.dataContracts.Permission;
import com.lehms.messages.dataContracts.RoleDataContract;
import com.lehms.serviceInterface.ISerializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlSerializer implements ISerializer {

	private XStream _xstream;
	private HashMap<Class, String> _mappedObjects = new HashMap<Class, String>(); 
	
	public 	XmlSerializer()
	{ 
		DomDriver driver = new DomDriver();
		_xstream = new XStream(driver);
		_xstream.registerConverter(new SingleValueEnumConverter(Permission.class));
		
		MapObject(RoleDataContract.class);
		MapObject(Permission.class);
	}
	
	@Override
	public String Serializer(Object o) throws Exception {
		MapObject(o.getClass());
		return AddDefaultNamespace(_xstream.toXML(o));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T Deserializer(String data, Class<T> type) throws Exception {
		MapObject(type);
		return (T)_xstream.fromXML(data);
	}

	@Override
	public Object Deserializer(String data, Type type) throws Exception {
		MapObject(type.getClass());
		return _xstream.fromXML(data);
	}

	@Override
	public String GetSerializerContentType() {
		return "text/xml";
	}

	@Override
	public String GetDeserializerContentType() {
		return "text/xml";
	}
	
	private void MapObject(Class type)
	{
		if( ! _mappedObjects.containsKey(type))
		{
			_xstream.alias(type.getSimpleName(), type);
			_mappedObjects.put(type, type.getSimpleName());
		}
	}
	
	private String AddDefaultNamespace(String xml)
	{
		return xml;
	}

}
