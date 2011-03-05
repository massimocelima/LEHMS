package com.lehms.persistence;

import java.util.List;

public interface IEventRepository extends IRepository {

    long saveEvent(Event event) throws Exception;
    boolean deleteEvent(Event event);
    List<Event> fetchEvents() throws Exception;
}
