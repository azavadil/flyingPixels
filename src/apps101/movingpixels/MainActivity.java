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
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private Bitmap mBitmap;
	private Bitmap mPenguin;
	private int mPHwidth; // Penguin half height
	private int mPHheight; // Penguin half height
	private Paint mPaint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPenguin = BitmapFactory.decodeResource(getResources(),
				R.drawable.rain_penguin_180);
		// Calculate the half width and height
		mPHwidth = mPenguin.getWidth() / 2;
		mPHheight = mPenguin.getHeight() / 2;
		
		mBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(mBitmap);
		c.drawColor(0xff808080); // Opaque Gray or 'grey'

		mPaint = new Paint();
		mPaint.setColor(0xff0000ff);
		mPaint.setStrokeWidth(0);
		// paint.setAntiAlias(false);
		// paint.setStyle(Style.STROKE);
		// Surprise!
		// The end point (3,3) is NOT drawn by drawLine.
		// Nor by drawRect with the default fill style.
		// mCanvas.drawLine(1, 1, 3, 3, mPaint); 
		// mCanvas.drawRect(1, 1, 3, 3, mPaint);
		c.drawLine(1, 1, 3, 3, mPaint);
		c.drawRect(1, 1, 3, 3, mPaint);

		View v = new View(this) {
			@Override
			protected void onDraw(Canvas canvas) {
				canvas.drawColor(0xffff9900); // Orange
				float scaleX = this.getWidth() / ((float) mBitmap.getWidth());
				float scaleY = this.getHeight() / ((float) mBitmap.getHeight());
				// Log.d("MainActivity","Scale:"+scaleX+","+scaleY);
				canvas.save();
				canvas.scale(scaleX, scaleY);
				// For Android 4.x we also need to pass a paint
				// to turn off filtering  -
				mPaint.setFilterBitmap(false); // Experiment with false vs true
				canvas.drawBitmap(mBitmap, 0, 0, mPaint);
				canvas.restore();

				mPaint.setColor(0xffffffff); // White
				mPaint.setStyle(Style.FILL_AND_STROKE);
				canvas.drawCircle(mPHwidth, mPHheight, mPHheight, mPaint);

				float angle = SystemClock.uptimeMillis() / 10.0f;
				canvas.rotate(angle, mPHwidth, mPHheight);
				canvas.drawBitmap(mPenguin, 0, 0, null);
				
				// In 20ms (1/50th second) this view will need to be redrawn
				postInvalidateDelayed(20);
			}
		};
		setContentView(v);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
