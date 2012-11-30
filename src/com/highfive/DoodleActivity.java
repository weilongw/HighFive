package com.highfive;

import com.highfive.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class DoodleActivity extends Activity {
	
	public static String doodleImageUri;
	
	private DoodleView doodleView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        doodleImageUri = intent.getStringExtra(HomeActivity.HOME_TO_DOODLE);
        setContentView(R.layout.activity_doodle);
       
        doodleView = (DoodleView)findViewById(R.id.doodleView);
        //Bitmap bitmap = BitmapFactory.decodeFile(doodleImageUri);
        //doodleImageView.setImageBitmap(bitmap);
        //textView.setText(doodleImageUri);
        
        
        
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_doodle, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
          /*  case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

}
