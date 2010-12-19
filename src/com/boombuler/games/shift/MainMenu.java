package com.boombuler.games.shift;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.*;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;

import android.util.Log;



public class MainMenu extends CCLayer {

	public static CCScene scene() {
		CCScene result = CCScene.node();
		result.addChild(getBackground());
		result.addChild(new MainMenu());
		return result;
	}
	
	private CCLayer getMenu() {	
		
		CCMenuItem itm = CCMenuItemAtlasFont.item("HALLO", "Fonts.png", 16, 16, '0', this, "onLog");
		
		
        CCMenu result = CCMenu.menu(itm);
		result.alignItemsVertically();
		return result;
	}
	
	private MainMenu() {
		this.addChild(getMenu());
	}
	
	public void onQuit() {
		CCDirector.sharedDirector().end();
	}
	
	public void onLog() {
		Log.d("BOOMBULER", "test me!");
	}
	
	private static CCNode getBackground() {
		CCSprite bgimg = CCSprite.sprite("bground.png");
		
		CGSize s = CCDirector.sharedDirector().winSize();
		
		bgimg.setPosition(s.width / 2, s.height / 2);
		return bgimg;
	}
	
}
