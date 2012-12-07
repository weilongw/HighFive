package com.highfive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
            pic[i].degree = (float)Math.toDegrees((double)pic[i].height / (double)pic[i].width);
            System.out.println(pic[i].preX +":" +pic[i].preY +"<==>" + pic[i].width + ":" + pic[i].height);
        }
        setContentView(drawView = new Draw(this, pic));
        
        
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
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
        		if (drawView.saved_img != null)
        			showToSharePageDialog();
        		return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToSharePageDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(ShakeActivity.this);
    	builder.setMessage(R.string.message_share);
    	builder.setCancelable(true);
   		
    	builder.setPositiveButton(R.string.button_share_to_fb, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent(ShakeActivity.this, ShareActivity.class);
                intent.putExtra("com.highfive.share", DoodleView.saved_img);
                DoodleView.saved_img = null;
                startActivity(intent);
			} 
		});
    	
    	builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel(); 
			} 
		});
		builder.show(); 
   	}
    
    class Draw extends View { 
    	private Bitmap canvasBitmap;
        private Bmp tempBitmap = null;
        private Canvas canvas; 
                private float X = 0f;
        private float Y = 0f;
        private Bmp[] pic; 
        private float X_1;
        private float X_2;
        private float Y_1;
        private float Y_2;
        private float dist = 0f;
        private boolean x1larger = false;
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
        private Uri saved_img;
        private Uri uri;
    	Bmp bmp[];
    	
        public Draw(Context context) {
        	super(context);
        	
        }
        
        public Draw(Context context, Bmp[] pic) {
        	super(context);
        	String fileName = "HighFive" + System.currentTimeMillis();

       		ContentValues values = new ContentValues();
       		values.put(Images.Media.TITLE, fileName);
       		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
       		values.put(Images.Media.MIME_TYPE, "image/jpg");

       		uri = getContext().getContentResolver().insert(
       				Images.Media.EXTERNAL_CONTENT_URI, values);
            this.pic = pic;
        }
        
        public int getColor() { return color; }
        
        public void setColor(int color) { 
        	this.color = color;
        	this.canvas.drawColor(color);
        	for(int i = 0; i < pic.length; i++) {
        		tempBitmap = pic[0].findByPiority(pic, i);
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
                this.canvas.drawBitmap(tempBitmap.pic, tempBitmap.matrix, null);
            }
        }
        
        @Override
        public void onDraw(Canvas canvas) {
        	super.onDraw(canvas);
            canvas.drawBitmap(canvasBitmap, 0, 0, null);
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	int high = pic.length - 1;
            if(event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1) {
            	orderMove(event);
                this.X = event.getX();
                this.Y = event.getY();
                CX = pic[high].findByPiority(pic, high).getXY(1) - event.getX();
                CY = pic[high].findByPiority(pic, high).getXY(2) - event.getY();
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
                	rotalC = this.getT(tempBitmap.width / 2, tempBitmap.height / 2 , X + CX, Y + CY, tempBitmap.matrix);                    
                	canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                	tempBitmap.preX = X + CX;
                	tempBitmap.preY = Y + CY;
            }
            
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN && event.getPointerCount() == 2) {
            	orderRotate(event);
            	BeginMove = false;
            }
            
            if (event.getPointerCount() == 2 && BeginRotate && event.getAction() == MotionEvent.ACTION_MOVE) {
            	X_1 = event.getX(0);
            	X_2 = event.getX(1);
            	Y_1 = event.getY(0);
            	Y_2 = event.getY(1);
            	tan = (Y_2 - Y_1) / (X_2 - X_1);
            	rotary = (float) Math.atan((double)tan);
            	
            	this.canvas.drawColor(color);
            	
            	for(int i = 0; i < high; i++) {
            		tempBitmap = pic[0].findByPiority(pic, i);
            		tempBitmap.matrix.preTranslate(0f, 0f);
            		canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
            	}
            	
            	tempBitmap = pic[0].findByPiority(pic, high);
            	float change = (float)Math.sqrt((X_1-X_2)*(X_1-X_2) + (Y_1-Y_2)*(Y_1-Y_2));
            	scale = change / dist;
            	Matrix mat = new Matrix();
            	mat.setScale(scale, scale);
            	
            	double rot = Math.toDegrees(rotary) + tempBitmap.degree;
            	double radius = Math.sqrt(tempBitmap.getWidth()*tempBitmap.getWidth() +
            							  tempBitmap.getHeight()*tempBitmap.getHeight()) / 2;
            	
            	Log.i("rot", Math.toDegrees(rotary) + "");
            	if (X_1 < X_2 == !x1larger) {
            		mat.postRotate((int)Math.toDegrees(rotary));
            		mat.postTranslate(tempBitmap.preX - (float)(radius * scale * Math.cos(Math.toRadians(rot))), 
      			          tempBitmap.preY - (float)(radius * scale * Math.sin(Math.toRadians(rot))));
            		
            	}
            	else {
            		mat.postRotate((int)Math.toDegrees(rotary) + 180);
            		mat.postTranslate(tempBitmap.preX + (float)(radius * scale * Math.cos(Math.toRadians(rot))), 
            			          tempBitmap.preY + (float)(radius * scale * Math.sin(Math.toRadians(rot))));
            	}
            	tempBitmap.rot = rotary;
            	tempBitmap.scale = scale;
            	canvas.drawBitmap(tempBitmap.pic, mat, null);
            	tempBitmap.matrix = new Matrix(mat);
            } 
            
            
            
            if(event.getAction() == MotionEvent.ACTION_UP && BeginMove) {
            	CX = 0f;
            	CY = 0f;
            	BeginMove = false;
            }
            if ((event.getAction() == MotionEvent.ACTION_POINTER_UP || event.getAction() == MotionEvent.ACTION_UP ) && BeginRotate) {
            	BeginRotate = false;
            	if (scale > 0) {
            		tempBitmap = pic[0].findByPiority(pic, pic.length - 1);
            	    scale = -1;
            	    dist = -1f;
            	}
            }
            invalidate();
            return true;
        }
         
        public float calculate(float x1, float x2, float y1, float y2, float width, float height) {
        	double dist = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
        	double length1 = width / Math.abs(x1 - x2) * dist;
        	double length2 = height / Math.abs(y1 - y2) * dist;
        	
        	return (float)(dist / Math.min(length1, length2));
        }
        
        public void orderMove(MotionEvent event) {
        	Bmp temp = null;
            for(int i = (pic.length - 1); i > -1; i--) {
            	if (inRect(event.getX(), event.getY(), pic[0].findByPiority(pic, i))) {
            		temp = pic[0].findByPiority(pic, i);
                    for(Bmp bmp: pic) {
                    	if(bmp.getPriority() > pic[0].findByPiority(pic, i).getPriority())
                    		bmp.priority--;
                    }
                    temp.setPriority(pic.length - 1);
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
        		if (inRect(x1, y1, pic[0].findByPiority(pic, i)) &&
        				inRect(x2, y2, pic[0].findByPiority(pic, i))) {
        			temp = pic[0].findByPiority(pic, i);
        			for(Bmp bmp: pic) {
                    	if(bmp.getPriority() > temp.getPriority())
                    		bmp.priority--;
                    }
        			temp.setPriority(pic.length - 1);
        			BeginRotate = true;
        			X_1 = event.getX(0);
                	X_2 = event.getX(1);
                	if (X_1 > X_2)
                		x1larger = true;
                	else 
                		x1larger = false;
                	dist = (float)Math.sqrt((X_1-X_2)*(X_1-X_2) + (Y_1-Y_2)*(Y_1-Y_2));
                	
        			return;
        		}
        	}
        }
        
        private boolean inRect(float x, float y, Bmp bmp) {
        	
        	float tan = (y - bmp.preY) / (x - bmp.preX);
        	double rotary = Math.atan((double)tan); // in radians
        	double dist = Math.sqrt((x-bmp.preX)*(x-bmp.preX) + (y-bmp.preY)*(y-bmp.preY));
        	if (Math.abs(dist * Math.sin(rotary - bmp.rot)) > bmp.height / 2 * bmp.scale) {
        		return false;
        	}
        		
        	if (Math.abs(dist * Math.cos(rotary - bmp.rot)) > bmp.width / 2 * bmp.scale)
        		return false;
        	return true;
        }
        
        public float[] getT(float preX, float preY, float x, float y, Matrix matrix) {
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
        	
       		try { 
       			OutputStream outStream = 
       					getContext().getContentResolver().openOutputStream(uri);

       			if (canvasBitmap.getWidth() * canvasBitmap.getHeight() > 1500000) {
       				float scale = (float)canvasBitmap.getHeight() / (float)canvasBitmap.getWidth();
       	   			double el = Math.sqrt(1400000 / scale );
       	   			int re_width = (int)el;
       	   			int re_height = (int)(re_width * scale);
       	   			Bitmap save = Bitmap.createScaledBitmap(canvasBitmap, re_width, re_height, false);
       	   			save.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
       			}
       			else {
       				canvasBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
       			}

       			outStream.flush(); // empty the buffer
       			outStream.close(); // close the stream

       			saved_img = uri;
       		} 
       		catch (IOException ex) {
       			// display a message indicating that the image was saved
       			Toast message = Toast.makeText(getContext(), 
       					R.string.message_error_saving, Toast.LENGTH_SHORT);
       			message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
       					message.getYOffset() / 2);
       			message.show();
       		} 
        }
        
    }
    
    class Bmp {
    	
    	float scale = 1;
        float rot = 0;
        float preX = 0;
        float preY = 0;
        float width = 0;
        float height = 0;
        Bitmap pic = null;
        int priority = 0;
        float degree;
        private Matrix matrix = new Matrix();
    	
        public Bmp(Bitmap pic, int piority) {
        	this.pic = pic;
            this.priority = piority;
        }
        public Bmp(Bitmap pic, int priority, float preX, float preY) {
        	this(pic, priority);
            this.preX = preX + pic.getWidth() / 2;
            this.preY = preY + pic.getHeight() / 2;
        }
        
        public Bmp findByPiority(Bmp[] pic, int priority) {
        	for(Bmp p : pic) {
        		if(p.priority == priority) 
        			return p;
            }
        	return null;
        }
        
        public void setPriority(int priority) {
        	this.priority = priority;
        }
        
        public int getPriority() {
        	return this.priority;
        }
        
        public float getXY(int i) {
        	if(i == 1) {
        		return this.preX;
            }
            else if(i == 2) {
            	return this.preY;
            }
            return (Float) null;
        }

        public Bitmap getPic() {
        	return this.pic;
        }
        
        public float getWidth() {
        	return width;
        }
        
        public float getHeight() {
        	return height;
        }
        
    }
}