package com.lehms.util;

import java.util.List;

import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.DoctorDataContract;
import com.lehms.serviceInterface.IPreviousMeasurmentProvider;
import com.lehms.ui.clinical.model.MeasurementSummary;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;

public interface IMeasurmentReportProvider {
	
	MeasurmentReportDocument createReport(ClientDataContract client, IPreviousMeasurmentProvider provider) throws Exception;
	MeasurmentReportDocument createReport(ClientDataContract client, List<MeasurementSummary> measurements, MeasurementTypeEnum type) throws Exception;
	String getFaxNumberForEmail(String faxNumber);
	String getFaxNumberForEmail(String faxNumber, DoctorDataContract doctor);
}
