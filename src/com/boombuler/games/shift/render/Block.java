package com.boombuler.games.shift.render;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.boombuler.games.shift.Board;
import com.boombuler.games.shift.Game;
import com.boombuler.games.shift.Main;

public class Block extends CCSprite{

	private static float BLOCK_SIZE = 48f;
	
	private int mRow, mCol, mNextRow, mNextCol;
	private boolean mAnimating = false;
	
	public Block(int row, int col, byte type) {
		super(getFile(type));
		mRow = row;
		mCol = col;
		setPosition(posToPoint(mRow, mCol));
	}
	
	private static String getFile(byte type) {
		switch(type) { 
			case Game.BLOCK_TYPE_1: return "block_blue.png";
			case Game.BLOCK_TYPE_2: return "block_green.png";
			case Game.BLOCK_TYPE_3: return "block_red.png";
			case Game.BLOCK_TYPE_4: return "block_yellow.png";
			case Game.BLOCK_TYPE_5: return "block_grey.png";
		}
		return "";
	}
	
	public boolean isAtPos(int row, int col) {
		return mRow == row && mCol == col;
	}
	
	private CGPoint posToPoint(int row, int col) {
		return CGPoint.ccp(col * BLOCK_SIZE, Main.SUPPOSED_WIN_HEIGHT -(row*BLOCK_SIZE));	
	}
	
	public void FadeOut(CCCallFunc onComplete) {
		mCol = 0;
		mRow = 0;
		CCFadeOut fo = CCFadeOut.action(Board.ANIMATION_TIME);
		if (onComplete != null)
			this.runAction(CCSequence.actions(fo, onComplete));
		else 
			this.runAction(fo);
	}
	
	public void MoveTo(int row, int col) {
		if (row != mRow || col != mCol) {
			mNextCol = col;
			mNextRow = row;
			CCMoveTo by = CCMoveTo.action(Board.ANIMATION_TIME, posToPoint(row, col));
			CCCallFunc update = CCCallFunc.action(this, "updatePosition");
			mAnimating = true;		
			this.runAction(CCSequence.actions(by, update));
		}
	}
	
	public void updatePosition() {
		mCol = mNextCol;
		mRow = mNextRow;
		mAnimating = false;
	}
		
	public boolean isAnimating() {
		return mAnimating;
	}
	
}
