package com.lehms.persistence;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RepositoryBase implements IRepository{

	private DatabaseHelper _dbHelper;
	private SQLiteDatabase _db;
	private Context _context;
	
	public RepositoryBase(Context context)
	{
		_context = context;
	}
	
    public void open() throws SQLException {
        _dbHelper = new DatabaseHelper(_context);
        _db = _dbHelper.getWritableDatabase();
    }

    public void close() {
        _db.close();
        _dbHelper.close();
    }
	
	public Context getContext()
	{
		return _context;
	}
	
	public SQLiteDatabase getDb()
	{
		return _db;
	}
	
	public DatabaseHelper getDatabaseHelper()
	{
		return _dbHelper;
	}
}
