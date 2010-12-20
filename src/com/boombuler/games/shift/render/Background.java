package com.boombuler.games.shift.render;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import com.boombuler.games.shift.Main;

public class Background extends CCSprite {

	public Background() {
		super("bground.png");
		CGSize s = CCDirector.sharedDirector().winSize();
		setScaleX(Main.SCALE_X);
		setScaleY(Main.SCALE_Y);
		setPosition(CGPoint.make(s.width / 2, s.height / 2));

	}
}
