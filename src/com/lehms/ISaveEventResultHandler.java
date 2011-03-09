package com.lehms;

public interface ISaveEventResultHandler {

	void onSuccess(Object data);
	void onError(Exception e);
	
}
