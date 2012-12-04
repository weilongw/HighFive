package com.highfive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ShakeActivity extends Activity implements ColorPickerDialog.OnColorChangedListener{
    
	private static ArrayList<String> shakeImages;
	private static final int BACKGROUND_MENU_ID = Menu.FIRST;
	private static final int SAVE_MENU_ID = Menu.FIRST + 1;
	private Draw drawView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        shakeImages = intent.getStringArrayListExtra(HomeActivity.HOME_TO_SHAKE);
        int screenHeight = intent.getIntExtra("SCREEN_HEIGHT", 720);
        Bmp[] pic = new Bmp[shakeImages.size()];
        
        
        int max_height = screenHeight / (pic.length + 1);
        for(int i = 0; i < pic.length; i++) {
        	Bitmap tmp = BitmapFactory.decodeFile(shakeImages.get(i));
        	int width  = (int)((float)tmp.getWidth() / (float)tmp.getHeight() * (float)max_height);
        	
            pic[i] = new Bmp(Bitmap.createScaledBitmap(tmp, width, max_height, false), i, 0,0);
            pic[i].width = pic[i].getPic().getWidth();
            pic[i].height = pic[i].getPic().getHeight();
            System.out.println(pic[i].preX +":" +pic[i].preY +"<==>" + pic[i].width + ":" + pic[i].height);
        }
        setContentView(drawView = new Draw(this, pic));
        
        
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
        //getMenuInflater().inflate(R.menu.activity_doodle, menu);
    	
    	menu.add(0, BACKGROUND_MENU_ID, 0, "Background");
    	menu.add(0, SAVE_MENU_ID, 0, "Save");	
        return true;
    }
    
	@Override
	public void colorChanged(int color) {
		drawView.setColor(color);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        switch (item.getItemId()) {
     
        	case BACKGROUND_MENU_ID :
        		new ColorPickerDialog(this, this, drawView.getColor()).show();
        	
        		return true;       	
        	case SAVE_MENU_ID :
        		drawView.saveImage();
        		return true;
        }
        return super.onOptionsItemSelected(item);
    }


    
    class Draw extends View {   
    	Bmp bmp[];
    	
        public Draw(Context context) {
        	super(context);
        	
        }
        
        public Draw(Context context, Bmp[] pic) {
        	super(context);
        	
            this.pic = pic;
        }
        
        public int getColor() { return color; }
        public void setColor(int color) { 
        	this.color = color;
        	this.canvas.drawColor(color);
        	for(int i = 0; i < pic.length; i++) {
        		tempBitmap = pic[0].findByPiority(pic, i);
                //tempBitmap.matrix.preTranslate(0f, 0f);
                canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
        	}
        	invalidate(); 
        }
        
        @Override
        public void onSizeChanged(int w, int h, int oldW, int oldH) {
        	canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), 
        			Bitmap.Config.ARGB_8888);
        	canvas = new Canvas(canvasBitmap);
        	this.canvas.drawColor(color);
            for(int i = 0; i < pic.length; i++) {
            	tempBitmap = pic[0].findByPiority(pic, i);
                tempBitmap.matrix.preTranslate(tempBitmap.getXY(1) - tempBitmap.getWidth() / 2, tempBitmap.getXY(2) - tempBitmap.getHeight() / 2);
                //tempBitmap.matrix.preScale(2.0f, 1.5f);
                //tempBitmap.matrix.preTranslate(0f, 0f);
                this.canvas.drawBitmap(tempBitmap.pic, tempBitmap.matrix, null);
                //this.canvas.drawBitmap(tempBitmap.pic, new Matrix(), null);
            }
        }
        
        @Override
        public void onDraw(Canvas canvas) {
        	super.onDraw(canvas);
        
            canvas.drawBitmap(canvasBitmap, 0, 0, null);
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	/*Toast message = Toast.makeText(getContext(), 
   					"" + event.getAction(), Toast.LENGTH_SHORT);
   			message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
   					message.getYOffset() / 2);
   			message.show();*/
        	int high = pic.length - 1;
            if(event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1) {
            	orderMove(event);
            	//System.out.println(event.getPointerCount());
                this.X = event.getX();
                this.Y = event.getY();
                CX = pic[high].findByPiority(pic, high).getXY(1) - event.getX();
                CY = pic[high].findByPiority(pic, high).getXY(2) - event.getY();
                //BeginMove = true;
                BeginRotate = false;
            }

            if(event.getAction() == MotionEvent.ACTION_MOVE && BeginMove && event.getPointerCount() == 1) {
            	this.X = event.getX();
                this.Y = event.getY();
                this.canvas.drawColor(color);
                for(int i = 0; i < high; i++) {
                	tempBitmap = pic[0].findByPiority(pic, i);
                    tempBitmap.matrix.preTranslate(0f, 0f);
                    canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                }
                tempBitmap = pic[0].findByPiority(pic, high);
                //rotalP = rotalPoint(new float[]{this.X, this.Y}, tempBitmap.preX, tempBitmap.preY, tempBitmap.width / 2, tempBitmap.height / 2, tempBitmap.matrix);
                //if((Math.abs(this.X - pic[0].findByPiority(pic, high).getXY(1)) < pic[0].findByPiority(pic, high).getWidth() / 2) 
                //    && (Math.abs(this.Y - pic[0].findByPiority(pic, high).getXY(2)) < pic[0].findByPiority(pic, high).getHeight() / 2)) {
                	//tempBitmap.matrix.preTranslate(X + CX - tempBitmap.preX, Y + CY - tempBitmap.preY)
                	//tempBitmap.matrix.preRotate(30);
                	rotalC = this.getT(tempBitmap.width / 2, tempBitmap.height / 2 , X + CX, Y + CY, tempBitmap.matrix);                    
                //	Log.i("matrix", "the matrix");                  
                	//tempBitmap.matrix.setTranslate(T[0], T[1]);
                	canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                	tempBitmap.preX = X + CX;
                	tempBitmap.preY = Y + CY;
                //}
                //else {  
                //	tempBitmap.matrix.preTranslate(0f, 0f);
                //	canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);               
                //}            
            }
            
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN && event.getPointerCount() == 2) {
            	orderRotate(event);
            	//Matrix matrix = null;
            	//matrix.preScale(1.0f, 1.0f);
            	BeginMove = false;
            }
            
            if (event.getPointerCount() == 2 && BeginRotate && event.getAction() == MotionEvent.ACTION_MOVE) {
            	X_1 = event.getX(0);
            	X_2 = event.getX(1);
            	Y_1 = event.getY(0);
            	Y_2 = event.getY(1);
            	//X_1 = X_2;
            	//Log.i("2 touch ", String.valueOf(event.getPointerCount()));
            	tan = (Y_2 - Y_1) / (X_2 - X_1);
            	rotary = (float) Math.atan((double)tan);
            	
            	this.canvas.drawColor(color);
            	
            	for(int i = 0; i < high; i++) {
            		tempBitmap = pic[0].findByPiority(pic, i);
            		tempBitmap.matrix.preTranslate(0f, 0f);
            		canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
            	}
            	
            	tempBitmap = pic[0].findByPiority(pic, high);
            	//tempBitmap.matrix.setRotate(rotary);
            	scale = Math.abs(X_1 - X_2) / tempBitmap.getWidth();
            	Matrix mat = new Matrix();
            	//tempBitmap.matrix.setScale(scale, scale);
            	mat.setScale(scale, scale);
            	mat.postRotate((int)Math.toDegrees(rotary));
            	//if((Math.abs(pic[0].findByPiority(pic, high).getXY(1) - this.X_1) < pic[0].findByPiority(pic, high).getWidth() / 2) 
                  //  && (Math.abs(pic[0].findByPiority(pic, high).getXY(2) - this.Y_1) < pic[0].findByPiority(pic, high).getHeight() / 2)
                    //&&(Math.abs(pic[0].findByPiority(pic, high).getXY(1) - this.X_2) < pic[0].findByPiority(pic, high).getWidth() / 2) 
                    //&& (Math.abs(pic[0].findByPiority(pic, high).getXY(2) - this.Y_2) < pic[0].findByPiority(pic, high).getHeight() / 2)) {
                                      
            		//tempBitmap.matrix.preTranslate(X + CX - tempBitmap.preX, Y + CY - tempBitmap.preY);
                	//tempBitmap.matrix.setTranslate(tempBitmap.preX, tempBitmap.preY);
            	    mat.postTranslate(tempBitmap.preX - tempBitmap.getWidth() / 2, tempBitmap.preY - tempBitmap.getHeight() / 2);
            		//canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
            	    canvas.drawBitmap(tempBitmap.pic, mat, null);
            	    tempBitmap.matrix = new Matrix(mat);
            	    //tempBitmap.preX = (tempBitmap.preX - tempBitmap.getWidth() / 2) + (tempBitmap.getWidth() * scale / 2);
            	    //tempBitmap.preY = (tempBitmap.preY - tempBitmap.getHeight() / 2) + (tempBitmap.getHeight() * scale / 2);
            	    
            		//tempBitmap.preX = X + CX;
                    
            		//tempBitmap.preY = Y + CY;
                    
            	//}
                
            	//else {
                     
            		//tempBitmap.matrix.preTranslate(0f, 0f);
                    
            		//canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
            	//}
            } 
            
            if(event.getAction() == MotionEvent.ACTION_UP && BeginMove) {
            	CX = 0f;
            	CY = 0f;
            	BeginMove = false;
            	//BeginRotate = false;
            }
            if ((event.getAction() == MotionEvent.ACTION_POINTER_UP || event.getAction() == MotionEvent.ACTION_UP ) && BeginRotate) {
            	BeginRotate = false;
            	if (scale > 0) {
            		tempBitmap = pic[0].findByPiority(pic, pic.length - 1);
            		tempBitmap.preX = (tempBitmap.preX - tempBitmap.getWidth() / 2) + (tempBitmap.getWidth() * scale / 2);
            	    tempBitmap.preY = (tempBitmap.preY - tempBitmap.getHeight() / 2) + (tempBitmap.getHeight() * scale / 2);
            	    //tempBitmap.width *= scale;
            	    //tempBitmap.height *= scale;
            	    /*Toast message = Toast.makeText(getContext(), 
   					"" + rotary, Toast.LENGTH_SHORT);
            	    message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
            	    		message.getYOffset() / 2);
            	    message.show();*/
            	    scale = -1;
            	}
            }
            invalidate();
            return true;
        }
         
        public void orderMove(MotionEvent event) {
        	Bmp temp = null;
            for(int i = (pic.length - 1); i > -1; i--) {
            	if((Math.abs(pic[0].findByPiority(pic, i).getXY(1) - event.getX()) < pic[0].findByPiority(pic, i).getWidth() / 2) 
                   && (Math.abs(pic[0].findByPiority(pic, i).getXY(2) - event.getY()) < pic[0].findByPiority(pic, i).getHeight() / 2)) {
            		temp = pic[0].findByPiority(pic, i);
                    for(Bmp bmp: pic) {
                    	if(bmp.getPriority() > pic[0].findByPiority(pic, i).getPriority())
                    		bmp.priority--;
                    }
                    temp.setPiority(pic.length - 1);
                    BeginMove = true;
                    return;
                }
            }
        }
        
        public void orderRotate(MotionEvent event) {
        	Bmp temp = null;
        	float x1 = event.getX(0);
        	float x2 = event.getX(1);
        	float y1 = event.getY(0);
        	float y2 = event.getY(1);
        	for (int i = (pic.length - 1); i > -1; i--) {
        		if((Math.abs(pic[0].findByPiority(pic, i).getXY(1) - x1) < pic[0].findByPiority(pic, i).getWidth() / 2) 
                    && (Math.abs(pic[0].findByPiority(pic, i).getXY(2) - y1) < pic[0].findByPiority(pic, i).getHeight() / 2)
                    &&(Math.abs(pic[0].findByPiority(pic, i).getXY(1) - x2) < pic[0].findByPiority(pic, i).getWidth() / 2) 
                    && (Math.abs(pic[0].findByPiority(pic, i).getXY(2) - y2) < pic[0].findByPiority(pic, i).getHeight() / 2)) {
        			temp = pic[0].findByPiority(pic, i);
        			for(Bmp bmp: pic) {
                    	if(bmp.getPriority() > temp.getPriority())
                    		bmp.priority--;
                    }
        			temp.setPiority(pic.length - 1);
        			BeginRotate = true;
        			return;
        		}
        	}
        }
        
        public float[] getT(float preX, float preY, float x, float y, Matrix matrix)
        {
                float[] re = new float[2];
                float[] matrixArray = new float[9];
                matrix.getValues(matrixArray);
                float a = x - preX * matrixArray[0] - preY * matrixArray[1];
                float b = y - preX * matrixArray[3] - preY * matrixArray[4];
                matrixArray[2] = a;
                matrixArray[5] = b;
                matrix.setValues(matrixArray);
                re[0] = a;
                re[1] = b;
                Log.i("a", String.valueOf(a));
                Log.i("b", String.valueOf(b));
                return re;
        }
        
        public float[] rotalPoint(float[] p, float X, float Y, float width, float height, Matrix matrix) {
        	float re[] = new float[2];
        	float matrixArray[] = new float[9];
        	matrix.getValues(matrixArray);
        	float a = p[0] - X;
        	float b = p[1] - Y;
        	re[0] = a * matrixArray[0] - b * matrixArray[1] + X;
        	re[1] = - a * matrixArray[3] + b * matrixArray[4] + Y;
        	return re;
        }
        public void saveImage() {
        	String fileName = "HighFive" + System.currentTimeMillis();

       		// create a ContentValues and configure new image's data
       		ContentValues values = new ContentValues();
       		values.put(Images.Media.TITLE, fileName);
       		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
       		values.put(Images.Media.MIME_TYPE, "image/jpg");

       		// get a Uri for the location to save the file
       		Uri uri = getContext().getContentResolver().insert(
       				Images.Media.EXTERNAL_CONTENT_URI, values);
       		try { 
       			// get an OutputStream to uri
       			OutputStream outStream = 
       					getContext().getContentResolver().openOutputStream(uri);

       			// copy the bitmap to the OutputStream
       			canvasBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

       			// flush and close the OutputStream
       			outStream.flush(); // empty the buffer
       			outStream.close(); // close the stream

       			// display a message indicating that the image was saved
       			Toast message = Toast.makeText(getContext(), 
       					R.string.message_saved, Toast.LENGTH_SHORT);
       			message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
       					message.getYOffset() / 2);
       			message.show(); // display the Toast
       		} // end try
       		catch (IOException ex) {
       			// display a message indicating that the image was saved
       			Toast message = Toast.makeText(getContext(), 
       					R.string.message_error_saving, Toast.LENGTH_SHORT);
       			message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
       					message.getYOffset() / 2);
       			message.show(); // display the Toast
       		} // end catch
        }
        private Bitmap canvasBitmap;   //Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        private Bmp tempBitmap = null;
        private Canvas canvas; //= new Canvas(canvasBitmap);
                private float X = 0f;
        private float Y = 0f;
                @SuppressWarnings("unused")
                private float DownX = 0f;
        @SuppressWarnings("unused")
                private float DownY = 0f;
        private Bmp[] pic; //= new Bmp[4];
        private float X_1;
        private float X_2;
        private float Y_1;
        private float Y_2;
        private float tan;
        private float rotary;
        private float scale = -1;
        private float CX = 0f;
        private float CY = 0f;
        private boolean BeginMove;
        private boolean BeginRotate;
        float rotalC[] = new float[2];
        float rotalP[] = new float[2];
        private int color = Color.CYAN;
    }
    
    
    
    
//    @param pic:the Bitmap to draw
//    @param piority: the order to draw picture
//    @param preX,preY: the X and Y 
    class Bmp {
//      ???
        public Bmp(Bitmap pic, int piority) {
        	this.pic = pic;
            this.priority = piority;
        }
//      ???
        public Bmp(Bitmap pic, int priority, float preX, float preY) {
        	this(pic, priority);
            this.preX = preX + pic.getWidth() / 2;
            this.preY = preY + pic.getHeight() / 2;
        }
        
//      findPiority : given an array of bmp, return the bmp with right priority
        public Bmp findByPiority(Bmp[] pic, int priority) {
        	for(Bmp p : pic) {
        		if(p.priority == priority) 
        			return p;
            }
        	return null;
        }
        
//      set Priority
        public void setPiority(int priority) {
        	this.priority = priority;
        }
        
//      return Priority
        public int getPriority() {
        	return this.priority;
        }
        
//      return X and Y
        public float getXY(int i) {
        	if(i == 1) {
        		return this.preX;
            }
            else if(i == 2) {
            	return this.preY;
            }
            return (Float) null;
        }

//      getPicture
        public Bitmap getPic() {
        	return this.pic;
        }
        
//      getWidth
        public float getWidth() {
        	return width;
        }
        
//      getHeight
        public float getHeight() {
        	return height;
        }
        
        float preX = 0;
        float preY = 0;
        float width = 0;
        float height = 0;
        Bitmap pic = null;
        int priority = 0;
        private Matrix matrix = new Matrix();
    }
}