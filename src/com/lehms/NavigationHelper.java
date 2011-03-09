package com.lehms;

import java.util.Date;
import java.util.UUID;

import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class NavigationHelper {

	public static final int RESULT_CREATE_PROGRESS_NOTE = 0;
	
	private NavigationHelper() {}
	
	public static void goEmergency(Context context)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}
	
	public static void goContactCallCentre(Context context)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}
	
	public static void goTakePicture(Context context, String clientId)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}
	
	public static void goContact(Context context, String clientId)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}
	
	public static void goCliniclaDetails(Context context, String clientId)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}
	
	public static void goNextService(Context context, Long clientId)
	{
		UIHelper.ShowUnderConstructionMessage(context);
	}

	public static void goForms(Context context, ClientDataContract client)
	{
        Intent intent = new Intent(context, FormsActivity.class);
        intent.putExtra(FormsActivity.EXTRA_CLIENT, client);
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
	
}
