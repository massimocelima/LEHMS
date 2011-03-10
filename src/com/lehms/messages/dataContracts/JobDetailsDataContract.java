package com.lehms.messages.dataContracts;

import java.io.Serializable;
import java.util.Date;

public class JobDetailsDataContract implements Serializable {

	public String JobId;
	public Date ScheduledStartTime;
    public float ExtendOfService;
    public String Type;
    public String UnitOfMeasure;
    public ClientDataContract Client;
    
	public Date StartTime;
	public Date EndTime;
	public Date AddmissionDate;
	public String Comments;
	public String Description;
    public String FundingCode;

    public float TKLM;
    public float SKLM;
    
    public int Status;
    public JobStatusDataContract StatusEnum()
    {
    	return JobStatusDataContract.get(Status);
    }

    public void Reset() throws Exception
    {
    	Status = JobStatusDataContract.Pending.getCode();
    	StartTime = null;
    	EndTime = null;
    }

    public void Start(float kilometersTravelled) throws Exception
    {
    	if( StatusEnum() != JobStatusDataContract.Pending)
    		throw new Exception("Job is not in the correct status of pending");
    	
    	Status = JobStatusDataContract.Started.getCode();
    	StartTime = new Date();
    	TKLM = kilometersTravelled;
    }

    public void End(float kilometersTravelled) throws Exception
    {
    	if( StatusEnum() != JobStatusDataContract.Started)
    		throw new Exception("Job is not in the correct status of started");
    	
    	Status = JobStatusDataContract.Finished.getCode();
    	EndTime = new Date();
    	SKLM = kilometersTravelled;
    }
}
