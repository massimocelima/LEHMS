package com.lehms.ui.clinical;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.google.inject.Inject;
import com.lehms.NavigationHelper;
import com.lehms.R;
import com.lehms.UIHelper;
import com.lehms.adapters.JobAdapter;
import com.lehms.adapters.ProgressNoteAdapter;
import com.lehms.messages.GetClientDetailsResponse;
import com.lehms.messages.GetMeasurementSummaryListResponse;
import com.lehms.messages.GetProgressNotesResponse;
import com.lehms.messages.dataContracts.ClientDataContract;
import com.lehms.messages.dataContracts.ClientSummaryDataContract;
import com.lehms.messages.dataContracts.ProgressNoteDataContract;
import com.lehms.messages.dataContracts.RosterDataContract;
import com.lehms.serviceInterface.IClientResource;
import com.lehms.serviceInterface.IOfficeContactProvider;
import com.lehms.serviceInterface.IProgressNoteResource;
import com.lehms.serviceInterface.clinical.IClinicalMeasurementResource;
import com.lehms.ui.clinical.model.MeasurementSummary;
import com.lehms.ui.clinical.model.MeasurementType;
import com.lehms.ui.clinical.model.MeasurementTypeAdapter;
import com.lehms.ui.clinical.model.MeasurementTypeEnum;
import com.lehms.util.AppLog;
import com.lehms.util.IMeasurmentReportProvider;
import com.lehms.util.MeasurmentReportDocument;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;;

public class MeasurementSummaryListActivity extends RoboListActivity {

	public static final String EXTRA_CLIENT= "client"; 
	public static final String EXTRA_MEASUREMENT_TYPE= "measurement_type"; 

	private int _currentPageIndex = 0;
	private static final int PAGE_SIZE = 10;
	
	@InjectExtra(EXTRA_CLIENT) public ClientDataContract _client;
	@InjectExtra(EXTRA_MEASUREMENT_TYPE) public MeasurementType _measurementType;

	@InjectView(R.id.activity_title_image) ImageView _titleImageView;
	@InjectView(R.id.activity_title) TextView _titleTextView;
	@InjectView(R.id.activity_sub_title) TextView _subTitleTextView;

	@Inject IMeasurmentReportProvider _measurmentReportProvider;
	@Inject IOfficeContactProvider _officeContactProvider; 
	@Inject IClientResource _clientResource;
	
	private ImageView _refreshImageView;
	private TextView _refreshTextView;
	private TextView _noConnectionTextView;
	
	private MeasurementSummaryAdapter _adapter = null;

	@Inject IClinicalMeasurementResource _clinicalMeasurementResource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measurement_summary_list);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_CLIENT) != null )
			_client = (ClientDataContract)savedInstanceState.get(EXTRA_CLIENT);

		if(savedInstanceState != null && savedInstanceState.get(EXTRA_MEASUREMENT_TYPE) != null )
			_measurementType = (MeasurementType)savedInstanceState.get(EXTRA_MEASUREMENT_TYPE);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
        //listView.addHeaderView(buildListHeader(), null, true);
        listView.addFooterView(buildListFooter(), null, true);
        
		listView.setOnItemClickListener(new ListViewOnItemClickListener());
		
		initHeader();

		fillDataAsync(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_CLIENT, _client);
		outState.putSerializable(EXTRA_MEASUREMENT_TYPE, _measurementType);
	}
	
	protected void initHeader()
	{
		_titleTextView.setText(_client.FirstName + " " + _client.LastName);
		_subTitleTextView.setText(_measurementType.Name);
		MeasurementTypeAdapter.SetTitleImage(_measurementType.Type, _titleImageView);
	}

	/*
	protected View buildListHeader()
	{
        LayoutInflater inflater = getLayoutInflater(); 
        View header = inflater.inflate( R.layout.activity_progress_notes_list_header, null, false);
        return header;
	}
	*/
	protected View buildListFooter()
	{
        LayoutInflater inflater = getLayoutInflater(); 
        View header = inflater.inflate( R.layout.activity_measurement_summary_list_footer, null, false);
        _refreshImageView = (ImageView)header.findViewById(R.id.activity_list_footer_ic_refresh);
        _refreshTextView = (TextView)header.findViewById(R.id.activity_list_footer_refresh);
        _noConnectionTextView = (TextView) header.findViewById(R.id.activity_list_footer_no_connection);
        return header;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		 switch (resultCode) {
		 case  RESULT_OK:
			 //ProgressNoteDataContract newProgressNote = (ProgressNoteDataContract)data.getSerializableExtra(ProgressNoteDetailsActivity.EXTRA_PROGRESS_NOTE);
			 //_adapter.insert(newProgressNote, 0);
			 getListView().invalidate();
		 }
		
	}
	
	public void onRefreshClick(View view)
	{
		fillDataAsync(true);
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	private void fillDataAsync(Boolean refreshData)
	{
		//if( ! UIHelper.IsOnline(this))
		//{
		//	_adapter = new MeasurementSummaryAdapter(this, R.layout.measurement_summary_item, new ArrayList<MeasurementSummary>());
		//	getListView().setAdapter(_adapter);

		//	_refreshImageView.setVisibility(View.GONE);
		//	_refreshTextView.setVisibility(View.GONE);
		//	_noConnectionTextView.setVisibility(View.VISIBLE);
		//}
		//else
		//{
			_refreshImageView.setVisibility(View.VISIBLE);
			_refreshTextView.setVisibility(View.VISIBLE);
			_noConnectionTextView.setVisibility(View.GONE);

			LoadMeasurementSummariesTask task = new LoadMeasurementSummariesTask(this, refreshData);
			task.execute();
		//}
	}
	
	
	private class ListViewOnItemClickListener implements OnItemClickListener
	{
	
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {
			
			if( id == -1)
			{
				//if( index == 0 ) header click
				// else footer click
				_currentPageIndex += 1;
				fillDataAsync(false);
			}
			
		}
	}
	
	public void onSendMeasurmentsClick(View view)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("Who do you want to send the measurments to:")
        .setItems(R.array.send_measurment_selection, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	
            	try
            	{
	            	MeasurmentReportDocument doc = _measurmentReportProvider.createReport(_client, _adapter.getList(), _measurementType.Type);
	            	
	            	switch(which)
	            	{
	            	case 0:
	            		//OFFICE
	            		NavigationHelper.sendEmail(MeasurementSummaryListActivity.this, 
	            				_officeContactProvider.getOfficeEmail(), doc.Subject, doc.Body);
	            		break;
	            	case 1:
	            		//DOCTOR
	            		GetClientDetailsResponse response = _clientResource.GetClientDetails(Long.parseLong(_client.ClientId));
	            		NavigationHelper.sendEmail(MeasurementSummaryListActivity.this, 
	            				response.Doctor.Email, doc.Subject, doc.Body);
	            		break;
	            	case 2:
	            		//OTHER
	            		NavigationHelper.sendEmail(MeasurementSummaryListActivity.this, 
	            				"", doc.Subject, doc.Body);
	            		break;
	            	}
            	
	        	}
	        	catch(Exception e)
	        	{
	        		UIHelper.ShowAlertDialog(MeasurementSummaryListActivity.this, 
	        				"Error faxing measurments", 
	        				"Error faxing measurments: " + e.getMessage());
	        	}
           }
        })
        .setCancelable(true)
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .create();
	
		dialog.show();
	}

	public void onFaxMeasurmentsClick(View view)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("Who do you want to send the measurments to:")
        .setItems(R.array.send_measurment_selection, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	      
            	try
            	{
            		
		        	MeasurmentReportDocument doc = _measurmentReportProvider.createReport( _client, _adapter.getList(), _measurementType.Type);
		        	
		        	switch(which)
		        	{
		        	case 0:
		        		//OFFICE
		        		NavigationHelper.sendEmail(MeasurementSummaryListActivity.this, 
		        				_measurmentReportProvider.getFaxNumberForEmail(_officeContactProvider.getOfficeFax()), 
		        				doc.Subject, 
		        				doc.Body);
		        		break;
		        	case 1:
		        		//DOCTOR
		        		GetClientDetailsResponse response = _clientResource.GetClientDetails(Long.parseLong(_client.ClientId));
		        		NavigationHelper.sendEmail(MeasurementSummaryListActivity.this, 
		        				_measurmentReportProvider.getFaxNumberForEmail(response.Doctor.FaxNumber), 
		        				doc.Subject, 
		        				doc.Body);
		        		break;
		        	case 2:
		        		//OTHER
		        		NavigationHelper.sendEmail(MeasurementSummaryListActivity.this, 
		        				_measurmentReportProvider.getFaxNumberForEmail(""), 
		        				doc.Subject, 
		        				doc.Body);
		        		break;
		        	}
            	
            	}
            	catch(Exception e)
            	{
            		UIHelper.ShowAlertDialog(MeasurementSummaryListActivity.this, 
            				"Error faxing measurments", 
            				"Error faxing measurments: " + e.getMessage());
            	}
           }
        })
        .setCancelable(true)
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        })
        .create();
	
		dialog.show();
	}
	
	public void onViewChartClick(View view)
	{
		
	}
	
	private class LoadMeasurementSummariesTask extends AsyncTask<Void, Integer, GetMeasurementSummaryListResponse>
    {
    	private Activity _context;
    	private ProgressDialog _progressDialog;
    	private Exception _exception;
    	private Boolean _refreshData;

    	public LoadMeasurementSummariesTask(Activity context, Boolean refreshData)
    	{
    		_context = context;
    		
            _progressDialog = new ProgressDialog(context);
            _progressDialog.setMessage("Loading measurements for client...");
            _progressDialog.setIndeterminate(true);
            _progressDialog.setCancelable(true);
            
            _progressDialog.setOnCancelListener(new OnCancelListener() 
            {             
            	@Override             
            	public void onCancel(DialogInterface dialog) {                 
            		cancel(true);             
            	}         
            });
            
            _refreshData = refreshData;
            if(_refreshData)
        		_currentPageIndex = 0;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
            _progressDialog.show();
    	}
    	
    	@Override
    	protected void onCancelled() {
    		super.onCancelled();
			ListView listView = getListView();

			if(_currentPageIndex >= 0)
				_currentPageIndex -= 1;
			
			if( _adapter == null )
			{
				_adapter = new MeasurementSummaryAdapter(_context, R.layout.measurement_summary_item, new ArrayList<MeasurementSummary>());
				listView.setAdapter(_adapter);
			}
			listView.invalidate();
    	}
    	
		@Override
		protected GetMeasurementSummaryListResponse doInBackground(Void... arg0) {
			
			GetMeasurementSummaryListResponse result = null;
			
			try {
				result = _clinicalMeasurementResource.GetMeasurements(_client.ClientId, _measurementType, _currentPageIndex * PAGE_SIZE, PAGE_SIZE);
				
			} catch (Exception e) {
				AppLog.error(e.getMessage());
				_exception = e;
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(GetMeasurementSummaryListResponse result) {
			super.onPostExecute(result);
			
			if(isCancelled())
				return;
			
			if( result == null )
			{
				if( _exception != null )
					createDialog("Error", "Error retriving measurements: " + _exception.getMessage());
				else
					createDialog("Error", "Error retriving measurements");
			}
			else
			{
				ListView listView = getListView();
				
				if( _refreshData )
				{
					_adapter = new MeasurementSummaryAdapter(_context, R.layout.measurement_summary_item, result.Measurements);
					listView.setAdapter(_adapter);
					_currentPageIndex = 0;
				}
				else {
					for(int i = 0; i < result.Measurements.size(); i++)
						_adapter.add(result.Measurements.get(i));
				}
				
				listView.invalidate();
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
	
}
