package com.example.highfive;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

public class HomeActivity extends Activity {

	private Button launchDoodle;
	private Button launchShake;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        launchDoodle = (Button)findViewById(R.id.LaunchDoodleButton);
        launchShake = (Button)findViewById(R.id.LaunchShakeButton);
        
        launchDoodle.setOnClickListener(toDoodleListener);
        launchShake.setOnClickListener(toShakeListener);
        
    }

    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
}
