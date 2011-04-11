package com.lehms.serviceInterface;

import com.lehms.messages.UpdateTrackingRequest;
import com.lehms.messages.UpdateTrackingResponse;

public interface ITrackingResource {

	UpdateTrackingResponse Update(UpdateTrackingRequest request) throws Exception;
	
}
