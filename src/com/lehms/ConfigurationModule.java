package com.lehms;

import com.lehms.persistence.*;
import com.lehms.serviceInterface.*;
import com.lehms.serviceInterface.clinical.ClinicalMeasurementResource;
import com.lehms.serviceInterface.clinical.IClinicalMeasurementResource;
import com.lehms.serviceInterface.implementation.*;
import com.lehms.util.IMeasurmentReportProvider;
import com.lehms.util.MeasurmentReportProvider;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

public class ConfigurationModule extends AbstractAndroidModule {

	private LehmsApplication _context;
	
	public ConfigurationModule(LehmsApplication context)
	{
		_context = context;
	}
	
	@Override
	protected void configure() {
		
		bind(ISerializer.class).to(JsonSerializer.class);
		//bind(ISerializer.class).to(XmlSerializer.class);
		bind(IAuthenticationProvider.class).to(AuthenticationProvider.class);
		bind(IRosterResource.class).to(RosterResource.class);
		bind(IClientResource.class).to(ClientResource.class);
		bind(IProgressNoteResource.class).to(ProgressNoteResource.class);
		bind(IFormDefinitionResource.class).to(FormDefinitionResource.class);
		bind(IFormDataResource.class).to(FormDataResource.class);
		bind(IAlarmResource.class).to(AlarmResource.class);
		bind(IApkResource.class).to(ApkResource.class);
		bind(IClinicalMeasurementResource.class).to(ClinicalMeasurementResource.class);
		bind(IMeasurmentReportProvider.class).to(MeasurmentReportProvider.class); 
		bind(IJobResource.class).to(JobResource.class); 
		
		bind(IEventExecuter.class).to(EventExecuter.class);
		bind(IEventFactory.class).to(EventFactory.class);
		
		bind(IRosterRepository.class).toInstance(new RosterRepository(new JsonSerializer(), _context, _context));
		bind(IEventRepository.class).toInstance(new EventRepository(_context, new JsonSerializer(),_context));
		
		bind(IDepartmentProvider.class).toInstance(_context);
		bind(IDeviceIdentifierProvider.class).toInstance(_context);
		bind(IIdentityProvider.class).toInstance(_context);
		bind(IProfileProvider.class).toInstance(_context);
		bind(IOfficeContactProvider.class).toInstance(_context);
		bind(IAuthorisationProvider.class).toInstance(_context);
		bind(IActiveJobProvider.class).toInstance(_context);
		bind(IDefualtDeviceAddressProvider.class).toInstance(_context);
		bind(ITracker.class).toInstance(_context);
		bind(IPreviousMeasurmentProvider.class).toInstance(_context);
		
		bind(IChannelFactory.class).toInstance(
				new HttpChannelFactory(new JsonSerializer(), 
				_context, 
				_context, 
				_context));
	}
	
}
