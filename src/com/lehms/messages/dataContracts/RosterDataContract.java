package com.lehms.messages.dataContracts;

import java.util.Date;
import java.util.List;

public class RosterDataContract {

	public RosterDataContract()
	{
		LastUpdatedFromServer = new Date();
	}
	
	public Date Date;
	public List<JobDetailsDataContract> Jobs;
	public Date LastUpdatedFromServer;
}
