package com.lehms.persistence;

import java.util.Date;

public class EventFactory implements IEventFactory {

	@Override
	public Event create(Object data, EventType type) {
		Event e = new Event();
		e.Attempts = 0;
		e.CreatedDate = new Date();
		e.Data = data;
		e.DataType = data.getClass().getName();
		e.Status = EventStatus.Pending;
		e.Type = type;
		return e;
	}

}
