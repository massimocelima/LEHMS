package com.lehms.service;

import java.util.Date;

import com.lehms.messages.dataContracts.RosterDataContract;

public interface IRosterResource {

	RosterDataContract GetRosterFor(Date date) throws Exception;
	
}
