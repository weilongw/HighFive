package com.highfive;

import java.util.ArrayList;

import com.highfive.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_MULTIPLE = 2;
	
	public static final String HOME_TO_DOODLE = "HomeToDoodle";
	public static final String HOME_TO_SHAKE = "HomeToShake";
	
	private Button launchDoodle;
	private Button launchShake;
	private int screenHeight;
	private String doodleImageUri;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        launchDoodle = (Button)findViewById(R.id.LaunchDoodleButton);
        launchShake = (Button)findViewById(R.id.LaunchShakeButton);
        Display display = getWindowManager().getDefaultDisplay(); 
        screenHeight = display.getHeight();
        
        launchDoodle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);		
			}
        });
        launchShake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, MultiChooserActivity.class);
				intent.putExtra("MAX_IMAGES", 3);
				startActivityForResult(intent, SELECT_MULTIPLE);
				
			}
        	
        });
        
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		if (requestCode == SELECT_PICTURE) {
    			Uri selectedImageUri = data.getData();
    			doodleImageUri = getPath(selectedImageUri);
    			
    			//System.out.println(doodleImageUri);
    			if (doodleImageUri != null) {
    				Intent intent = new Intent(HomeActivity.this, DoodleActivity.class);
    				intent.putExtra(HOME_TO_DOODLE, doodleImageUri);
    				startActivity(intent);
    			}
    		}
    		else if (requestCode == SELECT_MULTIPLE) {
    			ArrayList<String> results = data.getStringArrayListExtra("MULTIPLEFILENAMES");
    			if (results != null) {
    				Intent intent = new Intent(HomeActivity.this, ShakeActivity.class);
    				intent.putStringArrayListExtra(HOME_TO_SHAKE, results);
    				intent.putExtra("SCREEN_HEIGHT", screenHeight);
    				startActivity(intent);
    			}
    		}
    	}
    }
    
    public String getPath(Uri uri) {
    	String[] projection = {MediaStore.Images.Media.DATA};
    	Cursor cursor = managedQuery(uri, projection, null, null, null);
    	if (cursor != null) {
    		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    		cursor.moveToFirst();
    		return cursor.getString(column_index);
    		
    	}
    	return null;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        return true;
    }
}
