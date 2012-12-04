package com.highfive;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ShakeActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ArrayList<String> shakeImages = intent.getStringArrayListExtra(HomeActivity.HOME_TO_SHAKE);
        Bmp[] pic = new Bmp[shakeImages.size()];
        for(int i = 0; i < pic.length; i++) {
        	Bitmap tmp = BitmapFactory.decodeFile(shakeImages.get(i));
            pic[i] = new Bmp(Bitmap.createScaledBitmap(tmp, 400, 400, false), i, i * 50f, i * 60f);
            pic[i].width = pic[i].getPic().getWidth();
            pic[i].height = pic[i].getPic().getWidth();
        }
        setContentView(new Draw(this, pic));
    }
    
    class Draw extends View {   
    	Bmp bmp[];
    	
        public Draw(Context context) {
        	super(context);
        	bmp = new Bmp[4]; 
        	for(int i = 0; i < 4; i++) {
        		bmp[i] = new Bmp(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.you)), 240, 240, false), i, i * 50f, i * 60f);
        		bmp[i].width = bmp[i].getPic().getWidth();
        		bmp[i].height = bmp[i].getPic().getWidth();
        	}
        	this.pic = bmp;   
        }
        
        public Draw(Context context, Bmp[] pic) {
        	super(context);
            this.pic = pic;
        }
        
        @Override
        public void onSizeChanged(int w, int h, int oldW, int oldH) {
        	canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), 
        			Bitmap.Config.ARGB_8888);
        	canvas = new Canvas(canvasBitmap);
        	this.canvas.drawColor(-232432445);
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
                this.canvas.drawColor(-232432445);
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
            	
            	this.canvas.drawColor(-232432445);
            	
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
            this.preX = preX + pic.getWidth() / 2 * 1.5f;
            this.preY = preY + pic.getHeight() / 2 * 1.5f;
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