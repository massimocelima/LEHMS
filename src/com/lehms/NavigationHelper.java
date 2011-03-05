package com.lehms;

import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class NavigationHelper {

	private NavigationHelper() {}
	
	public static void goEmergency(Context context)
	{
		
	}
	
	public static void viewProgressNote(Context context, UUID progressNoteId)
	{
        Intent intent = new Intent(context, ProgressNoteDetailsActivity.class);
        intent.putExtra(ProgressNoteDetailsActivity.EXTRA_PROGRESS_NOTE_ID, progressNoteId.toString());
        context.startActivity(intent);
	}

	public static void createProgressNote(Context context)
	{
        Intent intent = new Intent(context, ProgressNoteDetailsActivity.class);
        context.startActivity(intent);
	}
	
	public static void openProgressNotes(Context context, long clientId, String clientName)
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
