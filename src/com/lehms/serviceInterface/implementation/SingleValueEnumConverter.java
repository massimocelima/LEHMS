package com.lehms.serviceInterface.implementation;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class SingleValueEnumConverter  extends AbstractSingleValueConverter 
{ 
    private final Class enumType; 
 
    public SingleValueEnumConverter(Class type) 
    { 
        this.enumType = type; 
    } 
 
    public boolean canConvert(Class c) 
    { 
        return c.equals(enumType); 
    } 
 
    public Object fromString(String value) 
    { 
        return Enum.valueOf(enumType, value); 
    } 
} 

