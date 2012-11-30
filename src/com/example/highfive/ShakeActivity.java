package com.example.highfive;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShakeActivity extends Activity {

	private TextView shakeTextView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        
        shakeTextView = (TextView)findViewById(R.id.shakeTextView);
        
        Intent data = getIntent();
        ArrayList<String> files = data.getStringArrayListExtra(HomeActivity.HOME_TO_SHAKE);
        StringBuilder results = new StringBuilder();
        for (String file: files) {
        	results.append(file + "\n");
        }
        shakeTextView.setText(results.toString());
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shake, menu);
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
