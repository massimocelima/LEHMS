package com.lehms;

import java.util.Date;
import java.util.List;

import com.lehms.messages.dataContracts.AddressDataContract;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.format.DateFormat;

public class UIHelper {

	private UIHelper() {}
	
	public static void GoHome(Context context)
	{
        final Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
	}

	public static void ShowAlertDialog(Context context, String title, String message)
	{
		AlertDialog dialog = new AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		})
        .create();
		
		dialog.show();
	}
	
	public static void ShowUnderConstructionMessage(Context context)
	{
		AlertDialog dialog = new AlertDialog.Builder(context)
        .setTitle("Under Construction")
        .setMessage("This functionality is under construction")
        .setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		})
        .create();
		
		dialog.show();
	}
	
	public static void LaunchNavigation(String desinationAddress, Context context)
	{
		String uri = "google.navigation";
		if( desinationAddress != null && ! desinationAddress.equals(""))
			uri += ":q=" + desinationAddress;
		
		Intent navigationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities( navigationIntent, PackageManager.MATCH_DEFAULT_ONLY);
		if(! resolveInfoList.isEmpty())
			context.startActivity(navigationIntent);
		else
		{
			AlertDialog dialog = new AlertDialog.Builder(context)
		        .setTitle("Google Navigation Not Installed")
		        .setMessage("In order to use the navigaton on this device you will need to install Google Navigation. Please visit the Marketplace and download the Google Navigation application")
		        .setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				})
		        .create();
			dialog.show();
		}
	}

	public static String FormatShortDate(Date date)
	{
		return DateFormat.format("dd/MM/yyyy", date).toString();
	}

	public static String FormatShortDateTime(Date date)
	{
		return DateFormat.format("dd/MM/yyyy h:mmaa", date).toString();
	}

	public static String FormatLongDate(Date date)
	{
		return DateFormat.format("EEEE, MMMM dd, yyyy", date).toString();
	}

	public static String FormatLongDateTime(Date date)
	{
		return DateFormat.format("EEEE, MMMM dd, yyyy h:mmaa", date).toString();
	}

	public static String FormatTime(Date date)
	{
		return DateFormat.format("h:mmaa", date).toString();
	}
	
	public static String FormatAddress(AddressDataContract address)
	{
		String result = "";
		if( address.Appartment != null && ! address.Appartment.equalsIgnoreCase(""))
			result += address.Appartment + "/";
		
		result += address.StreetNumber; 
		result += " " + address.Street; 
				
		result +=  "\n";
			
		result += address.Suburb;
		result += ", " + address.State;
		result += " " + address.Postcode;
		
		return result;
	}

}
