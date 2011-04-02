package com.lehms.activerecord;

import java.util.Set;

public interface IApplicationContext {

	DatabaseManager getDatabaseManager();
	void addEntity(ActiveRecordBase<?> entity);
	void addEntities(Set<ActiveRecordBase<?>> entities);
	void removeEntity(ActiveRecordBase<?> entity);
	ActiveRecordBase<?> getEntity(Class<? extends ActiveRecordBase<?>> entityType, long id);
	  
}
