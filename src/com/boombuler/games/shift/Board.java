package com.boombuler.games.shift;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;

import com.boombuler.games.shift.render.Block;

import android.view.MotionEvent;

public class Board extends CCLayer implements Game.BlockChangeListener {

	public static final float ANIMATION_TIME = 0.5f;
	private static CCScene fCurrent = null;
	
	public static CCScene scene() {
		if (fCurrent == null) {
			fCurrent = CCScene.node();
			fCurrent.addChild(new Board());
		}
		return fCurrent;
	}
	
	
	public Board() {
		Game.Current().setBlockChangedListener(this);
		this.setIsTouchEnabled(true);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		if (!IsAnimating()) {
			float posX = event.getX();
			float posY = event.getY();
	
			CGSize winSize = CCDirector.sharedDirector().winSize();
			posX = posX - (winSize.width / 2);
			posY = posY - (winSize.height / 2);
			
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
		return super.ccTouchesEnded(event);
	}

	@Override
	public void BlockAdded(int row, int col, byte blockType) {
		Block sprite = new Block(row, col, blockType);
		this.addChild(sprite);
	}


	@Override
	public void Cleared() {
		this.children_.clear();
	}
	
	@Override
	public void BlockMoved(int rowOld, int colOld, int rowNew, int colNew) {
		Block b = FindBlock(rowOld, colOld);
		if (b != null)
			b.MoveTo(rowNew, colNew);
	}
	
	@Override
	public void EndMoving() {
		this.schedule("finishmoving", ANIMATION_TIME);
	}
	
	public void finishmoving(float dt) {
		this.unschedule("finishmoving");	
		Game.Current().AnimationsComplete();
	}
	
	@Override
	public void BlockRemoved(int row, int col) {
		Block b = FindBlock(row, col);
		if (b != null) {
			children_.remove(b);
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
	
}
