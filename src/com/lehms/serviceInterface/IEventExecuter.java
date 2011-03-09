package com.lehms.serviceInterface;

import com.lehms.persistence.Event;

public interface IEventExecuter {

	Object ExecuteEvent(Event event) throws Exception;
	
}
