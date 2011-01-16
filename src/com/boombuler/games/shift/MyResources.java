/*
 * Copyright (C) 2010 Florian Sundermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boombuler.games.shift;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.ccColor4B;

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
	
	public static String[] stringArray(int resourceId) {
		return mResources.getStringArray(resourceId);
	}
	
	public static CCSprite sprite(int resourceId) {
		Bitmap myBitmap = BitmapFactory.decodeResource(mResources, resourceId);
		
		return CCSprite.sprite(myBitmap, String.valueOf(resourceId));
	}
	
	public static ccColor4B color(int resourceId) {
		int[] color = mResources.getIntArray(resourceId);
		return new ccColor4B(color[0], color[1], color[2], color[3]);
	}
}
