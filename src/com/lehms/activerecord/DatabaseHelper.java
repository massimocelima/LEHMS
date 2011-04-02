package com.lehms.activerecord;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import dalvik.system.DexFile;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;

class DatabaseHelper extends SQLiteOpenHelper
{
  private static final String AA_DB_NAME = "AA_DB_NAME";
  private static final String AA_DB_VERSION = "AA_DB_VERSION";
  private Context mContext;

  public DatabaseHelper(Context context)
  {
    super(context, getDBName(context), null, getDBVersion(context));
    this.mContext = context;
  }

  public void onCreate(SQLiteDatabase db)
  {
    ArrayList tables = getEntityClasses(this.mContext);

    Log.i("ActiveAndroid", "Creating " + tables.size() + " tables");

    for (Object tableObject : tables) {
    	Class table = (Class)tableObject;
      ArrayList fields = ReflectionUtils.getTableFields(table);
      ArrayList definitions = new ArrayList();

      for (Object fieldObj : fields) {
    	  Field field = (Field)fieldObj;
        Class fieldType = field.getType();
        String fieldName = ReflectionUtils.getColumnName(field);
        Integer fieldLength = ReflectionUtils.getColumnLength(field);
        String definition = null;

        if (ReflectionUtils.typeIsSQLiteFloat(fieldType)) {
          definition = fieldName + " FLOAT";
        }
        else if (ReflectionUtils.typeIsSQLiteInteger(fieldType)) {
          definition = fieldName + " INTEGER";
        }
        else if (ReflectionUtils.typeIsSQLiteString(fieldType)) {
          definition = fieldName + " TEXT";
        }

        if (definition != null) {
          if ((fieldLength != null) && (fieldLength.intValue() > 0)) {
            definition = definition + "(" + fieldLength + ")";
          }

          if (fieldName.equals("Id")) {
            definition = definition + " PRIMARY KEY AUTOINCREMENT";
          }

          definitions.add(definition);
        }
      }

      String sql = StringUtils.format("CREATE TABLE {0} ({1});", new Object[] { ReflectionUtils.getTableName(table), 
        StringUtils.join(definitions, ", ") });

      Log.i("ActiveAndroid", sql);

      db.execSQL(sql);
    }
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    ArrayList tables = getEntityClasses(this.mContext);
    for (Object tableObj : tables) {
    	Class table = (Class)tableObj;
      db.execSQL("DROP TABLE IF EXISTS " + ReflectionUtils.getTableName(table));
    }

    onCreate(db);
  }

  private static ArrayList<Class<? extends ActiveRecordBase<?>>> getEntityClasses(Context context)
  {
    ArrayList entityClasses = new ArrayList();
    try
    {
      String path = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
      DexFile dexfile = new DexFile(path);
      Enumeration entries = dexfile.entries();

      while (entries.hasMoreElements()) {
        String name = (String)entries.nextElement();
        Class discoveredClass = null;
        Class superClass = null;
        try
        {
          discoveredClass = Class.forName(name, true, context.getClass().getClassLoader());
          superClass = discoveredClass.getSuperclass();
        }
        catch (ClassNotFoundException e) {
          Log.e("ActiveAndroid", e.getMessage());
        }

        if ((discoveredClass == null) || (superClass == null) || 
          (!discoveredClass.getSuperclass().equals(ActiveRecordBase.class))) continue;
        entityClasses.add(discoveredClass);
      }

    }
    catch (IOException e)
    {
      Log.e("ActiveAndroid", e.getMessage());
    }
    catch (PackageManager.NameNotFoundException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }

    return entityClasses;
  }

  private static String getDBName(Context context) {
    String aaName = getMetaDataString(context, "AA_DB_NAME");

    if (aaName == null) {
      aaName = "Application.db";
    }

    return aaName;
  }

  private static int getDBVersion(Context context) {
    Integer aaVersion = getMetaDataInteger(context, "AA_DB_VERSION");

    if ((aaVersion == null) || (aaVersion.intValue() == 0)) {
      aaVersion = Integer.valueOf(1);
    }

    return aaVersion.intValue();
  }

  private static String getMetaDataString(Context context, String name) {
    String value = null;

    PackageManager pm = context.getPackageManager();
    try
    {
      ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 128);
      value = ai.metaData.getString(name);
    }
    catch (Exception e)
    {
      Log.w("ActiveAndroid", "Couldn't find meta data string: " + name);
    }

    return value;
  }

  private static Integer getMetaDataInteger(Context context, String name) {
    Integer value = null;

    PackageManager pm = context.getPackageManager();
    try
    {
      ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 128);
      value = Integer.valueOf(ai.metaData.getInt(name));
    }
    catch (Exception e)
    {
      Log.w("ActiveAndroid", "Couldn't find meta data string: " + name);
    }

    return value;
  }
}