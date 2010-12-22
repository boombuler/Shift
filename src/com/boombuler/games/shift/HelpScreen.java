package com.boombuler.games.shift;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.transitions.*;
import org.cocos2d.types.CGSize;

import android.view.MotionEvent;

import com.boombuler.games.shift.render.Background;
import com.boombuler.games.shift.render.Label;

public class HelpScreen extends CCLayer {

	public static CCScene scene(CCScene next) {
		CCScene result = CCScene.node();
		result.addChild(new Background());
		result.addChild(new HelpScreen(next));
		result.setScale(Main.SCALE);
		return result;
	}
	
	private CCScene mNext;
	
	public HelpScreen(CCScene next) {
		mNext = next;
		
		CGSize s = CCDirector.sharedDirector().winSize();

		String[] text = MyResources.stringArray(R.array.helpscreen);
		int pos=text.length * (int)Label.SMALLER;
		for (String str : text) {		
			Label lbl = new Label(str, Label.SMALLER);		
			lbl.setPosition(0, pos);
			pos -= Label.SMALLER;
			addChild(lbl);
		}
		setPosition(s.width / 2, (s.height / 2) - (text.length * Label.SMALLER)/2);
		setIsTouchEnabled(true);
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		Settings.Current().setHasReadManual(true);
		CCTransitionScene transition = Main.getTransisionFor(mNext);
		CCDirector.sharedDirector().replaceScene(transition);
		mNext = null;		
		return super.ccTouchesEnded(event);
	}
	
}
