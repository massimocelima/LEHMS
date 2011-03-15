package com.lehms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.adapters.PhotoAdapter;
import com.lehms.controls.ActionItem;
import com.lehms.controls.ListQuickAction;
import com.lehms.controls.QuickAction;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.PhotoDataContract;
import com.lehms.messages.dataContracts.PhotoType;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.serviceInterface.IOfficeContactProvider;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class ViewPhotosActivity  extends RoboListActivity { //implements AsyncQueryListener 

	public static final String EXTRA_CLIENT = "client";
	public static final String EXTRA_PHOTO_TYPE = "type";
	
	@InjectExtra(optional=true, value=EXTRA_CLIENT) ClientSummaryDataContract _clientSummary;
	@InjectExtra(optional=true, value=EXTRA_PHOTO_TYPE) PhotoType _photoType;

	@InjectView(R.id.activity_view_photos_title) TextView _title;
	@InjectView(R.id.activity_view_photos_sub_title) TextView _subtitle;
	@InjectView(R.id.activity_view_photos_sub_title2) TextView _subtitle2;
	private PhotoAdapter _adapter; 
	private PhotoDataContract _selectedPhoto;
	private ListQuickAction _listQuickActions;

	@Inject protected IOfficeContactProvider _officeContactProvider;

	private class PhotoComparer implements Comparator<PhotoDataContract>
	{
		@Override
		public int compare(PhotoDataContract arg0, PhotoDataContract arg1) {
			return arg0.CreatedDate.compareTo(arg1.CreatedDate);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_photos);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_clientSummary = (ClientSummaryDataContract)savedInstanceState.get(EXTRA_CLIENT);
		
		_subtitle.setText(_clientSummary.toString());

		List<PhotoDataContract> photoList = new ArrayList<PhotoDataContract>();
		
		try {
			if(_photoType == null )
			{
				for(int i = 0; i < PhotoType.values().length; i++)
					photoList.addAll(GetPhotos(PhotoType.values()[i]));	
			}
			else
				photoList = GetPhotos(_photoType);
			
			Collections.sort(photoList, new PhotoComparer());
			
			_adapter = new PhotoAdapter(this, R.layout.photo_item, photoList);
			getListView().setAdapter(_adapter);
			
			LoadListMenu();
			
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this, "Error loading photos", "Error loading photos: " + e);
			AppLog.error("Error loading photos", e);
		}
	}
	
	private void LoadListMenu()
	{
		final ActionItem qaViewPhoto = new ActionItem();
		
		qaViewPhoto.setTitle("View Photo");
		qaViewPhoto.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_camera));
		qaViewPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	try {
					ViewPhoto(_selectedPhoto);
				} catch (Exception e) {
					UIHelper.ShowAlertDialog(ViewPhotosActivity.this, "Error retriving photo", "Error retriving photo: " + e);
					AppLog.error("Error retriving photo", e);
				}
				_listQuickActions.dismiss();
			}
		});
				
		final ActionItem qaSendPhoto = new ActionItem();
		
		qaSendPhoto.setTitle("Send Photo...");
		qaSendPhoto.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_contact));
		qaSendPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    	try {
					SendPhoto(_selectedPhoto);
				} catch (Exception e) {
					UIHelper.ShowAlertDialog(ViewPhotosActivity.this, "Error retriving photo", "Error retriving photo: " + e);
					AppLog.error("Error retriving photo", e);
				}
				_listQuickActions.dismiss();
			}
		});
		
		getListView().setOnItemClickListener(new OnItemClickListener() { 
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	
		    	_selectedPhoto = _adapter.getItem(position);
	    		
				_listQuickActions = new ListQuickAction(view);
				
				_listQuickActions.addActionItem(qaViewPhoto);
				_listQuickActions.addActionItem(qaSendPhoto);
				_listQuickActions.setAnimStyle(ListQuickAction.ANIM_AUTO);
				
				_listQuickActions.show();
		    }}); 
	}

	public void ViewPhoto(PhotoDataContract photo) throws Exception
	{
		File file = new File(UIHelper.GetClientPhotoDirectory(photo.ClientId, photo.Type) + "/" + photo.Name);
		Intent intent = new Intent(); 
		intent.setAction(android.content.Intent.ACTION_VIEW); 
		intent.setDataAndType(Uri.fromFile(file), "image/jpg"); 
		startActivity(intent); 
	}
	
	public void SendPhoto(PhotoDataContract photo) throws Exception
	{
		File file = new File(UIHelper.GetClientPhotoDirectory(photo.ClientId, photo.Type) + "/" + photo.Name);
		NavigationHelper.sendEmail(this, 
				_officeContactProvider.getOfficeEmail(), 
				file, _clientSummary);
	}
	
	/*
	private ArrayList<PhotoDataContract> GetAllPhotos() throws Exception
	{
		File photoDirectory = new File(UIHelper.GetPhotoDirectory());
		for(int i = 0; i < photoDirectory.listFiles().length; i++)
		{
			photoDirectory.listFiles()[0].getName();
		}
	}
	 */
	
	private ArrayList<PhotoDataContract> GetPhotos(PhotoType type) throws Exception
	{
		ArrayList<PhotoDataContract> photoList = new ArrayList<PhotoDataContract>();
		File file = new File( UIHelper.GetClientPhotoDirectory(_clientSummary.ClientId.toString(), type));
		for(int i = 0; i < file.listFiles().length; i++)
		{
			PhotoDataContract photo = new PhotoDataContract();
			photo.ClientId = _clientSummary.ClientId;
			photo.CreatedDate = new Date( file.listFiles()[i].lastModified() );
			photo.Name = file.listFiles()[i].getName();
			photo.Type = type;
			photoList.add(photo);
		}
		
		return photoList;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_CLIENT, _clientSummary);
		outState.putSerializable(EXTRA_CLIENT, _photoType);
	}

	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
}
