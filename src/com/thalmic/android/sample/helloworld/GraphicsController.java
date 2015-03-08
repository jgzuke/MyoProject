package com.thalmic.android.sample.helloworld;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
public final class GraphicsController extends View
{
	public GraphicsController(Context co)
	{
		super(co);
	}
	@Override
	protected void onDraw(Canvas g)
	{
		//g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
	}
}