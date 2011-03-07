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

    public static final String KEY_STATUS = "status";
    public static final String KEY_ATTEMPS = "attempts";
    public static final String KEY_ERROR_MESSAGE = "error_message";

    private ISerializer _serializer;
    private IIdentityProvider _identityProvider;
    
    private String[] _columns = new String[] 
                                           {
												KEY_ROWID, 
												KEY_EVENT_TYPE, 
												KEY_CREATED_DATE, 
												KEY_DATA,
												KEY_DATA_TYPE,
												KEY_STATUS,
												KEY_ATTEMPS,
												KEY_ERROR_MESSAGE
											};
    
    public EventRepository(Context context, 
    		ISerializer serializer, 
    		IIdentityProvider identityProvider) {
		super(context);
		
		_serializer = serializer;
		_identityProvider = identityProvider;
	}

    public long create(Object Data, EventType eventType) throws Exception
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_EVENT_TYPE, eventType.toString());
        initialValues.put(KEY_CREATED_DATE, GetDateValue(new Date()));
        initialValues.put(KEY_DATA, _serializer.Serializer(Data));
        initialValues.put(KEY_DATA_TYPE, Data.getClass().getName());
        
        initialValues.put(KEY_STATUS, EventStatus.Pending.toString());
        initialValues.put(KEY_ATTEMPS, 0);
        initialValues.put(KEY_ERROR_MESSAGE, "");
        
        return getDb().insert(DatabaseHelper.DATABASE_TABLE_EVENT, null, initialValues);
    }
    
	@Override
	public void update(Event event) throws Exception {
		throw new Exception("EventRepository.update(...) Unimplemented");
	}
    
    public boolean delete(Event event)
    {
        return getDb().delete(DatabaseHelper.DATABASE_TABLE_EVENT, KEY_ROWID + "=" + event.Id, null) > 0;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Event> fetchPending(EventType eventType) throws Exception
    {
    	ArrayList<Event> result = new ArrayList<Event>();
    	
        Cursor c = getDb().query(DatabaseHelper.DATABASE_TABLE_EVENT, _columns, 
        		KEY_EVENT_TYPE + "='" + eventType.toString() + "' AND " + KEY_STATUS + "='" + EventStatus.Pending.toString() + "'", null, null, null, null);

        //Cursor c = getDb().query(DatabaseHelper.DATABASE_TABLE_EVENT, _columns, 
        //		null, null, null, null, null);

    	while(c.moveToNext())
    		result.add(Map(c));
        
        c.close();
        
        return result;
    }
        
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Event> fetchPending() throws Exception
    {
    	ArrayList<Event> result = new ArrayList<Event>();
    	
        Cursor c = getDb().query(DatabaseHelper.DATABASE_TABLE_EVENT, _columns, 
        		KEY_STATUS + "='" + EventStatus.Pending.toString() + "'", null, null, null, null);
    	while(c.moveToNext())
    		result.add(Map(c));
        
        c.close();
        
        return result;
    }
    
    private Event Map(Cursor c) throws Exception
    {
		Event e = new Event();
		
		e.Id = c.getLong(0);
		e.Type = EventType.valueOf(c.getString(1));
		e.CreatedDate = new Date(c.getLong(2));
		e.DataType = c.getString(4);

		Class dataTypeClass = Class.forName(e.DataType);
		
    	String data = c.getString(3);
    	e.Data =_serializer.Deserializer(data, dataTypeClass);

		e.Status = EventStatus.valueOf( c.getString(5) );
		e.Attempts = c.getInt(6);
		e.ErrorMessage = c.getString(7);
    	
		return e;
    }
    
    private long GetDateValue(Date date)
    {
    	 return new Date(date.getYear(), date.getMonth(), date.getDate() ).getTime();
    }

}
