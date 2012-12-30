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

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;

import com.boombuler.games.shift.Game;
import com.boombuler.games.shift.Main;
import com.boombuler.games.shift.MyResources;
import com.boombuler.games.shift.R;
import com.boombuler.games.shift.render.TextEntry.TextBoxType;

public class ScoreLabel extends CCNode implements Game.ScoreChangedListener{

	private final Label mTotalLabel;
	private final Label mLMLabel;
	
	public ScoreLabel() {
		super();
		 
		
		CGSize s = CCDirector.sharedDirector().winSize();
				
		TextEntry txTotal = new TextEntry(TextBoxType.Normal, " ");
		mTotalLabel = txTotal.getLabel();
		txTotal.setPosition(0f, 40f);
		
		TextEntry txLastMove = new TextEntry(TextBoxType.Normal, " ");
		mLMLabel = txLastMove.getLabel();

		this.addChild(txTotal);
		this.addChild(txLastMove);
		
		setPosition(s.width / 2, s.height / 15);
		setScale(Main.SCALE * Block.SCALE);
		Game.Current().setScoreChangedListener(this);
	}

	public void OnScoreChanged(int lastMove, long totalScore) {
		String txtTotal = String.format(MyResources.string(R.string.score), totalScore);
		String txtLM = String.format(MyResources.string(R.string.lastmove), lastMove);
		mTotalLabel.setString(txtTotal);
		mLMLabel.setString(txtLM);
	}

}
