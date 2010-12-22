package com.boombuler.games.shift;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.transitions.CCTransitionScene;

import android.graphics.Point;

public class Game {

	private final int MIN_DESTROY_COUNT = 3;
	public static final int BOARD_SIZE = 6;
	public static final int BOARD_CACHE_SIZE = 1;
	public static final int BOARD_SIZE_WITH_CACHE = BOARD_SIZE + 2 * BOARD_CACHE_SIZE;
	private static final int[] CACHE_INDEXES = new int[] { 7, 0 };
	
	
	public static final byte BLOCK_COLOR_COUNT_EASY = 4;
	public static final byte BLOCK_COLOR_COUNT_NORMAL = 5;
	
	public static final byte BLOCK_TYPE_FREE = 0;
	public static final byte BLOCK_TYPE_1 = 1;
	public static final byte BLOCK_TYPE_2 = 2;
	public static final byte BLOCK_TYPE_3 = 3;
	public static final byte BLOCK_TYPE_4 = 4;
	public static final byte BLOCK_TYPE_5 = 5;
	
	public interface BlockChangeListener {
		void Cleared();
		void EndMoving();
		void BlockAdded(int row, int col, byte blockType);
		void BlockMoved(int rowOld, int colOld, int rowNew, int colNew);
		void BlockRemoved(int row, int col);
	}
	
	public interface ScoreChangedListener {
		void OnScoreChanged(int lastMove, long totalScore);
	}
	

	public enum Difficulty {
		Easy,
		Normal
	}
	
	public enum MoveDirection {
		Up,
		Left,
		Down,
		Right
	}
	
	private static Game fCurrent = null;
	
	public static Game Current() {
		if (fCurrent == null) {
			fCurrent = new Game();
		}
		return fCurrent;
	}
		
	private GameState mState = null;
	private final Random mRandom = new Random();
	private ScoreChangedListener mScoreListener;
	private BlockChangeListener mBlockListener; 
	
	
	
	private Game() {
		// initialize default GameState
		setDifficulty(Difficulty.Normal);
	}
		
	public void setDifficulty(Difficulty difficulty) {
		mState = new GameState(difficulty);
		if (mScoreListener != null)
			mScoreListener.OnScoreChanged(0, 0);
		InitNewBoard();
	}
	
	private void InitNewBoard() {
		if (mBlockListener != null)
			mBlockListener.Cleared();
		FillMissingCache();
		Move(MoveDirection.Up);
		Move(MoveDirection.Down);
		Move(MoveDirection.Left);
		Move(MoveDirection.Right);		
	}
	
	private void FillMissingCache() {
		for(int i = 0; i < CACHE_INDEXES.length; i++) {
			int row = CACHE_INDEXES[i];
			for (int col = BOARD_CACHE_SIZE; col < BOARD_SIZE + BOARD_CACHE_SIZE; col++) {
				if (mState.Board[row][col] == BLOCK_TYPE_FREE)
					setNewBlock(row, col, getValidRandomBlockForPos(row, col));
				if (mState.Board[col][row] == BLOCK_TYPE_FREE)
					setNewBlock(col, row, getValidRandomBlockForPos(col, row));
			}
		}
	}
	
	private void setNewBlock(int row, int col, byte type) {
		mState.Board[row][col] = type;
		if (mBlockListener != null) 
			mBlockListener.BlockAdded(row, col, type);
	}
	
	private byte getValidRandomBlockForPos(int row, int col) {
		int blockCnt = mState.Difficulty == Difficulty.Easy ? 
				BLOCK_COLOR_COUNT_EASY : BLOCK_COLOR_COUNT_NORMAL;
		while (true) {
			byte rndV = (byte)(mRandom.nextInt(blockCnt)+BLOCK_TYPE_1);
			
			// Check Above
			if (row > 0 && mState.Board[row-1][col] == rndV)
				continue;
			// Check Below
			if (row < BOARD_SIZE_WITH_CACHE-1 && mState.Board[row+1][col] == rndV)
				continue;
			// Check Left
			if (col > 0 && mState.Board[row][col-1] == rndV)
				continue;
			// Check Right
			if (col < BOARD_CACHE_SIZE-1 && mState.Board[row][col+1] == rndV)
				continue;			

			return rndV;
		}
	}
	
	public void Move(MoveDirection direction) {
		
		boolean anyMoved = false;
		if (direction.equals(MoveDirection.Down)) {
			for(int row = BOARD_SIZE; row >= 0; row--) {
				for(int col = BOARD_CACHE_SIZE; col < BOARD_SIZE + BOARD_CACHE_SIZE; col++) {
					anyMoved |= DoMoveBlock(row, col, row+1, col);
				}
			}
		} else if (direction.equals(MoveDirection.Up)) {
			for(int row = BOARD_CACHE_SIZE; row < BOARD_SIZE_WITH_CACHE; row++) {
				for(int col = BOARD_CACHE_SIZE; col < BOARD_SIZE + BOARD_CACHE_SIZE; col++) {
					anyMoved |= DoMoveBlock(row, col, row-1, col);
				}
			}
		} else if (direction.equals(MoveDirection.Left)) {
			for(int col = BOARD_CACHE_SIZE; col < BOARD_SIZE_WITH_CACHE; col++) {
				for(int row = BOARD_CACHE_SIZE; row < BOARD_SIZE + BOARD_CACHE_SIZE; row++) {
					anyMoved |= DoMoveBlock(row, col, row, col-1);
				}
			}
		} else if (direction.equals(MoveDirection.Right)) {
			for(int col = BOARD_SIZE; col >= 0; col--) {
				for(int row = BOARD_CACHE_SIZE; row < BOARD_SIZE + BOARD_CACHE_SIZE; row++) {
					anyMoved |= DoMoveBlock(row, col, row, col+1);
				}
			}
		}
		
		FillMissingCache();
		if (mBlockListener != null) {
			mBlockListener.EndMoving();
		}
		
	}
	
	public void AnimationsComplete() {
		CheckDestroyBlocks();
		if (IsGameOver()) {
			this.setBlockChangedListener(null);
			CCScene gameOver = GameOverScreen.scene(mState.Difficulty, mState.TotalScore);
			CCTransitionScene trans = Main.getTransisionFor(gameOver);			
			CCDirector.sharedDirector().replaceScene(trans);
			setDifficulty(Difficulty.Normal);
		}
	}
	
	private void CheckDestroyBlocks() {
		List<Point> allDestroyed = new LinkedList<Point>();
		
		for(int row = BOARD_CACHE_SIZE; row < BOARD_SIZE + BOARD_CACHE_SIZE; row++) {
			for (int col = BOARD_CACHE_SIZE; col < BOARD_SIZE + BOARD_CACHE_SIZE; col++) {
				byte type = mState.Board[row][col];
				if (type != BLOCK_TYPE_FREE) {
					List<Point> lst = new LinkedList<Point>();
					Point curPnt = new Point(col, row);
					lst.add(curPnt);
					
					PopulateDestroyList(lst, allDestroyed, curPnt, type);
					if (lst.size() >= MIN_DESTROY_COUNT) {
						allDestroyed.addAll(lst);
					}
				}
			}
		}

		for (Point p : allDestroyed) {
			if (mBlockListener != null)
				mBlockListener.BlockRemoved(p.y, p.x);
			mState.Board[p.y][p.x] = BLOCK_TYPE_FREE;	
		}
		int score = allDestroyed.size();		
		if (score > 0) {
			mState.Bonus++;
			score = mState.Bonus * score;
			mState.LastMoveScore = score;
			mState.TotalScore += score;
			if (mScoreListener != null)
				mScoreListener.OnScoreChanged(mState.LastMoveScore, mState.TotalScore);
		} else {
			mState.LastMoveScore = 0;
			mState.Bonus = 0;
		}		
	}
	
	private void PopulateDestroyList(List<Point> lst,List<Point> allDestroyed, Point fromPoint, byte testtype) {
		
		Point[] checks = new Point[] {
				new Point(fromPoint.x, fromPoint.y - 1),
				new Point(fromPoint.x, fromPoint.y + 1),
				new Point(fromPoint.x - 1, fromPoint.y),
				new Point(fromPoint.x + 1, fromPoint.y)
		};
		
		for(Point p : checks) { 
			if (p.x >= BOARD_CACHE_SIZE && p.y >= BOARD_CACHE_SIZE &&
					p.x < BOARD_CACHE_SIZE + BOARD_SIZE &&
					p.y < BOARD_CACHE_SIZE + BOARD_SIZE) {
				if (mState.Board[p.y][p.x] == testtype &&
						!lst.contains(p) && !allDestroyed.contains(p)) {
					lst.add(p);
					PopulateDestroyList(lst, allDestroyed, p, testtype);
				}
			}
		}
	}
		
	private boolean IsGameOver() {
		for(int row = BOARD_CACHE_SIZE; row < BOARD_SIZE + BOARD_CACHE_SIZE; row++) {
			for(int col = BOARD_CACHE_SIZE; col < BOARD_SIZE + BOARD_CACHE_SIZE; col++) {
				if (mState.Board[row][col] == BLOCK_TYPE_FREE)
					return false;
			}
		}
		return true;
	}
	
	private boolean DoMoveBlock(int rowOld, int colOld, int rowNew, int colNew) {
		if (mState.Board[rowNew][colNew] != BLOCK_TYPE_FREE)
			return false;
		mState.Board[rowNew][colNew] = mState.Board[rowOld][colOld];
		mState.Board[rowOld][colOld] = BLOCK_TYPE_FREE;
		if (mBlockListener != null)
			mBlockListener.BlockMoved(rowOld, colOld, rowNew, colNew);
		return true;
	}
	
	public void setBlockChangedListener(BlockChangeListener listener) {
		if (listener == mBlockListener)
			return;
		mBlockListener = listener;
		if (mBlockListener != null) {
			mBlockListener.Cleared();
			
			for (int row = 0; row < BOARD_SIZE_WITH_CACHE; row++) {
				for (int col = 0; col < BOARD_SIZE_WITH_CACHE; col++) {
					byte typ = mState.Board[row][col];
					if (typ != BLOCK_TYPE_FREE)
						mBlockListener.BlockAdded(row, col, typ);
				}
			}
		}		
	}
	
	public void setScoreChangedListener(ScoreChangedListener listener) {
		mScoreListener = listener;
		if (mScoreListener != null) {
			mScoreListener.OnScoreChanged(mState.LastMoveScore, mState.TotalScore);
		}
	}
	
}
