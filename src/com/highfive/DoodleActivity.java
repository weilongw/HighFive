package com.highfive;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;



public class DoodleActivity extends Activity implements ColorPickerDialog.OnColorChangedListener {
	
	public static String doodleImageUri;
	
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
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        doodleImageUri = intent.getStringExtra(HomeActivity.HOME_TO_DOODLE);
        
        
        setContentView(R.layout.doodle);
        doodleView = (DoodleView)findViewById(R.id.doodleView);
        ImageView doodleImageView = (ImageView)findViewById(R.id.doodleImageView);
        
        //doodleView = new DoodleView(this);
        //
        //doodleView.setClipToPadding(true);
        
        //doodleView.setPadding(100, 100, 100, 100);
        Bitmap bitmap = BitmapFactory.decodeFile(doodleImageUri);
        Display display = getWindowManager().getDefaultDisplay(); 
        int screenWidth = display.getWidth();
        
        float scale = (float)bitmap.getHeight() / (float)bitmap.getWidth();
        float imageHeight = scale * screenWidth;
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenWidth, (int)imageHeight, false);
        doodleImageView.setImageBitmap(scaled);
        //textView.setText(doodleImageUri);
        
        
        
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
        //getMenuInflater().inflate(R.menu.activity_doodle, menu);
    	
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
        	
          /*  case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;*/
        	case COLOR_MENU_ID :
        		
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
        		doodleView.clear();
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
		//Button srcButton = (Button)dialog.findViewById(R.id.srcButton);
		
		pencilButton.setOnClickListener(setPencilListener);
		embossButton.setOnClickListener(setEmbossListener);
		blurButton.setOnClickListener(setBlurListener);
		//srcButton.setOnClickListener(setSrcListener);
		
		dialog.show();
	}
	// display a dialog for setting the line width
	private void showLineWidthDialog() {
		// create the dialog and inflate its content
	    dialog = new Dialog(this);
	    dialog.setContentView(R.layout.width_dialog);
	    dialog.setTitle(R.string.button_set_line_width);
	    dialog.setCancelable(true);
	      
	    // get widthSeekBar and configure it
	    SeekBar widthSeekBar = 
	         (SeekBar)dialog.findViewById(R.id.widthSeekBar);
	    widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChanged);
	    widthSeekBar.setProgress(doodleView.getLineWidth()); 
	       
	    // set the Set Line Width Button's onClickListener
	    Button setLineWidthButton = 
	         (Button)dialog.findViewById(R.id.widthDialogDoneButton);
	    setLineWidthButton.setOnClickListener(setLineWidthButtonListener);
	      
	     // dialog is on the screen
	    dialog.show(); // show the dialog      
	} // end method showLineWidthDialog

	// OnSeekBarChangeListener for the SeekBar in the width dialog
	private OnSeekBarChangeListener widthSeekBarChanged = 
				new OnSeekBarChangeListener() {
		Bitmap bitmap = Bitmap.createBitmap( // create Bitmap
	            400, 100, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); // associate with Canvas     
	         @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {  
	            // get the ImageView
	            ImageView widthImageView = (ImageView) 
	               dialog.findViewById(R.id.widthImageView);
	            
	            // configure a Paint object for the current SeekBar value
	            Paint p = new Paint();
	            p.setColor(doodleView.getDrawingColor());
	            p.setStrokeCap(Paint.Cap.ROUND);
	            p.setStrokeWidth(progress);
	            
	            // erase the bitmap and redraw the line
	            bitmap.eraseColor(Color.WHITE);
	            canvas.drawLine(30, 50, 370, 50, p);
	            widthImageView.setImageBitmap(bitmap);
	         } // end method onProgressChanged
	   
	         // required method of interface OnSeekBarChangeListener
	         @Override
	         public void onStartTrackingTouch(SeekBar seekBar) 
	         {
	         } // end method onStartTrackingTouch
	   
	         // required method of interface OnSeekBarChangeListener
	         @Override
	         public void onStopTrackingTouch(SeekBar seekBar) 
	         {
	         } // end method onStopTrackingTouch
    }; // end widthSeekBarChanged

    // OnClickListener for the line width dialog's Set Line Width Button
    private OnClickListener setLineWidthButtonListener = new OnClickListener() {
         @Override
         public void onClick(View v) 
         {
            // get the color SeekBars
            SeekBar widthSeekBar = 
               (SeekBar) dialog.findViewById(R.id.widthSeekBar);
   
            // set the line color
            doodleView.setLineWidth(widthSeekBar.getProgress());
            
            dialog.dismiss(); // hide the dialog
            dialog = null; // dialog no longer needed
         } // end method onClick
    }; // end setColorButtonListener
    
    private OnClickListener setPencilListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			doodleView.getPaint().setMaskFilter(null);
			dialog.dismiss();
			dialog = null;
		}
    	
    };
    
    private OnClickListener setEmbossListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			doodleView.getPaint().setMaskFilter(emboss);
			dialog.dismiss();
			dialog = null;
		}
    	
    };
    
    private OnClickListener setBlurListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			doodleView.getPaint().setMaskFilter(blur);
			dialog.dismiss();
			dialog = null;
		}
    	
    };
    
    private void showToSharePageDialog(){
   		dialog = new Dialog(this);
		dialog.setContentView(R.layout.share_dialog);
		dialog.setTitle("Share to Facebook");
		dialog.setCancelable(true);
   		
   		Button toSharePageBtn = (Button) dialog.findViewById(R.id.toSharePageButton);
   		Button cancelBtn = (Button) dialog.findViewById(R.id.cancelToSharePageButton);
   		

   		toSharePageBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoodleActivity.this, ShareActivity.class);
                intent.putExtra("com.highfive.share", DoodleView.saved_img);
                DoodleView.saved_img = null;
                startActivity(intent);
            }

        });
   		cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	DoodleView.saved_img = null;
            	dialog.dismiss();
    			dialog = null;
            }
        }); 	
   		
   		dialog.show();

   	}
	    
	    
}
