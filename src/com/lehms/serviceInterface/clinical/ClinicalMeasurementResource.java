package com.lehms.serviceInterface.clinical;

import java.util.Date;

import android.app.Application;

import com.google.inject.Inject;
import com.lehms.messages.CreateMeasurementResponse;
import com.lehms.messages.GetMeasurementSummaryListResponse;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IChannel;
import com.lehms.serviceInterface.IChannelFactory;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.ui.clinical.model.BloodPressureMeasurement;
import com.lehms.ui.clinical.model.BloodSugerLevelMeasurement;
import com.lehms.ui.clinical.model.ECGMeasurement;
import com.lehms.ui.clinical.model.INRMeasurement;
import com.lehms.ui.clinical.model.Measurement;
import com.lehms.ui.clinical.model.MeasurementType;
import com.lehms.ui.clinical.model.RespiratoryRateMeasurement;
import com.lehms.ui.clinical.model.SPO2Measurement;
import com.lehms.ui.clinical.model.TemperatureMeasurement;
import com.lehms.ui.clinical.model.UrineMeasurement;
import com.lehms.ui.clinical.model.WeightMeasurement;

public class ClinicalMeasurementResource implements IClinicalMeasurementResource {

	private IChannelFactory _channelFactory;
	private IProfileProvider _profileProvider;
	
	@Inject
	private ClinicalMeasurementResource(IChannelFactory channelFactory, 
			IProfileProvider profileProvider, 
			IDepartmentProvider departmentProvider)
	{
		_profileProvider = profileProvider;
		_channelFactory = channelFactory;
	}
	
	public GetMeasurementSummaryListResponse GetMeasurements(String client, 
			MeasurementType type, 
			int skip, 
			int take) throws Exception {
		
		return GetSummaryChannel().Get(skip, take, "", "ClientId = " + client + " AND Type = " + type.Type.toString(), 
				GetMeasurementSummaryListResponse.class);
	}
	
	public GetMeasurementSummaryListResponse GetPreviousMeasurements(String client) throws Exception
	{
		return GetPreviousMeasurmentChannel().Get(client, GetMeasurementSummaryListResponse.class);
	}
	
	public CreateMeasurementResponse Save(Measurement measurement) throws Exception
	{
		return GetMeasurementChannelFor(measurement).Create(measurement, CreateMeasurementResponse.class);
	}
	
	private IChannel GetSummaryChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetMeasurementSummaryResourceEndPoint());
	}

	private IChannel GetPreviousMeasurmentChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetPreviousMeasurmentChannelResourceEndPoint());
	}

	private IChannel GetPreviousMeasurementChannel()
	{
		return _channelFactory.Create(_profileProvider.getProfile().GetPreviousMeasurementResourceEndPoint());
	}
	
	private IChannel GetMeasurementChannelFor(Measurement measurement)
	{
		String endPoint = "";
		
		if( measurement.getClass().equals(BloodPressureMeasurement.class))
			endPoint = _profileProvider.getProfile().GetBloodPressureMeasurementResourceEndPoint();
		if( measurement.getClass().equals(BloodSugerLevelMeasurement.class))
			endPoint = _profileProvider.getProfile().GetBloodSugerLevelMeasurementResourceEndPoint();
		if( measurement.getClass().equals(ECGMeasurement.class))
			endPoint = _profileProvider.getProfile().GetECGMeasurementResourceEndPoint();
		if( measurement.getClass().equals(INRMeasurement.class))
			endPoint = _profileProvider.getProfile().GetINRMeasurementResourceEndPoint();
		if( measurement.getClass().equals(SPO2Measurement.class))
			endPoint = _profileProvider.getProfile().GetSPO2MeasurementResourceEndPoint();
		if( measurement.getClass().equals(TemperatureMeasurement.class))
			endPoint = _profileProvider.getProfile().GetTemperatureMeasurementResourceEndPoint();
		if( measurement.getClass().equals(UrineMeasurement.class))
			endPoint = _profileProvider.getProfile().GetUrineMeasurementResourceEndPoint();
		if( measurement.getClass().equals(WeightMeasurement.class))
			endPoint = _profileProvider.getProfile().GetWeightMeasurementResourceEndPoint();
		if( measurement.getClass().equals(RespiratoryRateMeasurement.class))
			endPoint = _profileProvider.getProfile().GetRespiratoryRateMeasurementResourceEndPoint();
		return _channelFactory.Create(endPoint);

	}
}
