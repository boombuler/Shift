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

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.transitions.CCTransitionScene;
import org.cocos2d.types.CGSize;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.boombuler.games.shift.Game.Difficulty;
import com.boombuler.games.shift.render.Background;
import com.boombuler.games.shift.render.Label;

public class Highscores extends CCLayer implements KeyHandler{

	public static CCScene scene() {
		CCScene result = CCScene.node();
		result.addChild(Background.node());
		result.addChild(new Highscores());
		return result;
	}
	
	private Highscores() {
		setIsTouchEnabled(true);
		
		CGSize s = CCDirector.sharedDirector().winSize();
		float colWidth = (s.width / 4);
		CCNode easy = buildTable(Difficulty.Easy, colWidth * 1);
		CCNode normal = buildTable(Difficulty.Normal, colWidth * 3);

		float y = (s.height / 2) - (Settings.MAX_HIGHSCORE_COUNT * Label.DEFAULT)/2;
		
		easy.setPosition(0, y);
		normal.setPosition(0, y);		
		
	    this.addChild(easy);
	    this.addChild(normal);
	}
	
	private CCNode buildTable(Difficulty difficulty, float x) {
		CCNode result = CCNode.node();
		Long[] scores = Settings.Current().getHighscores(difficulty);
		int pos=1+(scores.length) * (int)Label.DEFAULT;

		Label header = new Label(difficulty == Difficulty.Easy ? MyResources.string(R.string.easy):
			MyResources.string(R.string.normal), Label.DEFAULT);		
		header.setPosition(x, pos);
		pos -= Label.DEFAULT;
		result.addChild(header);
		
		
		for (Long score : scores) {
			String str = score > 0 ? score.toString() : " ";
			Label lbl = new Label(str, Label.DEFAULT);		
			lbl.setPosition(x, pos);
			pos -= Label.DEFAULT;
			result.addChild(lbl);
		}
		return result;
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		done();
		return super.ccTouchesEnded(event);
	}
	
	private void done() {
		Settings.Current().setHasReadManual(true);
		CCTransitionScene transition = Main.getTransisionFor(MainMenu.scene());
		CCDirector.sharedDirector().replaceScene(transition);
	}

	@Override
	public boolean HandleKeyEvent(KeyEvent event) {
		done();
		return false;
	}
}
