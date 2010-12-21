package com.boombuler.games.shift;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import com.boombuler.games.shift.render.Background;
import com.boombuler.games.shift.render.Block;
import com.boombuler.games.shift.render.ScoreLabel;

import android.graphics.PointF;
import android.view.MotionEvent;

public class Board extends CCLayer implements Game.BlockChangeListener {

	public static final float ANIMATION_TIME = 0.3f;
	private static CCScene fCurrent = null;
	
	public static void GoToBoard() {
		if (Settings.Current().getHasReadManual())
			CCDirector.sharedDirector().runWithScene(Board.scene());
		else
			CCDirector.sharedDirector().runWithScene(HelpScreen.scene(Board.scene()));
	}
	
	private static CCScene scene() {
		if (fCurrent == null) {
			fCurrent = CCScene.node();
			fCurrent.addChild(new Background());
			fCurrent.addChild(new Board());
			fCurrent.addChild(new ScoreLabel());			
		}
		return fCurrent;
	}
		
	public Board() {
		Game.Current().setBlockChangedListener(this);
		this.setIsTouchEnabled(true);
		setScale(Main.SCALE);
		setAnchorPoint(0f, 0f);
		CGSize s = CCDirector.sharedDirector().winSize();
		float boardSize = Game.BOARD_SIZE_WITH_CACHE * Block.BLOCK_SIZE * Main.SCALE;
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
					Game.Current().Move(Game.MoveDirection.Up);
				else
					Game.Current().Move(Game.MoveDirection.Down);
			}
		}
		mTouchStart = null;
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
			CCCallFunc cf = CCCallFunc.action(this, "removeNullBlocks");
			b.FadeOut(cf);
		}
	}
	
	public void removeNullBlocks() {
		Block b = null;
		while((b = FindBlock(0, 0)) != null) {
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
