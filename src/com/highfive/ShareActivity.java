package com.highfive;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	
    private TextView mText;
	
    public static final String APP_ID = "433628393357305";
    
//    private Facebook mFacebook;
//    private AsyncFacebookRunner mAsyncRunner;
    
    String[] permissions = { "offline_access", "publish_stream", "user_photos", "publish_checkins",
    "photo_upload" };


	private Handler mHandler;
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
		Button backHomeButton = (Button) findViewById(R.id.backHomeButton);
		mText = (TextView) findViewById(R.id.shareTxt);
		mHandler = new Handler();
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
                    params.putString("caption", "HighFive photo upload");
                    Utility.mAsyncRunner.request("me/photos", params, "POST",
                            new DoodleUploadListener(), null);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error selecting image from the gallery.", Toast.LENGTH_SHORT)
                            .show();
                }            	
            }
        });
        
        shareButton.setVisibility(Utility.mFacebook.isSessionValid() ?
                View.VISIBLE :
                View.INVISIBLE);
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
            //dialog.dismiss();
           /* mHandler.post(new Runnable() {
                @Override
                public void run() {
                    new UploadPhotoResultDialog(ShareActivity.this, "Upload Photo executed", response)
                            .show();
                }
            });*/
        	//mText.setText(response);
        	ShareActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mText.setText("Hello there, your photo has been uploaded\n");
                }
            });
        	//Toast.makeText(getApplicationContext(), response,
              //      Toast.LENGTH_LONG).show();
        }

        public void onFacebookError(FacebookError error) {
            //dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
	
}
