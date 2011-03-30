package com.lehms;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import com.lehms.messages.dataContracts.AlarmType;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.DoctorDataContract;
import com.lehms.messages.dataContracts.PhotoType;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.messages.formDefinition.FormData;
import com.lehms.messages.formDefinition.FormDefinition;
import com.lehms.ui.clinical.ClinicalDetailsListActivity;
import com.lehms.ui.clinical.MeasurementSummaryListActivity;
import com.lehms.ui.clinical.model.MeasurementType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

public class NavigationHelper {

	public static final int RESULT_CREATE_PROGRESS_NOTE = 0;
	
	private NavigationHelper() {}

	public static void goContactCallCentre(Context context)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}

	public static void goContact(Context context, String clientId)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}
	
	public static void goNextService(Context context, Long clientId)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}

	public static void goTestEmergency(Context context)
	{
        Intent intent = new Intent(context, RaiseAlarmActivity.class);
        intent.putExtra(RaiseAlarmActivity.EXTRA_ALARM_TYPE, AlarmType.Test);
        context.startActivity(intent);
	}

	public static void goEmergency(Context context)
	{
        Intent intent = new Intent(context, RaiseAlarmActivity.class);
        context.startActivity(intent);
	}
	
	public static void viewMeasurementSummaryList(Context context, ClientDataContract client, MeasurementType measurementType)
	{
        Intent intent = new Intent(context, MeasurementSummaryListActivity.class);
        intent.putExtra(MeasurementSummaryListActivity.EXTRA_CLIENT, client);
        intent.putExtra(MeasurementSummaryListActivity.EXTRA_MEASUREMENT_TYPE, measurementType);
        context.startActivity(intent);
	}

	public static void goCliniclaDetails(Context context, ClientDataContract client)
	{
        Intent intent = new Intent(context, ClinicalDetailsListActivity.class);
        intent.putExtra(ClinicalDetailsListActivity.EXTRA_CLIENT, client);
        context.startActivity(intent);
	}

	public static void viewPictures(Context context, ClientSummaryDataContract client)
	{
        Intent intent = new Intent(context, ViewPhotosActivity.class);
        intent.putExtra(ViewPhotosActivity.EXTRA_CLIENT, client);
        context.startActivity(intent);
	}

	public static void viewPictures(Context context, ClientSummaryDataContract client, PhotoType type)
	{
        Intent intent = new Intent(context, ViewPhotosActivity.class);
        intent.putExtra(ViewPhotosActivity.EXTRA_CLIENT, client);
        intent.putExtra(ViewPhotosActivity.EXTRA_PHOTO_TYPE, type);
        context.startActivity(intent);
	}
	
	public static void goTakePicture(Context context)
	{
        Intent intent = new Intent(context, TakePhotoActivity.class);
        context.startActivity(intent);
	}
    
	public static void goTakePicture(Context context, ClientSummaryDataContract client)
	{
        Intent intent = new Intent(context, TakePhotoActivity.class);
        intent.putExtra(TakePhotoActivity.EXTRA_CLIENT, client);
        context.startActivity(intent);
	}

	public static File goTakePicture(Activity context, ClientSummaryDataContract client, PhotoType type) throws Exception
	{
		UUID photoId = UUID.randomUUID();
		File file = new File(UIHelper.GetClientPhotoPath(client.ClientId, photoId, type, "jpg"));
	    
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
	    context.startActivityForResult(intent, TakePhotoActivity.CAPTURE_PICTURE_INTENT);
	    return file;
	}

	public static void goForms(Context context, ClientDataContract client)
	{
        Intent intent = new Intent(context, FormsActivity.class);
        intent.putExtra(FormsActivity.EXTRA_CLIENT, client);
        context.startActivity(intent);
	}

	public static void createFormDetails(Context context, FormDefinition form, ClientDataContract client)
	{
        Intent intent = new Intent(context, FormDetailsActivity.class);
        intent.putExtra(FormDetailsActivity.EXTRA_FORM_DEFINITION, form);
        intent.putExtra(FormDetailsActivity.EXTRA_CLIENT, client);
        intent.putExtra(FormDetailsActivity.EXTRA_IS_NEW, true);
        context.startActivity(intent);
	}	

	public static void viewFormDetails(Context context, FormDefinition form, FormData formData, ClientDataContract client)
	{
        Intent intent = new Intent(context, FormDetailsActivity.class);
        intent.putExtra(FormDetailsActivity.EXTRA_FORM_DEFINITION, form);
        intent.putExtra(FormDetailsActivity.EXTRA_FORM_DATA, formData);
        intent.putExtra(FormDetailsActivity.EXTRA_IS_NEW, false);
        intent.putExtra(FormDetailsActivity.EXTRA_CLIENT, client);
        context.startActivity(intent);
	}	

	public static void viewFormDetailsList(Context context, ClientDataContract client, FormDefinition form)
	{
        Intent intent = new Intent(context, FormDetailsListActivity.class);
        intent.putExtra(FormDetailsListActivity.EXTRA_FORM_DEFINITION, form);
        intent.putExtra(FormDetailsListActivity.EXTRA_CLIENT, client);
        context.startActivity(intent);
	}	
	
	public static void viewProgressNote(Activity context, ProgressNoteDataContract progressNote)
	{
        Intent intent = new Intent(context, ProgressNoteDetailsActivity.class);
        //intent.putExtra(ProgressNoteDetailsActivity.EXTRA_PROGRESS_NOTE_ID, progressNoteId.toString());
        intent.putExtra(ProgressNoteDetailsActivity.EXTRA_PROGRESS_NOTE, progressNote);
        context.startActivityForResult(intent, RESULT_CREATE_PROGRESS_NOTE);
	}

	public static void createProgressNote(Activity context, Long clientId, String clientName)
	{
        Intent intent = new Intent(context, ProgressNoteDetailsActivity.class);
        intent.putExtra(ProgressNoteDetailsActivity.EXTRA_CLIENT_ID, clientId);
        intent.putExtra(ProgressNoteDetailsActivity.EXTRA_CLIENT_NAME, clientName);
        context.startActivityForResult(intent, RESULT_CREATE_PROGRESS_NOTE);
	}
	
	public static void viewProgressNotes(Context context, long clientId, String clientName)
	{
        Intent intent = new Intent(context, ProgressNoteListActivity.class);
        intent.putExtra(ProgressNoteListActivity.EXTRA_CLIENT_ID, clientId);
        intent.putExtra(ProgressNoteListActivity.EXTRA_CLIENT_NAME, clientName);
        context.startActivity(intent);
	}

	public static void openRoster(Context context, Date rosterDate)
	{
		Intent i = new Intent(context, RosterActivity.class);
		i.putExtra(RosterActivity.EXTRA_ROSTER_DATE, rosterDate);
		context.startActivity(i);
	}

	public static void openClients(Context context)
	{
		Intent i = new Intent(context, ClientsActivity.class);
		context.startActivity(i);
	}

	public static void openClient(Context context, long clientId)
	{
    	Intent intent = new Intent(context, ClientDetailsActivity.class);
    	intent.putExtra(ClientDetailsActivity.EXTRA_CLIENT_ID, clientId);
    	context.startActivity(intent);
	}

	public static void goHome(Context context)
	{
        final Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
	}
	
	public static void sendEmail(Context context, String recipient, String body, String subject)
	{
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		if( recipient != null && !recipient.equals(""))
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient} );
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, body);   
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, subject);
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	public static void sendEmail(Context context, String recipient, File attachment, ClientSummaryDataContract client)
	{
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		if( recipient != null && !recipient.equals(""))
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient} );
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, client.FirstName + " " + client.LastName + " - Photo");   
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		
		Uri uri = Uri.fromFile(attachment);
		emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
	
}
