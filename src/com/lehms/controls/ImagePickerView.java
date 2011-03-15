package com.lehms.controls;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.lehms.FormDetailsActivity;
import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.PhotoDataContract;
import com.lehms.messages.dataContracts.PhotoType;
import com.lehms.messages.formDefinition.FormDataElement;
import com.lehms.messages.formDefinition.FormElement;
import com.lehms.util.AppLog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ImagePickerView extends View {

	private ClientSummaryDataContract _client;
	private Spinner _spinner;
	private ImageView _image;
	private TextView _label;
	private Button _takePhotoButton;
	private IViewIdGenerator _idGenerator;
	private ArrayAdapter<PhotoDataContract> _adapter;

	
	public ImagePickerView(Context context, AttributeSet attrs,
			IViewIdGenerator idGenerator,
			ViewGroup container,
			ClientSummaryDataContract client) {
		super(context, attrs);
		
		_client = client;
		_idGenerator = idGenerator;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_image_picker, container, true);

		_spinner = (Spinner)view.findViewById(R.id.form_image_picker_edit);
		_image = (ImageView)view.findViewById(R.id.form_image_picker_image);
		_label = (TextView)view.findViewById(R.id.form_image_picker_label);
		_takePhotoButton  = (Button)view.findViewById(R.id.form_image_picker_take);
		
		_takePhotoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onTakePhotoClick();
			}
		});
		
		_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					PhotoDataContract photo = (PhotoDataContract)arg0.getItemAtPosition(position);
					String path = UIHelper.GetClientPhotoDirectory(_client.ClientId, PhotoType.Wound) + "/" + photo.Name;
					
					//_formData.AttachmentId = UUID.fromString( photo.Name.substring(0, photo.Name.indexOf(".")));
					
					_image.setImageURI(Uri.fromFile(new File( path) ));
					_image.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					UIHelper.ShowAlertDialog(ImagePickerView.this.getContext(), "Error geting photo", "Error geting photo: " + e);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				_image.setVisibility(View.GONE);
			}
		});

		
		/*
		try {
			
			if( _formData.AttachmentId != null )
			{
				if( _isNew )
				{
					String path = UIHelper.GetClientPhotoPath(_client.ClientId, _formData.AttachmentId, PhotoType.Wound);
					image.setImageURI(Uri.fromFile(new File(path)));
				}
				else
				{
					String url = _profileProvider.getProfile().GetFormDataAttachmentResourceEndPoint() + "/" + _formData.AttachmentId.toString();
					DrawableManager dManager = new DrawableManager(_identityProvider, _departmentProvider);
					dManager.fetchDrawableOnThread(url, image);
					image.setVisibility(View.VISIBLE);
					spinner.setVisibility(View.GONE);
				}
			}
			

		} catch (Exception e) {
			UIHelper.ShowAlertDialog(FormDetailsActivity.this, "Error geting photo", "Error geting photo: " + e);
			AppLog.error("Error geting photo", e);
		}
		*/
	}

	
	public void LoadElement(FormElement element, Boolean readonly, PhotoType type) throws Exception
	{
		_adapter = new ArrayAdapter<PhotoDataContract>( this.getContext(), android.R.layout.simple_spinner_item, getPhotos(type));
		_adapter.insert(null, 0);
		_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinner.setAdapter(_adapter);
		
		_spinner.setId(element.Id);
		_label.setText(element.Label);
		_label.setId(_idGenerator.getNextId());
		
		_spinner.setEnabled(!readonly);
		_spinner.setFocusable(!readonly);
	}
	
	public void setSelectedPhoto(PhotoDataContract photo)
	{
		_spinner.setSelection(_adapter.getPosition(photo));
	}

	public PhotoDataContract getSelectedPhoto()
	{
		return (PhotoDataContract)_spinner.getSelectedItem();
	}

	protected void onTakePhotoClick()
	{
		ClientSummaryDataContract client = new ClientSummaryDataContract();
		client.ClientId = _client.ClientId;
		client.LastName = _client.LastName;
		client.FirstName = _client.FirstName;
		try {
			//_imagePickerFile = NavigationHelper.goTakePicture(this, client, PhotoType.Wound);
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(this.getContext(), "Error tring to take photo", "Error tring to take photo: " + e);
			AppLog.error("Error tring to take photo", e);
		}
	}
	
	private ArrayList<PhotoDataContract> getPhotos(PhotoType type) throws Exception
	{
		ArrayList<PhotoDataContract> photoList = new ArrayList<PhotoDataContract>();
		File file = new File( UIHelper.GetClientPhotoDirectory(_client.ClientId.toString(), type));
		for(int i = 0; i < file.listFiles().length; i++)
		{
			PhotoDataContract photo = new PhotoDataContract();
			photo.ClientId = _client.ClientId;
			photo.CreatedDate = new Date( file.listFiles()[i].lastModified() );
			photo.Name = file.listFiles()[i].getName();
			photo.Type = type;
			photoList.add(photo);
		}
		
		return photoList;
	}
	
}
