package com.boombuler.games.shift.render;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCLabel;

import com.boombuler.games.shift.Game;
import com.boombuler.games.shift.Main;
import com.boombuler.games.shift.MyResources;
import com.boombuler.games.shift.R;

public class ScoreLabel extends CCLayer implements Game.ScoreChangedListener{

	private final CCLabel mLabel;
	
	public ScoreLabel() {
		super();
		mLabel = CCLabel.makeLabel(" ", "DroidSans", 20);
		mLabel.setAnchorPoint(0f, 0f);
		mLabel.setPosition(10, Main.SUPPOSED_WIN_HEIGHT - 50);
		this.addChild(mLabel);
		Game.Current().setScoreChangedListener(this);		
	}

	@Override
	public void OnScoreChanged(int lastMove, long totalScore) {
		String txt = String.format(MyResources.string(R.string.score), totalScore, lastMove);
		mLabel.setString(txt);
	}

}
