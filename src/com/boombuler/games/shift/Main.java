package com.boombuler.games.shift;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity {
	
	private CCGLSurfaceView mGLSurfaceView;
	
	public static final float SUPPOSED_WIN_WIDTH  = 320; 
	public static final float SUPPOSED_WIN_HEIGHT = 480;
	public static float SCREEN_HEIGHT;
	public static float SCALE_X;
	public static float SCALE_Y;
	public static float SCALE;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	MyResources.setContext(this);
    	// set the window status, no tile, full screen and don't sleep
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	mGLSurfaceView = new CCGLSurfaceView(this);
    	
    	calcScreenDimensions();

    	CCDirector.sharedDirector().setScreenSize(SUPPOSED_WIN_WIDTH, SUPPOSED_WIN_HEIGHT);
        CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
        CCDirector.sharedDirector().setDisplayFPS(false);
        CCDirector.sharedDirector().attachInView(mGLSurfaceView);
        
	    setContentView(mGLSurfaceView);
    }
    
    
    private void calcScreenDimensions() {
        final DisplayMetrics pDisplayMetrics = new DisplayMetrics();
		CCDirector.sharedDirector().getActivity().getWindowManager().getDefaultDisplay().getMetrics(pDisplayMetrics);
		
		SCREEN_HEIGHT = pDisplayMetrics.heightPixels;
		SCALE_X = pDisplayMetrics.widthPixels / SUPPOSED_WIN_WIDTH;
		SCALE_Y = pDisplayMetrics.heightPixels / SUPPOSED_WIN_HEIGHT;
		
		final float mRatio = (float)SUPPOSED_WIN_WIDTH / SUPPOSED_WIN_HEIGHT;
		final float realRatio = (float)pDisplayMetrics.widthPixels / pDisplayMetrics.heightPixels;

		if(realRatio < mRatio) {
			SCALE = SCALE_X;
		} else {
			SCALE = SCALE_Y;
		}
	}
      
    
    @Override
    public void onStart() {
        super.onStart(); 
        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);
        Board.GoToBoard();
    }

    @Override
    public void onPause() {
        super.onPause();
        CCDirector.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        CCDirector.sharedDirector().resume();
    }

    @Override
    public void onDestroy() {
        super.onStop();
        CCDirector.sharedDirector().end();
    }
    
}