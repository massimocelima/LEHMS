package com.lehms.persistence;

public interface IEventFactory {

	Event create(Object data, EventType type);
	
}
