package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RosterDataContract  implements Serializable {

	public RosterDataContract()
	{
		LastUpdatedFromServer = new Date();
	}
	
	public Date Date;
	public List<JobDetailsDataContract> Jobs;
	public Date LastUpdatedFromServer;
}
