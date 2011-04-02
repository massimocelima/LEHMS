package com.lehms.activerecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lehms.LehmsApplication;
import com.lehms.activerecord.annotation.Column;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

public abstract class ActiveRecordBase<T>
{

  @Column(name="Id", length = 0)
  private Long mId = null;
  private IApplicationContext mApplication;
  private Context mContext;
  private DatabaseManager mDatabaseManager;
  private String mTableName = ReflectionUtils.getTableName(getClass());

  public ActiveRecordBase(Context context)
  {	  
    this.mApplication = (IApplicationContext)context.getApplicationContext();
    this.mContext = context;
    this.mDatabaseManager = this.mApplication.getDatabaseManager();
    
    this.mApplication.addEntity(this);
  }

  public Long getId()
  {
    return this.mId;
  }

  public Context getContext()
  {
    return this.mContext;
  }

  public String getTableName()
  {
    return this.mTableName;
  }

  public void delete()
  {
    SQLiteDatabase db = this.mDatabaseManager.openDB();
    db.delete(this.mTableName, "Id=?", new String[] { getId().toString() });
    this.mDatabaseManager.closeDB();

    this.mApplication.removeEntity(this);
  }

  public void save()
  {
    SQLiteDatabase db = this.mDatabaseManager.openDB();
    ContentValues values = new ContentValues();

    for (Field field : ReflectionUtils.getTableFields(getClass())) {
      String fieldName = ReflectionUtils.getColumnName(field);
      Class fieldType = field.getType();

      field.setAccessible(true);
      try
      {
        if (field.get(this) == null) {
          continue;
        }
        Object value = field.get(this);

        if ((fieldType.equals(Boolean.class)) || (fieldType.equals(Boolean.TYPE))) {
          values.put(fieldName, (Boolean)value);
        }
        else if (fieldType.equals(java.util.Date.class)) {
          values.put(fieldName, Long.valueOf(((java.util.Date)field.get(this)).getTime()));
        }
        else if (fieldType.equals(java.sql.Date.class)) {
          values.put(fieldName, Long.valueOf(((java.sql.Date)field.get(this)).getTime()));
        }
        else if ((fieldType.equals(Double.class)) || (fieldType.equals(Double.TYPE))) {
          values.put(fieldName, (Double)value);
        }
        else if ((fieldType.equals(Float.class)) || (fieldType.equals(Float.TYPE))) {
          values.put(fieldName, (Float)value);
        }
        else if ((fieldType.equals(Integer.class)) || (fieldType.equals(Integer.TYPE))) {
          values.put(fieldName, (Integer)value);
        }
        else if ((fieldType.equals(Long.class)) || (fieldType.equals(Long.TYPE))) {
          values.put(fieldName, (Long)value);
        }
        else if ((fieldType.equals(String.class)) || (fieldType.equals(Character.TYPE))) {
          values.put(fieldName, value.toString());
        }
        else if ((fieldType.equals(UUID.class))) {
          values.put(fieldName, value.toString());
        } else {
          if ((fieldType.isPrimitive()) || (fieldType.getSuperclass() == null) || 
            (!fieldType.getSuperclass().equals(ActiveRecordBase.class)))
            continue;
          long entityId = ((ActiveRecordBase)value).getId().longValue();

          values.put(fieldName, Long.valueOf(entityId));
        }
      }
      catch (IllegalArgumentException e)
      {
        Log.e("ActiveAndroid", e.getMessage());
      }
      catch (IllegalAccessException e) {
        Log.e("ActiveAndroid", e.getMessage());
      }
    }

    if (this.mId == null) {
      this.mId = Long.valueOf(db.insert(this.mTableName, null, values));
    }
    else {
      db.update(this.mTableName, values, "Id=" + this.mId, null);
    }

    this.mDatabaseManager.closeDB();
  }

  protected <E> ArrayList<E> getMany(Class<? extends ActiveRecordBase<E>> type, String through)
  {
    String table = ReflectionUtils.getTableName(type);
    return query(this.mContext, type, null, StringUtils.format("{0}.{1}={2}", new Object[] { table, through, getId() }));
  }

  public static <T> T load(Context context, Class<? extends ActiveRecordBase<?>> type, long id)
  {
    String tableName = ReflectionUtils.getTableName(type);
    String selection = StringUtils.format("{0}.Id = {1}", new Object[] { tableName, Long.valueOf(id) });

    return querySingle(context, type, null, selection);
  }

  public static <T> T first(Context context, Class<? extends ActiveRecordBase<?>> type)
  {
    return querySingle(context, type, null);
  }

  public static <T> T last(Context context, Class<? extends ActiveRecordBase<?>> type)
  {
    return querySingle(context, type, null, null, "Id DESC");
  }

  public static <T> int delete(Context context, Class<? extends ActiveRecordBase<?>> type)
  {
    return delete(context, type, "1");
  }

  public static <T> boolean delete(Context context, Class<? extends ActiveRecordBase<?>> type, long id)
  {
    return delete(context, type, "Id=" + id) > 0;
  }

  public static <T> int delete(Context context, Class<? extends ActiveRecordBase<?>> type, String where)
  {
    DatabaseManager dbManager = ((IApplicationContext)context.getApplicationContext()).getDatabaseManager();
    SQLiteDatabase db = dbManager.openDB();
    String table = ReflectionUtils.getTableName(type);

    int count = db.delete(table, where, null);
    dbManager.closeDB();

    return count;
  }

  public static <T> ArrayList<T> query(Context context, Class<? extends ActiveRecordBase<?>> type)
  {
    return query(context, type, null, null, null, null, null, null);
  }

  public static <T> ArrayList<T> query(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns)
  {
    return query(context, type, columns, null, null, null, null, null);
  }

  public static <T> ArrayList<T> query(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns, String where)
  {
    return query(context, type, columns, where, null, null, null, null);
  }

  public static <T> ArrayList<T> query(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns, String where, String orderBy)
  {
    return query(context, type, columns, where, null, null, orderBy, null);
  }

  public static <T> ArrayList<T> query(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns, String where, String orderBy, String limit)
  {
    return query(context, type, columns, where, null, null, orderBy, limit);
  }

  public static <T> ArrayList<T> query(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns, String where, String groupBy, String having, String orderBy, String limit)
  {
    DatabaseManager dbManager = ((IApplicationContext)context.getApplicationContext()).getDatabaseManager();
    SQLiteDatabase db = dbManager.openDB();
    String table = ReflectionUtils.getTableName(type);

    Cursor cursor = db.query(table, columns, where, null, groupBy, having, orderBy, limit);

    ArrayList entities = processCursor(context, type, cursor);

    cursor.close();
    dbManager.closeDB();

    return entities;
  }

  public static <T> T querySingle(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns)
  {
    return (T)getFirst(query(context, type, columns, null, null, "1"));
  }

  public static <T> T querySingle(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns, String where)
  {
    return (T)getFirst(query(context, type, columns, where, null, "1"));
  }

  public static <T> T querySingle(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns, String where, String orderBy)
  {
    return (T)getFirst(query(context, type, columns, where, orderBy, "1"));
  }

  public static <T> T querySingle(Context context, Class<? extends ActiveRecordBase<?>> type, String[] columns, String selection, String groupBy, String having, String orderBy)
  {
    return (T)getFirst(query(context, type, columns, selection, groupBy, having, orderBy, "1"));
  }

  public static final <T> ArrayList<T> rawQuery(Context context, Class<? extends ActiveRecordBase<?>> type, String sql)
  {
    DatabaseManager dbManager = ((IApplicationContext)context.getApplicationContext()).getDatabaseManager();
    SQLiteDatabase db = dbManager.openDB();
    Cursor cursor = db.rawQuery(sql, null);

    ArrayList entities = processCursor(context, type, cursor);

    cursor.close();
    dbManager.closeDB();

    return entities;
  }

  public static final <T> T rawQuerySingle(Context context, Class<? extends ActiveRecordBase<?>> type, String sql)
  {
    return (T)getFirst(rawQuery(context, type, sql));
  }

  private static <T> T getFirst(ArrayList<T> entities)
  {
    if (entities.size() > 0) {
      return entities.get(0);
    }

    return null;
  }

  private static final <T> ArrayList<T> processCursor(Context context, Class<? extends ActiveRecordBase<?>> type, Cursor cursor)
  {
    ArrayList entities = new ArrayList();
    try
    {
      Constructor entityConstructor = type.getConstructor(new Class[] { Context.class });

      if (cursor.moveToFirst()) {
        do {
          Object entity = entityConstructor.newInstance(new Object[] { context });
          ((ActiveRecordBase)entity).loadFromCursor(context, type, cursor);
          entities.add(entity);
        }
        while (
          cursor.moveToNext());
      }
    }
    catch (IllegalArgumentException e)
    {
      Log.e("ActiveAndroid", e.getMessage());
    }
    catch (InstantiationException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }
    catch (IllegalAccessException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }
    catch (InvocationTargetException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }
    catch (SecurityException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }
    catch (NoSuchMethodException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }

    return entities;
  }

  private final void loadFromCursor(Context context, Class<? extends ActiveRecordBase<?>> type, Cursor cursor) {
    ArrayList fields = ReflectionUtils.getTableFields(type);

    for (Object fieldObj : fields) {
    	Field field = (Field)fieldObj;
      String fieldName = ReflectionUtils.getColumnName(field);
      Class fieldType = field.getType();
      int columnIndex = cursor.getColumnIndex(fieldName);

      if (columnIndex < 0)
      {
        continue;
      }
      field.setAccessible(true);
      try
      {
        if ((!fieldType.isPrimitive()) && (fieldType.getSuperclass() != null) && 
          (fieldType.getSuperclass().equals(ActiveRecordBase.class)))
        {
          long entityId = cursor.getLong(columnIndex);
          Class entityType = fieldType;

          IApplicationContext application = (IApplicationContext)context.getApplicationContext();
          ActiveRecordBase entity = application.getEntity(entityType, entityId);

          if (entity == null) {
            entity = (ActiveRecordBase)load(context, entityType, entityId);
          }

          field.set(this, entity);
        }
        else if ((fieldType.equals(Boolean.class)) || (fieldType.equals(Boolean.TYPE))) {
          field.set(this, Boolean.valueOf(cursor.getInt(columnIndex) != 0));
        }
        else if (fieldType.equals(Character.TYPE)) {
          field.set(this, Character.valueOf(cursor.getString(columnIndex).charAt(0)));
        }
        else if (fieldType.equals(java.util.Date.class)) {
          field.set(this, new java.util.Date(cursor.getLong(columnIndex)));
        }
        else if (fieldType.equals(java.sql.Date.class)) {
          field.set(this, new java.sql.Date(cursor.getLong(columnIndex)));
        }
        else if ((fieldType.equals(Double.class)) || (fieldType.equals(Double.TYPE))) {
          field.set(this, Double.valueOf(cursor.getDouble(columnIndex)));
        }
        else if ((fieldType.equals(Float.class)) || (fieldType.equals(Float.TYPE))) {
          field.set(this, Float.valueOf(cursor.getFloat(columnIndex)));
        }
        else if ((fieldType.equals(Integer.class)) || (fieldType.equals(Integer.TYPE))) {
          field.set(this, Integer.valueOf(cursor.getInt(columnIndex)));
        }
        else if ((fieldType.equals(Long.class)) || (fieldType.equals(Long.TYPE))) {
          field.set(this, Long.valueOf(cursor.getLong(columnIndex)));
        }
        else if (fieldType.equals(String.class)) {
          field.set(this, cursor.getString(columnIndex));
        }
        else if (fieldType.equals(UUID.class)) {
            field.set(this, UUID.fromString(cursor.getString(columnIndex)));
          }
      }
      catch (IllegalArgumentException e)
      {
        Log.e("ActiveAndroid", e.getMessage());
      }
      catch (IllegalAccessException e) {
        Log.e("ActiveAndroid", e.getMessage());
      }
      catch (SecurityException e) {
        Log.e("ActiveAndroid", e.getMessage());
      }
    }
  }

  public boolean equals(Object obj)
  {
    ActiveRecordBase other = (ActiveRecordBase)obj;

    return (this.mTableName == other.mTableName) && (this.mId == other.mId);
  }
}