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
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.transitions.CCTransitionScene;
import org.cocos2d.types.CGSize;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.boombuler.games.shift.Game.Difficulty;
import com.boombuler.games.shift.render.Background;
import com.boombuler.games.shift.render.Block;
import com.boombuler.games.shift.render.Label;
import com.boombuler.games.shift.render.TextEntry;
import com.boombuler.games.shift.render.TextEntry.TextBoxType;

public class Highscores extends CCLayer implements KeyHandler{

	private static final float smallHeaderHeigh = 35;
	private static final float tableSpacing = 10;
	public static CCScene scene() {
		CCScene result = CCScene.node();
		result.addChild(Background.node());
		result.addChild(new Highscores());
		return result;
	}
	
	private Highscores() {
		setIsTouchEnabled(true);
		setScale(Main.SCALE*Block.SCALE);
		CGSize s = CCDirector.sharedDirector().winSize();
		float colWidth = (s.width / 4);

		CCNode table = buildTables(colWidth);
		
		CCSprite head = CCSprite.sprite("highscores.png");
		head.setScale(Block.SCALE * Main.SCALE);
		final float headHeight = 63 * Block.SCALE;
		
		float totaltableSize = (Settings.MAX_HIGHSCORE_COUNT * Label.DEFAULT) + 
								tableSpacing + (2*smallHeaderHeigh);
		
		
		
		float y = (s.height / 2f) - (totaltableSize);
		table.setPosition(0, y);
		
		y = (s.height / 2) + (totaltableSize - headHeight + tableSpacing);
		
		head.setPosition(s.width / 2, y);
		this.addChild(head);
	    this.addChild(table);
	}
	
	private CCNode buildTables(float colWidth) {
		CCNode result = CCNode.node();		
		CCNode easy = buildTable(Difficulty.Easy, colWidth * 1);
		CCNode normal = buildTable(Difficulty.Normal, colWidth * 3);
		CCNode hard = buildTable(Difficulty.Hard, colWidth * 2);
		
		float tableSize = (Settings.MAX_HIGHSCORE_COUNT * Label.DEFAULT) + smallHeaderHeigh;
		easy.setPosition(0, tableSize + smallHeaderHeigh + tableSpacing);
		normal.setPosition(0, tableSize + smallHeaderHeigh + tableSpacing);
		hard.setPosition(0, 0 + smallHeaderHeigh);
		result.addChild(easy);
		result.addChild(normal);
		result.addChild(hard);
		return result;
	}
	
	private CCNode buildTable(Difficulty difficulty, float x) {
		CCNode result = CCNode.node();
		Long[] scores = Settings.Current().getHighscores(difficulty);
		int pos=1+(scores.length) * (int)Label.DEFAULT;

		CCSprite header = CCSprite.sprite( 
				difficulty == Difficulty.Easy ? 
						"easy.png":
						"normal.png");		
		header.setPosition(x, pos);
		pos -= 35;
		result.addChild(header);
		
		
		for (Long score : scores) {
			String str = score > 0 ? score.toString() : " ";
			TextEntry lbl = new TextEntry(TextBoxType.HighscoreEntry, str);
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
