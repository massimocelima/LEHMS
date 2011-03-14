package com.lehms;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;
import com.lehms.messages.CreateFormDataRequest;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.messages.formDefinition.*;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.serviceInterface.IFormDataResource;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.controls.*;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class FormDetailsActivity extends RoboActivity implements ISaveEventResultHandler { 

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
	
	@Inject IIdentityProvider _identityProvider;
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
		finish();
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
		label.setId(GetNextLabelId());
		
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
		label.setId(GetNextLabelId());
		
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
	        
	        int id = GetNextLabelId();
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
		label.setId(GetNextLabelId());
		
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
		label.setId(GetNextLabelId());
		
		textbox.setEnabled(_isNew && ! element.IsReadonly);
		textbox.setFocusable(_isNew && ! element.IsReadonly);
		
		return view;
	}

	private View CreateMultilineTextBoxView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_textbox, _container, true);

		EditText textbox = (EditText)view.findViewById(R.id.form_textbox_edit);
		textbox.setMinLines(3);
		textbox.setGravity(Gravity.TOP);
		TextView label = (TextView)view.findViewById(R.id.form_textbox_label);
		label.setId(GetNextLabelId());
		
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

	
	private TextView _currentDateEditor;
	
	private View CreateDateView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_date, _container, true);

		TextView textbox = (TextView)view.findViewById(R.id.form_date_edit);
		textbox.setClickable(true);
		TextView label = (TextView)view.findViewById(R.id.form_date_label);
		label.setId(GetNextLabelId());
		
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
		label.setId(GetNextLabelId());
		
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

		timeTextbox.setId(GetNextLabelId());
		_editors.put(element.Id, timeTextbox.getId());

		TextView label = (TextView)view.findViewById(R.id.form_datetime_label);
		label.setId(GetNextLabelId());
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
				break;
			case ImagePicker:
				break;
			default:
				//EditText defaultText = (EditText)findViewById(element.Id);
				//element.Value = defaultText.getText().toString();
				break;
			}
		}
	}
	
	private int GetNextLabelId()
	{
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
