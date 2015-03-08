/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.thalmic.android.sample.helloworld;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

public class HelloWorldActivity extends Activity {
	protected TouchListener detect;
    private int strumCount = 0;
    private ArrayList<Float> pastValues = new ArrayList<Float>();
    private boolean hadUpstroke = true;
    protected GraphicsController graphicsController;

    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
        }
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
        }

        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
        }

        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
        }

        @Override
        public void onUnlock(Myo myo, long timestamp) {
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
        }
        
        public boolean isStrumming()
        {
        	if(pastValues.size() < 1) return false;
        	float diff = pastValues.get(0) - pastValues.get(pastValues.size()-1);
        	if(!hadUpstroke)
    		{
        		if (diff > 6)
            	{
        			hadUpstroke=true;
            	}
    		} else
    		{
    			if (diff < -10)
            	{
            		hadUpstroke=false;
            		return true;
            	}
    		}
        	return false;
        }
        public void checkStrumming(float pitch)
        {
        	int[] toplay = detect.getKeys();
        	pastValues.add(pitch);
        	if(pastValues.size() > 20) pastValues.remove(0);
        	if(isStrumming())
        	{
        		strumCount ++;
        		for(int i = 0; i < 6; i++)
        			playNote(toplay[i]);
        		pastValues.clear();
        		pastValues.add(pitch);
        		Log.e("myid", "Strum");
        		Log.e("myid", Integer.toString(strumCount));
        		
        		
        		
        		//TODO working here right now
        	}
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }

            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            //mTextView.setRotation(roll);
            //mTextView.setRotationX(pitch);
            //mTextView.setRotationY(yaw);
            checkStrumming(pitch);
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            switch (pose) {
                case UNKNOWN:
                    //mTextView.setText(getString(R.string.hello_world));
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.hello_world;
                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    //mTextView.setText(getString(restTextId));
                    break;
                case FIST:
                    //mTextView.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    //mTextView.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    //mTextView.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    //mTextView.setText(getString(R.string.pose_fingersspread));
                    break;
            }

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detect = new TouchListener();
        graphicsController = new GraphicsController(this, detect);
		graphicsController.setOnTouchListener(detect);
		setContentView(graphicsController);
		setWindowAndAudio();


        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
    }
	protected MediaPlayer backMusic;
	private SoundPool spool;
	private int[] soundPoolMap = new int[12];
	protected AudioManager audioManager;
	protected void setWindowAndAudio()
	{
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		/*
		for (int i = 0; i < 12; i++) {
			soundPoolMap[i] = spool.load("res/raw/n" + i + ".mp3", 1);
		}*/
		soundPoolMap[0] = spool.load(this, R.raw.n0, 1);
		soundPoolMap[1] = spool.load(this, R.raw.n1, 1);
		soundPoolMap[2] = spool.load(this, R.raw.n2, 1);
		soundPoolMap[3] = spool.load(this, R.raw.n3, 1);
		soundPoolMap[4] = spool.load(this, R.raw.n4, 1);
		soundPoolMap[5] = spool.load(this, R.raw.n5, 1);
		soundPoolMap[6] = spool.load(this, R.raw.n6, 1);
		soundPoolMap[7] = spool.load(this, R.raw.n7, 1);
		soundPoolMap[8] = spool.load(this, R.raw.n8, 1);
		soundPoolMap[9] = spool.load(this, R.raw.n9, 1);
		soundPoolMap[10] = spool.load(this, R.raw.n10, 1);
		soundPoolMap[11] = spool.load(this, R.raw.n11, 1);
		/*
		soundPoolMap[12] = spool.load(this, R.raw.e2, 1);
		soundPoolMap[13] = spool.load(this, R.raw.f2, 1);
		soundPoolMap[14] = spool.load(this, R.raw.fs2, 1);
		soundPoolMap[15] = spool.load(this, R.raw.g2, 1);
		soundPoolMap[16] = spool.load(this, R.raw.gs2, 1);
		soundPoolMap[17] = spool.load(this, R.raw.a2, 1);
		soundPoolMap[18] = spool.load(this, R.raw.as2, 1);
		soundPoolMap[19] = spool.load(this, R.raw.b2, 1);
		soundPoolMap[20] = spool.load(this, R.raw.c2, 1);
		soundPoolMap[21] = spool.load(this, R.raw.cs2, 1);
		soundPoolMap[22] = spool.load(this, R.raw.d2, 1);
		soundPoolMap[23] = spool.load(this, R.raw.ds2, 1);
		soundPoolMap[24] = spool.load(this, R.raw.e3, 1);
		soundPoolMap[25] = spool.load(this, R.raw.f3, 1);
		soundPoolMap[26] = spool.load(this, R.raw.fs3, 1);
		soundPoolMap[27] = spool.load(this, R.raw.g3, 1);
		soundPoolMap[28] = spool.load(this, R.raw.gs3, 1);
		soundPoolMap[29] = spool.load(this, R.raw.a3, 1);*/
	}
    protected void playNote(int toPlay)
	{
		float newV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (toPlay != -1) {
			spool.play(soundPoolMap[toPlay%12], newV, newV, 1, 0, (float)Math.pow(2, toPlay/12));
		}
	}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
}
