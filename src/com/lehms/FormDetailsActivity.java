package com.lehms;

import java.util.Collection;
import java.util.List;

import com.lehms.controls.*;

import com.google.inject.Inject;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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

	private void LoadForm(){
		
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
			default:
				CreateTextBoxView(element);
				break;
			}
		}
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
		
		return view;
	}

	private View CreateMultilineTextBoxView(FormElement element)
	{
		LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.form_textbox, _container, true);

		EditText textbox = (EditText)view.findViewById(R.id.form_textbox_edit);
		textbox.setLines(3);
		TextView label = (TextView)view.findViewById(R.id.form_textbox_label);
		
		textbox.setText(element.Value);
		textbox.setId(element.Id);
		label.setText(element.Label);
		
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
			default:
				EditText defaultText = (EditText)findViewById(element.Id);
				element.Value = defaultText.getText().toString();
				break;
			}
		}
	}
	
}
