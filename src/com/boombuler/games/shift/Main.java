package com.boombuler.games.shift;

import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity {
	
	private CCGLSurfaceView mGLSurfaceView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	// set the window status, no tile, full screen and don't sleep
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    	WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
    	WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	mGLSurfaceView = new CCGLSurfaceView(this);
    	setContentView(mGLSurfaceView);    	

    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        // attach the OpenGL view to a window
        Director.sharedDirector().attachInView(mGLSurfaceView);

        // no effect here because device orientation is controlled by manifest
        Director.sharedDirector().setDeviceOrientation(Director.CCDeviceOrientationPortrait);

        // show FPS
        // set false to disable FPS display, but don't delete fps_images.png!!
        Director.sharedDirector().setDisplayFPS(true);

        // frames per second
        Director.sharedDirector().setAnimationInterval(1.0f / 60);

        Scene scene = TestScene.scene();
        // Make the Scene active
        Director.sharedDirector().runWithScene(scene);
    }

    @Override
    public void onPause() {
        super.onPause();

        Director.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        Director.sharedDirector().resume();
    }

    @Override
    public void onStop() {
        super.onStop();

        Director.sharedDirector().end();
    }
    
}