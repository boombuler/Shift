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

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.base.CCFiniteTimeAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.transitions.CCTransitionScene;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import com.boombuler.games.shift.Game.BlockMove;
import com.boombuler.games.shift.render.Background;
import com.boombuler.games.shift.render.Block;
import com.boombuler.games.shift.render.ScoreLabel;

import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Board extends CCLayer implements Game.BlockChangeListener, KeyHandler {

	public static boolean DEBUG = false;
	
	public static final float ANIMATION_TIME = 0.3f;
	private static CCScene fCurrent = null;
	private static Board fCurrentBoard = null;
	
	
	public static CCScene scene() {
		if (Settings.Current().getHasReadManual())
			return MakeScene();
		else
			return HelpScreen.scene(MakeScene());
	}
	
	public static CCSprite getCenterScaledImg(String img) {
		CCSprite result = CCSprite.sprite(img);
		CGSize s = CCDirector.sharedDirector().winSize();
		result.setScale(Block.SCALE * Main.SCALE);		
		result.setPosition(CGPoint.make(s.width / 2, s.height / 2));
		return result;
	}
	
	private static CCScene MakeScene() {
		if (fCurrent == null) {
			fCurrentBoard = new Board();
			fCurrent = CCScene.node();
			fCurrent.addChild(Background.node());
			fCurrent.addChild(getCenterScaledImg("gameboardbottom.png"));
			
			fCurrent.addChild(fCurrentBoard);
			if (!DEBUG)
				fCurrent.addChild(getCenterScaledImg("gameboardtop.png"));
			fCurrent.addChild(new ScoreLabel());			
		}
		Game.Current().setBlockChangedListener(fCurrentBoard);
		return fCurrent;
	}
		
	public Board() {
		this.setIsTouchEnabled(true);
		setScale(Main.SCALE);
		setAnchorPoint(0f, 0f);
		CGSize s = CCDirector.sharedDirector().winSize();
		float boardSize = (Game.BOARD_SIZE_WITH_CACHE) * 
		                  Block.BLOCK_SIZE * Main.SCALE;
		setPosition(CGPoint.make((s.width - boardSize) / 2, (s.height - boardSize) / 2));
	}

	private PointF mTouchStart = null;
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		// TODO Auto-generated method stub
		mTouchStart = new PointF(event.getX(), event.getY());
		return super.ccTouchesBegan(event);
	}
	
	@Override
	public boolean ccTouchesCancelled(MotionEvent event) {
		mTouchStart = null;
		return super.ccTouchesCancelled(event);
	}
	
	public float getTouchDistance(MotionEvent ev) {
		float wid = Math.abs(ev.getX() - mTouchStart.x);
		float hei = Math.abs(ev.getY() - mTouchStart.y);
		
		return (float)Math.sqrt(Math.pow(wid, 2) + Math.pow(hei, 2));
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		if (!IsAnimating()) {
			CGSize winSize = CCDirector.sharedDirector().winSize();
			final double screenDiag = Math.sqrt((winSize.height* winSize.height)+(winSize.width*winSize.width));
			final float posX;
			final float posY;
			if (getTouchDistance(event) > (screenDiag / 4)) {
				posX = event.getX() - mTouchStart.x;
				posY = event.getY() - mTouchStart.y;	
			} else {
				posX = event.getX() - (winSize.width / 2);
				posY = event.getY() - (winSize.height / 2);				
			}
			if (Math.abs(posX) > Math.abs(posY)) {
				if (posX > 0)
					Game.Current().Move(Game.MoveDirection.Right);
				else
					Game.Current().Move(Game.MoveDirection.Left);
			} else {
				if (posY > 0)
					Game.Current().Move(Game.MoveDirection.Down);
				else
					Game.Current().Move(Game.MoveDirection.Up);
			}
		}
		mTouchStart = null;
		return super.ccTouchesEnded(event);
	}
	
	@Override
	public void visit(GL10 gl) {
		if (!DEBUG) {
			gl.glEnable(GL10.GL_SCISSOR_TEST);
			int boardsize = (int)(Game.BOARD_SIZE * Block.BLOCK_SIZE * Main.SCALE);
			CGSize size = CCDirector.sharedDirector().winSize();
			int x = (int)(size.width - boardsize) / 2;
			int y = (int)(size.height - boardsize) / 2;
			
			gl.glScissor(x, y, boardsize, boardsize);
			
			super.visit(gl);
			
			gl.glDisable(GL10.GL_SCISSOR_TEST);
		}
		else
			super.visit(gl);
	}

	@Override
	public void BlockAdded(int row, int col, byte blockType) {
		Block sprite = new Block(row, col, blockType);
		this.addChild(sprite);
	}


	@Override
	public void Cleared() {
		if (this.children_ != null)
			this.children_.clear();
	}
	
	@Override
	public void BlocksMoved(List<Game.BlockMove> moves) {
		for(BlockMove bm : moves) {
			Block b = FindBlock(bm.RowOld, bm.ColOld);
			if (b != null) {
				CCFiniteTimeAction ac = b.MoveTo(bm.RowNew, bm.ColNew);
				if (ac != null)
					b.runAction(ac);
			}			
		}
	}
	
	@Override
	public void EndMoving() {
		this.schedule("finishmoving");
	}
	
	public void finishmoving(float dt) {		
		if (!this.IsAnimating()) {
			this.unschedule("finishmoving");
			Game.Current().AnimationsComplete();
		}
	}
	
	@Override
	public void BlockRemoved(int row, int col) {
		Block b = FindBlock(row, col);
		if (b != null) {
			CCCallFunc cf = CCCallFunc.action(this, "removeNullBlocks");
			b.FadeOut(cf);
		}
	}
	
	public void removeNullBlocks() {
		Block b = null;
		
		while((b = FindBlock(0, 0)) != null) {
			removeChild(b, true);
		}
	}
	
	private Block FindBlock(int row, int col) {
		for(CCNode blk : this.children_) {
			if (blk instanceof Block) {
				Block block = (Block)blk;
				if (block.isAtPos(row, col))
					return block;
			}
		}
		return null;
	}
	
	private boolean IsAnimating() {
		for(CCNode blk : this.children_) {
			if (blk instanceof Block) {
				Block block = (Block)blk;
				if (block.isAnimating())
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean HandleKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP && !IsAnimating()) {
			switch(event.getKeyCode()) {
				case KeyEvent.KEYCODE_MENU: {
					gotoMainMenu();
					return true;
				}
				case KeyEvent.KEYCODE_DPAD_LEFT: {
					Game.Current().Move(Game.MoveDirection.Left);
					return true;
				}
				case KeyEvent.KEYCODE_DPAD_RIGHT: {
					Game.Current().Move(Game.MoveDirection.Right);
					return true;
				}
				case KeyEvent.KEYCODE_DPAD_DOWN: {
					Game.Current().Move(Game.MoveDirection.Down);
					return true;
				}
				case KeyEvent.KEYCODE_DPAD_UP: {
					Game.Current().Move(Game.MoveDirection.Up);
					return true;
				}
				
				default: return false;
			}
		}
		return false;
	}
	
	private void gotoMainMenu() {
		CCTransitionScene trans = Main.getTransisionFor(MainMenu.scene());
		CCDirector.sharedDirector().replaceScene(trans);
	}
}
