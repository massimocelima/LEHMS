package com.lehms;

import com.lehms.util.MathUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import roboguice.activity.RoboActivity;  
import roboguice.inject.InjectView;  

public class Dashboard extends RoboActivity implements OnGestureListener
{
	private GestureDetector _gestureDetector;
	
	@InjectView(R.id.dashboard_tab_host) protected TabHost _tabHost;
	@InjectView(android.R.id.tabs) protected TabWidget _tabWidget;

    Animation _rightInAnimation;
    Animation _rightOutAnimation;
    Animation _leftInAnimation;
    Animation _leftOutAnimation;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		InitAnimations();
		
		_gestureDetector = new GestureDetector(this);

		_tabHost.setup();
		
		_tabHost.addTab(
        		_tabHost.newTabSpec("tabCarePractice")
    				.setIndicator(buildIndicator("Care Practice")) //, getResources().getDrawable(R.drawable.ic_tab_options))
    				.setContent(R.id.dashboard_care_practice_content)); // new Intent(this, Notepadv3.class)));

		_tabHost.addTab(
        		_tabHost.newTabSpec("tabPersonalResponse")
    				.setIndicator(buildIndicator("Personal Response")) //, getResources().getDrawable(R.drawable.ic_tab_options))
    				.setContent(R.id.dashboard_personal_response_content)); // new Intent(this, Notepadv3.class)));
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
		AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Please select the roster you would like to view")
            .setItems(R.array.dashboard_my_roster_selection, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

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

	public void onContactClick(View view)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
	        .setTitle("Please select how you would like to contact the office")
            .setItems(R.array.dashboard_contact_office_selection, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

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
		UIHelper.ShowUnderConstructionMessage(this);
	}
	
	public void onCameraClick(View view)
	{
		UIHelper.ShowUnderConstructionMessage(this);
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
