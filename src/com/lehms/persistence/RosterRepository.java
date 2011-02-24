package com.lehms.persistence;

import java.util.Date;

import com.google.inject.Inject;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.service.ISerializer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RosterRepository implements IRosterRepository {

    public static final String KEY_CREATED = "created";
    public static final String KEY_DATA = "data";
    public static final String KEY_ROWID = "_id";

    private DatabaseHelper _dbHelper;
    private SQLiteDatabase _db;
    
    private static final String DATABASE_CREATE =
        "create table roster (_id integer primary key, "
        + "data text not null, created integer not null);";

    private static final String DATABASE_NAME = "LEHMS";
    private static final String DATABASE_TABLE = "roster";
    private static final int DATABASE_VERSION = 1;

    private final Context _context;
    private ISerializer _serializer; 
    
	@Inject
    public RosterRepository(ISerializer serializer, Context ctx)
    {
    	_serializer = serializer;
        _context = ctx;
    }

    public void open() throws SQLException {
        _dbHelper = new DatabaseHelper(_context);
        _db = _dbHelper.getWritableDatabase();
    }

    public void close() {
        _dbHelper.close();
    }

    public long saveRoster(RosterDataContract roster) throws Exception {
    	
        Cursor c = _db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DATA, KEY_CREATED}, 
        		KEY_ROWID + "=" + GetDateId(roster.Date), null, null, null, null);
        if(c.getCount() > 0 )
        	deleteRoster(roster.Date);
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, GetDateId(roster.Date));
        initialValues.put(KEY_DATA, _serializer.serializer(roster));
        initialValues.put(KEY_CREATED, (new Date()).getTime());
        
        return _db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteRoster(Date date) {
        return _db.delete(DATABASE_TABLE, KEY_ROWID + "=" + GetDateId(date), null) > 0;
    }

    public RosterDataContract fetchRosterFor(Date date) throws Exception {

    	RosterDataContract result = null;
    	
        Cursor c = _db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DATA, KEY_CREATED}, 
        		KEY_ROWID + "=" + GetDateId(date), null, null, null, null);
        if( c.getCount() > 0 )
        {
        	c.moveToFirst();
        	String data = c.getString(1);
        	result =_serializer.Deserializer(data, RosterDataContract.class);
        	result.LastUpdatedFromServer = new Date( c.getLong(2) );
        }
        
        return result;
    }

    private long GetDateId(Date date)
    {
    	 return new Date(date.getYear(), date.getMonth(), date.getDate() ).getTime();
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
            //        + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS roster");
            onCreate(db);
        }
    }
}
