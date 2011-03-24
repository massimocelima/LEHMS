package com.lehms.serviceInterface.clinical;

import java.util.Date;

import com.lehms.messages.CreateMeasurementResponse;
import com.lehms.messages.GetMeasurementSummaryListResponse;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.ui.clinical.model.Measurement;
import com.lehms.ui.clinical.model.MeasurementType;

public interface IClinicalMeasurementResource {

	public GetMeasurementSummaryListResponse GetMeasurements(String client, 
			MeasurementType type, 
			int skip, 
			int take) throws Exception;
	
	public GetMeasurementSummaryListResponse GetPreviousMeasurements(String client) throws Exception;
	
	CreateMeasurementResponse Save(Measurement measurement) throws Exception;
}
