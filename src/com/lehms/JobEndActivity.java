package com.lehms;

import java.util.ArrayList;
import java.util.Date;

import com.google.inject.Inject;
import com.lehms.controls.*;
import com.lehms.messages.JobEndActionRequest;
import com.lehms.messages.JobStartActionRequest;
import com.lehms.messages.dataContracts.*;
import com.lehms.persistence.Event;
import com.lehms.persistence.EventType;
import com.lehms.persistence.IEventFactory;
import com.lehms.persistence.IEventRepository;
import com.lehms.persistence.IRosterRepository;
import com.lehms.serviceInterface.IActiveJobProvider;
import com.lehms.serviceInterface.IDutyManager;
import com.lehms.serviceInterface.IEventExecuter;
import com.lehms.util.AppLog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.OnFinished;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class JobEndActivity extends LehmsRoboListActivity {

	public final static String EXTRA_JOB = "job";
	
	@InjectExtra(EXTRA_JOB) JobDetailsDataContract _job;
	
	@Inject IDutyManager _dutyManager;

	private ArrayList<String> _items = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_end);
		
		if(savedInstanceState != null && savedInstanceState.get(EXTRA_JOB) != null)
			_job = (JobDetailsDataContract)savedInstanceState.getSerializable(EXTRA_JOB);
		
		_items.add("Enter Consumable Costs");
		_items.add("Get Clients Signature");
		_items.add("Go Off Duty");
		
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _items);
		getListView().setAdapter(adapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() { 
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 

		    	switch(position)
		    	{
		    	case 0:
		    		// Enter consumable cost
		    		break;
		    	case 1:
		    		// Get clients signature
		    		break;
		    	case 2:
		    		_dutyManager.OffDuty();
		    		_items.remove(2);
		    		ListAdapter adapter = new ArrayAdapter<String>(JobEndActivity.this, android.R.layout.simple_list_item_1, _items);
		    		getListView().setAdapter(adapter);
		    		break;
		    	}

		    }}); 
	}
	
    @Override 
    protected void onSaveInstanceState(Bundle outState) { 
        super.onSaveInstanceState(outState); 
        outState.putSerializable(EXTRA_JOB, _job); 
    }
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	public void onEmergencyClick()
	{
		NavigationHelper.goEmergency(this);
	}
	
	
	public void onCloseClick(View view)
	{
		
	}
	
	public void onBackPressed() {
		super.onBackPressed();
	}


}
