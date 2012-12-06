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
	// used to determine whether user moved a finger enough to draw again   
	private static final float TOUCH_TOLERANCE = 10;

	private Bitmap imageBitmap;
	private Bitmap bitmap; // drawing area for display or saving
	private Canvas bitmapCanvas; // used to draw on bitmap
    private Paint paintScreen; // use to draw bitmap onto screen
    private Paint paintLine; // used to draw lines onto bitmap
    private HashMap<Integer, Path> pathMap; // current Paths being drawn
    private HashMap<Integer, Point> previousPointMap; // current Points
    
    public static Uri saved_img;
   // DoodleView constructor initializes the DoodleView
    public DoodleView(Context context, AttributeSet attrs) {
    	super(context, attrs); // pass context to View's constructor

    	paintScreen = new Paint(); // used to display bitmap onto screen

    	//set the initial display settings for the painted line
    	paintLine = new Paint();
    	paintLine.setAntiAlias(true); // smooth edges of drawn line
    	paintLine.setColor(Color.RED); // default color is black
    	paintLine.setStyle(Paint.Style.STROKE); // solid line
    	paintLine.setStrokeWidth(5); // set the default line width
    	paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
    	pathMap = new HashMap<Integer, Path>();
    	previousPointMap = new HashMap<Integer, Point>();
    	
    	
    	
    	
    	
    	//BitmapDrawable backBitmap = new BitmapDrawable(imageBitmap);
    	
    	//setBackgroundDrawable(backBitmap);
    	
    	
       
    } // end DoodleView constructor

    // Method onSizeChanged creates BitMap and Canvas after app displays
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
    	
    	//imageBitmap = BitmapFactory.decodeFile(DoodleActivity.doodleImageUri);
    	imageBitmap = DoodleActivity.bitmap;
    	float scale = (float)imageBitmap.getHeight() / (float)imageBitmap.getWidth();
        float imageHeight = scale * (getWidth());
    	bitmap = Bitmap.createBitmap(getWidth(), (int)imageHeight, 
    			Bitmap.Config.ARGB_8888);
    	bitmapCanvas = new Canvas(bitmap);
    	
    	//RelativeLayout layout = (RelativeLayout)findViewById(R.id.doodleLayout);
        //System.out.println(doodleView.getWidth() + ":" + doodleView.getHeight());
        
        //layout.setPadding(100, 100, 100, 100);
    	//bitmap.eraseColor(Color.WHITE); // erase the BitMap with white
    } // end method onSizeChanged
   
    // clear the painting
    public void clear() {
    	pathMap.clear(); // remove all paths
    	previousPointMap.clear(); // remove all previous points
    	bitmap.eraseColor(Color.TRANSPARENT); // clear the bitmap 
    	invalidate(); // refresh the screen
    } // end method clear
    public Paint getPaint() {
    	return paintLine;
    }
    public void setErase() {
    	paintLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    // set the painted line's color
    public void setDrawingColor(int color) {
    	paintLine.setColor(color);
    } // end method setDrawingColor

    // 	return the painted line's color
    public int getDrawingColor() {
    	return paintLine.getColor();
    } // end method getDrawingColor

    // set the painted line's width
    public void setLineWidth(int width) {
    	paintLine.setStrokeWidth(width);
    } // end method setLineWidth

    // return the painted line's width
    public int getLineWidth() {
    	return (int) paintLine.getStrokeWidth();
    } // end method getLineWidth

    // called each time this View is drawn
    @Override
    protected void onDraw(Canvas canvas) {
    	// draw the background screen
    	canvas.drawColor(Color.TRANSPARENT);
    	canvas.drawBitmap(bitmap, 0, 0, paintScreen);

    	// for each path currently being drawn
    	for (Integer key : pathMap.keySet()) 
    		canvas.drawPath(pathMap.get(key), paintLine); // draw line
    } // end method onDraw

   // handle touch event
   @Override
   public boolean onTouchEvent(MotionEvent event) {
	   // get the event type and the ID of the pointer that caused the event
	   int action = event.getActionMasked(); // event type 
	   int actionIndex = event.getActionIndex(); // pointer (i.e., finger)
      
	   // 	determine which type of action the given MotionEvent 
	   // represents, then call the corresponding handling method
	   if (action == MotionEvent.ACTION_DOWN ||
			   action == MotionEvent.ACTION_POINTER_DOWN) {
		   touchStarted(event.getX(actionIndex), event.getY(actionIndex), 
				   event.getPointerId(actionIndex));
		   invalidate();
	   } // end if
	   else if (action == MotionEvent.ACTION_UP ||
         action == MotionEvent.ACTION_POINTER_UP) {
		   touchEnded(event.getPointerId(actionIndex));
		   invalidate();
	   } // end else if
	   else {
		   touchMoved(event);
		   invalidate();
	   } // end else
      
	   invalidate(); // redraw
	   return true; // consume the touch event
   	} // end method onTouchEvent

   	// called when the user touches the screen
   	private void touchStarted(float x, float y, int lineID) {
   		Path path; // used to store the path for the given touch id
   		Point point; // used to store the last point in path

   		// if there is already a path for lineID
   		if (pathMap.containsKey(lineID)) {
   			path = pathMap.get(lineID); // get the Path
   			path.reset(); // reset the Path because a new touch has started
   			point = previousPointMap.get(lineID); // get Path's last point
   		} // end if
   		else {
   			path = new Path(); // create a new Path
   			pathMap.put(lineID, path); // add the Path to Map
   			point = new Point(); // create a new Point
   			previousPointMap.put(lineID, point); // add the Point to the Map
   		} // end else

   		// move to the coordinates of the touch
   		path.moveTo(x, y);
   		point.x = (int) x;
   		point.y = (int) y;
   	} // end method touchStarted

   	// called when the user drags along the screen
   	private void touchMoved(MotionEvent event) {
   		// 	for each of the pointers in the given MotionEvent
   		for (int i = 0; i < event.getPointerCount(); i++) {
   			// get the pointer ID and pointer index
   			int pointerID = event.getPointerId(i);
   			int pointerIndex = event.findPointerIndex(pointerID);
            
   			// if there is a path associated with the pointer
   			if (pathMap.containsKey(pointerID)) {
   				// get the new coordinates for the pointer
   				float newX = event.getX(pointerIndex);
   				float newY = event.getY(pointerIndex);
            
   				// get the Path and previous Point associated with 
   				// this pointer
   				Path path = pathMap.get(pointerID);
   				Point point = previousPointMap.get(pointerID);
            
   				// calculate how far the user moved from the last update
   				float deltaX = Math.abs(newX - point.x);
   				float deltaY = Math.abs(newY - point.y);

   				// if the distance is significant enough to matter
   				if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
   					// move the path to the new location
   					path.quadTo(point.x, point.y, (newX + point.x) / 2,
   							(newY + point.y) / 2);

   					// store the new coordinates
   					point.x = (int) newX;
   					point.y = (int) newY;
   				} // end if
   			} // end if
   		} // end for
   	} // end method touchMoved

   	// called when the user finishes a touch
   	private void touchEnded(int lineID) {
   		Path path = pathMap.get(lineID); // get the corresponding Path
   		bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
   		path.reset(); // reset the Path
   	} // end method touch_ended

   	// save the current image to the Gallery
   	public void saveImage() {
   		// use "Doodlz" followed by current time as the image file name
   		String fileName = "HighFive" + System.currentTimeMillis();

   		// create a ContentValues and configure new image's data
   		ContentValues values = new ContentValues();
   		values.put(Images.Media.TITLE, fileName);
   		values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
   		values.put(Images.Media.MIME_TYPE, "image/jpg");

   		// get a Uri for the location to save the file
   		Uri uri = getContext().getContentResolver().insert(
   				Images.Media.EXTERNAL_CONTENT_URI, values);

   		
   		//Bitmap result = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), false);
   		Bitmap result = null;
   		int size = imageBitmap.getHeight() * imageBitmap.getWidth();
   		if (size > 1500000) {
   			System.out.println("too bad...");
   			float scale = imageBitmap.getHeight() / imageBitmap.getWidth();
   			double el = Math.sqrt(1400000 / scale );
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
   			// get an OutputStream to uri
   			OutputStream outStream = 
   					getContext().getContentResolver().openOutputStream(uri);

   			// copy the bitmap to the OutputStream
   			result.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

   			// flush and close the OutputStream
   			outStream.flush(); // empty the buffer
   			outStream.close(); // close the stream

   			// display a message indicating that the image was saved
   			Toast message = Toast.makeText(getContext(), 
   					R.string.message_saved, Toast.LENGTH_SHORT);
   			message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
   					message.getYOffset() / 2);
   			message.show(); // display the Toast
   			saved_img = uri;
   		} // end try
   		catch (IOException ex) {
   			// display a message indicating that the image was saved
   			Toast message = Toast.makeText(getContext(), 
   					R.string.message_error_saving, Toast.LENGTH_SHORT);
   			message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
   					message.getYOffset() / 2);
   			message.show(); // display the Toast
   		} // end catch
   	} // end method saveImage
	

}
