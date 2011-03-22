package com.lehms.ui.clinical.device;

import java.io.Serializable;

public interface IMeasurementDevice<T> extends Serializable {

	String getName();
	T readMeasurement() throws Exception;
	Boolean isDefault();
	String getAddress();

	void connect() throws Exception;
	void close() throws Exception;

}
