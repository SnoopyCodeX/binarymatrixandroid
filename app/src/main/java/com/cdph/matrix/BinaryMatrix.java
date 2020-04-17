/**
* Binary Matrix
*
* @author  SnoopyCodeX
* @copyright  2020
* @link  https://fb.me/john.roy.calimlim
* @link  https://github.com/SnoopyCodeX
* @email extremeclasherph@gmail.com
**/

package com.cdph.matrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import java.util.Random;

public class BinaryMatrix extends View
{
	private static final Random rand = new Random();
    private int width, height;
    private Canvas canvas;
    private Bitmap canvasBmp;
    private int fontSize = 12;
    private int colSize;
    private int[] cols;
    private Paint txt, bg, bgBmp, initBg, name;
	private boolean showTextAnim = true;
	
	String[] messages = {
		//Intro
		"Binary Matrix Effect",
		"Submitted by: John Roy L. Calimlim",
		"A challenge from: Michael Tan, Programming Philippines",
		"So, here is my own version of it...",
		"Hope you like my submission ^_^!",

		//Main Message
		"Wake up Neo...",
		"The matrix has you...",
		"Follow the white rabbit...",
		"Knock, knock, Neo..."
	};
	
	int index = 0;
	int step = 1;
	int delay = 16;
	String text = messages[index];
	int x = 1 * fontSize;
	int y = 1 * fontSize;

    public BinaryMatrix(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
		
		initBg = new Paint();
        initBg.setColor(Color.BLACK);
        initBg.setAlpha(255);
        initBg.setStyle(Paint.Style.FILL);
		
        txt = new Paint();
        txt.setAntiAlias(true);
        txt.setColor(Color.GREEN);
        txt.setTextSize(fontSize);
		txt.setTypeface(Typeface.MONOSPACE);
		
		name = new Paint();
		name.setColor(Color.GREEN);
		name.setTextSize(fontSize * 1.86f);
		name.setAntiAlias(true);
		name.setLetterSpacing((fontSize * 0.03f) + 0.2f);
		name.setTypeface(Typeface.MONOSPACE);
		
        bg = new Paint();
        bg.setColor(Color.BLACK);
        bg.setAlpha(5);
        bg.setStyle(Paint.Style.FILL);
        bgBmp = new Paint();
        bgBmp.setColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        canvasBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBmp);
        canvas.drawRect(0, 0, width, height, initBg);
        colSize = width / (fontSize-2);
        cols = new int[colSize + 1];

        for (int x = 0; x < colSize; x++)
            cols[x] = rand.nextInt(height / 2) + 1;
    }

    private void drawText() 
	{
        for (int i = 0; i < cols.length; i++) 
		{
            canvas.drawText("" + rand.nextInt(2), i * fontSize, cols[i] * fontSize, txt);
			
			if(Math.random() > 0.998)
				drawCenteredText(canvas, "SnoopyCodeX");
			
            if (cols[i] * fontSize > height && Math.random() > 0.975)
                cols[i] = 0;
            cols[i]++;
        }
    }

    private void drawCanvas() 
	{
        canvas.drawRect(0, 0, width, height, bg);
        drawText();
		((ImageView) getRootView().findViewById(R.id.logo)).setVisibility(View.VISIBLE);
    }

	private void drawCenteredText(Canvas canvas, String text)
	{
		Rect bounds = new Rect();
		canvas.getClipBounds(bounds);
		int cw = bounds.width();
		int ch = bounds.height();
		name.setTextAlign(Paint.Align.LEFT);
		name.getTextBounds(text, 0, text.length(), bounds);
		float xOff = cw / 2f - bounds.width() / 2f - bounds.left;
		float yOff = ch / 2f + bounds.height() / 2f - bounds.bottom;
		canvas.drawText(text, xOff, yOff, name);
	}
	
    @Override
    protected void onDraw(Canvas canvas)
	{
        super.onDraw(canvas);
        
		try {
			//Typing Text Animation
			if(showTextAnim)
			{
				if(step > text.length())
				{
					Thread.sleep(index >= 5 ? 3500 : 1200);
					step = 1;
					index++;
				}

				if(index >= messages.length)
				{
					showTextAnim = false;
					invalidate();
					return;
				}

				text = messages[index];
				canvas.drawBitmap(canvasBmp, 0, 0, bgBmp);
				canvas.drawRect(0, 0, width, height, bg);
				canvas.drawText(text.substring(0, step++), x, y, txt);
				postInvalidateDelayed(delay);
			}
			//Binary Matrix Animation
			else
			{
				canvas.drawBitmap(canvasBmp, 0, 0, bgBmp);
				drawCanvas();
				invalidate();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
}
