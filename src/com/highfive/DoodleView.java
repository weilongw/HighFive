package com.highfive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DoodleView extends View {
	private static final float TOUCH_TOLERANCE = 10;

	private Uri uri;
	private Bitmap imageBitmap;
	private Bitmap bitmap; 
	private Canvas bitmapCanvas; 
    private Paint paintScreen; 
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap; 
    
    public static Uri saved_img;
   
    public DoodleView(Context context, AttributeSet attrs) {
    	super(context, attrs); 

    	paintScreen = new Paint(); 

    	paintLine = new Paint();
    	paintLine.setAntiAlias(true); 
    	paintLine.setColor(Color.RED); 
    	paintLine.setStyle(Paint.Style.STROKE); 
    	paintLine.setStrokeWidth(5); 
    	paintLine.setStrokeCap(Paint.Cap.ROUND); 
    	pathMap = new HashMap<Integer, Path>();
    	previousPointMap = new HashMap<Integer, Point>();
    	
    	
    	
    	
    	
    } 

    // Method onSizeChanged creates BitMap and Canvas after app displays
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
    	
    	imageBitmap = DoodleActivity.bitmap;
    	float imageHeight;
    	
    	float scale = (float)imageBitmap.getHeight() / (float)imageBitmap.getWidth();
    	imageHeight = scale * (getWidth());
    	
    	bitmap = Bitmap.createBitmap(getWidth(), (int)imageHeight, 
    			Bitmap.Config.ARGB_8888);
    	bitmapCanvas = new Canvas(bitmap);
    	
    }
   
    // clear the painting
    public void clear() {
    	pathMap.clear(); 
    	previousPointMap.clear(); 
    	bitmap.eraseColor(Color.TRANSPARENT);
    	invalidate();
    } 
    
    public Paint getPaint() {
    	return paintLine;
    }
    
    public void setErase() {
    	paintLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    
   
    public void setDrawingColor(int color) {
    	paintLine.setColor(color);
    } 

    public int getDrawingColor() {
    	return paintLine.getColor();
    } 

    public void setLineWidth(int width) {
    	paintLine.setStrokeWidth(width);
    } 

    public int getLineWidth() {
    	return (int) paintLine.getStrokeWidth();
    } 

    // called each time this View is drawn
    @Override
    protected void onDraw(Canvas canvas) {
    	// draw the background screen
    	canvas.drawColor(Color.TRANSPARENT);
    	canvas.drawBitmap(bitmap, 0, 0, paintScreen);

    	// for each path currently being drawn
    	for (Integer key : pathMap.keySet()) 
    		canvas.drawPath(pathMap.get(key), paintLine); 
    } 

   // handle touch event
   @Override
   public boolean onTouchEvent(MotionEvent event) {
	   int action = event.getActionMasked(); 
	   int actionIndex = event.getActionIndex(); 
      
	   if (action == MotionEvent.ACTION_DOWN ||
			   action == MotionEvent.ACTION_POINTER_DOWN) {
		   touchStarted(event.getX(actionIndex), event.getY(actionIndex), 
				   event.getPointerId(actionIndex));
		   invalidate();
	   } 
	   else if (action == MotionEvent.ACTION_UP ||
         action == MotionEvent.ACTION_POINTER_UP) {
		   touchEnded(event.getPointerId(actionIndex));
		   invalidate();
	   } 
	   else {
		   touchMoved(event);
		   invalidate();
	   } 
      
	   invalidate(); // redraw
	   return true; 
   	} 

   	// called when the user touches the screen
   	private void touchStarted(float x, float y, int lineID) {
   		Path path; 
   		Point point;

   		if (pathMap.containsKey(lineID)) {
   			path = pathMap.get(lineID); 
   			path.reset(); 
   			point = previousPointMap.get(lineID); 
   		} 
   		else {
   			path = new Path(); 
   			pathMap.put(lineID, path); 
   			point = new Point(); 
   			previousPointMap.put(lineID, point); 
   		} 

   		path.moveTo(x, y);
   		point.x = (int) x;
   		point.y = (int) y;
   	} 

   	// called when the user drags along the screen
   	private void touchMoved(MotionEvent event) {
   		for (int i = 0; i < event.getPointerCount(); i++) {
   			int pointerID = event.getPointerId(i);
   			int pointerIndex = event.findPointerIndex(pointerID);
            
   			if (pathMap.containsKey(pointerID)) {
   				float newX = event.getX(pointerIndex);
   				float newY = event.getY(pointerIndex);
            
   				Path path = pathMap.get(pointerID);
   				Point point = previousPointMap.get(pointerID);
            
   				float deltaX = Math.abs(newX - point.x);
   				float deltaY = Math.abs(newY - point.y);

   				if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
   					path.quadTo(point.x, point.y, (newX + point.x) / 2,
   							(newY + point.y) / 2);

   					point.x = (int) newX;
   					point.y = (int) newY;
   				} 
   			} 
   		} 
   	} 

   	// called when the user finishes a touch
   	private void touchEnded(int lineID) {
   		Path path = pathMap.get(lineID); 
   		bitmapCanvas.drawPath(path, paintLine); 
   		path.reset(); 
   	} 

   	// save the current image to the Gallery
   	public void saveImage() {
   		Bitmap result = null;
   		String fileName = "HighFive" + System.currentTimeMillis();

   		ContentValues values = new ContentValues();
   		values.put(Images.Media.TITLE, fileName);
   		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
   		values.put(Images.Media.MIME_TYPE, "image/jpg");

   		uri = getContext().getContentResolver().insert(
   				Images.Media.EXTERNAL_CONTENT_URI, values);
   		int size = imageBitmap.getHeight() * imageBitmap.getWidth();
   		//rescale the image if it is larger than 4MB
   		if (size > 1000000) {
   			
   			float scale = (float)imageBitmap.getHeight() / (float)imageBitmap.getWidth();
   			double el = Math.sqrt(1000000 / scale );
   			int re_width = (int)el;
   			int re_height = (int)(re_width * scale);
   			result = Bitmap.createScaledBitmap(imageBitmap, re_width, re_height, false);   			
   		}
   		else {
   			result = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
   		}
   		Canvas canvas = new Canvas(result);
   		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, result.getWidth(), result.getHeight(), false);
   		canvas.drawBitmap(scaled, new Matrix(), null);

   		try { 
   			OutputStream outStream = 
   					getContext().getContentResolver().openOutputStream(uri);

   			result.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

   			outStream.flush(); // empty the buffer
   			outStream.close(); // close the stream

   			saved_img = uri;
   		} 
   		catch (Exception ex) {
   			// display a message indicating that the image was saved
   			Toast message = Toast.makeText(getContext(), 
   					R.string.message_error_saving, Toast.LENGTH_SHORT);
   			message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
   					message.getYOffset() / 2);
   			message.show(); 
   		} 
   	} 
	

}
