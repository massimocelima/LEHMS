package com.lehms.persistence;

import java.util.Date;

import android.database.SQLException;

import com.lehms.messages.dataContracts.RosterDataContract;

public interface IRosterRepository {

	void open() throws Exception;
	void close();
    long saveRoster(RosterDataContract roster) throws Exception;
    boolean deleteRoster(Date date);
    RosterDataContract fetchRosterFor(Date date) throws Exception;
    
}
