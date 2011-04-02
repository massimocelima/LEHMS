package com.lehms.activerecord;

import android.util.Log;
import com.lehms.activerecord.annotation.Column;
import com.lehms.activerecord.annotation.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

final class ReflectionUtils
{
  public static Integer getColumnLength(Field field)
  {
    Integer retVal = null;

    Column annotation = (Column)field.getAnnotation(Column.class);
    if (annotation != null) {
      int length = annotation.length();
      if (length > -1) {
        retVal = Integer.valueOf(length);
      }
    }

    return retVal;
  }

  public static String getColumnName(Field field) {
    Column annotation = (Column)field.getAnnotation(Column.class);
    if (annotation != null) {
      return annotation.name();
    }

    return null;
  }

  public static ArrayList<Field> getTableFields(Class<?> type) {
    ArrayList typeFields = new ArrayList();
    try
    {
      typeFields.add(type.getSuperclass().getDeclaredField("mId"));
    }
    catch (SecurityException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }
    catch (NoSuchFieldException e) {
      Log.e("ActiveAndroid", e.getMessage());
    }

    Field[] fields = type.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(Column.class)) {
        typeFields.add(field);
      }
    }

    return typeFields;
  }

  public static String getTableName(Class<?> type) {
    String tableName = null;
    Table annotation = (Table)type.getAnnotation(Table.class);

    if (annotation != null) {
      tableName = annotation.name();
    }
    else {
      tableName = type.getSimpleName();
    }

    return tableName;
  }

  public static boolean typeIsSQLiteFloat(Class<?> type)
  {
    return (type.equals(Double.class)) || (type.equals(Double.TYPE)) || (type.equals(Float.class)) || 
      (type.equals(Float.TYPE));
  }

  public static boolean typeIsSQLiteInteger(Class<?> type)
  {
    return (type.equals(Boolean.class)) || 
      (type.equals(Boolean.TYPE)) || 
      (type.equals(java.util.Date.class)) || 
      (type.equals(java.sql.Date.class)) || 
      (type.equals(Integer.class)) || 
      (type.equals(Integer.TYPE)) || 
      (type.equals(Long.class)) || 
      (type.equals(Long.TYPE)) || (
      (!type.isPrimitive()) && 
      (type.getSuperclass() != null) && 
      (type.getSuperclass().equals(ActiveRecordBase.class)));
  }

  public static boolean typeIsSQLiteString(Class<?> type)
  {
    return (type.equals(String.class)) || 
      (type.equals(Character.TYPE)) || (type.equals(UUID.class));
  }
}