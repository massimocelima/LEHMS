package com.lehms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.lehms.messages.dataContracts.AddressDataContract;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.PhotoType;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.persistence.Event;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

public class UIHelper {

	private UIHelper() {}
	
	public static UUID EmptyUUID()
	{
		return UUID.fromString("00000000-0000-0000-0000-000000000000");
	}

    public static String getVersionNumber(Context context) { 
        String version = ""; 
        try { 
                PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0); 
                version = pi.versionName; 
        } catch (PackageManager.NameNotFoundException e) {
                AppLog.error("Package name not found", e);
        }; 
        return version; 
    }

	
	public static String GetPhotoDirectory() throws Exception
	{
        String path = Environment.getExternalStorageDirectory().getAbsolutePath(); 
        path += "/lehms/photos/";

        File directory = new File(path);
        if( !directory.exists())
        {
        	if( ! directory.mkdirs() )
        		throw new Exception("Could not create file");
        }
        return path;
	}
	
	public static String GetClientPhotoDirectory(String clientId, PhotoType type) throws Exception
	{
        String path = Environment.getExternalStorageDirectory().getAbsolutePath(); 
        path += "/lehms/photos/" + clientId + "/" + type.toString();

        File directory = new File(path);
        if( !directory.exists())
        {
        	if( ! directory.mkdirs() )
        		throw new Exception("Could not create file");
        }
        return path;
	}
	
	public static String GetClientPhotoPath(String clientId, UUID photoId, PhotoType type) throws Exception
	{
		return GetClientPhotoDirectory(clientId, type) + "/" + photoId.toString() + ".jpg";
	}

	public static String GetClientPhotoPath(String clientId, UUID photoId, PhotoType type, String ext) throws Exception
	{
		return GetClientPhotoDirectory(clientId, type) + "/" + photoId.toString() + "." + ext;
	}

	public static String GetFormImagePath(UUID attachmentId)
	{
        String path = Environment.getExternalStorageDirectory().getAbsolutePath(); 
        path += "/lehms/images/" + attachmentId.toString() +  ".png";
        File directory = new File(path).getParentFile();
        if( !directory.exists())
        	directory.mkdirs();
        return path;
	}
	
	public static void ShowToast(Context context, String message)
	{
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
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
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return formatter.format(date);
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
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(date);
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
	
	public static String FormatClientName(ClientDataContract client)
	{
		return client.FirstName + " " + client.LastName;
	}
	
	public static boolean IsOnline(Context context) 
	{
    	Boolean result = false;
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo ni = cm.getActiveNetworkInfo();
    	if( ni != null )
    		result = cm.getActiveNetworkInfo().isConnectedOrConnecting();
    	return result;	
    }
	
	public static Boolean HasBluetoth()
	{
		return BluetoothAdapter.getDefaultAdapter() != null; 
	}
	
	public static void MakeCall(String phoneNumber, Context context)
	{
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + phoneNumber));
		context.startActivity(intent);
	}

	public static void OpenCall(Context context)
	{
		Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
		context.startActivity(intent);
	}
	
	public static void SaveEvent(Activity context,
			ISaveEventResultHandler handler,
			IEventRepository eventRepository, 
			IEventExecuter executer, 
			Event event, 
			String title) throws Exception
	{
		if(IsOnline(context))
		{
			SaveEventTask task = new SaveEventTask(context, executer, title, handler);
			task.execute(event);
		}
		else
		{
			eventRepository.create(event);
			UIHelper.ShowToast(context, title + " saved");
		}
	}
	
	public static Date ParseDateTimeString(String dateTime)
	{
		
		Date date = new Date();
		
		if(dateTime.equals("Now"))
		{
			return new Date();
		}
		else
		{
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm");
			SimpleDateFormat formatterLegacy = new SimpleDateFormat("dd/MM/yy h:mm:ss a");
			SimpleDateFormat formatterLegacy2 = new SimpleDateFormat("dd/MM/yy h:mm a");
			
			try {
				date = formatter.parse(dateTime);
			} catch (ParseException e) 
			{ 
				try {
					date = formatterLegacy.parse(dateTime);
				} catch (ParseException el) 
				{ 
					try {
						date = formatterLegacy2.parse(dateTime);
					} catch (ParseException el2) { }
				}
			}
		}
		return date;
	}
	
	public static void CompressImage(File file) throws FileNotFoundException
	{
		Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
		file.delete();
		bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
		bitmap.compress(CompressFormat.JPEG, 60, new FileOutputStream(file));
	}
	
	public static UUID getApplicationUUID()
	{
		return UUID.fromString("ea001101-0000-1000-8000-00805F9B34FB");  
	}
	
}
