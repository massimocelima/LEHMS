package com.lehms.persistence;

import java.util.Date;

public class Event {
	public long Id;
	public EventType Type;
	public Date CreatedDate;
	public Object Data;
	public String DataType;
	
	public EventStatus Status;
	public int Attempts;
	public String ErrorMessage;
}
