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
import android.view.MotionEvent;
import android.view.View;

public class ShakeActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        ArrayList<String> shakeImages = intent.getStringArrayListExtra(HomeActivity.HOME_TO_SHAKE);
        Bmp[] pic = new Bmp[shakeImages.size()];
        for(int i = 0; i < pic.length; i++)
        {
        	Bitmap tmp = BitmapFactory.decodeFile(shakeImages.get(i));
            pic[i] = new Bmp(Bitmap.createScaledBitmap(tmp, 240, 240, false), i, i * 50f, i * 60f);
            pic[i].width = pic[i].getPic().getWidth();
            pic[i].height = pic[i].getPic().getWidth();
        }
        setContentView(new Draw(this, pic));
    }
    
    class Draw extends View
    {   

                Bmp bmp[];
        public Draw(Context context)
        {
                
                super(context);
                bmp = new Bmp[4]; 
                {
                
                for(int i = 0; i < 4; i++)
                {
                        bmp[i] = new Bmp(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.you)), 240, 240, false), i, i * 50f, i * 60f);
                        bmp[i].width = bmp[i].getPic().getWidth();
                        bmp[i].height = bmp[i].getPic().getWidth();
                }
                }
                this.pic = bmp;
                
                
        }
        
        public Draw(Context context, Bmp[] pic)
        {
                super(context);
                this.pic = pic;
        }
        @Override
        public void onSizeChanged(int w, int h, int oldW, int oldH) {
        	canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), 
        			Bitmap.Config.ARGB_8888);
        	canvas = new Canvas(canvasBitmap);
        	this.canvas.drawColor(-232432445);
            for(int i = 0; i < pic.length; i++)
            {
                    tempBitmap = pic[0].findByPiority(pic, i);
                    tempBitmap.matrix.preTranslate(tempBitmap.getXY(1) - tempBitmap.getWidth() / 2, tempBitmap.getXY(2) - tempBitmap.getHeight() / 2);
                    this.canvas.drawBitmap(tempBitmap.pic, tempBitmap.matrix, null);
            }
        }
        
        @Override
        public void onDraw(Canvas canvas)
        {
                super.onDraw(canvas);
                canvas.drawBitmap(canvasBitmap, 0, 0, null);
        }
        
        @Override
                public boolean onTouchEvent(MotionEvent event)
        {
        		int high = pic.length - 1;
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                	
                        order(event);
                        this.X = event.getX();
                        this.Y = event.getY();
                        CX = pic[high].findByPiority(pic, high).getXY(1) - event.getX();
                        CY = pic[high].findByPiority(pic, high).getXY(2) - event.getY();
                        Begin = true;
                }

                        if(event.getAction() == MotionEvent.ACTION_MOVE && Begin && event.getPointerCount() == 1)
                        {
                        this.X = event.getX();
                        this.Y = event.getY();
                                this.canvas.drawColor(-232432445);
                                for(int i = 0; i < high; i++)
                                {
                                        tempBitmap = pic[0].findByPiority(pic, i);
                                        tempBitmap.matrix.preTranslate(0f, 0f);
                                        canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                                }
                                tempBitmap = pic[0].findByPiority(pic, high);
                                rotalP = rotalPoint(new float[]{this.X, this.Y}, tempBitmap.preX, tempBitmap.preY, tempBitmap.width / 2, tempBitmap.height / 2, tempBitmap.matrix);
                                if(
//                                              (Math.abs(pic[0].findByPiority(pic, 3).getXY(1) - this.X) < pic[0].findByPiority(pic, 3).getWidth() / 2) 
                                                (Math.abs(rotalP[0] - pic[0].findByPiority(pic, high).getXY(1)) < pic[0].findByPiority(pic, high).getWidth() / 2) 
//                                      && (Math.abs(pic[0].findByPiority(pic, 3).getXY(2) - this.Y) < pic[0].findByPiority(pic, 3).getHeight() / 2)
                                        && (Math.abs(rotalP[1] - pic[0].findByPiority(pic, high).getXY(2)) < pic[0].findByPiority(pic, high).getHeight() / 2)
                                        )
                                {
                                        //tempBitmap.matrix.preTranslate(X + CX - tempBitmap.preX, Y + CY - tempBitmap.preY);
                                        tempBitmap.matrix.preRotate(30);
                                        rotalC = this.getT(tempBitmap.width / 2, tempBitmap.height / 2 , X + CX, Y + CY, tempBitmap.matrix);
                                        
                                        Log.i("matrix", "the matrix");
//                                      tempBitmap.matrix.setTranslate(T[0], T[1]);
                                        canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                                        tempBitmap.preX = X + CX;
                                        tempBitmap.preY = Y + CY;
                                }
                                else 
                                {
                                        tempBitmap.matrix.preTranslate(0f, 0f);
                                        canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                                }
                        }
                        
//                      ????
                        if (event.getPointerCount() >= 2)
                        {
                                X_1 = event.getX(0);
                                X_2 = event.getX(1);
                                Y_1 = event.getY(0);
                                Y_2 = event.getY(1);
                                Log.i("2 touch ", String.valueOf(event.getPointerCount()));
                                tan = (Y_2 - Y_1) / (X_2 - X_1);
                                rotary = (float) Math.atan((double)tan);
                                tempBitmap.matrix.setRotate(rotary);
                                
                                this.canvas.drawColor(-232432445);
                                for(int i = 0; i < high; i++)
                                {
                                        tempBitmap = pic[0].findByPiority(pic, i);
                                        tempBitmap.matrix.preTranslate(0f, 0f);
                                        canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                                }
                                tempBitmap = pic[0].findByPiority(pic, high);
                                if(
                                                (Math.abs(pic[0].findByPiority(pic, high).getXY(1) - this.X_1) < pic[0].findByPiority(pic, high).getWidth() / 2) 
                                        && (Math.abs(pic[0].findByPiority(pic, high).getXY(2) - this.Y_1) < pic[0].findByPiority(pic, high).getHeight() / 2)
                                        &&(Math.abs(pic[0].findByPiority(pic, high).getXY(1) - this.X_2) < pic[0].findByPiority(pic, high).getWidth() / 2) 
                                        && (Math.abs(pic[0].findByPiority(pic, high).getXY(2) - this.Y_2) < pic[0].findByPiority(pic, high).getHeight() / 2)
                                        )
                                {
                                        tempBitmap.matrix.preTranslate(X + CX - tempBitmap.preX, Y + CY - tempBitmap.preY);
                                        canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                                        
                                        tempBitmap.preX = X + CX;
                                        tempBitmap.preY = Y + CY;
                                }
                                else 
                                {
                                        tempBitmap.matrix.preTranslate(0f, 0f);
                                        canvas.drawBitmap(tempBitmap.getPic(), tempBitmap.matrix, null);
                                }
                        } 
                        
                        if(event.getAction() == MotionEvent.ACTION_UP)
                        {
                                CX = 0f;
                                CY = 0f;
                                Begin = false;
                        }
                        
                        invalidate();
                        
                        return true;
                }
         
        public void order(MotionEvent event)
        {

                Bmp temp = null;
                for(int i = (pic.length - 1); i > -1; i--)
                {
                        if(
                                        (Math.abs(pic[0].findByPiority(pic, i).getXY(1) - event.getX()) < pic[0].findByPiority(pic, i).getWidth() / 2) 
                                        && (Math.abs(pic[0].findByPiority(pic, i).getXY(2) - event.getY()) < pic[0].findByPiority(pic, i).getHeight() / 2)
                                        )
                        {
                                temp = pic[0].findByPiority(pic, i);
                                for(Bmp bmp: pic)
                                {
                                        if(bmp.getPriority() > pic[0].findByPiority(pic, i).getPriority())
                                        {
                                                bmp.priority--;
                                        }
                                }
                                temp.setPiority(pic.length - 1);
                                Begin = true;
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
        
        public float[] rotalPoint(float[] p, float X, float Y, float width, float height, Matrix matrix)
        {
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
        private float CX = 0f;
        private float CY = 0f;
        private boolean Begin = true;
        float rotalC[] = new float[2];
        float rotalP[] = new float[2];
    }
    
    
    
    
//    @param pic:the Bitmap to draw
//    @param piority: the order to draw picture
//    @param preX,preY: the X and Y 
    class Bmp
    {
//      ???
        public Bmp(Bitmap pic, int piority)
        {
                this.pic = pic;
                this.priority = piority;
        }
        
//      ???
        public Bmp(Bitmap pic, int priority, float preX, float preY)
        {
                this(pic, priority);
                this.preX = preX + pic.getWidth() / 2 * 1.5f;
                this.preY = preY + pic.getHeight() / 2 * 1.5f;
        }
        
//      findPiority
        public Bmp findByPiority(Bmp[] pic, int priority)
        {
                for(Bmp p : pic)
                {
                        if(p.priority == priority)
                        {
                                return p;
                        }
                }
                return null;
        }
        
//      set Priority
        public void setPiority(int priority)
        {
                this.priority = priority;
        }
        
//      return Priority
        public int getPriority()
        {
                return this.priority;
        }
        
//      return X and Y
        public float getXY(int i)
        {
                if(i == 1)
                {
                        return this.preX;
                }
                else if(i == 2)
                {
                        return this.preY;
                }
                return (Float) null;
        }

//      getPicture
        public Bitmap getPic()
        {
                return this.pic;
        }
        
//      getWidth
        public float getWidth()
        {
                return width;
        }
        
//      getHeight
        public float getHeight()
        {
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