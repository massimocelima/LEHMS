package com.lehms.persistence;

import java.util.Date;

import com.google.inject.Inject;
import com.lehms.messages.dataContracts.JobDetailsDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.ISerializer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RosterRepository extends RepositoryBase implements IRosterRepository {

    public static final String KEY_CREATED = "created";
    public static final String KEY_DATA = "data";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USER_ID = "user_id";

    private ISerializer _serializer; 
    private IIdentityProvider _identityProvider;
    
	@Inject
    public RosterRepository(ISerializer serializer, 
    		Context ctx, 
    		IIdentityProvider identityProvider)
    {
		super(ctx);
		
    	_serializer = serializer;
        _identityProvider = identityProvider;
    }

    public long saveRoster(RosterDataContract roster) throws Exception {
    	
        Cursor c = getDb().query(DatabaseHelper.DATABASE_TABLE_ROSTER, new String[] {KEY_ROWID, KEY_DATA, KEY_CREATED, KEY_USER_ID}, 
        		KEY_ROWID + "=" + GetDateId(roster.Date) + " AND " + 
        		KEY_USER_ID + "='" + _identityProvider.getCurrent().GetFullUsername() + "'", null, null, null, null);
        int count = c.getCount();
        c.close();
        if(count > 0 )
            deleteRoster(roster.Date);
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, GetDateId(roster.Date));
        initialValues.put(KEY_DATA, _serializer.Serializer(roster));
        initialValues.put(KEY_CREATED, (new Date()).getTime());
        initialValues.put(KEY_USER_ID, _identityProvider.getCurrent().GetFullUsername());
        
        return getDb().insert(DatabaseHelper.DATABASE_TABLE_ROSTER, null, initialValues);
    }

    public boolean deleteRoster(Date date) {
        return getDb().delete(DatabaseHelper.DATABASE_TABLE_ROSTER, KEY_ROWID + "=" + GetDateId(date), null) > 0;
    }

    public RosterDataContract fetchRosterFor(Date date) throws Exception {

    	RosterDataContract result = null;
    	
        Cursor c = getDb().query(DatabaseHelper.DATABASE_TABLE_ROSTER, new String[] {KEY_ROWID, KEY_DATA, KEY_CREATED, KEY_USER_ID}, 
        		KEY_ROWID + "=" + GetDateId(date) + " AND " + 
        		KEY_USER_ID + "='" + _identityProvider.getCurrent().GetFullUsername() + "'", null, null, null, null);
        if( c.getCount() > 0 )
        {
        	c.moveToFirst();
        	String data = c.getString(1);
        	result =_serializer.Deserializer(data, RosterDataContract.class);
        	result.LastUpdatedFromServer = new Date( c.getLong(2) );
        }
        
        c.close();
        
        return result;
    }

    private long GetDateId(Date date)
    {
    	 return new Date(date.getYear(), date.getMonth(), date.getDate() ).getTime();
    }

}
