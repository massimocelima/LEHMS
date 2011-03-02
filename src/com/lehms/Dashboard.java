package com.lehms;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import com.lehms.serviceInterface.IIdentityProvider;
import com.lehms.serviceInterface.IOfficeContactProvider;
import com.lehms.util.MathUtils;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import roboguice.activity.RoboActivity;  
import roboguice.inject.InjectView;  

public class Dashboard extends RoboActivity implements OnGestureListener
{
	@Inject protected IIdentityProvider _identityProvider;
	@Inject protected IOfficeContactProvider _officeContactProvider;
	@InjectView(R.id.footer_text) protected TextView _footerText;
	
	@InjectView(R.id.dashboard_tab_host) protected TabHost _tabHost;
	@InjectView(android.R.id.tabs) protected TabWidget _tabWidget;
	@InjectView(R.id.footer_container) protected LinearLayout _footerContainer;
	
    Animation _rightInAnimation;
    Animation _rightOutAnimation;
    Animation _leftInAnimation;
    Animation _leftOutAnimation;

	private GestureDetector _gestureDetector;
    private GuestureListener _gestureListener;
	
    private int CAPTURE_PICTURE_INTENT = 1;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		InitAnimations();
	
		_gestureDetector = new GestureDetector(this);
		_gestureListener = new GuestureListener(_gestureDetector); 

		_tabHost.setup();
		_tabHost.setOnTouchListener(_gestureListener);
		
		_tabHost.addTab(
        		_tabHost.newTabSpec("tabCarePractice")
    				.setIndicator(buildIndicator("Care Practice")) //, getResources().getDrawable(R.drawable.ic_tab_options))
    				.setContent(R.id.dashboard_care_practice_content)); // new Intent(this, Notepadv3.class)));

		_tabHost.addTab(
        		_tabHost.newTabSpec("tabPersonalResponse")
    				.setIndicator(buildIndicator("Personal Response")) //, getResources().getDrawable(R.drawable.ic_tab_options))
    				.setContent(R.id.dashboard_personal_response_content)); // new Intent(this, Notepadv3.class)));

		SetTouchEventOnChildViews(_tabHost);

		try
		{
			_footerText.setText(
					"Welcome " + 
					_identityProvider.getCurrent().FirstName + " " + _identityProvider.getCurrent().LastName);
		}
		catch(Exception ex)
		{
			Log.e("LEHMS", "Failed to set footer in dashboard");
		}
		
	}

	private void SetTouchEventOnChildViews(ViewGroup viewGroup)
	{
		for(int i = 0; i < viewGroup.getChildCount(); i++)
		{
			View view = viewGroup.getChildAt(i);
			view.setOnTouchListener(_gestureListener);
			if(  view instanceof ViewGroup ){
				SetTouchEventOnChildViews((ViewGroup)view);
			}
		}
	}
	
	private void InitAnimations()
	{
        _rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        _rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        _leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        _leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
	}
	
	public void onMyRosterClick(View view)
	{
		Intent i = new Intent(getApplicationContext(), RosterActivity.class);
		Date rosterDate = new Date();
		i.putExtra("roster_date", rosterDate);
		startActivity(i);
	}

	public void onContactClick(View view)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Please select how you would like to contact the office")
            .setItems(R.array.dashboard_contact_office_selection, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	switch(which)
                	{
                	case 0:
                		UIHelper.MakeCall( _officeContactProvider.getOfficePhoneNumber(), Dashboard.this);
                		break;
                	case 1:
                		UIHelper.OpenEmail(Dashboard.this, _officeContactProvider.getOfficeEmail());
                		break;
                	case 2:
                		UIHelper.OpenCall(Dashboard.this);
                		break;
                	case 3:
                		UIHelper.OpenEmail(Dashboard.this, null);
                		break;
                	}
                	
                	//_officeContactProvider.getOfficePhoneNumber();
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
	
	public void onClientsClick(View view)
	{
		Intent i = new Intent(this, ClientsActivity.class);
		startActivity(i);
	}
	
	public void onMapClick(View view)
	{
        // This example shows how to add a custom layout to an AlertDialog
        LayoutInflater factory = LayoutInflater.from(this);
        final View navigationLocationDialogView = factory.inflate(R.layout.navigation_location_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Enter the location you are traveling to:")
            .setView(navigationLocationDialogView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                	EditText location = (EditText)navigationLocationDialogView.findViewById(R.id.navigation_location_dialog_location);
                	if( ! location.getText().equals(""))
	            		UIHelper.LaunchNavigation(location.getText().toString(), Dashboard.this);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            })
            .create();
		
        dialog.show();

	    //String uri = "geo:0,0?q=MCNAMARA+TERMINAL+ROMULUS+MI+48174";             
	    //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));     
	    //startActivity(i);
		
		//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + an + address + city)));
		//startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + phoneNumber)));
		//Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		//        photoPickerIntent.setType("image/gif");
		//        photoPickerIntent.setType("image/jpeg");
		//        photoPickerIntent.setType("image/tiff");
		//        photoPickerIntent.setType("image/png");
		//        photoPickerIntent.setType("image/bmp");
		//        startActivityForResult(photoPickerIntent, RESULT_IMAGE_RETURNED);
		
		//UIHelper.ShowUnderConstructionMessage(this);
	}
	
	private Uri _capturedImageURI;
	
	public void onCameraClick(View view)
	{
		//TODO Get the image and do something with it
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        _capturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);            
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _capturedImageURI);
        startActivityForResult(intent, CAPTURE_PICTURE_INTENT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{

		if( requestCode == CAPTURE_PICTURE_INTENT)
		{
			if( resultCode == -1 )
			{
		        String[] projection = { MediaStore.Images.Media.DATA};              
		        Cursor cursor = managedQuery(_capturedImageURI, projection, null, null, null);              
		        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);              
		        cursor.moveToFirst();              
		        String capturedImageFilePath = cursor.getString(column_index_data);
		        
		        UIHelper.ShowAlertDialog(this, "Picture saved", "The picture was saved to " + capturedImageFilePath);
			}
		}
        
	}
	
	public void onEmergencyClick(View view)
	{
		UIHelper.ShowUnderConstructionMessage(this);
	}
	
	public void onCallCallCentreClick(View view)
	{
		UIHelper.ShowUnderConstructionMessage(this);
	}
	
	public void onCalendarClick(View view)
	{
		UIHelper.ShowUnderConstructionMessage(this);
	}
	
    private View buildIndicator(String string) {
    	
        final TextView indicator = (TextView) getLayoutInflater().inflate(R.layout.tab_indicator,
        		_tabWidget, false);
        indicator.setText(string);
        return indicator;
    }

	public boolean onTouchEvent(MotionEvent event) {  
		return _gestureDetector.onTouchEvent(event);
	} 

    // Begin OnGestureListener Implementation
    
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		
		int minScaledFlingVelocity  = ViewConfiguration.get(this).getScaledMinimumFlingVelocity() * 10; // 10 = fudge by experimentation
		
        int tabCount = _tabWidget.getTabCount();
        int currentTab = _tabHost.getCurrentTab();
        if (Math.abs(velocityX) > minScaledFlingVelocity  &&
            Math.abs(velocityY) < minScaledFlingVelocity ) {

            int newTab = currentTab;
            if( velocityX < 0 )
            	newTab = MathUtils.constrain(currentTab + 1, 0, tabCount - 1);
            else 
            	newTab = MathUtils.constrain(currentTab - 1, 0, tabCount - 1);
            
            if (newTab != currentTab) {
                View currentView = _tabHost.getCurrentView();
                _tabHost.setCurrentTab(newTab);
                View newView = _tabHost.getCurrentView();
                newView.startAnimation(velocityX < 0 ? _rightInAnimation : _leftInAnimation);
                currentView.startAnimation(velocityX < 0 ? _rightOutAnimation : _leftOutAnimation);
            }
        }
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,float arg3) { return false; }

	@Override
	public void onShowPress(MotionEvent arg0) {	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) { return false; }
	
    // End OnGestureListener Implementation

 
}
