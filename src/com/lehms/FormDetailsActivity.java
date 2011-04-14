package com.lehms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.messages.CreateFormDataRequest;
import com.lehms.messages.dataContracts.AttachmentDataContract;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.PhotoDataContract;
import com.lehms.messages.dataContracts.PhotoType;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.messages.formDefinition.*;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IDepartmentProvider;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IFormDataResource;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IProfileProvider;
import com.lehms.util.AppLog;
import com.lehms.controls.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class FormDetailsActivity extends LehmsRoboActivity implements ISaveEventResultHandler, IViewIdGenerator { 

	public final static String EXTRA_CLIENT = "client";
	public final static String EXTRA_FORM_DEFINITION = "form_definition";
	public final static String EXTRA_FORM_DATA = "form_data";
	public final static String EXTRA_IS_NEW = "is_new";
	public final static String STATE_PAGE_INDEX = "page_index";
	
	@InjectView(R.id.title_bar_title) TextView _bannerTitle; 
	@InjectView(R.id.activity_forms_details_title) TextView _title; 
	@InjectView(R.id.activity_forms_details_sub_title) TextView _subTitle; 
	@InjectView(R.id.activity_forms_details_sub_title2) TextView _subTitle2; 
	@InjectView(R.id.activity_forms_details_container) LinearLayout _container; 
	
	@InjectExtra(EXTRA_CLIENT) protected ClientDataContract _client;
	@InjectExtra(EXTRA_FORM_DEFINITION) protected FormDefinition _form;
	@InjectExtra( value = EXTRA_FORM_DATA, optional = true) protected FormData _formData;
	@InjectExtra(EXTRA_IS_NEW) protected Boolean _isNew;
	private int _pageIndex = 0;

	
	@InjectView(R.id.activity_form_details_back) Button _backButton; 
	@InjectView(R.id.activity_form_details_cancel) Button _cancelButton; 
	@InjectView(R.id.activity_form_details_next) Button _nextButton; 
	@InjectView(R.id.activity_form_details_finish) Button _finishButton; 
	@InjectView(R.id.activity_forms_details_scroll_view) ScrollView _scroll;

	@Inject IIdentityProvider _identityProvider;
	@Inject IDepartmentProvider _departmentProvider;
	@Inject IProfileProvider _profileProvider;
	@Inject IEventRepository _eventRepository;
	@Inject IEventExecuter _eventExecuter;
	@Inject IEventFactory _eventEventFactory;
	@Inject IActiveJobProvider _jobProvider;
	
	private GestureDetector _gestureDetector;
    private GuestureListener _gestureListener;
	
    // Random number so that the label are drawn correctly on the screen
    private final int LABEL_ID_SEED = 277323;
    private int _labelId = LABEL_ID_SEED;
	private HashMap<Integer, Integer> _editors = new HashMap<Integer, Integer>();
	private HashMap<String, Integer> _radioButtons = new HashMap<String, Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forms_details);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_FORM_DEFINITION) != null)
			_form = (FormDefinition)savedInstanceState.get(EXTRA_FORM_DEFINITION);
		if(savedInstanceState != null && savedInstanceState.get(STATE_PAGE_INDEX) != null)
			_pageIndex = savedInstanceState.getInt(STATE_PAGE_INDEX);
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_FORM_DATA) != null)
			_formData = (FormData)savedInstanceState.get(EXTRA_FORM_DATA);
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null)
			_formData = (FormData)savedInstanceState.get(EXTRA_CLIENT);
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_IS_NEW) != null )
			_isNew = savedInstanceState.getBoolean(EXTRA_IS_NEW);

		
		_gestureDetector = new GestureDetector(this, new FlingGestureDetector());
		_gestureListener = new GuestureListener(_gestureDetector);
	
		_container.setOnTouchListener(_gestureListener);
		
		_bannerTitle.setText(_form.Title);
		_title.setText(_form.Title);
		
		LoadForm();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// save fields to form definition and then to form data object
		saveFieldData();
		_form.fillFormData(_formData);
		
		outState.putSerializable(EXTRA_FORM_DEFINITION, _form);
		outState.putInt(STATE_PAGE_INDEX, _pageIndex);
		outState.putSerializable(EXTRA_FORM_DATA, _formData);
		outState.putSerializable(EXTRA_CLIENT, _client);
		outState.putSerializable(EXTRA_IS_NEW, _isNew);
	}
	
	@Override 
	public void onBackPressed() {
		
		if(_isNew)
		{
			AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Close Without Saving?")
	        .setMessage("Are you sure you want to close this form and loose you changes?")
	        .setCancelable(true)
	        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	FormDetailsActivity.this.finish();
	            }
	        })
	        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        })
	        .create();
		
			dialog.show();
		}
		else
			finish();
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onBackClick(View view)
	{
		saveFieldData();
		_form.fillFormData(_formData);
		
		_pageIndex -= 1;
		
		LoadForm();
	}

	public void onNextClick(View view)
	{
		saveFieldData();
		_form.fillFormData(_formData);
		
		_pageIndex += 1;
		
		LoadForm();
	}

	public void onCancelClick(View view)
	{
		onBackPressed();
	}
	
	public void onFinishClick(View view)
	{
		if( _isNew )
		{
			saveFieldData();
			_form.fillFormData(_formData);
		
			CreateFormDataRequest request = new CreateFormDataRequest();
			request.Data = _formData;
			
			if( _jobProvider.get() != null )
				request.JobId = _jobProvider.get().JobId;
			
			Event event = _eventEventFactory.create(request, EventType.FormCompleted);
			try {
				UIHelper.SaveEvent(this, this, _eventRepository, _eventExecuter, event, _form.Title);
			} catch (Exception e) {
				onError(e);
			}
		}
		else
		{
			finish();			
		}
	}
	
	@Override
	public void onSuccess(Object data) {
		//Intent intent = this.getIntent(); 
		//intent.putExtra(EXTRA_FORM_DATA, (CreateFormDataRequest)_formData); 
		//setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	public void onError(Exception e) {
		UIHelper.ShowAlertDialog(this, "Error saving form data", "Error ssving form data: " + e.getMessage());
	}

	private void LoadForm(){
		
		// Reseeds the label id's
		_labelId = LABEL_ID_SEED;
		
		_container.removeAllViews();
		
		if( _formData == null )
		{
			_formData = new FormData(_form);
			_formData.CreatedDate = new Date();
			try {
				_formData.CreatedBy = _identityProvider.getCurrent().getCreatedByFormat();
			} catch (Exception e) {
				_formData.CreatedBy = "Unknown User";
			}
			_formData.ClientId = _client.ClientId;
		}
		
		_form.loadFormData(_formData);
		
		View view = null;
		FormPage page = _form.Pages.get(_pageIndex);
		_subTitle.setText(page.Title);
		_subTitle2.setText("Page " + (_pageIndex + 1) + " of " + _form.Pages.size());
		
		for(int i = 0; i < page.Elements.size(); i++ )
		{
			FormElement element = page.Elements.get(i);
			switch (element.Type) {
			case TextBox:
				view = CreateTextBoxView(element);
				break;
			case MultilineTextBox:
				view = CreateMultilineTextBoxView(element);
				break;
			case Date:
				view = CreateDateView(element);
				break;
			case DateTime:
				view = CreateDateTimeView(element);
				break;
			case Time:
				view = CreateTimeView(element);
				break;
			case Label:
				view = CreateLabelView(element);
				break;
			case Checkbox:
				view = CreateCheckboxView(element);
				break;
			case DropDown:
				view = CreateDropDownView(element);
				break;
			case RadioButtonList:
				view = CreateRadioButtonListView(element);
				break;
			case ImageDrawable:
				view = CreateImageDrawableView(element);
				break;
			case ImagePicker:
				view = CreateImagePickerView(element);
				break;
			case Number:
				view = CreateNumberTextBoxView(element);
				break;
			default:
				view = CreateTextBoxView(element);
				break;
			}
			
			if( i == 0 && view != null && _isNew )
				view.requestFocus();

		}
		LayoutButtonBar();
		
		// If we are viewing a form then hide the soft kayboard
		if( ! _isNew )
		{
			InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			if( inputManager != null )
			{
				View focusedView = this.getCurrentFocus();
				if( focusedView != null )
					inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	private void LayoutButtonBar()
	{
		if( _pageIndex <= 0 )
			_backButton.setVisibility(View.INVISIBLE);
		else
			_backButton.setVisibility(View.VISIBLE);
		
		if(_pageIndex >= _form.Pages.size() - 1)
		{
			_finishButton.setVisibility(View.VISIBLE);
			_nextButton.setVisibility(View.GONE);
		}
		else
		{
			_finishButton.setVisibility(View.GONE);
			_nextButton.setVisibility(View.VISIBLE);
		}
	}
	
	private View CreateLabelView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_label, _container, true);
		TextView label = (TextView)view.findViewById(R.id.form_label_label);
		label.setText(element.Label);
		label.setId(getNextId());
		
		return view;
	}
	
	private View CreateDropDownView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_dropdown, _container, true);

		Spinner spinner = (Spinner)view.findViewById(R.id.form_dropdown_edit);
		TextView label = (TextView)view.findViewById(R.id.form_dropdown_label);
		
        ArrayAdapter<FormElementOption> adapter = new ArrayAdapter<FormElementOption>( this, android.R.layout.simple_spinner_item, element.Options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setEnabled(_isNew && ! element.IsReadonly);
        spinner.setFocusable(_isNew && ! element.IsReadonly);

		if(element.Value != null && ! element.Value.equals(""))
		{
			int index = element.GetIndexByValue(element.Value);
			spinner.setSelection(index);
		}

		spinner.setId(element.Id);
		label.setText(element.Label);
		label.setId(getNextId());
		
		return view;
	}
	
	private View CreateRadioButtonListView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_radio_button_list, _container, true);

		//Spinner spinner = (Spinner)view.findViewById(R.id.form_dropdown_edit);
		TextView label = (TextView)view.findViewById(R.id.form_radio_button_list_label);
		RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.form_radio_button_list_edit);
		
		for(int i = 0; i < element.Options.size(); i++)
		{
			FormElementOption option = element.Options.get(i);
			
	        RadioButton newRadioButton = new RadioButton(this);
	        newRadioButton.setText(option.Name);
	        newRadioButton.setEnabled(_isNew && ! element.IsReadonly);
	        newRadioButton.setFocusable(_isNew && ! element.IsReadonly);
	        
	        int id = getNextId();
	        newRadioButton.setId(id);
	        _radioButtons.put(element.Name + option.Name, id);
	        
	        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
	                RadioGroup.LayoutParams.WRAP_CONTENT,
	                RadioGroup.LayoutParams.WRAP_CONTENT);
	        
	        radioGroup.addView(newRadioButton, layoutParams);
	        
	        if( element.Value != null && element.Value.equals(option.Name))
	        	newRadioButton.setChecked(true);
	        else
	        	newRadioButton.setChecked(false);
		}
		
		radioGroup.setId(element.Id);
		radioGroup.setEnabled(_isNew && ! element.IsReadonly);
		radioGroup.setFocusable(_isNew && ! element.IsReadonly);
		
		label.setText(element.Label);
		label.setId(getNextId());
		
		return view;
	}
	
	private View CreateTextBoxView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_textbox, _container, true);

		EditText textbox = (EditText)view.findViewById(R.id.form_textbox_edit);
		TextView label = (TextView)view.findViewById(R.id.form_textbox_label);
		
		if(element.Value != null && ! element.Value.equals(""))
		{
			if(element.Value.equals("[USER]"))
			{
				try { textbox.setText(_identityProvider.getCurrent().getCreatedByFormat()); }
				catch (Exception ex) { textbox.setText(element.Value); }
			}
			else
				textbox.setText(element.Value);
		}
		
		textbox.setId(element.Id);
		label.setText(element.Label);
		label.setId(getNextId());
		
		textbox.setEnabled(_isNew && ! element.IsReadonly);
		textbox.setFocusable(_isNew && ! element.IsReadonly);
		
		return view;
	}
	
	public View CreateNumberTextBoxView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_textbox, _container, true);

		EditText textbox = (EditText)view.findViewById(R.id.form_textbox_edit);
		textbox.setInputType(InputType.TYPE_CLASS_NUMBER);
		TextView label = (TextView)view.findViewById(R.id.form_textbox_label);
		
		if(element.Value != null && ! element.Value.equals(""))
		{
			if(element.Value.equals("[USER]"))
			{
				try { textbox.setText(_identityProvider.getCurrent().getCreatedByFormat()); }
				catch (Exception ex) { textbox.setText(element.Value); }
			}
			else
				textbox.setText(element.Value);
		}
		
		textbox.setId(element.Id);
		label.setText(element.Label);
		label.setId(getNextId());
		
		textbox.setEnabled(_isNew && ! element.IsReadonly);
		textbox.setFocusable(_isNew && ! element.IsReadonly);
		
		return view;
	}

	private ImagePickerView _imagePickerView = null;

	private View CreateImagePickerView(FormElement element)
	{
		try {
		
			DrawableManager dManager = new DrawableManager(_identityProvider, _departmentProvider);
			_imagePickerView = new ImagePickerView(this, 
					this, 
					_container, 
					_client.createSummary(), 
					_profileProvider, 
					dManager);

			_imagePickerView.LoadElement(element, element.IsReadonly || ! _isNew, PhotoType.Wound);
			_imagePickerView.setSelectedFromFormData(_formData, _isNew);
			
		} catch (Exception e) {
			UIHelper.ShowAlertDialog(FormDetailsActivity.this, "Error creating photo picker", "Error creating photo picker: " + e);
			AppLog.error("Error creating photo picker", e);
		}
		
		return _imagePickerView.getView();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if( requestCode == TakePhotoActivity.CAPTURE_PICTURE_INTENT)
		{
			_imagePickerView.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private View CreateMultilineTextBoxView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_textbox, _container, true);

		EditText textbox = (EditText)view.findViewById(R.id.form_textbox_edit);
		textbox.setMinLines(3);
		textbox.setGravity(Gravity.TOP);
		TextView label = (TextView)view.findViewById(R.id.form_textbox_label);
		label.setId(getNextId());
		
		textbox.setEnabled(_isNew && ! element.IsReadonly);
		textbox.setFocusable(_isNew && ! element.IsReadonly);
		
		textbox.setText(element.Value);
		textbox.setId(element.Id);
		label.setText(element.Label);
		
		return view;
	}
	
	private View CreateCheckboxView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_checkbox, _container, true);

		CheckBox checkbox = (CheckBox)view.findViewById(R.id.form_checkbox_edit);
		
		Boolean checked = false;
		if(element.Value != null && ! element.Value.equals(""))
			checked = Boolean.parseBoolean(element.Value);
		
		checkbox.setChecked(checked);
		checkbox.setText(element.Label);
		checkbox.setId(element.Id);
		checkbox.setEnabled(_isNew && ! element.IsReadonly);
		checkbox.setFocusable(_isNew && ! element.IsReadonly);

		return view;
	}

	private ImageDrawableView _imageDrawableView; 
	
	private View CreateImageDrawableView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_image_drawable, _container, true);
		
		_imageDrawableView = (ImageDrawableView)view.findViewById(R.id.form_image_drawable_edit);
		ImageView img =  (ImageView)view.findViewById(R.id.form_image_drawable_view);
		
		// Disable Scrolling when drawing by setting up an OnTouchListener of the scroll view to bubble the event to the image view
		_scroll.setOnTouchListener(new DisableOnTouchListenerWhenDrawing());
		
		if( _isNew )
		{
			_imageDrawableView.setVisibility(View.VISIBLE);
			img.setVisibility(View.GONE);
		}
		else
		{
			_imageDrawableView.setVisibility(View.GONE);
			img.setVisibility(View.VISIBLE);
		}
		
		if( _formData.AttachmentId == null || _formData.AttachmentId.equals(UIHelper.EmptyUUID()) )
		{
			_imageDrawableView.open(this, R.drawable.observation_male_female);
		}
		else
		{
			if( _isNew )
			{
				String path = UIHelper.GetFormImagePath(_formData.AttachmentId);
				try {
					_imageDrawableView.open(path);
				} catch (FileNotFoundException e) {
					_imageDrawableView.open(this, R.drawable.observation_male_female);
				}
			}
			else
			{
				if( _formData.AttachmentId != null )
				{
					String url = _profileProvider.getProfile().GetFormDataAttachmentResourceEndPoint() + "/" + _formData.AttachmentId.toString();
					DrawableManager dManager = new DrawableManager(_identityProvider, _departmentProvider);
					dManager.fetchDrawableOnThread(url, img);
				}
			}
		}

        return view;
	}
	
	private class DisableOnTouchListenerWhenDrawing implements OnTouchListener { 
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
	    	if(_imageDrawableView != null && _imageDrawableView.getIsDrawing())
	    	{
	    		((View)_imageDrawableView.getParent()).getTop();
	    		
	    		MotionEvent motionEvent =  MotionEvent.obtain(event.getDownTime(), 
	    				event.getEventTime(), 
	    				event.getAction(), 
	    				event.getX() - GetAbsolutLeft(_imageDrawableView, v), 
	    				event.getY() - GetAbsolutTop(_imageDrawableView, v), 
	    				event.getMetaState());
	    		
	    		return _imageDrawableView.onTouchEvent(motionEvent);
	    	}
	    	return false;
	    }
	    
	    private int GetAbsolutTop(View view, View topView)
	    {
	    	int top = view.getTop();
	    	View parent = (View)view.getParent();
	    	if( parent != null && parent != topView )
	    		top += GetAbsolutTop(parent, topView);
	    	return top;
	    }
	    
	    private int GetAbsolutLeft(View view, View topView)
	    {
	    	int left = view.getLeft();
	    	View parent = (View)view.getParent();
	    	if( parent != null && parent != topView )
	    		left += GetAbsolutLeft(parent, topView);
	    	return left;
	    }

	}
	
	private TextView _currentDateEditor;
	
	private View CreateDateView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_date, _container, true);

		TextView textbox = (TextView)view.findViewById(R.id.form_date_edit);
		textbox.setClickable(true);
		TextView label = (TextView)view.findViewById(R.id.form_date_label);
		label.setId(getNextId());
		
		if( element.Value != null && !element.Value.equals(""))
		{
			if(element.Value.equals("Now") )
				textbox.setText(UIHelper.FormatLongDate(new Date()));
			else
				textbox.setText(element.Value);
		}
		textbox.setId(element.Id);
		label.setText(element.Label);

		if( _isNew && ! element.IsReadonly )
			textbox.setOnClickListener(new DateClickListener());

		return view;
	}

	private View CreateTimeView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_time, _container, true);

		TextView textbox = (TextView)view.findViewById(R.id.form_time_edit);
		textbox.setClickable(true);
		TextView label = (TextView)view.findViewById(R.id.form_time_label);
		label.setId(getNextId());
		
		if( element.Value != null && !element.Value.equals(""))
		{
			if(element.Value.equals("Now") )
				textbox.setText(UIHelper.FormatTime(new Date()));
			else
				textbox.setText(element.Value);
		}
		
		textbox.setId(element.Id);
		label.setText(element.Label);

		if( _isNew && ! element.IsReadonly )
			textbox.setOnClickListener(new TimeClickListener());

		return view;
	}
	
	private View CreateDateTimeView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_datetime, _container, true);

		TextView dateTextbox = (TextView)view.findViewById(R.id.form_datetime_edit_date);
		dateTextbox.setClickable(true);
		dateTextbox.setId(element.Id);
		
		if( _isNew && ! element.IsReadonly )
			dateTextbox.setOnClickListener(new DateClickListener());
		
		TextView timeTextbox = (TextView)view.findViewById(R.id.form_datetime_edit_time);
		timeTextbox.setClickable(true);
		
		if( _isNew && ! element.IsReadonly )
			timeTextbox.setOnClickListener(new TimeClickListener());

		timeTextbox.setId(getNextId());
		_editors.put(element.Id, timeTextbox.getId());

		TextView label = (TextView)view.findViewById(R.id.form_datetime_label);
		label.setId(getNextId());
		label.setText(element.Label);
		
		if( element.Value != null && !element.Value.equals(""))
		{
			Date date = UIHelper.ParseDateTimeString(element.Value);
			dateTextbox.setText(UIHelper.FormatLongDate(date));
			timeTextbox.setText(UIHelper.FormatTime(date));
		}
		
		return view;
	}
	
	private void saveFieldData()
	{
		List<FormElement> elements = _form.Pages.get(_pageIndex).Elements;

		for( int i = 0; i < elements.size(); i++ )
		{
			FormElement element = elements.get(i);
			switch(element.Type)
			{
			case TextBox:
			case Number:
				EditText editText = (EditText)findViewById(element.Id);
				element.Value = editText.getText().toString();
				break;
			case MultilineTextBox:
				EditText mutilineEditText = (EditText)findViewById(element.Id);
				element.Value = mutilineEditText.getText().toString();
				break;
			case Date:
				TextView dateEditText = (TextView)findViewById(element.Id);
				element.Value = dateEditText.getText().toString();
				break;
			case DateTime:
				TextView dateEditor = (TextView)findViewById(element.Id);
				TextView timeEditor = (TextView)findViewById(_editors.get(element.Id));
				String date = dateEditor.getText().toString();
				String time = timeEditor.getText().toString();
				element.Value = date + " " + time;
				break;
			case Time:
				TextView timeEdititor = (TextView)findViewById(element.Id);
				element.Value = timeEdititor.getText().toString();
				break;
			case Checkbox:
				CheckBox checkBox = (CheckBox)findViewById(element.Id);
				element.Value = checkBox.isChecked() ? "true" : "false";
				break;
			case DropDown:
				Spinner spinner = (Spinner)findViewById(element.Id);
				FormElementOption option = (FormElementOption)spinner.getSelectedItem();
				if(option != null)
					element.Value = option.Value;
				else
					element.Value = "";
				break;
			case RadioButtonList:
				element.Value = "";
				RadioGroup radioGroup = (RadioGroup)findViewById(element.Id);
				for(int index = 0; index < element.Options.size(); index++ )
				{
					FormElementOption elementOption = element.Options.get(index);
					int readioButtonId = _radioButtons.get(element.Name + elementOption.Name);
					RadioButton radioButton = (RadioButton)radioGroup.findViewById(readioButtonId);
					if(radioButton.isChecked())
						element.Value = elementOption.Value;
				}
				break;
			case Label:
				// Do nothing - we do not need to save the label data
				break;
			case ImageDrawable:
				if( _isNew )
				{
					if( _formData.AttachmentId == null )
						_formData.AttachmentId = UUID.randomUUID();
					
					String path = UIHelper.GetFormImagePath(_formData.AttachmentId);
	
					try {
						_imageDrawableView.save(path);
					} catch (Exception e) {
						UIHelper.ShowAlertDialog(this, "Error creating image", "Error creating image: "  + e.getMessage());
					}
				}
				break;
			case ImagePicker:
				PhotoDataContract photo = _imagePickerView.getSelectedPhoto();
				if( photo == null )
					_formData.AttachmentId = null;
				else
					_formData.AttachmentId = UUID.fromString(photo.GetId()); 
				break;
			default:
				//EditText defaultText = (EditText)findViewById(element.Id);
				//element.Value = defaultText.getText().toString();
				break;
			}
		}
	}


	@Override
	public int getNextId() {
		_labelId += 1;
		return _labelId;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return _gestureDetector.onTouchEvent(event);
	}

    // define the listener which is called once a user selected the time.
    private DateSlider.OnDateSetListener _timeSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	_currentDateEditor.setText(UIHelper.FormatTime(selectedDate.getTime()));
            }
    };    
	
	private class TimeClickListener implements OnClickListener
	{
		@Override
		public void onClick(View view) {
			
			_currentDateEditor = (TextView)view;
			Date dt = new Date();
			String timeString = _currentDateEditor.getText().toString();
			
			if( ! timeString.equals("") )
			{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String dimeToParse = DateFormat.format("yyyy-MM-dd", dt).toString() + " " + timeString;
				try
				{
					dt = formatter.parse(dimeToParse);
				} catch(Exception ex) {}
			}
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			
			TimeSlider timeSlider = new TimeSlider(FormDetailsActivity.this, _timeSetListener, calendar);
			timeSlider.show();
		}
	}
	
	
    // define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener _dateSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	_currentDateEditor.setText(UIHelper.FormatLongDate(selectedDate.getTime()));            	
            }
    };    
	
	private class DateClickListener implements OnClickListener
	{
		@Override
		public void onClick(View view) {
			
			_currentDateEditor = (TextView)view;
			Date dt = new Date();
			if( ! _currentDateEditor.getText().toString().equals(""))
				dt = new Date(_currentDateEditor.getText().toString());
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			
			DefaultDateSlider dateSlider = new DefaultDateSlider(FormDetailsActivity.this, _dateSetListener, calendar);
			dateSlider.show();
		}
	}
	
	
	private class FlingGestureDetector extends SimpleOnGestureListener {
	 
		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX,
				float velocityY) {
	
			//DisplayMetrics dm = getResources().getDisplayMetrics(); 
			
			int minScaledFlingVelocity  = ViewConfiguration.get(getApplicationContext()).getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation
			
	        if (Math.abs(velocityX) > minScaledFlingVelocity  &&
	            Math.abs(velocityY) < minScaledFlingVelocity ) {
	
	            if( velocityX < 0 ) // Move to the next page
	            {
	            	if(_nextButton.getVisibility() == View.VISIBLE)
	            		onNextClick(null);
	            }
	            else  // Move to the previous page
	            {
	            	if(_backButton.getVisibility() == View.VISIBLE)
	            		onBackClick(null);
	            }
	            
				return true;
	        }
			return false;
		}
	}
	
}
