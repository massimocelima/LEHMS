package com.lehms;


import java.util.Date;

import com.google.inject.Inject;
import com.lehms.controls.ActionItem;
import com.lehms.controls.QuickAction;
import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.util.AppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class ClientDetailsActivity  extends RoboActivity { //implements AsyncQueryListener 

	public static final String EXTRA_CLIENT_ID = "ClientId";
	
	private GetClientDetailsResponse _clientResponse;
	private QuickAction _quickAction;
	@Inject protected IClientResource _clientResource;
	@InjectExtra(EXTRA_CLIENT_ID) protected Long _clientId;

	@InjectView(R.id.activity_client_details_address_value) TextView _addressTextView;
	@InjectView(R.id.activity_client_details_date_of_birth_value) TextView _dateOfBirthTextView;
	@InjectView(R.id.activity_client_details_phone_value) TextView _phoneTextView;
	@InjectView(R.id.activity_client_details_title) TextView _titleTextView;
	@InjectView(R.id.activity_client_details_sub_title) TextView _subTitleTextView;
	@InjectView(R.id.activity_client_details_sub_title2) TextView _subTitle2TextView;

	
	@InjectView(R.id.activity_client_details_contact_container) LinearLayout _contactsContainer;
	@InjectView(R.id.activity_client_details_contact_expander) ImageButton _contactsContainerExpander;
	
	@InjectView(R.id.activity_client_details_contacts_carer_name_value) TextView _carerNameTextView;
	@InjectView(R.id.activity_client_details_contacts_carer_rela_value) TextView _carerRelTextView;
	@InjectView(R.id.activity_client_details_contacts_carer_mobile_value) TextView _carerMobileTextView;
	@InjectView(R.id.activity_client_details_contacts_carer_phone_value) TextView _carerPhoneTextView;
	@InjectView(R.id.activity_client_details_contacts_emergency_value) TextView _emergencyContactTextView;

	
	
	@InjectView(R.id.activity_client_details_pharmacy_container) LinearLayout _pharmacyContainer;
	@InjectView(R.id.activity_client_details_pharmacy_expander) ImageButton _pharmacyContainerExpander;
	
	@InjectView(R.id.activity_client_details_pharmacy_name_value) TextView _pharmacyNameTextView;
	@InjectView(R.id.activity_client_details_pharmacy_address_value) TextView _pharmacyAddressView;
	@InjectView(R.id.activity_client_details_pharmacy_email_value) TextView _pharmacyEmailTextView;
	@InjectView(R.id.activity_client_details_pharmacy_phone_value) TextView _pharmacyPhoneTextView;
	@InjectView(R.id.activity_client_details_pharmacy_fax_value) TextView _pharmacyFaxTextView;

	
	@InjectView(R.id.activity_client_details_doctor_container) LinearLayout _doctorContainer;
	@InjectView(R.id.activity_client_details_doctor_expander) ImageButton _doctorContainerExpander;
	
	@InjectView(R.id.activity_client_details_doctor_name_value) TextView _doctorNameTextView;
	@InjectView(R.id.activity_client_details_doctor_address_value) TextView _doctorAddressView;
	@InjectView(R.id.activity_client_details_doctor_email_value) TextView _doctorEmailTextView;
	@InjectView(R.id.activity_client_details_doctor_phone_value) TextView _doctorPhoneTextView;
	@InjectView(R.id.activity_client_details_doctor_fax_value) TextView _doctorFaxTextView;

	
	@InjectView(R.id.activity_client_details_allergies_container) LinearLayout _allergiesContainer;
	@InjectView(R.id.activity_client_details_allergies_expander) ImageButton _allergiesContainerExpander;
	
	@InjectView(R.id.activity_client_details_allergies_value) TextView _allergiesTextView;

	
	@InjectView(R.id.activity_client_details_medical_conditions_container) LinearLayout _medicalConditionsContainer;
	@InjectView(R.id.activity_client_details_medical_conditions_expander) ImageButton _medicalConditionsContainerExpander;
	
	@InjectView(R.id.activity_client_details_medical_conditions_value) TextView _medicalConditionsTextView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_details);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT_ID) != null)
			_clientId = savedInstanceState.getLong(EXTRA_CLIENT_ID);
		
		LoadClient();
		CreateQuickActions();
	}

    @Override 
    protected void onSaveInstanceState(Bundle outState) { 
        super.onSaveInstanceState(outState); 
        outState.putSerializable(EXTRA_CLIENT_ID, _clientId); 
    }
	
	private void LoadClient(){
		LoadClientTask task = new LoadClientTask(this);
		task.execute();
	}
	
	public void onHomeClick(View view)
	{
		UIHelper.GoHome(this);
	}
	
	public void onRefreshClick(View view)
	{
		LoadClient();
	}
	
	private class LoadClientTask extends AsyncTask<Void, Void, GetClientDetailsResponse>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Exception _exception;

    	public LoadClientTask(Activity context)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading client details please wait...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(false);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
		@Override
		protected GetClientDetailsResponse doInBackground(Void... arg0) {
			
			try {
				_clientResponse = _clientResource.GetClientDetails(_clientId);
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			} 
			
			return _clientResponse;
		}
		
		@Override
		protected void onPostExecute(GetClientDetailsResponse result) {
			super.onPostExecute(result);
			
			if( result == null )
			{
				if( _exception != null )
					createDialog("Error", "Error retriving clients: " + _exception.getMessage());
				else
					createDialog("Error", "Error retriving clients");
			}
			else
			{
				_titleTextView.setText( result.Client.LastName + ", " + result.Client.FirstName );
				_subTitleTextView.setText( result.Client.ClientId );
				_addressTextView.setText( UIHelper.FormatAddress(result.Client.Address) );
				_dateOfBirthTextView.setText( UIHelper.FormatLongDate(result.Client.DateOfBirth) );
				_phoneTextView.setText( result.Client.Phone );
				
				LoadContacts(result.Client);
				LoadPharmacy(result);
				LoadDoctor(result);
				LoadAllergies(result);
				LoadMedicalConditions(result);
			}
			
			if( _progressDialog.isShowing() )
				_progressDialog.dismiss();
		}
		
	    private void createDialog(String title, String text) {
	        AlertDialog ad = new AlertDialog.Builder(_context)
	        	.setPositiveButton("Ok", null)
	        	.setTitle(title)
	        	.setMessage(text)
	        	.create();
	        ad.show();
	    }
		
    }
	
	// END LoadClientTask Class
	
	public void onContactsExpanderClick(View view)
	{
		if(_contactsContainer.getVisibility() == View.GONE)
		{
			_contactsContainer.setVisibility(View.VISIBLE);
			_contactsContainerExpander.setImageResource(R.drawable.expander_ic_maximized);
		}
		else
		{
			_contactsContainer.setVisibility(View.GONE);
			_contactsContainerExpander.setImageResource(R.drawable.expander_ic_minimized);
		}
	}
	
	private void LoadContacts(ClientDataContract client)
	{
		_emergencyContactTextView.setText(client.EmergencyContact);
		if( client.Carer != null )
		{
			_carerMobileTextView.setText(client.Carer.Mobile);
			_carerNameTextView.setText(client.Carer.Name);
			_carerPhoneTextView.setText(client.Carer.Phone);
			_carerRelTextView.setText(client.Carer.Relationship);
		}
	}
	
	public void onPharmacyExpanderClick(View view)
	{
		if(_pharmacyContainer.getVisibility() == View.GONE)
		{
			_pharmacyContainer.setVisibility(View.VISIBLE);
			_pharmacyContainerExpander.setImageResource(R.drawable.expander_ic_maximized);
		}
		else
		{
			_pharmacyContainer.setVisibility(View.GONE);
			_pharmacyContainerExpander.setImageResource(R.drawable.expander_ic_minimized);
		}
	}
	
	private void LoadPharmacy(GetClientDetailsResponse response)
	{
		if( response.Pharmcy != null )
		{
			_pharmacyAddressView.setText(UIHelper.FormatAddress(response.Pharmcy.Address));
			_pharmacyEmailTextView.setText(response.Pharmcy.Email);
			_pharmacyFaxTextView.setText(response.Pharmcy.FaxNumber);
			_pharmacyNameTextView.setText(response.Pharmcy.Name);
			_pharmacyPhoneTextView.setText(response.Pharmcy.PhoneNumber);
		}
	}

	
	public void onDoctorExpanderClick(View view)
	{
		if(_doctorContainer.getVisibility() == View.GONE)
		{
			_doctorContainer.setVisibility(View.VISIBLE);
			_doctorContainerExpander.setImageResource(R.drawable.expander_ic_maximized);
		}
		else
		{
			_doctorContainer.setVisibility(View.GONE);
			_doctorContainerExpander.setImageResource(R.drawable.expander_ic_minimized);
		}
	}
	
	private void LoadDoctor(GetClientDetailsResponse response)
	{
		if( response.Doctor != null )
		{
			_doctorAddressView.setText( UIHelper.FormatAddress( response.Doctor.Address));
			_doctorEmailTextView.setText(response.Doctor.Email);
			_doctorFaxTextView.setText(response.Doctor.FaxNumber);
			_doctorNameTextView.setText(response.Doctor.FirstName + " " + response.Doctor.LastName);
			_doctorPhoneTextView.setText(response.Doctor.PhoneNumber);
		}
	}
	
	public void onAllergiesExpanderClick(View view)
	{
		if(_allergiesContainer.getVisibility() == View.GONE)
		{
			_allergiesContainer.setVisibility(View.VISIBLE);
			_allergiesContainerExpander.setImageResource(R.drawable.expander_ic_maximized);
		}
		else
		{
			_allergiesContainer.setVisibility(View.GONE);
			_allergiesContainerExpander.setImageResource(R.drawable.expander_ic_minimized);
		}
	}
	
	private void LoadAllergies(GetClientDetailsResponse response)
	{
		String result = "";
		if( response.Allergies != null )
		{
			for(int i = 0; i<response.Allergies.size(); i++)
				result += response.Allergies.get(i).Name + "\n";
		}
		
		_allergiesTextView.setText(result);
	}
	
	public void onMedicalConditionsExpanderClick(View view)
	{
		if(_medicalConditionsContainer.getVisibility() == View.GONE)
		{
			_medicalConditionsContainer.setVisibility(View.VISIBLE);
			_medicalConditionsContainerExpander.setImageResource(R.drawable.expander_ic_maximized);
		}
		else
		{
			_medicalConditionsContainer.setVisibility(View.GONE);
			_medicalConditionsContainerExpander.setImageResource(R.drawable.expander_ic_minimized);
		}
	}
	
	private void LoadMedicalConditions(GetClientDetailsResponse response)
	{
		String result = "";
		if( response.MedicalConditions != null )
		{
			for(int i = 0; i<response.MedicalConditions.size(); i++)
				result += response.MedicalConditions.get(i).Name + "\n";
		}
		
		_medicalConditionsTextView.setText(result);
	}
	
	private void CreateQuickActions()
	{
		final ActionItem qaProgressNotes = new ActionItem();
		
		qaProgressNotes.setTitle("Progress Notes");
		qaProgressNotes.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_progress_notes));
		qaProgressNotes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientDetailsActivity.this, "Progress Notes", Toast.LENGTH_SHORT).show();
				_quickAction.dismiss();
			}
		});

		final ActionItem qaCompleteForms = new ActionItem();
		
		qaCompleteForms.setTitle("Complete Forms");
		qaCompleteForms.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_complete_forms));
		qaCompleteForms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientDetailsActivity.this, "Complete Forms", Toast.LENGTH_SHORT).show();
				_quickAction.dismiss();
			}
		});

		final ActionItem qaTakeAPicture = new ActionItem();
		
		qaTakeAPicture.setTitle("Take A Picture");
		qaTakeAPicture.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_camera));
		qaTakeAPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientDetailsActivity.this, "Take A Picture", Toast.LENGTH_SHORT).show();
				_quickAction.dismiss();
			}
		});
		
		final ActionItem qaClinicalDetails = new ActionItem();
		
		qaClinicalDetails.setTitle("Clinical Details");
		qaClinicalDetails.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_nurse));
		qaClinicalDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientDetailsActivity.this, "Clinical Details", Toast.LENGTH_SHORT).show();
				_quickAction.dismiss();
			}
		});

		final ActionItem qaContact = new ActionItem();
		
		qaContact.setTitle("Contact ...");
		qaContact.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_contact));
		qaContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(ClientDetailsActivity.this, "Contact", Toast.LENGTH_SHORT).show();
				_quickAction.dismiss();
			}
		});

		final ActionItem qaNavigate = new ActionItem();
		
		qaNavigate.setTitle("Navigate to Client's Home");
		qaNavigate.setIcon(getResources().getDrawable(R.drawable.quick_actions_ic_navigate));
		qaNavigate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.LaunchNavigation(_clientResponse.Client.Address.getAddressNameForGeocoding(), ClientDetailsActivity.this);
				_quickAction.dismiss();
			}
		});

		
		Button btn1 = (Button) this.findViewById(R.id.activity_client_details_more);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				_quickAction = new QuickAction(v);
				
				_quickAction.addActionItem(qaClinicalDetails);
				_quickAction.addActionItem(qaNavigate);
				_quickAction.addActionItem(qaProgressNotes);
				_quickAction.addActionItem(qaCompleteForms);
				_quickAction.addActionItem(qaTakeAPicture);
				_quickAction.addActionItem(qaContact);
				
				_quickAction.show();
			}
		});
	}
	
}
