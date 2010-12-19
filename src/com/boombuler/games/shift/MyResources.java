package com.boombuler.games.shift;


import org.cocos2d.nodes.CCSprite;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MyResources {
	
	private static Resources mResources;
	
	public static void setContext(Context context) {
		mResources = context.getResources();
	}
	
	public static String string(int resourceId) {
		return mResources.getString(resourceId);
	}
	
	public static CCSprite sprite(int resourceId) {
		Bitmap myBitmap = BitmapFactory.decodeResource(mResources, resourceId);
		
		return CCSprite.sprite(myBitmap, String.valueOf(resourceId));
	}
}
