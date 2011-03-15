package com.lehms.controls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import com.lehms.messages.formDefinition.FormData;
import com.lehms.messages.formDefinition.FormDataElement;
import com.lehms.messages.formDefinition.FormElement;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
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

public class ImagePickerView {

	private static final String EMPTY_PHOTO_NAME = "Please Select...";
	
	private ClientSummaryDataContract _client;
	private Spinner _spinner;
	private ImageView _image;
	private TextView _label;
	private Button _takePhotoButton;
	private IViewIdGenerator _idGenerator;
	private ArrayAdapter<PhotoDataContract> _adapter;

	private IProfileProvider _profileProvider;
	private DrawableManager _drawableManager;

	private File _imagePickerFile;
	private Activity _context;
	private View _view;
	
	public ImagePickerView(Activity context,
			IViewIdGenerator idGenerator,
			ViewGroup container,
			ClientSummaryDataContract client,
			IProfileProvider profileProvider, 
			DrawableManager drawableManager) 
	{
		_context = context;
		_client = client;
		_idGenerator = idGenerator;
		_profileProvider = profileProvider;
		_drawableManager = drawableManager;
		
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		_view = layoutInflater.inflate(R.layout.form_image_picker, container, true);

		_spinner = (Spinner)_view.findViewById(R.id.form_image_picker_edit);
		_image = (ImageView)_view.findViewById(R.id.form_image_picker_image);
		_label = (TextView)_view.findViewById(R.id.form_image_picker_label);
		_takePhotoButton  = (Button)_view.findViewById(R.id.form_image_picker_take);
		
		_takePhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onTakePhotoClick(arg0);
			}
		});
		
		_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					PhotoDataContract photo = (PhotoDataContract)arg0.getItemAtPosition(position);
					if(! photo.Name.equals(EMPTY_PHOTO_NAME))
					{
						String path = UIHelper.GetClientPhotoDirectory(_client.ClientId, PhotoType.Wound) + "/" + photo.Name;
						_image.setImageURI(Uri.fromFile(new File( path) ));
						_image.setVisibility(View.VISIBLE);
					}
					else
					{
						_image.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					UIHelper.ShowAlertDialog(_context, "Error geting photo", "Error geting photo: " + e);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				_image.setVisibility(View.GONE);
			}
		});

	}

	public View getView()
	{
		return _view;
	}
	
	public void LoadElement(FormElement element, Boolean readonly, PhotoType type) throws Exception
	{
		_adapter = new ArrayAdapter<PhotoDataContract>( _context, android.R.layout.simple_spinner_item, getPhotos(type));
		PhotoDataContract empty = new PhotoDataContract();
		empty.ClientId = "";
		empty.Name = EMPTY_PHOTO_NAME;
		_adapter.insert(empty, 0);

		_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinner.setAdapter(_adapter);
		
		_spinner.setId(element.Id);
		_label.setText(element.Label);
		_label.setId(_idGenerator.getNextId());
		
		_spinner.setEnabled(!readonly);
		_spinner.setFocusable(!readonly);
		
		if(readonly)
		{
			_takePhotoButton.setVisibility(View.GONE);
			_spinner.setVisibility(View.GONE);
		}
	}
	
	public void setSelectedPhoto(PhotoDataContract photo)
	{
		if(photo == null )
			_spinner.setSelection(0);
		else
			_spinner.setSelection(_adapter.getPosition(photo));
	}

	public PhotoDataContract getSelectedPhoto()
	{
		PhotoDataContract photo = (PhotoDataContract)_spinner.getSelectedItem();
		if( photo.Name.equals(EMPTY_PHOTO_NAME) )
			return null;
		return photo;
	}
	
	public void setSelectedFromFormData(FormData data, Boolean isNew) throws Exception
	{
		if( data.AttachmentId != null )
		{
			if( isNew )
			{
				File path = new File(UIHelper.GetClientPhotoPath(_client.ClientId, data.AttachmentId, PhotoType.Wound));
				_image.setImageURI(Uri.fromFile(path));
				setSelectedPhoto(getByName(path.getName()));
			}
			else
			{
				String url = _profileProvider.getProfile().GetFormDataAttachmentResourceEndPoint() + "/" + data.AttachmentId.toString();
				_drawableManager.fetchDrawableOnThread(url, _image);
				_image.setVisibility(View.VISIBLE);
				_spinner.setVisibility(View.GONE);
			}
		}
	}

	protected void onTakePhotoClick(View view)
	{
		try {
			_imagePickerFile = NavigationHelper.goTakePicture(_context, _client, PhotoType.Wound);
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(_context, "Error tring to take photo", "Error tring to take photo: " + e);
			AppLog.error("Error tring to take photo", e);
		}
	}
	
	public PhotoDataContract getByName(String name)
	{
		PhotoDataContract result = null;
		
		for(int i = 0; i < _adapter.getCount(); i++ )
		{
			if( _adapter.getItem(i).Name.equals(name))
			{
				result = _adapter.getItem(i);
				return result;
			}
		}
		
		return result;
	}
	
	private ArrayList<PhotoDataContract> getPhotos(PhotoType type) throws Exception
	{
		ArrayList<PhotoDataContract> photoList = new ArrayList<PhotoDataContract>();
		File file = new File( UIHelper.GetClientPhotoDirectory(_client.ClientId.toString(), type));
		for(int i = 0; i < file.listFiles().length; i++)
		{
			PhotoDataContract photo = Map( file.listFiles()[i], type);
			photoList.add(photo);
		}
		
		return photoList;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if( resultCode == Activity.RESULT_OK )
		{
			if( _imagePickerFile.exists() )
			{
				try {
					UIHelper.CompressImage(_imagePickerFile);

					Bitmap bitmap = BitmapFactory.decodeFile(_imagePickerFile.toString());
					bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 2, false);
					bitmap.compress(CompressFormat.JPEG, 50, new FileOutputStream(_imagePickerFile));
				
					PhotoDataContract photo = Map( _imagePickerFile, PhotoType.Wound);
					_adapter.insert(photo, 1);
					setSelectedPhoto(photo);

					String path = UIHelper.GetClientPhotoDirectory(_client.ClientId, PhotoType.Wound) + "/" + photo.Name;
					_image.setImageURI(Uri.fromFile(new File( path) ));
					_image.setVisibility(View.VISIBLE);
					
			        UIHelper.ShowToast(_context.getApplicationContext(), "Photo saved to " + _imagePickerFile.toString());
			        
				} catch (Exception e) {
					UIHelper.ShowAlertDialog(_context, "Error ssetting image", "Error ssetting image: " + e.getMessage());
					AppLog.error("Error ssetting image", e);
				}
				
			}
			else
				UIHelper.ShowToast(_context.getApplicationContext(), "Photo has not been saved");
		}
		else
			_imagePickerFile = null;
	}

	private PhotoDataContract Map(File file, PhotoType type)
	{
		PhotoDataContract photo = new PhotoDataContract();
		photo.ClientId = _client.ClientId;
		photo.CreatedDate = new Date( file.lastModified() );
		photo.Name = file.getName();
		photo.Type = type;
		return photo;
	}

}
