package com.thalmic.android.sample.helloworld;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
public final class GraphicsController extends View
{
	public Resources res;
	public BitmapFactory.Options opts;
	public String packageName;
	Bitmap back;
	private TouchListener touch;
	public GraphicsController(Context co)
	{
		super(co);
		opts = new BitmapFactory.Options();
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inTempStorage = new byte[16 * 1024];
		packageName = co.getPackageName();
		res = co.getResources();
		back = loadImage("guitar_frets", 1920, 1080);
	}
	public void setTouch(TouchListener t)
	{
		touch = t;
	}
	@Override
	protected void onDraw(Canvas g)
	{
        g.drawBitmap(back, 0, 0, null);
        for(int i = 0; i < 6; i++)
        {
            if(touch.activeNodes[i] != 0)
            {
                int l = 0;
                if(5-touch.activeNodes[i]!=0)
                {
                    l = touch.fretLocations[5-1-touch.activeNodes[i]];
                }
                g.drawLine(l,touch.stringLocations[i],touch.fretLocations[5-touch.activeNodes[i]],touch.stringLocations[i], null);
            }
        }

		//g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
	}
	public Bitmap loadImage(String imageName, int width, int height)
	{
		int imageNumber = res.getIdentifier(imageName, "drawable", packageName);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), width, height, false);
	}
}