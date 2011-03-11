package com.lehms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import com.lehms.controls.*;

import com.google.inject.Inject;
import com.lehms.RosterActivity.FlingGestureDetector;
import com.lehms.adapters.ClientSummaryAdapter;
import com.lehms.adapters.FormDefinitionAdapter;
import com.lehms.adapters.JobAdapter;
import com.lehms.messages.GetFormDefinitionResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.messages.formDefinition.*;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.serviceInterface.IFormDefinitionResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts.Intents.UI;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class FormDetailsActivity extends RoboActivity { 

	public final static String EXTRA_FORM_DEFINITION = "form_definition";
	public final static String EXTRA_FORM_DATA = "form_data";
	public final static String STATE_PAGE_INDEX = "page_index";
	
	@InjectView(R.id.title_bar_title) TextView _bannerTitle; 
	@InjectView(R.id.activity_forms_details_title) TextView _title; 
	@InjectView(R.id.activity_forms_details_sub_title) TextView _subTitle; 
	@InjectView(R.id.activity_forms_details_sub_title2) TextView _subTitle2; 
	@InjectView(R.id.activity_forms_details_container) LinearLayout _container; 
	
	@InjectExtra(EXTRA_FORM_DEFINITION) protected FormDefinition _form;
	@InjectExtra( value = EXTRA_FORM_DATA, optional = true) protected FormData _formData;
	private int _pageIndex = 0;

	
	@InjectView(R.id.activity_form_details_back) Button _backButton; 
	@InjectView(R.id.activity_form_details_cancel) Button _cancelButton; 
	@InjectView(R.id.activity_form_details_next) Button _nextButton; 
	@InjectView(R.id.activity_form_details_finish) Button _finishButton; 
	
	private GestureDetector _gestureDetector;
    private GuestureListener _gestureListener;
	
    // Random number so that the label are drawn correctly on the screen
    private final int LABEL_ID_SEED = 277323;
    private int _labelId = LABEL_ID_SEED;
	private HashMap<Integer, Integer> _editors = new HashMap<Integer, Integer>();
	
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
		saveFieldData();
		_form.fillFormData(_formData);
		
		// TODO : Save to the persistant store
		
		finish();
	}

	private void LoadForm(){
		
		// Reseeds the label id's
		_labelId = LABEL_ID_SEED;
		
		_container.removeAllViews();
		
		if( _formData == null )
			_formData = new FormData(_form);
		
		_form.loadFormData(_formData);
		
		FormPage page = _form.Pages.get(_pageIndex);
		_subTitle.setText(page.Title);
		_subTitle2.setText("Page " + (_pageIndex + 1) + " of " + _form.Pages.size());
		
		for(int i = 0; i < page.Elements.size(); i++ )
		{
			FormElement element = page.Elements.get(i);
			switch (element.Type) {
			case TextBox:
				CreateTextBoxView(element);
				break;
			case MultilineTextBox:
				CreateMultilineTextBoxView(element);
				break;
			case Date:
				CreateDateView(element);
				break;
			case DateTime:
				CreateDateTimeView(element);
				break;
			case Time:
				CreateTimeView(element);
				break;
			case Label:
				CreateLabelView(element);
				break;
			case Checkbox:
				CreateCheckboxView(element);
				break;
			default:
				CreateTextBoxView(element);
				break;
			}
		}
		LayoutButtonBar();
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
		
		if(element.Value != null && ! element.Value.equals(""))
		{
			//spinner.setSelection(position)
		}
		// Populate spinner values 
		
		spinner.setId(element.Id);
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
		
		textbox.setText(element.Value);
		textbox.setId(element.Id);
		label.setText(element.Label);
		label.setId(GetNextLabelId());
		
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
			textbox.setText(element.Value);
		textbox.setId(element.Id);
		label.setText(element.Label);

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
			textbox.setText(element.Value);
		textbox.setId(element.Id);
		label.setText(element.Label);

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
		
		dateTextbox.setOnClickListener(new DateClickListener());
		
		TextView timeTextbox = (TextView)view.findViewById(R.id.form_datetime_edit_time);
		timeTextbox.setClickable(true);
		timeTextbox.setOnClickListener(new TimeClickListener());

		timeTextbox.setId(GetNextLabelId());
		_editors.put(element.Id, timeTextbox.getId());

		TextView label = (TextView)view.findViewById(R.id.form_datetime_label);
		label.setId(GetNextLabelId());
		label.setText(element.Label);
		
		if( element.Value != null && !element.Value.equals(""))
		{
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm");
			
			try {
				Date date = formatter.parse(element.Value);
				dateTextbox.setText(UIHelper.FormatLongDate(date));
				timeTextbox.setText(UIHelper.FormatTime(date));
			} catch (ParseException e) { e.printStackTrace(); }
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
			default:
				EditText defaultText = (EditText)findViewById(element.Id);
				element.Value = defaultText.getText().toString();
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
			
			TimePickerDialog.OnTimeSetListener dateSetListener = new TimePickerDialog.OnTimeSetListener() 
			{
				@Override public void onTimeSet(android.widget.TimePicker arg0, int hour, int minute) 
				{
	            	Date date = new Date();
	            	date.setHours(hour);
	            	date.setMinutes(minute);
	            	_currentDateEditor.setText(UIHelper.FormatTime(date));
				};
			};
				
            TimePickerDialog dialog = new TimePickerDialog( FormDetailsActivity.this, dateSetListener, dt.getHours(), dt.getMinutes(), true );
			dialog.show();
		}
	}
	
	private class DateClickListener implements OnClickListener
	{
		@Override
		public void onClick(View view) {
			
			_currentDateEditor = (TextView)view;
			Date dt = new Date();
			if( ! _currentDateEditor.getText().toString().equals(""))
				dt = new Date(_currentDateEditor.getText().toString());
			
			DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
	            public void onDateSet(DatePicker view, int year, int monthOfYear,
	                    int dayOfMonth) {
	            	
	            	Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
	            	_currentDateEditor.setText(UIHelper.FormatLongDate(date));
	            }};

			DatePickerDialog dialog = new DatePickerDialog(FormDetailsActivity.this, dateSetListener, dt.getYear() + 1900, dt.getMonth(), dt.getDate());
			dialog.show();
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
