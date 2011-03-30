package com.lehms.serviceInterface;

import com.lehms.ui.clinical.model.Measurement;
import com.lehms.ui.clinical.model.MeasurementSummary;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;

public interface IPreviousMeasurmentProvider {

	Measurement getPreviousMeasurment(String clientId, MeasurementTypeEnum measurementType) throws Exception;
	void putPreviousMeasurment(String clientId, Measurement measurement) throws Exception;

}
