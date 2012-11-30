package com.example.highfive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class DoodleActivity extends Activity {

	private TextView textView;
	private ImageView doodleImageView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);
        
        Intent intent = getIntent();
        String doodleImageUri = intent.getStringExtra(HomeActivity.HOME_TO_DOODLE);
       
        doodleImageView = (ImageView)findViewById(R.id.doodleImageView);
        Bitmap bitmap = BitmapFactory.decodeFile(doodleImageUri);
        doodleImageView.setImageBitmap(bitmap);
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
