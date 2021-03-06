package com.highfive;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_MULTIPLE = 2;
	private static final int CAMERA_REQUEST = 3;
	
	public static final String HOME_TO_DOODLE = "HomeToDoodle";
	public static final String HOME_TO_SHAKE = "HomeToShake";
	
	private Button launchDoodle;
	private Button launchShake;
	private int screenHeight;
	private String doodleImageUri;
	
	private Dialog dialog;
	private Uri uri;
	
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
				showCameraDialog();
			}
        });
        launchShake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, MultiChooserActivity.class);
				intent.putExtra("MAX_IMAGES", 5);
				startActivityForResult(intent, SELECT_MULTIPLE);
				
			}
        	
        });
        
    }

    private void showCameraDialog(){
   		dialog = new Dialog(this);
		dialog.setContentView(R.layout.camera_dialog);
		dialog.setTitle("Doodle");
		dialog.setCancelable(true);
   		
   		Button galleryButton = (Button) dialog.findViewById(R.id.galleryButton);
   		Button cameraButton = (Button) dialog.findViewById(R.id.cameraButton);
   		Button blankButton = (Button) dialog.findViewById(R.id.blankButton);
   		

   		galleryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }

        });
   		cameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String fileName = "HighFive" + System.currentTimeMillis();

           		ContentValues values = new ContentValues();
           		values.put(Images.Media.TITLE, fileName);
           		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
           		values.put(Images.Media.MIME_TYPE, "image/jpg");

           		uri = getContentResolver().insert(
           				Images.Media.EXTERNAL_CONTENT_URI, values);
            	
           		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
           		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
           		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CAMERA_REQUEST); 
            }
        }); 
   		
   		blankButton.setOnClickListener(new OnClickListener() {
   			@Override
   			public void onClick(View v) {
   				Intent intent = new Intent(HomeActivity.this, DoodleActivity.class);
   				startActivity(intent);
   			}
   		});
   		
   		dialog.show();

   	}
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		if (requestCode == SELECT_PICTURE) {
    			Uri selectedImageUri = data.getData();
    			doodleImageUri = getPath(selectedImageUri);
    			
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
    		else if (requestCode == CAMERA_REQUEST) {
    			doodleImageUri = getPath(uri);
    			if (doodleImageUri != null) {
    				uri = null;
    				Intent intent = new Intent(HomeActivity.this, DoodleActivity.class);
    				intent.putExtra(HOME_TO_DOODLE, doodleImageUri);
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
