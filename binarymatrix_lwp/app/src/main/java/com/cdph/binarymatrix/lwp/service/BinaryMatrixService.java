/**
 * Binary Matrix Live Wallpaper
 *
 * @author  SnoopyCodeX
 * @copyright  2020
 * @link  https://fb.me/john.roy.calimlim
 * @link  https://github.com/SnoopyCodeX
 * @email extremeclasherph@gmail.com
 **/

package com.cdph.binarymatrix.lwp.service;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Vibrator;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import java.util.Random;

import com.cdph.binarymatrix.lwp.R;

public class BinaryMatrixService extends WallpaperService 
{
    @Override
    public Engine onCreateEngine()
	{
        return new BinaryMatrixEngine();
    }

    private class BinaryMatrixEngine extends Engine
	{
		private Handler mainHandler = new Handler();
		private Runnable mainThread = new Runnable() {
			@Override
			public void run()
			{
				try {
					updateCanvas();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		private String[] welcomeMessage = {
			"Welcome back %s",
			"Starting system diagnostics...",
			"System diagnostics finished...",
			"Loading system...",
			"Starting system..."
		};
		
		private OnBootCompletedReceiver boot = new OnBootCompletedReceiver();
		private Random rand = new Random();
		private int width, height;
		private Canvas canvas;
		private Bitmap canvasBmp;
		private int fontSize = 12;
		private int colSize;
		private int[] cols;
		private Paint txt, bg, bgBmp, initBg, name;
		
		private boolean isVisible = false;
		private boolean showWelcomeMessage = false;
		private int framesPerSecond = 30;
		private int delay = 100;
		private int step = 1;
		private int stepDelay = 2500;
		private int stepIndex = 0;
		
		public BinaryMatrixEngine()
		{
			//Falling binary numbers
			txt = new Paint();
			txt.setTypeface(Typeface.MONOSPACE);
			txt.setColor(Color.GREEN);
			txt.setAntiAlias(true);
			txt.setTextSize(fontSize);
			
			//Custom name
			name = new Paint();
			name.setTypeface(Typeface.MONOSPACE);
			name.setColor(Color.GREEN);
			name.setAntiAlias(true);
			name.setTextSize(fontSize * 1.5f);
			name.setLetterSpacing((fontSize * 0.03f) + 0.2f);
			
			//Backgrounds
			bg = new Paint();
			bg.setColor(Color.BLACK);
			bg.setAlpha(5);
			bg.setStyle(Paint.Style.FILL);
			
			bgBmp = new Paint();
			bgBmp.setColor(Color.BLACK);
			
			initBg = new Paint();
			initBg.setColor(Color.BLACK);
			initBg.setStyle(Paint.Style.FILL);
			initBg.setAlpha(255);
			
			registerReceiver(boot, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);
			
			//Update width & height
			this.width = width;
			this.height = height;
			
			//Initialize bitmap
			this.canvasBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			this.colSize = (width / (fontSize -2));
			this.cols = new int[colSize + 1];
			
			for(int x = 0; x < colSize; x++)
				cols[x] = rand.nextInt(height / 2) + 1;
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);
			
			unregisterReceiver(boot);
			mainHandler.removeCallbacks(mainThread);
			isVisible = false;
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			super.onVisibilityChanged(visible);
			
			isVisible = visible;
			
			if(visible)
			{
				updateCanvas();
				registerReceiver(boot, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
			}
			else
			{
				mainHandler.removeCallbacks(mainThread);
				unregisterReceiver(boot);
			}
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			mainHandler.removeCallbacks(mainThread);
			unregisterReceiver(boot);
		}
		
		private void drawBinaryMatrix()
		{
			//Loop through the cols array
			for(int i = 0; i < cols.length; i++)
			{
				//Generate random numbers between 1 & 0 then print it on canvas
				drawText(canvas, String.valueOf(rand.nextInt(2)), i * fontSize, cols[i] * fontSize, txt);
				
				//Print custom text every ¾ of a second
				if(Math.random() > 0.998)
					drawCenteredText(canvas, "John Roy");
					
				//Set the 'y' back to 0 if it's greater than screen height
				if(cols[i] * fontSize > height && Math.random() > 0.975)
					cols[i] = 0;
				cols[i]++;
			}
		}
		
		private void drawCenteredText(Canvas canvas, String text)
		{
			Rect bounds = new Rect();
			canvas.getClipBounds(bounds);
			
			//Canvas width & height
			int cw = bounds.width();
			int ch = bounds.height();
			
			//Set text alignment and store it's bounds
			name.setTextAlign(Paint.Align.LEFT);
			name.getTextBounds(text, 0, text.length(), bounds);
			
			//Text offset from x & y
			float xOff = cw / 2f - bounds.width() / 2f - bounds.left;
			float yOff = ch / 2f + bounds.height() / 2f - bounds.bottom;
			
			//Draw the text on the canvas
			canvas.drawText(text, xOff, yOff, name);
		}
		
		private void drawText(Canvas canvas, String text, float x, float y, Paint paint)
		{
			canvas.drawText(text, x, y, paint);
		}
		
		private void updateCanvas()
		{
			SurfaceHolder holder = getSurfaceHolder();
			
			try {
				canvas = holder.lockCanvas();
				
				if(canvas == null)
					return;
					
				if(showWelcomeMessage)
					greetWelcome();
				else
				{
					canvas.drawRect(0, 0, width, height, bg);
					drawBinaryMatrix();
				}
				
				holder.unlockCanvasAndPost(canvas);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			mainHandler.removeCallbacks(mainThread);
			if(isVisible)
				mainHandler.postDelayed(mainThread, delay / framesPerSecond);
		}
		
		private void greetWelcome()
		{
			try {
				String message = welcomeMessage[stepIndex];
				message = String.format(message, "John Roy");
				
				if(stepIndex == welcomeMessage.length-1)
				{
					showWelcomeMessage = false;
					stepIndex = 0;
					return;
				}
				
				if(step > message.length())
				{
					step = 1;
					stepIndex++;
					Thread.sleep(stepDelay);
				}

				canvas.drawBitmap(canvasBmp, 0, 0, bgBmp);
				canvas.drawRect(0, 0, width, height, bg);
				drawCenteredText(canvas, message.substring(0, step++));
				stepDelay = (stepIndex == 1) ? ((rand.nextInt(3) == 1) ? 5500 : 3500) : 2500;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private class OnBootCompletedReceiver extends BroadcastReceiver
		{
			@Override
			public void onReceive(Context ctx, Intent data)
			{
				showWelcomeMessage = (data.getAction().equals(Intent.ACTION_BOOT_COMPLETED));
			}
		}
    }
}
