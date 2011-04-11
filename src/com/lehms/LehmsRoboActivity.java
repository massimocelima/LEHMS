package com.lehms;

import com.google.inject.Inject;
import com.lehms.messages.dataContracts.Permission;
import com.lehms.serviceInterface.IAuthenticationProvider;
import com.lehms.serviceInterface.IAuthenticationService;
import com.lehms.serviceInterface.IAuthorisationProvider;
import com.lehms.serviceInterface.IDutyManager;
import com.lehms.serviceInterface.IIdentityProvider;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import roboguice.activity.RoboActivity;

public class LehmsRoboActivity extends RoboActivity {

	@Inject IDutyManager _dutyManager;
	@Inject IIdentityProvider _identityProvider;
	@Inject IAuthorisationProvider _authorisationProvider;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.xml.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_off_duty:
	        _dutyManager.OffDuty();
	        return true;
	    case R.id.menu_on_duty:
	        _dutyManager.OnDuty();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override 
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		menu.getItem(0).setVisible(!_dutyManager.IsOnDuty());
		menu.getItem(1).setVisible(_dutyManager.IsOnDuty());
        return super.onPrepareOptionsMenu(menu);
	};
	
	public void onEmergencyClick(View view)
	{
		NavigationHelper.goEmergency(this);
	}
	
	public void onHomeClick(View view)
	{
		NavigationHelper.goHome(this);
	}
	
	public IDutyManager getDutyManager()
	{
		return _dutyManager;
	}
	
	public IIdentityProvider getIdentityProvider()
	{
		return _identityProvider;
	}
	
	public Boolean isAuthrosied(Permission permission)
	{
		return _authorisationProvider.isAuthorised(permission);
	}
}
