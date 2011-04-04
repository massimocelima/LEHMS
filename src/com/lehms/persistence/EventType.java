package com.lehms.persistence;

public enum EventType {
	JobStarted,
	JobCompleted,
	ProgressNoteAdded,
	LocationTracking,
	FormCompleted,
	
	//Measurements
	BloodPressureTaken,
	BSLTaken,
	ECGTaken,
	INRTaken,
	SPO2Taken,
	TemperatureTaken,
	UrineTaken,
	WeightTaken,
	RespiratoryRateTaken
}
