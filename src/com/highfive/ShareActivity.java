package com.highfive;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.highfive.SessionEvents.AuthListener;
import com.highfive.SessionEvents.LogoutListener;

public class ShareActivity extends Activity{

	private LoginButton loginButton;
	private Button shareButton;
	private EditText photoCaption;
	private GPStracker gps;
    private TextView mText;
	private String imageCaption;
    public static final String APP_ID = "433628393357305";
    
    String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins",
    "photo_upload" };

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
                    + "specified before running this example: see FbAPIs.java");
            return;
        }
		
		setContentView(R.layout.activity_share);
		
		loginButton = (LoginButton) findViewById(R.id.loginFacebookButton);
		shareButton = (Button) findViewById(R.id.shareFacebookButton);
		photoCaption = (EditText)findViewById(R.id.shareCaption);
		Button backHomeButton = (Button) findViewById(R.id.backHomeButton);
		mText = (TextView) findViewById(R.id.shareTxt);
		Utility.mFacebook = new Facebook(APP_ID);
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		       	
       	SessionStore.restore(Utility.mFacebook, this);
        SessionEvents.addAuthListener(new DoodleAuthListener());
        SessionEvents.addLogoutListener(new DoodleLogoutListener());

        backHomeButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(ShareActivity.this, HomeActivity.class);
        		startActivity(intent);
        	}
        });
        loginButton.init(this,0,Utility.mFacebook, permissions);
        
        shareButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = getIntent();
            	Uri photoUri = intent.getParcelableExtra("com.highfive.share");
                if (photoUri != null) {
                    Bundle params = new Bundle();
                    try {
                        params.putByteArray("photo",
                                Utility.scaleImage(getApplicationContext(), photoUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageCaption = photoCaption.getText().toString();
                    
                    params.putString("caption", imageCaption.length() == 0 ? "HighFive photo upload" : imageCaption);
                    Utility.mAsyncRunner.request("me/photos", params, "POST",
                            new DoodleUploadListener(), null);
                    mText.setText("Please wait...");
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error selecting image from the gallery.", Toast.LENGTH_SHORT)
                            .show();
                }            	
            }
        });
        
        
        gps = new GPStracker(this);
        if (gps.canGetLocation()) {
        	
        	double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                StringBuffer ret = new StringBuffer();
                DecimalFormat f = new DecimalFormat("###.000");
                ret.append("latitude: " + f.format(latitude) + ", longitude: " + f.format(longitude) + "\n");
                int i = 1;
                while (add != null) {
                	ret.append(add + "\n");
                	add = obj.getAddressLine(i++);
                }
                photoCaption.setText(ret);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
        	
        	gps.showSettingsAlert();
        }
        
        shareButton.setVisibility(Utility.mFacebook.isSessionValid()?
                View.VISIBLE :
                View.INVISIBLE);
       
        
        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gps = new GPStracker(this);
        if (gps.canGetLocation()) {
        	
        	double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                StringBuffer ret = new StringBuffer();
                DecimalFormat f = new DecimalFormat("###.000");
                ret.append("latitude: " + f.format(latitude) + ", longitude: " + f.format(longitude) + "\n");
                int i = 1;
                while (add != null) {
                	ret.append(add + "\n");
                	add = obj.getAddressLine(i++);
                }
                photoCaption.setText(ret);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
		Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
	 
	 
	public class DoodleAuthListener implements AuthListener {

        public void onAuthSucceed() {
            mText.setText("You have logged in! ");
            shareButton.setVisibility(View.VISIBLE);
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }
	
	public class DoodleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
           mText.setText("Logging out...");
        }

        public void onLogoutFinish() {
           mText.setText("You have logged out! ");
           shareButton.setVisibility(View.INVISIBLE);          
        }
    }
	
	public class DoodleUploadListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
        	ShareActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mText.setText("Hello there, your photo has been uploaded\n");
                }
            });
        }

        public void onFacebookError(FacebookError error) {
            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
	
}
