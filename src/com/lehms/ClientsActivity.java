package com.lehms;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;

public class ClientsActivity  extends RoboListActivity { //implements AsyncQueryListener 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clients);
		
		ListView listView = getListView();
		listView.setTextFilterEnabled(true); 
        //listView.addHeaderView(buildHeader());		
        //listView.addFooterView(buildFooter(), null, false);

		//SimpleCursorAdapter adapter = 
	    //        new SimpleCursorAdapter(this, R.layout.client_item, null, mappingFrom, mappingTo);
	    //setListAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() { 
			    public void onItemClick(AdapterView<?> parent, View view, 
			        int position, long id) { 
			      // When clicked, show a toast with the TextView text 
			      Toast.makeText(getApplicationContext(), ((TextView) view).getText(), 
			          Toast.LENGTH_SHORT).show(); 
			    } 
			  }); 
	}

	
	public void onHomeClick(View view)
	{
		UIHelper.GoHome(this);
	}
	
	public void onRefreshClick(View view)
	{
	}
	
}
