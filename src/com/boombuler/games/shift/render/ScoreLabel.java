package com.boombuler.games.shift.render;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGSize;

import com.boombuler.games.shift.Game;
import com.boombuler.games.shift.MyResources;
import com.boombuler.games.shift.R;

public class ScoreLabel extends CCLayer implements Game.ScoreChangedListener{

	private final CCBitmapFontAtlas mLabel;
	
	public ScoreLabel() {
		super();
		mLabel = CCBitmapFontAtlas.bitmapFontAtlas(" ", "font.fnt");
		CGSize s = CCDirector.sharedDirector().winSize();		
		mLabel.setAnchorPoint(0f, 0f);
		mLabel.setScale(Block.SCALE);
		mLabel.setPosition(10, s.height - 30);
		this.addChild(mLabel);
		Game.Current().setScoreChangedListener(this);		
	}

	@Override
	public void OnScoreChanged(int lastMove, long totalScore) {
		String txt = String.format(MyResources.string(R.string.score), totalScore, lastMove);
		mLabel.setString(txt);
	}

}
