package com.highfive;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
	private static final int CAMERA_REQUEST = 3;
	
	public static final String HOME_TO_DOODLE = "HomeToDoodle";
	public static final String HOME_TO_SHAKE = "HomeToShake";
	
	private Button launchDoodle;
	private Button launchShake;
	private int screenHeight;
	//private int screenWidth;
	private String doodleImageUri;
	
	private Dialog dialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        launchDoodle = (Button)findViewById(R.id.LaunchDoodleButton);
        launchShake = (Button)findViewById(R.id.LaunchShakeButton);
        Display display = getWindowManager().getDefaultDisplay(); 
        screenHeight = display.getHeight();
        //screenWidth = display.getWidth();
        launchDoodle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Intent intent = new Intent();
				//intent.setType("image/*");
				//intent.setAction(Intent.ACTION_GET_CONTENT);
				//startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
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
            	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
                startActivityForResult(cameraIntent, CAMERA_REQUEST); 
            }
        }); 
   		
   		blankButton.setOnClickListener(new OnClickListener() {
   			@Override
   			public void onClick(View v) {
   				Intent intent = new Intent(HomeActivity.this, DoodleActivity.class);
   				intent.putExtra(HOME_TO_DOODLE, "blank");
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
    		else if (requestCode == CAMERA_REQUEST) {
    			Bitmap photo = (Bitmap) data.getExtras().get("data"); 
                //imageView.setImageBitmap(photo);
    			Intent intent = new Intent(HomeActivity.this, DoodleActivity.class);
    			intent.putExtra("com.highfive.doodle", photo);
    			startActivity(intent);
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
