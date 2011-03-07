package com.lehms.persistence;

import java.util.List;

public interface IEventRepository extends IRepository {

    public long create(Object Data, EventType eventType) throws Exception;
    boolean delete(Event event);
    public void update(Event event) throws Exception;
	public List<Event> fetchPending(EventType eventType) throws Exception;
	public List<Event> fetchPending() throws Exception;
}
