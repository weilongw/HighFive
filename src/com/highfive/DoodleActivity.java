package com.highfive;


import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;




public class DoodleActivity extends Activity implements ColorPickerDialog.OnColorChangedListener {
	
	public static Bitmap bitmap;
	
	private ImageView doodleImageView;
	private DoodleView doodleView;
	private Dialog dialog;
	private MaskFilter emboss = new EmbossMaskFilter(new float[] {1,1,1}, 0.4f, 6, 3.5f);
	private MaskFilter blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int BRUSH_MENU_ID = Menu.FIRST + 1;
	private static final int WIDTH_MENU_ID = Menu.FIRST + 2;
	private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int CLEAR_MENU_ID = Menu.FIRST + 4;
	private static final int SAVE_MENU_ID = Menu.FIRST + 5;
	
	private SensorManager sensorManager; 
	private float acceleration; 
	private float currentAcceleration; 
	private float lastAcceleration; 
	private AtomicBoolean dialogIsVisible = new AtomicBoolean(); 
	private static final int ACCELERATION_THRESHOLD = 15000;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String doodleImageUri = intent.getStringExtra(HomeActivity.HOME_TO_DOODLE);
        
        
        setContentView(R.layout.doodle);
        doodleView = (DoodleView)findViewById(R.id.doodleView);
        doodleImageView = (ImageView)findViewById(R.id.doodleImageView);
        
        
        Display display = getWindowManager().getDefaultDisplay(); 
        if (doodleImageUri == null) {
        	
        	bitmap = Bitmap.createBitmap(display.getWidth(), display.getHeight(), Bitmap.Config.RGB_565);
        	Canvas canvas = new Canvas(bitmap);
        	canvas.drawColor(Color.WHITE);
        	
        }
        else
        	bitmap = BitmapFactory.decodeFile(doodleImageUri);
        
        
        int screenWidth = display.getWidth();
        
        float scale = (float)bitmap.getHeight() / (float)bitmap.getWidth();
        float imageHeight = scale * screenWidth;
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenWidth, (int)imageHeight, false);
        doodleImageView.setImageBitmap(scaled);
        
        acceleration = 0.00f; 
        currentAcceleration = SensorManager.GRAVITY_EARTH;    
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        
        
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, 
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
                SensorManager.SENSOR_DELAY_NORMAL);
        
    }
    
    @Override 
    protected void onResume() {
    	super.onResume();
    	if (sensorManager == null) {
    		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    		sensorManager.registerListener(sensorEventListener, 
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
                SensorManager.SENSOR_DELAY_NORMAL);
    	}
    }
    @Override
    protected void onPause() {
    	super.onPause();
    	if (sensorManager != null) 
        {         
           sensorManager.unregisterListener(
              sensorEventListener, 
              sensorManager.getDefaultSensor(
                 SensorManager.SENSOR_ACCELEROMETER));         
           sensorManager = null;     
        }
    }
    
  
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private SensorEventListener sensorEventListener = new SensorEventListener() {
    	
    	@Override
    	public void onSensorChanged(SensorEvent event) {  
    		// ensure that other dialogs are not displayed
    		if (!dialogIsVisible.get()) {
    			float x = event.values[0];
    			float y = event.values[1];
    			float z = event.values[2];
    			lastAcceleration = currentAcceleration;
    			currentAcceleration = x * x + y * y + z * z;
    			acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);
    			if (acceleration > ACCELERATION_THRESHOLD) {
    					showClearDialog();
    			} 
    		}
    	} 
    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {} 
    }; 
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0, COLOR_MENU_ID, 0, "Color");
    	menu.add(0, BRUSH_MENU_ID, 0, "Brush");
    	menu.add(0, WIDTH_MENU_ID, 0 ,"Line Width");
    	menu.add(0, ERASE_MENU_ID, 0 , "Erase");
    	menu.add(0, CLEAR_MENU_ID, 0, "Clear");
    	menu.add(0, SAVE_MENU_ID, 0, "Save");
    	
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	doodleView.getPaint().setXfermode(null);
    	doodleView.getPaint().setAlpha(0xFF);
        switch (item.getItemId()) {
        	
        	case COLOR_MENU_ID :
        		dialogIsVisible.set(true);
        		new ColorPickerDialog(this, this, doodleView.getDrawingColor()).show();
        		return true;
        	case BRUSH_MENU_ID :
        		showChooseBrushDialog();
        		return true;
        	case WIDTH_MENU_ID :
        		showLineWidthDialog();
        		return true;
        	case ERASE_MENU_ID :
        		doodleView.setErase();
        		return true;
        	case CLEAR_MENU_ID :
        		showClearDialog();
        		return true;
        	case SAVE_MENU_ID :
        		doodleView.saveImage();
        		if (DoodleView.saved_img != null)
        			showToSharePageDialog();
        		return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void colorChanged(int color) {
		dialogIsVisible.set(false);
		doodleView.setDrawingColor(color);
	}
	
	private void showChooseBrushDialog() {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.brush_dialog);
		dialog.setTitle("Choose a brush");
		dialog.setCancelable(true);
		
		Button pencilButton = (Button)dialog.findViewById(R.id.pencilButton);
		Button embossButton = (Button)dialog.findViewById(R.id.embossButton);
		Button blurButton = (Button)dialog.findViewById(R.id.blurButton);
		
		
		pencilButton.setOnClickListener(setPencilListener);
		embossButton.setOnClickListener(setEmbossListener);
		blurButton.setOnClickListener(setBlurListener);
		
		dialogIsVisible.set(true);
		dialog.show();
	}
	
	private void showClearDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(DoodleActivity.this);
		builder.setMessage(R.string.message_erase);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.button_erase, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialogIsVisible.set(false);
				doodleView.clear(); 
			} 
		}); 
		
		builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialogIsVisible.set(false);
				dialog.cancel(); 
			}
		});
		dialogIsVisible.set(true); 
		builder.show(); 
	}
	
	private void showLineWidthDialog() {
	    dialog = new Dialog(this);
	    dialog.setContentView(R.layout.width_dialog);
	    dialog.setTitle(R.string.button_set_line_width);
	    dialog.setCancelable(true);
	      
	    SeekBar widthSeekBar = 
	         (SeekBar)dialog.findViewById(R.id.widthSeekBar);
	    widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChanged);
	    widthSeekBar.setProgress(doodleView.getLineWidth()); 
	       
	    Button setLineWidthButton = 
	         (Button)dialog.findViewById(R.id.widthDialogDoneButton);
	    setLineWidthButton.setOnClickListener(setLineWidthButtonListener);
	      
	    dialogIsVisible.set(true);
	    dialog.show();  
	} 

	private OnSeekBarChangeListener widthSeekBarChanged = 
				new OnSeekBarChangeListener() {
		Bitmap bitmap = Bitmap.createBitmap( 
	            400, 100, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);    
	         @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {  
	            
	            ImageView widthImageView = (ImageView) 
	               dialog.findViewById(R.id.widthImageView);
	            
	            Paint p = new Paint();
	            p.setColor(doodleView.getDrawingColor());
	            p.setStrokeCap(Paint.Cap.ROUND);
	            p.setStrokeWidth(progress);
	            
	            bitmap.eraseColor(Color.WHITE);
	            canvas.drawLine(30, 50, 370, 50, p);
	            widthImageView.setImageBitmap(bitmap);
	         } 
	   
	         @Override
	         public void onStartTrackingTouch(SeekBar seekBar) {}
	   
	         @Override
	         public void onStopTrackingTouch(SeekBar seekBar) {}
	         
    }; 

    private OnClickListener setLineWidthButtonListener = new OnClickListener() {
         @Override
         public void onClick(View v)  {
            SeekBar widthSeekBar = 
               (SeekBar) dialog.findViewById(R.id.widthSeekBar);
   
           
            doodleView.setLineWidth(widthSeekBar.getProgress());
            
            dialog.dismiss(); 
            dialogIsVisible.set(false);
            dialog = null; 
         } 
    }; 
    
    private OnClickListener setPencilListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			doodleView.getPaint().setMaskFilter(null);
			dialog.dismiss();
			dialogIsVisible.set(false);
			dialog = null;
		}
    	
    };
    
    private OnClickListener setEmbossListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			doodleView.getPaint().setMaskFilter(emboss);
			dialog.dismiss();
			dialogIsVisible.set(false);
			dialog = null;
		}
    	
    };
    
    private OnClickListener setBlurListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			doodleView.getPaint().setMaskFilter(blur);
			dialog.dismiss();
			dialogIsVisible.set(false);
			dialog = null;
		}
    	
    };
    
    private void showToSharePageDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(DoodleActivity.this);
    	builder.setMessage(R.string.message_share);
    	builder.setCancelable(true);
   		
    	builder.setPositiveButton(R.string.button_share_to_fb, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialogIsVisible.set(false);
				Intent intent = new Intent(DoodleActivity.this, ShareActivity.class);
                intent.putExtra("com.highfive.share", DoodleView.saved_img);
                DoodleView.saved_img = null;
                dialogIsVisible.set(false);
                startActivity(intent);
			}
		});
    	
    	builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialogIsVisible.set(false);
				dialog.cancel(); 
			} 
		});

		dialogIsVisible.set(true); 
		builder.show(); 
   	}
	    
	    
}
