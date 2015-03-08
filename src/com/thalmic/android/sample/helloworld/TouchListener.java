package com.thalmic.android.sample.helloworld;
import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchListener implements OnTouchListener {
	private ArrayList<int[]> fingers = new ArrayList<int[]>();
	private int actionMask;
	private ArrayList<Integer> prevIDs = new ArrayList<Integer>();
	private int [] screenDimensions = new int[2];
	public TouchListener(){
		Log.e("myid", "createdThing");
	}
	protected void setDimensions(int width, int height)
	{
		screenDimensions[0] = width;
		screenDimensions[1] = height;
	}
	int placToCoordsX(float x)
	{
		
		return 0; //TODO
	}
	int placToCoordsY(float y)
	{
		return 0; //TODO
	}
	int[] coordinate = new int[2];
	int ID;
	@Override
    public boolean onTouch(View v, MotionEvent e)
	{
		Log.e("myid", "created");
		for(int i = 0; i < fingers.size(); i++)
		{
			Log.e("myid", "C: ".concat(Integer.toString(coordinate[0])).concat(", ").concat(Integer.toString(coordinate[1])) );
		}
			actionMask = e.getActionMasked();
		    switch (actionMask)
		    {
		        case MotionEvent.ACTION_DOWN:
		        	ID = e.getPointerId(e.getActionIndex());
		        	coordinate[0] = placToCoordsX(e.getX());
		        	coordinate[1] = placToCoordsY(e.getY());
		        	fingers.add(coordinate.clone());
		        	prevIDs.add(ID);
		        break;
		        case MotionEvent.ACTION_POINTER_DOWN:
		        	ID = e.getPointerId(e.getActionIndex());
		        	coordinate[0] = placToCoordsX(e.getX());
		        	coordinate[1] = placToCoordsY(e.getY());
		        	fingers.add(coordinate.clone());
		        	prevIDs.add(ID);
		        break;
		        case MotionEvent.ACTION_MOVE:
		        	for(int i = 0; i < fingers.size(); i++)
		        	{
		        		coordinate[0] = placToCoordsX(e.getX(e.findPointerIndex(prevIDs.get(i))));
		        		coordinate[1] = placToCoordsY(e.getY(e.findPointerIndex(prevIDs.get(i))));
		        		fingers.set(i, coordinate);
		        	}
		        break;
		        case MotionEvent.ACTION_UP:
		        	fingers.clear();
		        	prevIDs.clear();
		        break;
		        case MotionEvent.ACTION_POINTER_UP:
		        	for(int i = 0; i < fingers.size(); i++)
		        	{
		        		if(e.getPointerId(e.getActionIndex()) == prevIDs.get(i))
			        	{
		        			prevIDs.remove(i);
		        			fingers.remove(i);
			        	}
		        	}
		        break;
		    }
		return true;
    }
}