package com.lehms.util;

import java.util.Date;
import java.util.List;

import com.lehms.UIHelper;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.DoctorDataContract;
import com.lehms.serviceInterface.IPreviousMeasurmentProvider;
import com.lehms.ui.clinical.model.Measurement;
import com.lehms.ui.clinical.model.MeasurementSummary;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;

public class MeasurmentReportProvider implements IMeasurmentReportProvider{
	
	public MeasurmentReportDocument createReport(ClientDataContract client, IPreviousMeasurmentProvider provider) throws Exception
	{
		MeasurmentReportDocument result = new MeasurmentReportDocument();
		result.Subject = createSubject(client);
		result.Body = createBodyFromMeasurments(provider, client.ClientId);
		return result;
	}

	public MeasurmentReportDocument createReport(ClientDataContract client, List<MeasurementSummary> measurements, MeasurementTypeEnum type) throws Exception
	{
		MeasurmentReportDocument result = new MeasurmentReportDocument();
		result.Subject = createSubject(client);
		result.Body = createBodyFromMeasurments( measurements, type);
		return result;
	}
	
	public String getFaxNumberForEmail(String faxNumber)
	{
		return faxNumber + "@fax.telstra.com";
	}

	public String getFaxNumberForEmail(String faxNumber, DoctorDataContract doctor)
	{
		String result = "@fax.telstra.com";
		result = doctor.FaxNumber + result;
		result = "Dr_" + doctor.FirstName + "_" + doctor.LastName + "." + result;
		return result;
	}

	private String createSubject(ClientDataContract client)
	{
        String result = client.FirstName + " " + client.LastName + ", ";
        if (client.Address != null)
        {
            if (client.Address.StreetNumber != "")
            {
                result += client.Address.StreetNumber + " ";
            }
            result += client.Address.Street + ", " + client.Address.Suburb + " " + client.Address.State + " " + client.Address.Postcode + ", ";
        }
        result += client.Phone;
        if (client.DateOfBirth != null)
        {
            result += ", DOB: " +  UIHelper.FormatShortDate(client.DateOfBirth);
        }
        return result;		
	}
	
	public String createBodyFromMeasurments(IPreviousMeasurmentProvider provider, String clientId) throws Exception
	{
		String body = "Clinical details " + UIHelper.FormatShortDate(new Date())+ "\r\n\r\n";
		body += "------------------------------------\r\n";
		for(MeasurementTypeEnum type : MeasurementTypeEnum.values())
		{
			Measurement measurement = provider.getPreviousMeasurment(clientId, type);
			if(measurement != null)
			{
				String typeString = getMeasurementTitle(type);
				if( ! typeString.equals(""))
				{
					body += typeString;
					body += "\r\n";
				}
				body += measurement.toString();
				body += "\r\n";
				body += "------------------------------------";
				body += "\r\n";
			}
		}
		return body;
	}
	
	public String createBodyFromMeasurments(List<MeasurementSummary> measurments, MeasurementTypeEnum type) throws Exception
	{
		String body = "Clinical details " + UIHelper.FormatShortDate(new Date())+ "\r\n\r\n";
		body += "------------------------------------\r\n";
		for(MeasurementSummary measurement : measurments)
		{
			if(measurement != null)
			{
				String typeString = getMeasurementTitle(type);
				if( ! typeString.equals(""))
				{
					body += typeString;
					body += "\r\n";
				}
				body += measurement.Description;
				body += "\r\n";
				body += UIHelper.FormatLongDateTime( measurement.CreatedDate );
				body += "\r\n";
				body += "------------------------------------";
				body += "\r\n";
			}
		}
		return body;
	}
	
	public static String getMeasurementTitle(MeasurementTypeEnum type)
	{
		String result = "";
		switch (type) {
		case BP:
			result = "Blood Pressure";
			break;
		case BSL:
			result = "Blood Suger Level";
			break;
		case ECG:
			result = "ECG";
			break;
		case INR:
			break;
		case SPO2:
			result = "SPO2";
			break;
		case Temp:
			break;
		case Urine:
			result = "Urine";
			break;
		case Weight:
			break;
		}
		return result;
	}

}
