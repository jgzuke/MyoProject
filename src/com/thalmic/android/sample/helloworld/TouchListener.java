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
	
	static int[] baseKeys = {0,5,10,15,19,24};
	static int[] stringLocations = {54,235,411,577,745,925};
	static int[] fretLocations = {408,740,1060,1350,2000};
	int[] activeNodes = {0,0,0,0,0,0};
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
		return (int)x; //TODO
	}
	int placToCoordsY(float y)
	{
		return (int)y; //TODO
	}
	int[] coordinate = new int[2];
	int ID;
	@Override
    public boolean onTouch(View v, MotionEvent e)
	{
		//Log.e("myid", "created");
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
		        	coordinate[0] = placToCoordsX(e.getX(ID));
		        	coordinate[1] = placToCoordsY(e.getY(ID));
		        	fingers.add(coordinate.clone());
		        	prevIDs.add(ID);
		        break;
		        case MotionEvent.ACTION_MOVE:
		        	for(int i = 0; i < fingers.size(); i++)
		        	{
		        		coordinate[0] = placToCoordsX(e.getX(e.findPointerIndex(prevIDs.get(i))));
		        		coordinate[1] = placToCoordsY(e.getY(e.findPointerIndex(prevIDs.get(i))));
		        		fingers.set(i, coordinate.clone());
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
		int[] newNodes = {0,0,0,0,0,0};
		for(int i = 0; i < fingers.size(); i++)
		{
			if(actionMask != MotionEvent.ACTION_MOVE) Log.e("myid", "C: ".concat(Integer.toString(fingers.get(i)[0])).concat(", ").concat(Integer.toString(fingers.get(i)[1])) );
			int x = fingers.get(i)[0] - 62;
	    	int y = fingers.get(i)[1];
	    	int ax = 0;
	    	int ay = 100;
			int screenx = 1630-62;
			int screeny = 978;
			for(int j = 0; j<5; j++)
			{
				if(x<fretLocations[j])
				{
					ax = j;
					break;
				}
			}			
			ax = 5-ax;
			for(int j = 0; j<6; j++)
			{
				if(Math.abs(y-stringLocations[j]) < 75)
				{
					ay = j;
					break;
				}
			}
			if(ay>6)
				continue;
			if(newNodes[ay] == 0)
			{
				newNodes[ay] = ax;
			}
		}
		activeNodes = newNodes;
		for(int i = 0; i<6; i++)
		{
			if(actionMask != MotionEvent.ACTION_MOVE) Log.e("myid", Integer.toString(activeNodes[i]+baseKeys[i]) + " ");
		}
		return true;
    }
}