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
		
		setPosition(s.width / 2, s.height / 10);
		Game.Current().setScoreChangedListener(this);		
	}

	@Override
	public void OnScoreChanged(int lastMove, long totalScore) {
		String txt = String.format(MyResources.string(R.string.score), totalScore, lastMove);
		mLabel.setString(txt);
	}

}
