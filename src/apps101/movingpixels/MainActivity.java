/*
Copyright (c) 2014 Lawrence Angrave
Dual licensed under Apache2.0 and MIT Open Source License (included below): 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package apps101.movingpixels;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private final String TAG = 	"penguin tag"; 
	
	private Bitmap mBitmap;
	private Bitmap mPenguin;
	private int mPHwidth; // Penguin half height
	private int mPHheight; // Penguin half height
	private Paint mPaint;
	private float x;
	private float y; 
	private float vx = 1; 
	private float vy = 1; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// the first thing we do is delete setContentView(R.layout.activity_main) 
		// because we are going to create the view programatically
		
		// BitmapFactory works great for decoding a resource if we know our resource is 
		// not too big
		// .decodeResources takes a pointer to the resources as the first argument
		
		mPenguin = BitmapFactory.decodeResource(getResources(),
				R.drawable.rain_penguin_180);
		// Calculate the half width and height
		mPHwidth = mPenguin.getWidth() / 2;
		mPHheight = mPenguin.getHeight() / 2;
	
		// next make a bitmap and experiment with drawing lines 
		// we need canvas and paint to draw on bitmap
		mBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(mBitmap);
		c.drawColor(0xff808080); // Opaque Gray or 'grey'
		
		
		// the first time around we use an imageview
		//ImageView image = new ImageView(this); 
		//image.setImageBitmap(mBitmap); 
		//setContentView(image)

		// by default android interpolates pixels. this is how we end up with 
		// the blurring effect
		
		mPaint = new Paint();
		mPaint.setColor(0xff0000ff);
		mPaint.setStrokeWidth(0);
		// paint.setAntiAlias(false);
		
		// when we draw rectangles we have to set the fill/
		// when we draw a rectangle with a stoke it does go to the cell
		// e.g. mCanvas.drawRect(1,1,3,3,mPaint), it will draw cell 3,3
		// paint.setStyle(Style.STROKE);
		// Surprise!
		
		
		// The end point (3,3) is NOT drawn by drawLine.
		// Nor by drawRect with the default fill style.
		// mCanvas.drawLine(1, 1, 3, 3, mPaint); 
		// mCanvas.drawRect(1, 1, 3, 3, mPaint);
		c.drawLine(1, 1, 3, 3, mPaint);
		c.drawRect(1, 1, 3, 3, mPaint);

		// here we make our own view so we can fully customize the view and avoid
		// some of the android defaults
		
		// anonymous inner class 
		// we override the onDraw
	    // every time android asks a view to paint itself it calls the onDraw method of the view
		
		
		View v = new View(this) {
			@Override
			protected void onDraw(Canvas canvas) {
				
				// delete super.onDraw(canvas), we'll write our own
				
				canvas.drawColor(0xffff9900); // Orange
				
				
				// this gets us the view size relative to the bitmap
				// getWidth and getHeight() return int so we case denom to float
				
				float scaleX = this.getWidth() / ((float) mBitmap.getWidth());
				float scaleY = this.getHeight() / ((float) mBitmap.getHeight());
				// Log.d("MainActivity","Scale:"+scaleX+","+scaleY);
				
				
				// we can scale the canvas. 
				// think about changes the rulers to the external 
				// 
				// this would double the bitmap size two times 
				// 
				// canvas.scale(2,2)
				// canvas.scale(2,2)
				
				// with canvas its possible to remember the settings using canvas.save(); 
				
				
				canvas.save();
				canvas.scale(scaleX, scaleY);
				
				// Here we try to add the bitmap to the canvas
				// canvas.drawBitmap(b,0,0,null)
				// Note: here b is a local variable declared above
				//       if we try to use local variables that were declared outside of the anonymous inner class
				//       we have 2 ways to make that work 
				//       1. we can make the declaration final. e.g. 
				// 
				//       final Bitmap b = Bitmap.createBitmap(4,4,Bitmap.Config.ARGB_8888); 
				//      
				//       for pointers that means that means that b is fated to always point to the 
				//       same java object. 
	            // 
				//       The other thing we can do is make the bitmap an instance variable
				
				
				//
				// For Android 4.x we also need to pass a paint
				// to turn off filtering  -
				mPaint.setFilterBitmap(false); // Experiment with false vs true
				canvas.drawBitmap(mBitmap, 0, 0, mPaint);
				
				
				// canvas.restore() works in concert with canvas.save() 
				// restores the prior settings
				canvas.restore();

				// this draws a circle around our penguin
				// the circle has to be drawn before we draw the penguin otherwise
				// the penguin will be hidden behind the circle
				
				mPaint.setColor(0x80ffffff); 					// White
				mPaint.setStyle(Style.FILL_AND_STROKE);

				
				// here we're drawing the penguin
				// initially the penguin wasn't visible because the scale was too large
				// one fix is to reverse the scale
				// 
				// $ canvas.scale(1/scaleX, 1/scaleY); 
				
				
				
				// canvas allows us to rotate. We do this in degrees. 
				// by default the canvas rotates around 0,0
				
				// we use this calculation to determine how much to rotate the penguin
				// SystemClock.uptimeMillis() does not include time asleep
				// good clock to use if you want to count how long a user has seen something
				// 
				// we take the SystemClock divided / 10.0f 
				
				// Note: instead of moving the penguin, we first translate the canvas and then 
				//       rotate the penguin relative to the canvas
				
				float angle = SystemClock.uptimeMillis() / 10.0f;
				canvas.translate(x,y); 

				// move the circle to be drawn after we translate the canvas
				canvas.drawCircle(mPHwidth, mPHheight, mPHheight, mPaint);
				canvas.rotate(angle, mPHwidth, mPHheight);
				canvas.drawBitmap(mPenguin, 0, 0, null);
				
				// here we add movement
				// we're adding gravity. 
				// we check to see if the penguin is too low, and if he is 
				// then we change the sign of 'gravity'
				
				// want to bounce on bottom of screen
				// canvas.getHeight doesn't work 
				// Log.d("TAG", "Canvas: " + canvas.getHeight()) + " View: " + this.getHeight())
				// the canvas is not as large as the view because things like status bars take up 
				// part of the view
				// 
				// the penguin is drawn from the topleft corner. To get the bottom corner we have to 
				// add 2*mPHheight
				
				if(y + 2*mPHheight + vy+1 >= this.getHeight()){ 
					vy = -0.8f * -vy;                   //make sure I stay with float 0 
				} else { 
					// only accelerate if not bouncing
					vy = vy + 1; 
				}
				
				x = x + vx; 
				y = y + vy; 
				
				// we use postInvalidate to animate the penguin
				// postInvalidate means 'please redraw me' 
				// you can call this method from anywhere
				// for example if you have a thread where you get a new sensor measurement, 
				// you can call post invalidate on your view
			
				
				// In 20ms (1/50th second) this view will need to be redrawn
				postInvalidateDelayed(20);
			}
		};
		setContentView(v);
		
		// now we want to add interactive 
		OnTouchListener onTouch = new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){ 
				Log.d(TAG, "OnTouch! " +event); 
				
				// the event is the touch event. 
				// the event contains the x,y position of the touch
				// the x,y values are in the coordinate system of the view
				// that means 0,0 is at the top
				
				// add conditional so we only capture the penguin on the 
				// first onDown
				
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					x = event.getX(); 
					y = event.getY(); 
					
					
					// when we click on the penguin we set the velocity to zero
					vx = 0; 
					vy = 0; 
					
					// we can use getAction to determine if a finger has come down or event
					// more specifically to determine which finger has come down
					Log.d(TAG, "Action: " + event.getAction()); 
				}
				// initially we had 
				// 
				// $ return false
				// 
				// this only captures one event
				// by changing that to return true we capture motion move events rather 
				// than just the first touch
				return true; 
			}
		}; 
				
				
		v.setOnTouchListener(onTouch); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
