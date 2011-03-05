package com.lehms.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.ISerializer;

public class EventRepository extends RepositoryBase implements IEventRepository {

    public static final String KEY_ROWID = "_id";
	public static final String KEY_EVENT_TYPE = "event_type";
    public static final String KEY_CREATED_DATE = "created_date";
    public static final String KEY_DATA = "data";
    public static final String KEY_DATA_TYPE = "data_type";

    private ISerializer _serializer;
    private IIdentityProvider _identityProvider;
    
    public EventRepository(Context context, 
    		ISerializer serializer, 
    		IIdentityProvider identityProvider) {
		super(context);
		
		_serializer = serializer;
		_identityProvider = identityProvider;
	}

    public long saveEvent(Event event) throws Exception
    {
        ContentValues initialValues = new ContentValues();
        //initialValues.put(KEY_ROWID, GetDateId(roster.Date));
        initialValues.put(KEY_EVENT_TYPE, event.Type.toString());
        initialValues.put(KEY_CREATED_DATE, GetDateValue(event.CreatedDate));
        initialValues.put(KEY_DATA, _serializer.Serializer(event.Data));
        initialValues.put(KEY_DATA_TYPE, event.Data.getClass().getName());
        
        return getDb().insert(DatabaseHelper.DATABASE_TABLE_EVENT, null, initialValues);
    }
    
    public boolean deleteEvent(Event event)
    {
        return getDb().delete(DatabaseHelper.DATABASE_TABLE_EVENT, KEY_ROWID + "=" + event.Id, null) > 0;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Event> fetchEvents() throws Exception
    {
    	ArrayList<Event> result = new ArrayList<Event>();
    	
        Cursor c = getDb().query(DatabaseHelper.DATABASE_TABLE_EVENT, new String[] {KEY_ROWID, KEY_EVENT_TYPE, KEY_CREATED_DATE, KEY_DATA}, 
        		null, null, null, null, null);
    	while(c.moveToNext())
    	{
    		Event e = new Event();
    		
    		e.Id = c.getLong(0);
    		e.Type = EventType.valueOf(c.getString(1));
    		e.CreatedDate = new Date(c.getLong(2));
    		e.DataType = c.getString(4);
    		
    		Class dataTypeClass = Class.forName(e.DataType);
    		
        	String data = c.getString(3);
        	e.Data =_serializer.Deserializer(data, dataTypeClass);
        }
        
        c.close();
        
        return result;
    }
    
    private long GetDateValue(Date date)
    {
    	 return new Date(date.getYear(), date.getMonth(), date.getDate() ).getTime();
    }

}
