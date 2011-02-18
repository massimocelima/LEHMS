package com.lehms;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv;
    
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
        
        tv = new TextView(this);
        setContentView(tv);
    }
    
    private void startup(Intent i) {
        int user = i.getIntExtra("userid",-1);
        tv.setText("UserID: "+String.valueOf(user)+" logged in");
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_CANCELED) 
            finish(); 
        else 
            startup(data);
    }
	
}

